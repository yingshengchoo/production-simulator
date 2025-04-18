package productsimulation.model.road;

import javafx.util.Pair;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.model.Building;
import java.io.Serializable;

import java.util.*;

// road容积无限，但不模拟运输过程。由request完成时的hook函数在latency回合后直接传送到目标仓库。
// GUI上点击road，只显示方向信息。road上也不准备做“有个货物在动”的动画。
public class Road implements Serializable {
    // key: (source,destination), value: distance
    // 如果存在，返回最短路程；如果不存在，说明不可达
    public static HashMap<Pair<Building, Building>, Road> roadMap = new HashMap<>();
    // 虽然已经有board，但让board存带方向信息的roadtile并不合适
    public static HashMap<Coordinate, ArrayList<RoadTile>> existingRoadTiles = new HashMap<>();
    // 相邻或自己到自己的特殊road，类似传送门，也类似ev1中的设定
    public static Road PORTAL = new Road(null, null, 0);

    public static void cleanup() {
        roadMap.clear();
        existingRoadTiles.clear();
    }

//    road的起点建筑和终点建筑
    private final Building st;
    private final Building ed;

    //    road的长度
    private int roadLength = 0;

    private final ArrayList<RoadTile> roadTiles = new ArrayList<>();

    private Road(Building st, Building ed, int length) {
        this.st = st;
        this.ed = ed;
        this.roadLength = length;
    }

    public int getRoadLength() {
        return roadLength;
    }

    public List<RoadTile> getRoadTiles() {
        return roadTiles;
    }

//    不检查指定位置是否合法，由caller保证安全调用，故设为private
    // coordinates中都是要建的，entrance和exit是不用建的，仅用作判断方向
    private void placeRoad(ArrayList<Coordinate> coordinates, Coordinate entrance, Coordinate exit) {
        if(entrance == null || exit == null) {
            throw new IllegalArgumentException("must have an exit and an entrance");
        }
        if(coordinates == null || coordinates.isEmpty()) {
            throw new IllegalArgumentException("no road tiles to place");
        }
        Coordinate lastPos, nextPos;
        for(int i = 0; i < coordinates.size(); i++) {
            if(i == 0) {
                lastPos = entrance;
            } else {
                lastPos = coordinates.get(i - 1);
            }
            if(i == coordinates.size() - 1) {
                nextPos = exit;
            } else {
                nextPos = coordinates.get(i + 1);
            }
            Coordinate curPos = coordinates.get(i);
            if(!Coordinate.isNeighbor(lastPos, curPos) || !Coordinate.isNeighbor(curPos, nextPos)) {
                throw new IllegalArgumentException("road should be continuous");
            }

            int weight = Board.getBoard().getBoardPosWeight(curPos);
            if(weight != 2 && weight != 1) {
                throw new IllegalArgumentException("cannot place road here");
            }

//            roadTiles中的tile都只有一个方向，总会创建新tile，用于记录操作过程，以便删除。
            RoadTile tile = new RoadTile(curPos);
            tile.setDirection(lastPos, curPos, nextPos);
            if(existingRoadTiles.containsKey(curPos)) {
                existingRoadTiles.get(curPos).add(tile);
            } else {
                ArrayList<RoadTile> tiles = new ArrayList<>();
                tiles.add(tile);
                existingRoadTiles.put(curPos, tiles);
            }
            Board.getBoard().setBoardPosWeight(curPos, 1);
            roadTiles.add(tile);
        }
    }

    public static Road generateRoad(Building st, Building ed) {
        if(roadMap.containsKey(new Pair<>(st, ed))) {
            return roadMap.get(new Pair<>(st, ed));
        }
         if(st == ed || st.isNeighbourBuilding(ed)) {
              return PORTAL;
         }

        ArrayList<Coordinate> path = new ArrayList<>();
        int distance = shortestPath(st.getCoordinate(), ed.getCoordinate(), path);
        if(distance != -1) {
            Road newRoad = new Road(st, ed, path.size());
            newRoad.placeRoad(path, st.getCoordinate(), ed.getCoordinate());
            roadMap.put(new Pair<>(st, ed), newRoad);
            return newRoad;
        }

        return null;
    }

    private void removeTiles() {
        for(RoadTile tile: roadTiles) {
            Coordinate c = tile.getCoordinate();
            ArrayList<RoadTile> tilesAtC = existingRoadTiles.get(c);
            for(RoadTile tileAtC: tilesAtC) {
                if(tile.equals(tileAtC)) {
                    tilesAtC.remove(tileAtC);
                    // 只删除一个，而非所有
                    break;
                }
            }
            if(tilesAtC.isEmpty()) {
                Board.getBoard().setBoardPosWeight(c, 2);
                existingRoadTiles.remove(c);
            }
        }
    }

    public static void removeRoad(Building st, Building ed) {
        Road toDelete = roadMap.get(new Pair<>(st, ed));
        roadMap.remove(new Pair<>(st, ed));
        toDelete.removeTiles();
    }

//    判断一个坐标是否邻近建筑
    private static boolean isPort(Coordinate c) {
        Coordinate left = new Coordinate(c.x - 1, c.y);
        Coordinate right = new Coordinate(c.x + 1, c.y);
        Coordinate up = new Coordinate(c.x, c.y - 1);
        Coordinate down = new Coordinate(c.x, c.y + 1);
        Board b = Board.getBoard();
        if(!b.isOutOfBound(left) && Board.getBoard().getBoardPosWeight(left) == Integer.MAX_VALUE) {
            return true;
        }
        if(!b.isOutOfBound(right) && Board.getBoard().getBoardPosWeight(right) == Integer.MAX_VALUE) {
            return true;
        }
        if(!b.isOutOfBound(up) && Board.getBoard().getBoardPosWeight(up) == Integer.MAX_VALUE) {
            return true;
        }
        if(!b.isOutOfBound(down) && Board.getBoard().getBoardPosWeight(down) == Integer.MAX_VALUE) {
            return true;
        }
        return false;
    }

