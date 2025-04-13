package productsimulation.GUI;

import javafx.collections.FXCollections;
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
import productsimulation.model.Recipe;
import productsimulation.model.Storage;
import productsimulation.model.StorageType;
import productsimulation.setup.TypeParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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

        Label nameLabel = new Label("Building Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter unique building name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label typeLabel = new Label("Building Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("Select building type");
        TypeParser typeParser = new TypeParser();
        String typeParseError;

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
//        try (BufferedReader br = new BufferedReader(new FileReader("/building_types.json"))) {
        try (BufferedReader br = new BufferedReader(new StringReader(json))) {

            typeParseError = typeParser.parse(br);
            if (typeParseError != null) {
                showError("Type parsing error: " + typeParseError);
                stage.close();
                return;
            }
        } catch (Exception ex) {
            showError("Error reading building types: " + ex.getMessage());
            stage.close();
            return;
        }
        // Retrieve the list of available building types.
        List<BuildingType> availableTypes = typeParser.getTypeMap();
        // Populate the drop down with type names.
        List<String> typeNames = availableTypes.stream()
                .map(BuildingType::getName)
                .collect(Collectors.toList());
        typeCombo.setItems(FXCollections.observableArrayList(typeNames));
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        // Coordinate Fields
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

        // --- Extra fields for Storage or Mine types ---
        // For Storage, show capacity and priority.
        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Positive integer");
        Label priorityLabel = new Label("Priority:");
        TextField priorityField = new TextField();
        priorityField.setPromptText("Positive float");
        capacityLabel.setVisible(false);
        capacityField.setVisible(false);
        priorityLabel.setVisible(false);
        priorityField.setVisible(false);
        grid.add(capacityLabel, 0, 4);
        grid.add(capacityField, 1, 4);
        grid.add(priorityLabel, 0, 5);
        grid.add(priorityField, 1, 5);
        // For Mine types, for example show a field for the mine output (if needed).
        Label mineOutputLabel = new Label("Mine Output:");
        TextField mineOutputField = new TextField();
        mineOutputField.setPromptText("e.g., metal");
        mineOutputLabel.setVisible(false);
        mineOutputField.setVisible(false);
        grid.add(mineOutputLabel, 0, 6);
        grid.add(mineOutputField, 1, 6);

        // --- Update extra fields based on selected type ---
        typeCombo.setOnAction(e -> {
            String selectedTypeName = typeCombo.getValue();
            // Find the BuildingType instance corresponding to the selected name.
            BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(selectedTypeName))
                    .findFirst().orElse(null);
            if (selectedType == null) return;
            // Check the category.
            // Assume that if the instance is of StorageType, its category is "storage".
            // Otherwise, define a method getCategory() in BuildingType.
            if (selectedType instanceof StorageType) {
                capacityLabel.setVisible(true);
                capacityField.setVisible(true);
                priorityLabel.setVisible(true);
                priorityField.setVisible(true);
                mineOutputLabel.setVisible(false);
                mineOutputField.setVisible(false);
            } else if (selectedType.getName().toLowerCase().contains("mine")) {
                // For mine type, show the mine output field (and hide storage fields).
                mineOutputLabel.setVisible(true);
                mineOutputField.setVisible(true);
                capacityLabel.setVisible(false);
                capacityField.setVisible(false);
                priorityLabel.setVisible(false);
                priorityField.setVisible(false);
            } else {
                // For factory types.
                capacityLabel.setVisible(false);
                capacityField.setVisible(false);
                priorityLabel.setVisible(false);
                priorityField.setVisible(false);
                mineOutputLabel.setVisible(false);
                mineOutputField.setVisible(false);
            }
        });

        // --- Submit and Cancel Buttons ---
        Button submitBtn = new Button("Add Building");
        Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            String bName = nameField.getText().trim();
            String tName = typeCombo.getValue();
            if (bName.isEmpty() || tName == null || tName.isEmpty()) {
                showError("Building name and type are required.");
                return;
            }

            // Determine coordinates. If not provided or invalid, auto-assign a valid coordinate.
            Coordinate coord;
            try {
                if (!xField.getText().trim().isEmpty() && !yField.getText().trim().isEmpty()) {
                    int x = Integer.parseInt(xField.getText().trim());
                    int y = Integer.parseInt(yField.getText().trim());
                    coord = new Coordinate(x, y);
                } else {
                    coord = Building.getValidCoordinate();  // auto-assign using static method in Building.
                }
            } catch (NumberFormatException ex) {
                coord = Building.getValidCoordinate();
            }

            // Find the corresponding type.
            BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(tName))
                    .findFirst().orElse(null);
            if (selectedType == null) {
                showError("Selected building type not found.");
                return;
            }

            Building newBuilding = null;
            try {
                if (selectedType instanceof StorageType) {
                    // Expect capacity and priority
                    if (capacityField.getText().trim().isEmpty() || priorityField.getText().trim().isEmpty()) {
                        showError("Please enter capacity and priority for storage type.");
                        return;
                    }
                    int capacity = Integer.parseInt(capacityField.getText().trim());
                    double priority = Double.parseDouble(priorityField.getText().trim());
                    // Here assume no sources initially.
                    newBuilding = Storage.addStorage("", // todo
                            List.of(), // empty sources list
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            (StorageType) selectedType);
                } else if (tName.toLowerCase().contains("mine")) {
                    // For mine, optionally allow the user to enter the mine output.
                    if (mineOutputField.getText().trim().isEmpty()) {
                        showError("Please enter the mine output (e.g., metal).");
                        return;
                    }
                    String mineOutput = mineOutputField.getText().trim();
                    // We create a dummy BuildingType for mine
                    BuildingType mineType = new BuildingType(tName, Map.of(mineOutput, new Recipe(Recipe.getRecipe(mineOutput).getLatency(), Map.of(), mineOutput)));
                    newBuilding = Mine.addMine("", //todo
                            List.of(), // no sources initially
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            mineType);
                } else {
                    // Default: factory
                    newBuilding = Factory.addFactory(" ", // todo
                            List.of(), // no sources initially; user can later connect buildings
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            selectedType);
                }
            } catch (Exception ex) {
                showError("Error creating building: " + ex.getMessage());
                return;
            }

            // After creation, add the building to state.
            // Also, update feedback: we show a success message with the building details.
            showInfo("Successfully added building: " + newBuilding.getName() +
                    " at (" + newBuilding.getX() + ", " + newBuilding.getY() + ")");
            if (onSuccess != null) {
                onSuccess.run();
            }
            stage.close();
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(submitBtn, 0, 7);
        grid.add(cancelBtn, 1, 7);

        Scene scene = new Scene(grid, 500, 380);
        stage.setScene(scene);
        stage.showAndWait();
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
