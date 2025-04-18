package productsimulation.model.road;

import javafx.util.Pair;
import productsimulation.Log;
import productsimulation.model.Building;

import java.util.List;

public class RoadHandler {
    private static Building[] buildingNameToObj(String srcName, String dstName) {
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
        return new Building[]{bsrc, bdst};
    }
    public static String connectHandler(String srcName, String dstName) {
        Building[] objs = buildingNameToObj(srcName, dstName);
        Building bsrc = objs[0];
        Building bdst = objs[1];
        if(bsrc == null || bdst == null) {
            return "Building does not exists. Check the building name.";
        }
        try{
            Road.generateRoad(bsrc, bdst);
            Log.level0Log(srcName + " -> " + dstName + " connected");
            return null;
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    public static String removeHandler(String srcName, String dstName) {
        Building[] objs = buildingNameToObj(srcName, dstName);
        Building bsrc = objs[0];
        Building bdst = objs[1];
        if(bsrc == null || bdst == null) {
            return "Building does not exists. Check the building name.";
        }
        if(!Road.roadMap.containsKey(new Pair<>(bsrc, bdst))) {
            // 本就不存在，可认为已成功删除
            return null;
        }
        try{
            Road.removeRoad(bsrc, bdst);
            return null;
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}
