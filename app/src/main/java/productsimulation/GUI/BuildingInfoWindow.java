package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.LogicTime;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import productsimulation.model.BuildingType;
import productsimulation.model.road.RoadTile;
import productsimulation.request.Request;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * BuildingInfoWindow displays details for a building in a pop-up window.
 * It includes a "Connect" button that activates interactive connect mode.
 */
public class BuildingInfoWindow {

    /**
     * Displays a modal window with building information.
     *
     * @param b the building whose details are shown.
     */
    public static void show(Building b) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Building Info");

        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(15));

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

        if (b instanceof Mine) {
            layout.getChildren().addAll(mineLabels((Mine) b));
        } else if (b instanceof Storage) {
            layout.getChildren().addAll(storageLabels((Storage) b));
        } else if (b instanceof Factory) {
            layout.getChildren().addAll(factoryLabels((Factory) b));
        } else {
            layout.getChildren().add(new Label("Type: Unknown"));
        }

        Button connectBtn = new Button("Connect");
        connectBtn.setOnAction(e -> {
            popup.close();
            // Activate interactive connection mode, and provide a callback that updates the log text in the feedback panel.
            InteractiveConnectMode.activate(b.getName(), State.getInstance(), GUI.getBoardDisplay(), GUI.getRootPane(), () -> {
                GUI.getFeedbackPane().setText(Log.getLogText());
            });
        });
        layout.getChildren().add(connectBtn);

        Scene scene = new Scene(layout, 300, 280);
        popup.setScene(scene);
        popup.showAndWait();
    }

    public static void show(RoadTile tile) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Road Info");

        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(15));

        layout.getChildren().addAll(
                new Label("From: " + tile.getFromDirection().getDirections()),
                new Label("To: " + tile.getToDirection().getDirections())
        );

        Scene scene = new Scene(layout, 300, 280);
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

        BuildingType bt = factory.getBuildingType();
        if (bt != null) {
            Map<String, ?> recipeMap = bt.getAllRecipes();
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
            vb.getChildren().add(new Label("In progress: " + currentReq.getIngredient() +
                    " (" + factory.getCurrentRemainTime() + " steps remain)"));
        } else {
            vb.getChildren().add(new Label("Currently idle"));
        }
        return vb;
    }
}
