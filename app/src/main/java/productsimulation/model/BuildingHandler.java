package productsimulation.model;

import javafx.util.Pair;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.model.road.Road;
import productsimulation.model.road.RoadHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuildingHandler {

    public static Coordinate getValidCoordinate() {
        List<Coordinate> existingCoordinates = new ArrayList<>();
        for (Building b : Building.buildingGlobalList) {
            existingCoordinates.add(b.getCoordinate());
        }

        // first coordinate on 0,0
        if (existingCoordinates.isEmpty()) {
            return new Coordinate(0, 0);
        }

        // for every exist building, try coordinates in their range.
        for (Coordinate c : existingCoordinates) {
            for (int dx = 5; dx < 10; dx++) {
                for (int dy = 5; dy < 10; dy++) {
                    // 正右上
                    int candidateX = c.x + dx;
                    int candidateY = c.y + dy;
                    if (isValid(candidateX, candidateY, existingCoordinates)) {
                        return new Coordinate(candidateX, candidateY);
                    }
                    // 左右上
                    candidateX = c.x - dx;
                    candidateY = c.y + dy;
                    if (isValid(candidateX, candidateY, existingCoordinates)) {
                        return new Coordinate(candidateX, candidateY);
                    }
                    // 右下
                    candidateX = c.x + dx;
                    candidateY = c.y - dy;
                    if (isValid(candidateX, candidateY, existingCoordinates)) {
                        return new Coordinate(candidateX, candidateY);
                    }
                    // 左下
                    candidateX = c.x - dx;
                    candidateY = c.y - dy;
                    if (isValid(candidateX, candidateY, existingCoordinates)) {
                        return new Coordinate(candidateX, candidateY);
                    }
                }
            }
        }

        throw new RuntimeException("no valid coordinate!");
    }

    static boolean isValid(int candidateX, int candidateY, List<Coordinate> existingPoints) {
        if (existingPoints.isEmpty()) return true;
        if(candidateX < 0 || candidateY <0){
            return false;
        }

        Board board = Board.getBoard();
        int weight = board.getBoardPosWeight(new Coordinate(candidateX, candidateY));
        if (weight == 1 || weight == Integer.MAX_VALUE) {
            return false;
        }

        boolean withinX = false;
        boolean withinY = false;
        for (Coordinate c : existingPoints) {
            if (Math.abs(candidateX - c.x) < 5 && Math.abs(candidateY - c.y) < 5) {
                return false;
            }
            if (Math.abs(candidateX - c.x) <= 10) {
                withinX = true;
            }
            if (Math.abs(candidateY - c.y) <= 10) {
                withinY = true;
            }
        }
        return withinX && withinY;
    }

    public static String removeBuilding(Building building) {
        String error = null;
        // 删除为起点或为终点的connection
        ArrayList<Pair<String, String>> toRemove = new ArrayList<>();
        for(Pair<Building, Building> p: Road.roadMap.keySet()) {
            if(p.getKey().equals(building)) {
                toRemove.add(new Pair<>(building.getName(), p.getValue().getName()));
            }
            if(p.getValue().equals(building)) {
                toRemove.add(new Pair<>(p.getKey().getName(), building.getName()));
            }
        }
        for(Pair<String, String> p: toRemove) {
            error = RoadHandler.removeHandler(p.getKey(), p.getValue());
        }

        // 从buildingGlobalList中删除
        Building.buildingGlobalList.remove(building);
        // board重置为空地
        Board.getBoard().setBoardPosWeight(building.getCoordinate(), 2);
        return error;
    }

    public static String removeHandler(String buildingName) {
        Building building = null;
        for(Building b: Building.buildingGlobalList) {
            if (b.getName().equals(buildingName)) {
                building = b;
            }
        }
        if(building == null) {
            return "Building does not exists. Check the building name.";
        }
        // 1) Can't remove if there are pending requests
        if (!building.getRequestQueue().isEmpty()) {
            return "Cannot remove '" + buildingName + "': pending requests exist.";
        }
        // 2) If storage, ensure it’s empty
        if (building instanceof Storage) {
            Storage s = (Storage) building;
            if (s.getStockCount() > 0) {
                return "Cannot remove storage '" + buildingName + "': storage not empty.";
            }
        }
        try{
            Log.level0Log(buildingName + " removed");
            return removeBuilding(building);
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}