    // 假定输入的dir是合法的，即{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}中的一个
    private static boolean hasDirectionConflict(int[] dir, Coordinate coordinate) {
        // 如果不是路，则不存在道路方向冲突
        if(!existingRoadTiles.containsKey(coordinate)) {
            return false;
        }
        // 如果路邻近建筑，则为port，方向限制较少
        boolean isPort = isPort(coordinate);

        ArrayList<RoadTile> tiles = existingRoadTiles.get(coordinate);
        for(RoadTile tile: tiles) {
            // 左
            if(dir[0] == -1 && dir[1] == 0) {
                if(tile.getToDirection().hasDirection(Direction.RIGHT)
                        && tile.getFromDirection().hasDirection(Direction.LEFT)) {
                    return true;
                } else if(
                        tile.getToDirection().hasDirection(Direction.RIGHT)
                                || tile.getFromDirection().hasDirection(Direction.LEFT)
                ) {
                    return !isPort;
                }
            }
            // 右
            else if(dir[0] == 1 && dir[1] == 0) {
                if(tile.getToDirection().hasDirection(Direction.LEFT)
                        && tile.getFromDirection().hasDirection(Direction.RIGHT)) {
                    return true;
                } else if(
                        tile.getToDirection().hasDirection(Direction.LEFT)
                                || tile.getFromDirection().hasDirection(Direction.RIGHT)
                ) {
                    return !isPort;
                }
            }
            // 上
            else if(dir[0] == 0 && dir[1] == -1) {
                if(tile.getToDirection().hasDirection(Direction.DOWN)
                        && tile.getFromDirection().hasDirection(Direction.UP)) {
                    return true;
                } else if(
                        tile.getToDirection().hasDirection(Direction.DOWN)
                                || tile.getFromDirection().hasDirection(Direction.UP)
                ) {
                    return !isPort;
                }
            }
            // 下
            else{
                if(tile.getToDirection().hasDirection(Direction.UP)
                        && tile.getFromDirection().hasDirection(Direction.DOWN)) {
                    return true;
                } else if(
                        tile.getToDirection().hasDirection(Direction.UP)
                                || tile.getFromDirection().hasDirection(Direction.DOWN)
                ) {
                    return !isPort;
                }
            }
        }

        return false;
    }

    // 返回值是最短距离，最短路线通过修改入参传回
    public static int shortestPath(Coordinate start, Coordinate end, ArrayList<Coordinate> path) {
        // 节点的优先队列，按距离排序
        PriorityQueue<Map.Entry<Coordinate, Integer>> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        // 距离映射：记录从起点到每个点的最短距离
        Map<Coordinate, Integer> distance = new HashMap<>();
        distance.put(start, 0);
        priorityQueue.offer(Map.entry(start, 0));

        // 前驱映射：用于重建路径
        Map<Coordinate, Coordinate> prev = new HashMap<>();

        // 方向：上下左右
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        Coordinate pathEnd = null;
        while (!priorityQueue.isEmpty()) {
            Map.Entry<Coordinate, Integer> entry = priorityQueue.poll();
            Coordinate current = entry.getKey();
            int currentDist = entry.getValue();

            // 如果当前节点已经与终点建筑相邻，跳出循环
            if (Coordinate.isNeighbor(current, end)) {
                pathEnd = current;
                break;
            }

            // 如果当前距离大于记录的最短距离，跳过
//            从clover来看似乎不会执行到
//            if (currentDist > distance.get(current)) {
//                continue;
//            }

            // 遍历四个方向
            for (int[] dir : directions) {
                Coordinate neighbor = new Coordinate(current.x + dir[0], current.y + dir[1]);

//                跳过界外或者普通建筑
                int weight = Board.getBoard().getBoardPosWeight(neighbor);
                if (weight == Integer.MAX_VALUE) {
                    continue;
                }
                // 反向的路也视为不可达
                if(hasDirectionConflict(dir, neighbor)) {
                    continue;
                }

                // 计算新距离
                int newDist = currentDist + weight;

                // 如果新距离更小，更新距离和前驱
                if (!distance.containsKey(neighbor) || newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    priorityQueue.offer(Map.entry(neighbor, newDist));
                }
            }
        }

        // 不可达
        if (!prev.containsKey(pathEnd)) {
            return -1;
        }

        // 此处掐头去尾，留下的都是要修路的位置
        Coordinate current = pathEnd;
        while (current != start) {
            path.add(current);
            current = prev.get(current);
        }
        Collections.reverse(path);
        return distance.get(pathEnd);
    }

    public static int getDistance(Building a, Building b) {
        if(a == b || a.isNeighbourBuilding(b)) {
            return 0;
        }
        if(roadMap.containsKey(new Pair<>(a, b))) {
            return roadMap.get(new Pair<>(a, b)).getRoadLength();
        } else {
            throw new IllegalArgumentException("road not connected!");
        }
    }
}
