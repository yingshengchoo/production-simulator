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
import productsimulation.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simplified window for adding a building at a known (x,y) coordinate
 * that the user clicked on. No user input for coordinate is required.
 */
public class AddBuildingAtCellWindow {

    /**
     * Displays a modal window for creating a new building at the given (grid) coordinate.
     *
     * @param state       The current simulation state.
     * @param fixedCoord  The coordinate for the building (already determined from BoardDisplay).
     * @param onSuccess   Callback to run if building creation is successful.
     */
    public static void show(State state, Coordinate fixedCoord, Runnable onSuccess) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Building at (" + fixedCoord.x + ", " + fixedCoord.y + ")");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // 1) Building Name
        Label nameLabel = new Label("Building Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter unique building name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // 2) Building Type
        Label typeLabel = new Label("Building Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("Select building type");
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        // 3) Sources (let user select existing buildings as sources)
        Label sourcesLabel = new Label("Select sources:");
        List<String> existingBuildings = state.getBuildings().stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        ListView<String> sourcesListView = new ListView<>();
        sourcesListView.setItems(FXCollections.observableArrayList(existingBuildings));
        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        grid.add(sourcesLabel, 0, 2);
        grid.add(sourcesListView, 1, 2);

        // 4) Populate typeCombo from the global BuildingType list
        List<BuildingType> allTypes = BuildingType.getBuildingTypeGlobalList();
        List<String> typeNames = allTypes.stream()
                .map(BuildingType::getName)
                .collect(Collectors.toList());
        typeCombo.setItems(FXCollections.observableArrayList(typeNames));

        // 5) Submit & Cancel
        Button submitBtn = new Button("Add Building");
        Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            String buildingName = nameField.getText().trim();
            String buildingTypeName = typeCombo.getValue();

            if (buildingName.isEmpty() || buildingTypeName == null || buildingTypeName.isEmpty()) {
                showError("Building name and type are required.");
                return;
            }
            // Disallow duplicates
            boolean duplicateName = state.getBuildings().stream()
                    .anyMatch(b -> b.getName().equalsIgnoreCase(buildingName));
            if (duplicateName) {
                showError("A building named \"" + buildingName + "\" already exists.");
                return;
            }

            // Build source list
            ObservableList<String> selectedSources = sourcesListView.getSelectionModel().getSelectedItems();
            List<Building> chosenSources = state.getBuildings().stream()
                    .filter(b -> selectedSources.contains(b.getName()))
                    .collect(Collectors.toList());

            // Find the BuildingType
            BuildingType selectedType = allTypes.stream()
                    .filter(bt -> bt.getName().equals(buildingTypeName))
                    .findFirst()
                    .orElse(null);
            if (selectedType == null) {
                showError("Selected building type not found.");
                return;
            }

            // Attempt to create the building
            try {
                Building newBuilding = createBuilding(buildingName, chosenSources, state, fixedCoord, buildingTypeName, selectedType);
                showInfo("Successfully added building: " + newBuilding.getName() +
                        " at (" + newBuilding.getX() + ", " + newBuilding.getY() + ")" +
                        "\nSources: " + chosenSources.stream()
                        .map(Building::getName)
                        .collect(Collectors.joining(", ")));
                if (onSuccess != null) {
                    onSuccess.run();
                }
                stage.close();
            } catch (Exception ex) {
                showError("Error creating building: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(submitBtn, 0, 3);
        grid.add(cancelBtn, 1, 3);

        Scene scene = new Scene(grid, 460, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // Helper to create the building
    private static Building createBuilding(String buildingName,
                                           List<Building> sources,
                                           State state,
                                           Coordinate coord,
                                           String buildingTypeName,
                                           BuildingType selectedType) throws Exception {

        if (buildingTypeName.toLowerCase().contains("storage")) {
            return Storage.addStorage(
                    buildingName,
                    sources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord,
                    (StorageType) selectedType
            );
        } else if (buildingTypeName.toLowerCase().contains("mine")) {
            return Mine.addMine(
                    buildingName,
                    sources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord,
                    selectedType
            );
        } else {
            return Factory.addFactory(
                    buildingName,
                    sources,
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
