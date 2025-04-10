package productsimulation.model.road;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.model.Building;

import java.lang.reflect.Array;
import java.util.*;

// road容积无限，但不模拟运输过程。由request完成时的hook函数在latency回合后直接传送到目标仓库。
// GUI上点击road，只显示方向信息。road上也不准备做“有个货物在动”的动画。
public class Road {
    // 此处Road作为第一个caller，先new用着，之后改成getBoard
    public static Board board = new Board();
    // 地图中心点，之后肯定要修改，我先假定是个100x100的地图
    private Coordinate center = new Coordinate(50, 50);

    // 需要一个全局Road,以便全盘考虑路线规划等问题,暂时先放road类里，
    // key为终点，value为起点:road
    // todo：如果建筑直接相邻，也要注册一条特殊的road，保证可达性判断无误
    public static HashMap<Building, HashMap<Building, Road>> existingRoads = new HashMap<>();
//    也需要一个全局roadTiles，以方便查询某个位置是否有路，以便复用
    public static HashMap<Coordinate, RoadTile> existingRoadTiles = new HashMap<>();

//    ===================== 以下是Road自己的内容 ===================
//    road的起点建筑和终点建筑
    private Building st;
    private Building ed;
    // 一条road里的tiles集合，如果能到达roadTiles中第m个元素，就能到达roadTiles中第n个元素，n>m
    public ArrayList<RoadTile> roadTiles = new ArrayList<>();

    public Road() {}
    public Road(Building st, Building ed) {
        this.st = st;
        this.ed = ed;
        generateRoad(st, ed);
    }

