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
import productsimulation.setup.TypeParser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

public class AddBuildingWindow {

    private static final String BUILDING_TYPES_JSON = "{\n" +
            "  \"types\": [\n" +
            "    {\n" +
            "      \"name\": \"Bolt Storage (100)\",\n" +
            "      \"type\": \"storage\",\n" +
            "      \"info\": {\n" +
            "        \"stores\": \"bolt\",\n" +
            "        \"capacity\": 100,\n" +
            "        \"priority\": 1.7\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Door Factory\",\n" +
            "      \"type\": \"factory\",\n" +
            "      \"info\": {\n" +
            "        \"recipes\": [\n" +
            "          \"door\",\n" +
            "          \"bolt\"\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static void show(final State state, final Runnable onSuccess) {
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Building");

        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Building name input
        final Label nameLabel = new Label("Building Name:");
        final TextField nameField = new TextField();
        nameField.setPromptText("Enter unique building name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // Building type selection
        final Label typeLabel = new Label("Building Type:");
        final ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("Select building type");
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        // Coordinate inputs (optional)
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

        // Selection of existing buildings as sources
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

        // Parse the building types from the JSON using the TypeParser.
        final TypeParser typeParser = new TypeParser();
        String typeParseError;
        try (BufferedReader br = new BufferedReader(new StringReader(BUILDING_TYPES_JSON))) {
            typeParseError = typeParser.parse(br);
        } catch (Exception ex) {
            typeParseError = "Error reading in-memory JSON: " + ex.getMessage();
        }
        if (typeParseError != null) {
            showError("TypeParser error: " + typeParseError);
            stage.close();
            return;
        }
        final List<BuildingType> availableTypes = typeParser.getTypeMap();
        final List<String> typeNames = availableTypes.stream()
                .map(BuildingType::getName)
                .collect(Collectors.toList());
        typeCombo.setItems(FXCollections.observableArrayList(typeNames));

        // Submit and Cancel buttons
        final Button submitBtn = new Button("Add Building");
        final Button cancelBtn = new Button("Cancel");
        submitBtn.setOnAction(e -> {
            final String buildingName = nameField.getText().trim();
            final String buildingTypeName = typeCombo.getValue();
            if (buildingName.isEmpty() || buildingTypeName == null || buildingTypeName.isEmpty()) {
                showError("Building name and type are required.");
                return;
            }

            // Parse or generate a valid coordinate.
            final Coordinate coord = parseCoordinate(xField, yField);

            // Get selected source buildings.
            final ObservableList<String> selectedSourceNames = sourcesListView.getSelectionModel().getSelectedItems();
            final List<Building> chosenSources = state.getBuildings().stream()
                    .filter(b -> selectedSourceNames.contains(b.getName()))
                    .collect(Collectors.toList());

            // Find the BuildingType instance matching the selected type name.
            final BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(buildingTypeName))
                    .findFirst().orElse(null);
            if (selectedType == null) {
                showError("Selected building type not found.");
                return;
            }

            // Create the new building based on the type.
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

    /**
     * Attempts to parse the coordinate from the given text fields.
     * If both fields are non-empty and valid integers, returns a new Coordinate.
     * Otherwise, returns an automatically assigned valid coordinate.
     */
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
            // Fall through to return a default coordinate.
        }
        return Building.getValidCoordinate();
    }

    /**
     * Creates a new Building instance based on the selected building type.
     *
     * @param buildingName      the unique name of the building
     * @param chosenSources     the list of source buildings
     * @param state             the current state for default policies
     * @param coord             the coordinate for the building
     * @param buildingTypeName  the name of the building type (used for determining the category)
     * @param selectedType      the BuildingType instance to use
     * @return the newly created Building instance
     * @throws Exception if an error occurs during creation
     */
    private static Building createBuilding(String buildingName, List<Building> chosenSources, State state,
                                           Coordinate coord, String buildingTypeName, BuildingType selectedType) throws Exception {
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