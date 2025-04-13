package productsimulation.model.road;

import javafx.util.Pair;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.model.Building;

import java.util.*;

// road容积无限，但不模拟运输过程。由request完成时的hook函数在latency回合后直接传送到目标仓库。
// GUI上点击road，只显示方向信息。road上也不准备做“有个货物在动”的动画。
// todo：如果建筑直接相邻，也要注册一条特殊的road，保证可达性判断无误
public class Road {
    // key: (source,destination), value: distance
    // 如果存在，返回最短路程；如果不存在，说明不可达
    public static HashMap<Pair<Building, Building>, Integer> distanceMap = new HashMap<>();
    // 虽然已经有board，但让board存带方向信息的roadtile并不合适
    public static HashMap<Coordinate, RoadTile> existingRoadTiles = new HashMap<>();

    public static void cleanup() {
        distanceMap.clear();
        existingRoadTiles.clear();
    }

//    road的起点建筑和终点建筑
    private Building st;
    private Building ed;

    public ArrayList<RoadTile> roadTiles = new ArrayList<>();

    public Road() {}
    public Road(Building st, Building ed) {
        this.st = st;
        this.ed = ed;
        generateRoad(st, ed);
    }

    public static String connectHandler(String srcName, String dstName) {
        List<Building> buildings = Building.buildingGlobalList;
        Building bsrc = null;
        Building bdst = null;
        for(Building b: buildings) {
            if(b.getName().equals(srcName)) {
                bsrc = b;
            }
            if(b.getName().equals(dstName)) {
                bdst = b;
            }
        }
        if(bsrc == null || bdst == null) {
            return "Building does not exists. Check the building name.";
        }
        Road road = new Road(bsrc, bdst);
        Log.level0Log(srcName + " -> " + dstName + " connected");
        return "";
    }

    // 此处不检查夹角，依靠shortestPath内的逻辑检查夹角
    private void setDirection(RoadTile lastTile, Coordinate lastPos, Coordinate c) {
        if(lastPos.x + 1 == c.x) {
            lastTile.getDirection().addDirection(Direction.RIGHT);
        } else if(lastPos.y + 1 == c.y) {
            lastTile.getDirection().addDirection(Direction.UP);
        } else if(lastPos.x - 1 == c.x) {
            lastTile.getDirection().addDirection(Direction.LEFT);
        } else {
            lastTile.getDirection().addDirection(Direction.DOWN);
        }
    }

//    不检查指定位置是否合法，由caller保证安全调用，故设为private
    // coordinates中都是要建的，exit是不用建的，仅用作判断最后一个roadTile的方向
    private void placeRoad(ArrayList<Coordinate> coordinates, Coordinate exit) {
        if(exit == null) {
            throw new IllegalArgumentException("must have an exit");
        }
        if(coordinates == null || coordinates.isEmpty()) {
            throw new IllegalArgumentException("no road tiles to place");
        }
        Coordinate lastPos = null;
        RoadTile lastTile = null;
        for(Coordinate c: coordinates) {
//            未必每次迭代都会建新路
            RoadTile tile;
            int weight = Board.getBoard().getBoardPosWeight(c);
            if(weight == 2) {
                Board.getBoard().setBoardPosWeight(c, 1);
//            原来没路，建新路。初始化时均未定义方向，下一轮迭代时才会赋实际值
                tile = new RoadTile(c, Direction.UNDEFINED());
                existingRoadTiles.put(c, tile);
            } else if (weight == 1) {
//            原来有路，复用之前的tile
                tile = existingRoadTiles.get(c);
            } else {
                throw new IllegalArgumentException("cannot place road here");
            }

//            同一个tile可以在多条road里，此处不管是否新建tile，都应当在当前road中添加这个tile
            roadTiles.add(tile);
            if(lastPos != null) {
                if(!Coordinate.isNeighbor(lastPos, c)) {
                    throw new IllegalArgumentException("invalid road");
                }
                setDirection(lastTile, lastPos, c);
            }
            lastPos = c;
            lastTile = tile;
        }
        if(!Coordinate.isNeighbor(lastPos, exit)) {
            throw new IllegalArgumentException("invalid road");
        }
        setDirection(lastTile, lastPos, exit);
    }

    public void generateRoad(Building st, Building ed) {
        if(st == ed || distanceMap.containsKey(new Pair<>(st, ed))) {
            return;
        }
         if(st.isNeighbourBuilding(ed)) {
              distanceMap.put(new Pair<>(st, ed), 0);
              return;
         }

        ArrayList<Coordinate> path = new ArrayList<>();
        int distance = shortestPath(st.getCoordinate(), ed.getCoordinate(), path);
        if(distance != -1) {
            placeRoad(path, ed.getCoordinate());
        }

        distanceMap.put(new Pair<>(st, ed), path.size());
    }

    // 假定输入的dir是合法的，即{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}中的一个
    private static boolean hasDirectionConflict(int[] dir, Coordinate coordinate) {
        // 如果不是路，则不存在道路方向冲突
        if(!existingRoadTiles.containsKey(coordinate)) {
            return false;
        }

        RoadTile tile = existingRoadTiles.get(coordinate);
        // 左
        if(dir[0] == -1 && dir[1] == 0) {
            if(tile.getDirection().hasDirection(Direction.RIGHT)) {
                return true;
            }
        }
        // 右
        else if(dir[0] == 1 && dir[1] == 0) {
            if(tile.getDirection().hasDirection(Direction.LEFT)) {
                return true;
            }
        }
        // 下
        else if(dir[0] == 0 && dir[1] == -1) {
            if(tile.getDirection().hasDirection(Direction.UP)) {
                return true;
            }
        }
        // 上
        else{
            if(tile.getDirection().hasDirection(Direction.DOWN)) {
                return true;
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
        if(distanceMap.containsKey(new Pair<>(a, b))) {
            return distanceMap.get(new Pair<>(a, b));
        } else {
            throw new IllegalArgumentException("road not connected!");
        }
    }
}
