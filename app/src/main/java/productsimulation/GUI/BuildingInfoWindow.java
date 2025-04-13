package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.LogicTime;
import productsimulation.model.*;
import productsimulation.request.Request;

import java.util.Map;
import java.util.stream.Collectors;

public class BuildingInfoWindow {

    public static void show(Building b) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Building Info");

        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(15));

        // Basic information
        String stepInfo = "Current Step: " + LogicTime.getInstance().getStep();
        String nameInfo = "Name: " + b.getName();
        String coordInfo = "Coordinate: (" + b.getX() + ", " + b.getY() + ")";
        String queueInfo = "Request Queue Size: " + b.getRequestQueue().size();

        layout.getChildren().addAll(
                new Label(stepInfo),
                new Label(nameInfo),
                new Label(coordInfo),
                new Label(queueInfo)
        );

        // Subtype-specific details
        if (b instanceof Mine) {
            layout.getChildren().addAll(mineLabels((Mine) b));
        }
        else if (b instanceof Storage) {
            layout.getChildren().addAll(storageLabels((Storage) b));
        }
        else if (b instanceof Factory) {
            layout.getChildren().addAll(factoryLabels((Factory) b));
        }
        else {
            layout.getChildren().add(new Label("Type: Unknown"));
        }

        Scene scene = new Scene(layout, 300, 250);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static VBox mineLabels(Mine mine) {
        VBox vb = new VBox(5);
        vb.getChildren().add(new Label("Type: Mine"));
        BuildingType bt = mine.getBuildingType();
        if (bt != null && !bt.getName().isEmpty()) {
            vb.getChildren().add(new Label("Producing: " + bt.getName()));
        }
        return vb;
    }

    private static VBox storageLabels(Storage storage) {
        VBox vb = new VBox(5);
        vb.getChildren().add(new Label("Type: Storage"));
        vb.getChildren().add(new Label("Stores: " + storage.getRecipeOutput()));
        vb.getChildren().add(new Label("Capacity: " + storage.getTotalCapacity()));
        int currentCount = storage.getStockCount();
        vb.getChildren().add(new Label("Current Stock: " + currentCount));
        return vb;
    }

    private static VBox factoryLabels(Factory factory) {
        VBox vb = new VBox(5);
        vb.getChildren().add(new Label("Type: Factory"));

        // Gather recipe info
        BuildingType bt = factory.getBuildingType();
        if (bt != null) {
            Map<String, Recipe> recipeMap = bt.getAllRecipes();
            int recipeCount = recipeMap.size();
            vb.getChildren().add(new Label("Number of recipes: " + recipeCount));
            if (!recipeMap.isEmpty()) {
                String recipeList = recipeMap.keySet().stream()
                        .collect(Collectors.joining(", "));
                vb.getChildren().add(new Label("Recipes: " + recipeList));
            }
        }

        Request currentReq = factory.getCurrentRequest();
        if (currentReq != null) {
            vb.getChildren().add(new Label("In progress: " + currentReq.getIngredient()
                    + " (" + factory.getCurrentRemainTime() + " steps remain)"));
        } else {
            vb.getChildren().add(new Label("Currently idle"));
        }
        return vb;
    }
}
