package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import productsimulation.model.StorageType;

import java.util.List;
import java.util.stream.Collectors;

public class AddBuildingWindow {

    public static void show(final State state, final Runnable onSuccess) {
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Building");

        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final Label nameLabel = new Label("Building Name:");
        final TextField nameField = new TextField();
        nameField.setPromptText("Enter unique building name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        final Label typeLabel = new Label("Building Type:");
        final ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("Select building type");
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        final Label xLabel = new Label("X Coordinate (optional):");
        final TextField xField = new TextField();
        xField.setPromptText("Enter X coordinate");
        grid.add(xLabel, 0, 2);
        grid.add(xField, 1, 2);

        final Label yLabel = new Label("Y Coordinate (optional):");
        final TextField yField = new TextField();
        yField.setPromptText("Enter Y coordinate");
        grid.add(yLabel, 0, 3);
        grid.add(yField, 1, 3);

        final Label sourcesLabel = new Label("Select sources:");
        final List<String> existingBuildings = state.getBuildings()
                .stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        final ListView<String> sourcesListView = new ListView<>();
        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourcesListView.setItems(FXCollections.observableArrayList(existingBuildings));
        grid.add(sourcesLabel, 0, 4);
        grid.add(sourcesListView, 1, 4);

        final List<BuildingType> availableTypes = BuildingType.getBuildingTypeGlobalList();
        final List<String> typeNames = availableTypes.stream()
                .map(BuildingType::getName)
                .collect(Collectors.toList());
        typeCombo.setItems(FXCollections.observableArrayList(typeNames));

        final Button submitBtn = new Button("Add Building");
        final Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            final String buildingName = nameField.getText().trim();
            final String buildingTypeName = typeCombo.getValue();

            // Check for empty name or type
            if (buildingName.isEmpty() || buildingTypeName == null || buildingTypeName.isEmpty()) {
                showError("Building name and type are required.");
                return;
            }

            // Check if a building with the same name already exists
            boolean duplicateName = state.getBuildings().stream()
                    .anyMatch(b -> b.getName().equalsIgnoreCase(buildingName));
            if (duplicateName) {
                showError("A building with the name \"" + buildingName + "\" already exists.");
                return;
            }

            final Coordinate coord = parseCoordinate(xField, yField);

            final ObservableList<String> selectedSourceNames = sourcesListView.getSelectionModel().getSelectedItems();
            final List<Building> chosenSources = state.getBuildings().stream()
                    .filter(b -> selectedSourceNames.contains(b.getName()))
                    .collect(Collectors.toList());

            final BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(buildingTypeName))
                    .findFirst()
                    .orElse(null);

            if (selectedType == null) {
                showError("Selected building type not found.");
                return;
            }

            final Building newBuilding;
            try {
                newBuilding = createBuilding(buildingName, chosenSources, state, coord, buildingTypeName, selectedType);
            } catch (Exception ex) {
                showError("Error creating building: " + ex.getMessage());
                return;
            }

            showInfo("Successfully added building: " + newBuilding.getName() +
                    " at (" + newBuilding.getX() + ", " + newBuilding.getY() + ")" +
                    "\nSources: " + chosenSources.stream()
                    .map(Building::getName)
                    .collect(Collectors.joining(", ")));

            if (onSuccess != null) {
                onSuccess.run();
            }
            stage.close();
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(submitBtn, 0, 5);
        grid.add(cancelBtn, 1, 5);

        final Scene scene = new Scene(grid, 560, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private static Coordinate parseCoordinate(TextField xField, TextField yField) {
        try {
            final String xText = xField.getText().trim();
            final String yText = yField.getText().trim();
            if (!xText.isEmpty() && !yText.isEmpty()) {
                int x = Integer.parseInt(xText);
                int y = Integer.parseInt(yText);
                return new Coordinate(x, y);
            }
        } catch (NumberFormatException ex) {
            // Fall through to return a default coordinate if parse fails
        }
        return Building.getValidCoordinate();
    }

    private static Building createBuilding(String buildingName,
                                           List<Building> chosenSources,
                                           State state,
                                           Coordinate coord,
                                           String buildingTypeName,
                                           BuildingType selectedType) throws Exception {
        if (buildingTypeName.toLowerCase().contains("storage")) {
            return Storage.addStorage(
                    buildingName,
                    chosenSources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord,
                    (StorageType) selectedType
            );
        } else if (buildingTypeName.toLowerCase().contains("mine")) {
            return Mine.addMine(
                    buildingName,
                    chosenSources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord,
                    selectedType
            );
        } else {
            return Factory.addFactory(
                    buildingName,
                    chosenSources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord,
                    selectedType
            );
        }
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