    // 此处不检查夹角，依靠shortestPath内的逻辑检查夹角
    private void setDirection(RoadTile lastTile, Coordinate lastPos, Coordinate c) {
        if(lastPos.x + 1 == c.x) {
            lastTile.direction.addDirection(Direction.RIGHT);
        } else if(lastPos.y + 1 == c.y) {
            lastTile.direction.addDirection(Direction.UP);
        } else if(lastPos.x - 1 == c.x) {
            lastTile.direction.addDirection(Direction.LEFT);
        } else if(lastPos.y - 1 == c.y) {
            lastTile.direction.addDirection(Direction.DOWN);
        }
    }

//    不检查指定位置是否合法，由caller保证安全调用，故设为private
    // coordinates中都是要建的，exit是不用建的，仅用作判断最后一个roadTile的方向
    // isEndAtBuilding为true时，代表road的终点与普通建筑相邻
    private void placeRoad(ArrayList<Coordinate> coordinates, Coordinate exit, boolean isEndAtBuilding) {
        if(exit == null) {
            return;
        }
        if(coordinates == null || coordinates.isEmpty()) {
            throw new IllegalArgumentException("no road tiles to place");
        }
        Coordinate lastPos = null;
        RoadTile lastTile = null;
        for(Coordinate c: coordinates) {
//            未必每次迭代都会建新路
            RoadTile tile;
            int boardStatus = board.getBoardPosStatus(c);
            if(boardStatus != 0 && boardStatus != 2) {
                throw new IllegalArgumentException("cannot place road here");
            } else if(boardStatus == 0) {
                board.setBoardPosStatus(c, 2);
//            原来没路，建新路。初始化时均未定义方向，下一轮迭代时才会赋实际值
                tile = new RoadTile(c, Direction.UNDEFINED());
                existingRoadTiles.put(c, tile);
            } else {
//            原来有路，复用之前的tile
                tile = existingRoadTiles.get(c);
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
        setDirection(lastTile, lastPos, exit);

        roadTiles.get(0).setIsEnd(true);
        if(isEndAtBuilding) {
            roadTiles.get(roadTiles.size() - 1).setIsEnd(true);
        }
    }

    // 传入: 一个building所占据的坐标
    // 为了简化问题，挑选buliding的端口位置时，从最靠近地图中心的建筑格的[上，右，下，左]中第一个可建造的邻位开端口
    // 如果这个位置被其它建筑给堵住了，那玩家可以先画一条相邻建筑到目的地的路线。
    public Coordinate chooseBuildingPort(Iterable<Coordinate> coordinates) {
        if (coordinates == null || !coordinates.iterator().hasNext()) {
            return null; // 如果没有点，返回null
        }

        // 找到离中心最近的点
        Coordinate closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Coordinate coord : coordinates) {
            double distance = Coordinate.ManhattanDis(coord, center);
            if (distance < minDistance) {
                minDistance = distance;
                closest = coord;
            }
        }

        // 按照上、右、下、左的顺序遍历邻居，如果可建造，则返回
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // 上、右、下、左
        for (int[] dir : directions) {
            Coordinate neighbor = new Coordinate(closest.x + dir[0], closest.y + dir[1]);
            int status = board.getBoardPosStatus(neighbor);
            if(status == 0 || status == 2) {
                return neighbor;
            }
        }

        return null;
    }

    private Coordinate generateRoadByReuse(Building st, Building ed, ArrayList<Coordinate> pathToGenReuse) {
        HashMap<Building, Road> roads = existingRoads.get(ed);
        // 如果从源建筑到目标建筑的道路已存在，返回之前的finalEnd，同时pathToGenReuse为空
        if(roads.containsKey(st)) {
            ArrayList<RoadTile> roadTiles = roads.get(st).roadTiles;
            return roadTiles.get(roadTiles.size() - 1).getCoordinate();
        }
        // 如果有到目标建筑的道路，但不是从源建筑出发，则需要考虑复用已有的道路
        // 计算一个建筑到某条道路的最短距离，目前做法是遍历所有同终点road的所有tiles
        Coordinate startPos = chooseBuildingPort(st.getCoordinates());
        int globalMin = Integer.MAX_VALUE;
        Coordinate finalEnd = null;
        for(Road r: roads.values()) {
            ArrayList<RoadTile> tiles = r.roadTiles;
            for(RoadTile tile: tiles) {
                Coordinate endPosCandidate = tile.getCoordinate();
                ArrayList<Coordinate> tmp = new ArrayList<>();
                int res = shortestPath(startPos, endPosCandidate, tmp);
                if(res < globalMin) {
                    globalMin = res;
                    finalEnd = endPosCandidate;
                }
            }
        }
        int distance = shortestPath(startPos, finalEnd, pathToGenReuse);
        return distance == -1 ? null : finalEnd;
    }


    private Coordinate generateWholeRoad(Building st, Building ed, ArrayList<Coordinate> pathToGenNew) {
        ArrayList<Coordinate> tmp = new ArrayList<>();
        Coordinate startPos = chooseBuildingPort(st.getCoordinates());
        Coordinate endPos = chooseBuildingPort(ed.getCoordinates());
        int distance = shortestPath(startPos, endPos, tmp);

        // 加个early stop，否则有可能在终点建筑外围绕圈
        for(Coordinate c: tmp) {
            pathToGenNew.add(c);
            if(ed.isNeighborCoordinate(c)) {
                break;
            }
        }
        return distance == -1 ? null : endPos;
    }

    public void generateRoad(Building st, Building ed) {
        if(st == ed) {
            return;
        }

        // todo 如果两个建筑相邻，注册一条特殊的road然后就可以return了
//         if(st.isNeighborBuilding(ed)) {
//              existingRoads.put(xxx, xxx);
//              return;
//         }

        ArrayList<Coordinate> pathToGenReuse = new ArrayList<>();
        Coordinate finalEnd = null;
        if(existingRoads.containsKey(ed)) {
             finalEnd = generateRoadByReuse(st, ed, pathToGenReuse);
//             已经存在 同起点 同终点的road，无需生成
             if(finalEnd != null && pathToGenReuse.isEmpty()) {
                 return;
             }
        }

        ArrayList<Coordinate> pathToGenNew = new ArrayList<>();
        Coordinate endPos = generateWholeRoad(st, ed, pathToGenNew);

        //        代价的对比需要两种方案都执行过后，才能获知
        // 如果没有任何到这个终点的路，或者有但复用代价比新建代价还高，则需要生成一条完整新路
        if(finalEnd == null || pathToGenNew.size() <= pathToGenReuse.size()) {
            //  如果采用新建方案，第一个和最后一个tile需要设为isEnd
            placeRoad(pathToGenNew, endPos, true);
        } else {
            // 如果最终采用复用方案，则仅第一个tile需要设为isEnd
            placeRoad(pathToGenReuse, finalEnd, false);
        }

        // 当前road建造(place)完毕，添加到全局HashMap<Building, HashMap<Building, Road>> roads
        // 如果并未发生实际建造(place)，则无需新添
        if(!roadTiles.isEmpty()) {
            HashMap<Building, Road> values = existingRoads.getOrDefault(ed, new HashMap<>());
            values.put(st, this);
            existingRoads.put(ed, values);
        }
    }

    // 先用dijkstra，其它算法后续再议
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

        while (!priorityQueue.isEmpty()) {
            Map.Entry<Coordinate, Integer> entry = priorityQueue.poll();
            Coordinate current = entry.getKey();
            int currentDist = entry.getValue();

            // 如果当前节点是终点，跳出循环
            if (current.equals(end)) {
                break;
            }

            // 如果当前距离大于记录的最短距离，跳过
//            考虑到所有边权重都为1，实际上不存在“通过中转缩短距离”的可能
//            if (currentDist > distance.get(current)) {
//                continue;
//            }

            // 遍历四个方向
            for (int[] dir : directions) {
                Coordinate neighbor = new Coordinate(current.x + dir[0], current.y + dir[1]);

                // 检查是否可以通行
                int boardPositionStatus = board.getBoardPosStatus(neighbor);
//                不可以在界外或者普通建筑上修路，可以在路上修路
                if (boardPositionStatus == -1 || boardPositionStatus == 1) {
                    continue;
                }
//                todo 检查路的夹角是否为180度，如果是，也不能修路

                // 计算新距离
                int newDist = currentDist + 1;

                // 如果新距离更小，更新距离和前驱
                if (!distance.containsKey(neighbor) || newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    priorityQueue.offer(Map.entry(neighbor, newDist));
                }
            }
        }

        // 不可达
        if (!prev.containsKey(end)) {
            return -1;
        }

        Coordinate current = end;
        while (current != null) {
            path.add(current);
            current = prev.get(current);
        }
        Collections.reverse(path);
        return distance.get(end);
    }
}
