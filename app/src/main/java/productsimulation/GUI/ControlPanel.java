package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import productsimulation.State;
import productsimulation.command.CommandParser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ControlPanel extends VBox {

    private CommandParser commandParser;
    private BoardDisplay boardDisplay;
    private State simulationState;

    public ControlPanel(CommandParser commandParser, BoardDisplay boardDisplay, State simulationState) {
        this.commandParser = commandParser;
        this.boardDisplay = boardDisplay;
        this.simulationState = simulationState;

        setPadding(new Insets(10));
        setSpacing(15);

        // --- Simulation Control Section ---
        TitledPane simControlPane = new TitledPane();
        simControlPane.setText("Simulation Control");
        GridPane simControlGrid = new GridPane();
        simControlGrid.setHgap(10);
        simControlGrid.setVgap(10);
        simControlGrid.setPadding(new Insets(10));

        // STEP control using a Spinner.
        Label stepLabel = new Label("Step count:");
        Spinner<Integer> stepSpinner = new Spinner<>(1, 100, 1);
        Button stepButton = new Button("Step");
        stepButton.setOnAction(e -> {
            int steps = stepSpinner.getValue();
            try {
                commandParser.parseLine("step " + steps);
            } catch(Exception ex) {
                showError("Step error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });
        simControlGrid.add(stepLabel, 0, 0);
        simControlGrid.add(stepSpinner, 1, 0);
        simControlGrid.add(stepButton, 2, 0);

        // FINISH button.
        Button finishButton = new Button("Finish");
        finishButton.setOnAction(e -> {
            try {
                commandParser.parseLine("finish");
            } catch(Exception ex) {
                showError("Finish error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });
        simControlGrid.add(finishButton, 0, 1, 3, 1);
        simControlPane.setContent(simControlGrid);

        // --- Request Item Section ---
        TitledPane requestPane = new TitledPane();
        requestPane.setText("Request Item");
        GridPane requestGrid = new GridPane();
        requestGrid.setHgap(10);
        requestGrid.setVgap(10);
        requestGrid.setPadding(new Insets(10));

        Label requestItemLabel = new Label("Item:");
        // For now, a fixed list of items; you could later update it dynamically.
        ComboBox<String> itemCombo = new ComboBox<>(FXCollections.observableArrayList("door", "handle", "hinge"));
        itemCombo.setPromptText("Select item");

        Label requestBuildingLabel = new Label("From Building:");
        // Populate building names from simulationState.
        List<String> buildingNames = simulationState.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        ComboBox<String> requestBuildingCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        requestBuildingCombo.setPromptText("Select building");

        Button requestButton = new Button("Request");
        requestButton.setOnAction(e -> {
            String item = itemCombo.getValue();
            String building = requestBuildingCombo.getValue();
            if(item == null || building == null) {
                showError("Please select both an item and a building.");
                return;
            }
            String cmd = String.format("request '%s' from '%s'", item, building);
            try {
                commandParser.parseLine(cmd);
            } catch(Exception ex) {
                showError("Request error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });

        requestGrid.add(requestItemLabel, 0, 0);
        requestGrid.add(itemCombo, 1, 0);
        requestGrid.add(requestBuildingLabel, 0, 1);
        requestGrid.add(requestBuildingCombo, 1, 1);
        requestGrid.add(requestButton, 0, 2, 2, 1);
        requestPane.setContent(requestGrid);

        // --- Connect Buildings Section ---
        TitledPane connectPane = new TitledPane();
        connectPane.setText("Connect Buildings");
        GridPane connectGrid = new GridPane();
        connectGrid.setHgap(10);
        connectGrid.setVgap(10);
        connectGrid.setPadding(new Insets(10));

        Label connectSourceLabel = new Label("Source:");
        ComboBox<String> connectSourceCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        connectSourceCombo.setPromptText("Select source");

        Label connectDestLabel = new Label("Destination:");
        ComboBox<String> connectDestCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        connectDestCombo.setPromptText("Select destination");

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> {
            String source = connectSourceCombo.getValue();
            String dest = connectDestCombo.getValue();
            if(source == null || dest == null) {
                showError("Please select both a source and a destination building.");
                return;
            }
            String cmd = String.format("connect '%s' to '%s'", source, dest);
            try {
                commandParser.parseLine(cmd);
            } catch(Exception ex) {
                showError("Connect error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });

        connectGrid.add(connectSourceLabel, 0, 0);
        connectGrid.add(connectSourceCombo, 1, 0);
        connectGrid.add(connectDestLabel, 0, 1);
        connectGrid.add(connectDestCombo, 1, 1);
        connectGrid.add(connectButton, 0, 2, 2, 1);
        connectPane.setContent(connectGrid);

        // --- Set Policy / Verbosity Section ---
        TitledPane policyPane = new TitledPane();
        policyPane.setText("Set Policy / Verbosity");
        GridPane policyGrid = new GridPane();
        policyGrid.setHgap(10);
        policyGrid.setVgap(10);
        policyGrid.setPadding(new Insets(10));

        // Policy configuration:
        Label policyTypeLabel = new Label("Policy Type:");
        ComboBox<String> policyTypeCombo = new ComboBox<>(FXCollections.observableArrayList("request", "source"));
        policyTypeCombo.setPromptText("Select type");

        Label policyValueLabel = new Label("Policy Value:");
        ComboBox<String> policyValueCombo = new ComboBox<>();
        // Update policy values when type changes.
        policyTypeCombo.setOnAction(e -> {
            String type = policyTypeCombo.getValue();
            if ("request".equals(type)) {
                policyValueCombo.setItems(FXCollections.observableArrayList("fifo", "sjf", "ready", "default"));
            } else if ("source".equals(type)) {
                policyValueCombo.setItems(FXCollections.observableArrayList("qlen", "simplelat", "recursivelat", "default"));
            } else {
                policyValueCombo.setItems(FXCollections.observableArrayList());
            }
        });
        policyValueCombo.setPromptText("Select value");

        Label policyTargetLabel = new Label("Policy Target:");
        // Target: a building name, "*" or "default".
        ComboBox<String> policyTargetCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        policyTargetCombo.getItems().addAll("*", "default");
        policyTargetCombo.setPromptText("Select target");

        Button policyButton = new Button("Set Policy");
        policyButton.setOnAction(e -> {
            String type = policyTypeCombo.getValue();
            String value = policyValueCombo.getValue();
            String target = policyTargetCombo.getValue();
            if (type == null || value == null || target == null) {
                showError("Please select policy type, value, and target.");
                return;
            }
            String cmd = String.format("set policy %s '%s' on %s", type, value, target);
            try {
                commandParser.parseLine(cmd);
            } catch(Exception ex) {
                showError("Policy error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });

        policyGrid.add(policyTypeLabel, 0, 0);
        policyGrid.add(policyTypeCombo, 1, 0);
        policyGrid.add(policyValueLabel, 0, 1);
        policyGrid.add(policyValueCombo, 1, 1);
        policyGrid.add(policyTargetLabel, 0, 2);
        policyGrid.add(policyTargetCombo, 1, 2);
        policyGrid.add(policyButton, 0, 3, 2, 1);

        // Verbosity configuration:
        Label verbosityLabel = new Label("Verbosity:");
        ComboBox<Integer> verbosityCombo = new ComboBox<>(FXCollections.observableArrayList(0, 1, 2));
        verbosityCombo.setPromptText("Select level");
        Button verbosityButton = new Button("Set Verbosity");
        verbosityButton.setOnAction(e -> {
            Integer level = verbosityCombo.getValue();
            if (level == null) {
                showError("Please select a verbosity level.");
                return;
            }
            try {
                commandParser.parseLine("verbose " + level);
            } catch(Exception ex) {
                showError("Verbosity error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });
        policyGrid.add(verbosityLabel, 0, 4);
        policyGrid.add(verbosityCombo, 1, 4);
        policyGrid.add(verbosityButton, 0, 5, 2, 1);
        policyPane.setContent(policyGrid);

        // --- File Load/Save Section ---
        TitledPane filePane = new TitledPane();
        filePane.setText("Load / Save");
        HBox fileBox = new HBox(10);
        Button loadButton = new Button("Load Setup");
        Button saveButton = new Button("Save Simulation");
        loadButton.setOnAction(e -> {
            File f = FileDialogs.showLoadDialog();
            if (f != null) {
                try {
                    commandParser.parseLine("load " + f.getAbsolutePath());
                } catch (Exception ex) {
                    showError("Load error: " + ex.getMessage());
                }
                boardDisplay.refresh();
            }
        });
        saveButton.setOnAction(e -> {
            File f = FileDialogs.showSaveDialog();
            if (f != null) {
                try {
                    commandParser.parseLine("save " + f.getAbsolutePath());
                } catch (Exception ex) {
                    showError("Save error: " + ex.getMessage());
                }
            }
        });
        fileBox.getChildren().addAll(loadButton, saveButton);
        filePane.setContent(fileBox);

        // Add all sections to the main control panel.
        getChildren().addAll(simControlPane, requestPane, connectPane, policyPane, filePane);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}