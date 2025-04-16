package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.LogicTime;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.RequestCommand;
import productsimulation.GUI.InteractiveConnectMode;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import productsimulation.model.BuildingType;
import productsimulation.model.road.RoadTile;

import java.util.Map;
import java.util.stream.Collectors;

public class BuildingInfoWindow {

    /**
     * Displays a modal window with detailed building information and options to request an item or initiate a connection.
     * Layout improvements include use of separate sections with increased spacing and padding.
     *
     * @param b the building whose information is to be displayed.
     */
    public static void show(Building b) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Building Info");

        // Use a VBox as the main container for vertical layout.
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setPrefWidth(320);

        VBox infoSection = new VBox(8);
        infoSection.getChildren().addAll(
                new Label("Current Step: " + LogicTime.getInstance().getStep()),
                new Label("Name: " + b.getName()),
                new Label("Coordinate: (" + b.getX() + ", " + b.getY() + ")"),
                new Label("Request Queue Size: " + b.getRequestQueue().size())
        );

        // Type-specific details
        if (b instanceof Mine) {
            infoSection.getChildren().addAll(mineLabels((Mine) b));
        } else if (b instanceof Storage) {
            infoSection.getChildren().addAll(storageLabels((Storage) b));
        } else if (b instanceof Factory) {
            infoSection.getChildren().addAll(factoryLabels((Factory) b));
        } else {
            infoSection.getChildren().add(new Label("Type: Unknown"));
        }
        // Set a border for clarity.
        infoSection.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");

        // --- Section 2: Request Item ---
        VBox requestSection = new VBox(10);
        requestSection.setPadding(new Insets(10));
        requestSection.setStyle("-fx-border-color: lightblue; -fx-border-width: 1; -fx-padding: 10;");
        Label requestSectionLabel = new Label("Request an Item:");
        requestSectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ObservableList<String> itemOptions = FXCollections.observableArrayList();
        if (b instanceof Mine) {
            BuildingType bt = ((Mine) b).getBuildingType();
            if (bt != null) {
                Map<String, ?> recipes = bt.getAllRecipes();
                // For mines, add the produced item
                itemOptions.addAll(recipes.keySet());
            }
        } else if (b instanceof Storage) {
            itemOptions.add(((Storage) b).getRecipeOutput());
        } else if (b instanceof Factory) {
            BuildingType bt = ((Factory) b).getBuildingType();
            if (bt != null) {
                Map<String, ?> recipes = bt.getAllRecipes();
                itemOptions.addAll(recipes.keySet());
            }
        }
        ComboBox<String> requestCombo = new ComboBox<>(itemOptions);
        requestCombo.setPrefWidth(180);
        requestCombo.setPromptText("Select item");

        Button requestBtn = new Button("Submit");
        requestBtn.setStyle("-fx-font-size: 12px; -fx-padding: 4 12 4 12;");
        requestBtn.setOnAction(e -> {
            String selectedItem = requestCombo.getValue();
            if (selectedItem == null) {
                showError("Please select an item to request.");
                return;
            }
            RequestCommand cmd = new RequestCommand(selectedItem, b.getName());
            String error = cmd.execute();
            if (error == null || error.trim().isEmpty()) {
                showInfo("Requested " + selectedItem + " from " + b.getName() + ".");
            } else {
                showError(error);
            }
        });

        // Place the combo box and the request button in an HBox.
        HBox requestBox = new HBox(10, requestCombo, requestBtn);
        requestBox.setAlignment(Pos.CENTER_LEFT);

        requestSection.getChildren().addAll(requestSectionLabel, requestBox);

        Button connectBtn = new Button("Connect to another building");
        connectBtn.setMaxWidth(Double.MAX_VALUE);
        connectBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 12 6 12;");
        connectBtn.setOnAction(e -> {
            popup.close();
            InteractiveConnectMode.activate(b.getName(), State.getInstance(), GUI.getBoardDisplay(), GUI.getRootPane(), () -> {
                GUI.getFeedbackPane().setText(Log.getLogText());
            });
        });

        mainLayout.getChildren().addAll(infoSection, requestSection, connectBtn);

        Scene scene = new Scene(mainLayout);
        popup.setScene(scene);
        popup.showAndWait();
    }

    /**
     * Overloaded method to show road tile information.
     *
     * @param tile the road tile
     */
    public static void show(RoadTile tile) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Road Info");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        layout.getChildren().addAll(
                new Label("From: " + tile.getFromDirection().getDirections()),
                new Label("To: " + tile.getToDirection().getDirections())
        );

        Scene scene = new Scene(layout, 300, 150);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // Utility methods for type-specific labels
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
            vb.getChildren().add(new Label("Number of Recipes: " + recipeCount));
            if (!recipeMap.isEmpty()) {
                String recipeList = recipeMap.keySet().stream()
                        .collect(Collectors.joining(", "));
                vb.getChildren().add(new Label("Recipes: " + recipeList));
            }
        }
        if (factory.getCurrentRequest() != null) {
            vb.getChildren().add(new Label("In Progress: " + factory.getCurrentRequest().getIngredient() +
                    " (" + factory.getCurrentRemainTime() + " steps remain)"));
        } else {
            vb.getChildren().add(new Label("Currently Idle"));
        }
        return vb;
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.showAndWait();
    }

    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle("Success");
        a.setHeaderText(null);
        a.showAndWait();
    }
}
