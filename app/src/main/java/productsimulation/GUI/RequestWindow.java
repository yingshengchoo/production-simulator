package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.RequestCommand;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import productsimulation.model.BuildingType;

import java.util.List;
import java.util.stream.Collectors;

public class RequestWindow {

    public static void show(State state, Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Request Item");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // ComboBox for selecting a building from which to request.
        Label bldLabel = new Label("From building:");
        List<String> buildingNames = state.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        ComboBox<String> bldBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        bldBox.setPromptText("Select building");

        // ComboBox for selecting an item.
        Label itemLabel = new Label("Item:");
        ComboBox<String> itemBox = new ComboBox<>();
        itemBox.setPromptText("Select item");

        // When the building selection changes, update the item drop-down.
        bldBox.setOnAction(e -> {
            String buildingName = bldBox.getValue();
            ObservableList<String> items = FXCollections.observableArrayList();
            if (buildingName != null) {
                Building target = state.getBuildings().stream()
                        .filter(b -> b.getName().equals(buildingName))
                        .findFirst().orElse(null);
                if (target != null) {
                    if (target instanceof Mine) {
                        Mine mine = (Mine) target;
                        BuildingType bt = mine.getBuildingType();
                        if (bt != null && bt.getAllRecipes() != null) {
                            items.addAll(bt.getAllRecipes().keySet());
                        }
                    } else if (target instanceof Storage) {
                        // For storage buildings, use the stored item.
                        Storage storage = (Storage) target;
                        items.add(storage.getRecipeOutput());
                    } else if (target instanceof Factory) {
                        Factory factory = (Factory) target;
                        BuildingType bt = factory.getBuildingType();
                        if (bt != null && bt.getAllRecipes() != null) {
                            items.addAll(bt.getAllRecipes().keySet());
                        }
                    }
                }
            }
            itemBox.setItems(items);
            // Clear any previous selection.
            itemBox.getSelectionModel().clearSelection();
        });

        // Submit and Cancel buttons.
        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            String building = bldBox.getValue();
            String item = itemBox.getValue();
            if (building == null || item == null || building.trim().isEmpty() || item.trim().isEmpty()) {
                showError("Please select both a building and an item.");
                return;
            }
            // Check if the selected building is capable of producing or storing the requested item.
            Building target = state.getBuildings().stream()
                    .filter(b -> b.getName().equals(building))
                    .findFirst().orElse(null);
            if (target != null) {
                boolean canProduce = false;
                if (target instanceof Mine) {
                    BuildingType bt = ((Mine) target).getBuildingType();
                    if (bt != null && bt.getAllRecipes() != null) {
                        canProduce = bt.getAllRecipes().containsKey(item);
                    }
                    canProduce = bt.getAllRecipes().containsKey(item);
                } else if (target instanceof Storage) {
                    canProduce = item.equals(((Storage) target).getRecipeOutput());
                } else if (target instanceof Factory) {
                    BuildingType bt = ((Factory) target).getBuildingType();
                    if (bt != null && bt.getAllRecipes() != null) {
                        canProduce = bt.getAllRecipes().containsKey(item);
                    }
                }
                if (!canProduce) {
                    showError("The building '" + building + "' is not capable of producing or storing '" + item + "'.");
                    return;
                }
            } else {
                showError("Selected building not found.");
                return;
            }
            // Create and execute the request command.
            RequestCommand cmd = new RequestCommand(item, building);
            String error = cmd.execute();
            if (error == null || error.trim().isEmpty()) {
                showInfo("Requested " + item + " from " + building + ".");
                if (onSuccessRefresh != null) {
                    onSuccessRefresh.run();
                }
                stage.close();
            } else {
                showError(error);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(bldLabel, 0, 0);
        grid.add(bldBox, 1, 0);
        grid.add(itemLabel, 0, 1);
        grid.add(itemBox, 1, 1);
        grid.add(submitBtn, 0, 2);
        grid.add(cancelBtn, 1, 2);

        stage.setScene(new Scene(grid, 350, 180));
        stage.showAndWait();
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Success");
        a.setContentText(msg);
        a.showAndWait();
    }
}
