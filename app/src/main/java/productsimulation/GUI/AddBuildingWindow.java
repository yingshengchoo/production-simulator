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
import productsimulation.setup.TypeParser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

public class AddBuildingWindow {

    public static void show(State state, Runnable onSuccess) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Building");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Building name input
        Label nameLabel = new Label("Building Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter unique building name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // Building type selection
        Label typeLabel = new Label("Building Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("Select building type");
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        // Coordinate inputs (optional)
        Label xLabel = new Label("X Coordinate (optional):");
        TextField xField = new TextField();
        xField.setPromptText("Enter X coordinate");
        grid.add(xLabel, 0, 2);
        grid.add(xField, 1, 2);

        Label yLabel = new Label("Y Coordinate (optional):");
        TextField yField = new TextField();
        yField.setPromptText("Enter Y coordinate");
        grid.add(yLabel, 0, 3);
        grid.add(yField, 1, 3);

        // Selection of existing buildings as sources
        Label sourcesLabel = new Label("Select sources:");
        List<String> existingBuildings = state.getBuildings()
                .stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        ListView<String> sourcesListView = new ListView<>();
        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourcesListView.setItems(FXCollections.observableArrayList(existingBuildings));
        grid.add(sourcesLabel, 0, 4);
        grid.add(sourcesListView, 1, 4);

        // The TypeParser uses JSON-defined building types.
        String json = "{\n" +
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
        TypeParser typeParser = new TypeParser();
        String typeParseError;
        try (BufferedReader br = new BufferedReader(new StringReader(json))) {
            typeParseError = typeParser.parse(br);
        } catch (Exception ex) {
            typeParseError = "Error reading in-memory JSON: " + ex.getMessage();
        }
        if (typeParseError != null) {
            showError("TypeParser error: " + typeParseError);
            stage.close();
            return;
        }
        List<BuildingType> availableTypes = typeParser.getTypeMap();
        List<String> typeNames = availableTypes.stream()
                .map(BuildingType::getName)
                .collect(Collectors.toList());
        typeCombo.setItems(FXCollections.observableArrayList(typeNames));

        // Removal of extra field inputs; the extra parameters are now taken from JSON.
        // No additional fields for capacity, priority, or mine output are added.

        // Submit and Cancel buttons
        Button submitBtn = new Button("Add Building");
        Button cancelBtn = new Button("Cancel");
        submitBtn.setOnAction(e -> {
            String bName = nameField.getText().trim();
            String tName = typeCombo.getValue();
            if (bName.isEmpty() || tName == null || tName.isEmpty()) {
                showError("Building name and type are required.");
                return;
            }

            // Determine the coordinate; if absent or invalid, assign automatically.
            Coordinate coord;
            try {
                if (!xField.getText().trim().isEmpty() && !yField.getText().trim().isEmpty()) {
                    int x = Integer.parseInt(xField.getText().trim());
                    int y = Integer.parseInt(yField.getText().trim());
                    coord = new Coordinate(x, y);
                } else {
                    coord = Building.getValidCoordinate();
                }
            } catch (NumberFormatException ex) {
                coord = Building.getValidCoordinate();
            }

            // Get selected source buildings from the list view.
            ObservableList<String> selectedSourceNames = sourcesListView.getSelectionModel().getSelectedItems();
            List<Building> chosenSources = state.getBuildings().stream()
                    .filter(b -> selectedSourceNames.contains(b.getName()))
                    .collect(Collectors.toList());

            // Find the BuildingType instance from the available types.
            BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(tName))
                    .findFirst().orElse(null);
            if (selectedType == null) {
                showError("Selected building type not found.");
                return;
            }

            Building newBuilding;
            try {
                // Creation based on type; extra building parameters come from the JSON.
                if (tName.toLowerCase().contains("storage")) {
                    newBuilding = Storage.addStorage(
                            bName,
                            chosenSources,
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            (StorageType) selectedType
                    );
                } else if (tName.toLowerCase().contains("mine")) {
                    newBuilding = Mine.addMine(
                            bName,
                            chosenSources,
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            selectedType
                    );
                } else {
                    newBuilding = Factory.addFactory(
                            bName,
                            chosenSources,
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            selectedType
                    );
                }
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

        Scene scene = new Scene(grid, 560, 400);
        stage.setScene(scene);
        stage.showAndWait();
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