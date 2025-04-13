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
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        Label xLabel = new Label("X Coordinate:");
        TextField xField = new TextField();
        xField.setPromptText("Enter X coordinate");
        grid.add(xLabel, 0, 2);
        grid.add(xField, 1, 2);

        Label yLabel = new Label("Y Coordinate:");
        TextField yField = new TextField();
        yField.setPromptText("Enter Y coordinate");
        grid.add(yLabel, 0, 3);
        grid.add(yField, 1, 3);

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

        Label mineOutputLabel = new Label("Mine Output:");
        TextField mineOutputField = new TextField();
        mineOutputField.setPromptText("e.g. metal");
        mineOutputLabel.setVisible(false);
        mineOutputField.setVisible(false);
        grid.add(mineOutputLabel, 0, 6);
        grid.add(mineOutputField, 1, 6);

        Label sourcesLabel = new Label("Select sources:");
        List<String> existingBuildings = state.getBuildings().stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        ListView<String> sourcesListView = new ListView<>();
        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourcesListView.setItems(FXCollections.observableArrayList(existingBuildings));
        grid.add(sourcesLabel, 0, 7);
        grid.add(sourcesListView, 1, 7);

        Button submitBtn = new Button("Add Building");
        Button cancelBtn = new Button("Cancel");

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
        String parseError;
        try (BufferedReader br = new BufferedReader(new StringReader(json))) {
            parseError = typeParser.parse(br);
        } catch (Exception e) {
            parseError = "Error reading in-memory JSON for building types: " + e.getMessage();
        }
        if (parseError != null) {
            showError("TypeParser error: " + parseError);
            stage.close();
            return;
        }

        List<BuildingType> availableTypes = typeParser.getTypeMap();
        List<String> typeNames = availableTypes.stream()
                .map(BuildingType::getName)
                .collect(Collectors.toList());
        typeCombo.setItems(FXCollections.observableArrayList(typeNames));

        typeCombo.setOnAction(e -> {
            String selectedTypeName = typeCombo.getValue();
            BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(selectedTypeName))
                    .findFirst().orElse(null);
            if (selectedType == null) {
                return;
            }
            boolean isStorage = selectedType instanceof StorageType;
            boolean isMine = selectedType.getName().toLowerCase().contains("mine");

            capacityLabel.setVisible(isStorage);
            capacityField.setVisible(isStorage);
            priorityLabel.setVisible(isStorage);
            priorityField.setVisible(isStorage);

            mineOutputLabel.setVisible(isMine);
            mineOutputField.setVisible(isMine);
        });

        submitBtn.setOnAction(e -> {
            String bName = nameField.getText().trim();
            String tName = typeCombo.getValue();
            if (bName.isEmpty() || tName == null || tName.isEmpty()) {
                showError("Building name and type are required.");
                return;
            }

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

            ObservableList<String> selectedSourceNames = sourcesListView.getSelectionModel().getSelectedItems();
            List<Building> chosenSources = state.getBuildings().stream()
                    .filter(b -> selectedSourceNames.contains(b.getName()))
                    .collect(Collectors.toList());

            BuildingType selectedType = availableTypes.stream()
                    .filter(bt -> bt.getName().equals(tName))
                    .findFirst().orElse(null);

            if (selectedType == null) {
                showError("Selected building type not found.");
                return;
            }

            Building newBuilding;
            try {
                if (selectedType instanceof StorageType) {
                    if (capacityField.getText().trim().isEmpty() || priorityField.getText().trim().isEmpty()) {
                        showError("Please enter capacity and priority for Storage type.");
                        return;
                    }
                    int capacity = Integer.parseInt(capacityField.getText().trim());
                    double priority = Double.parseDouble(priorityField.getText().trim());

                    newBuilding = Storage.addStorage(
                            bName,
                            chosenSources,
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            (StorageType) selectedType
                    );
                    newBuilding.changePolicy(state.getDefaultSourcePolicy());
                    newBuilding.changePolicy(state.getDefaultServePolicy());
                } else if (tName.toLowerCase().contains("mine")) {
                    if (mineOutputField.getText().trim().isEmpty()) {
                        showError("Please enter the mine output (e.g. metal).");
                        return;
                    }
                    String mineOutput = mineOutputField.getText().trim();
                    BuildingType mineType = new BuildingType(
                            tName,
                            Map.of(mineOutput,
                                    new Recipe(Recipe.getRecipe(mineOutput).getLatency(),
                                            Map.of(),
                                            mineOutput))
                    );

                    newBuilding = Mine.addMine(
                            bName,
                            chosenSources,
                            state.getDefaultSourcePolicy(),
                            state.getDefaultServePolicy(),
                            coord,
                            mineType
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

            showInfo("Successfully added building: " + newBuilding.getName()
                    + " at (" + newBuilding.getX() + ", " + newBuilding.getY() + ")"
                    + "\nSources: " + chosenSources.stream().map(Building::getName).collect(Collectors.joining(", ")));

            if (onSuccess != null) {
                onSuccess.run();
            }
            stage.close();
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(submitBtn, 0, 8);
        grid.add(cancelBtn, 1, 8);

        Scene scene = new Scene(grid, 560, 600);
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