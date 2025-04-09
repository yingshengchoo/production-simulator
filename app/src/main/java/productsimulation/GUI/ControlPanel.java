package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.CommandParser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ControlPanel extends VBox {

    private final CommandParser commandParser;
    private final BoardDisplay boardDisplay;
    private final State simulationState;

    public ControlPanel(CommandParser commandParser, BoardDisplay boardDisplay, State simulationState) {
        this.commandParser = commandParser;
        this.boardDisplay = boardDisplay;
        this.simulationState = simulationState;

        setPadding(new Insets(10));
        setSpacing(15);

        // Main Buttons on the Control Panel
        Button stepButton = new Button("Step");
        Button finishButton = new Button("Finish");
        Button requestButton = new Button("Request Item");
        Button connectButton = new Button("Connect Buildings");
        Button policyButton = new Button("Set Policy");
        Button verbosityButton = new Button("Set Verbosity");
        Button loadButton = new Button("Load Setup");
        Button saveButton = new Button("Save Simulation");

        // Event Handlers:
        stepButton.setOnAction(e -> openStepWindow());

        finishButton.setOnAction(e -> {
            try {
                commandParser.parseLine("finish");
            } catch (Exception ex) {
                showError("Finish error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });

        requestButton.setOnAction(e -> openRequestWindow());

        connectButton.setOnAction(e -> openConnectWindow());

        policyButton.setOnAction(e -> openPolicyWindow());

        verbosityButton.setOnAction(e -> openVerbosityWindow());

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

        // Add all main buttons to the panel.
        getChildren().addAll(stepButton, finishButton, requestButton, connectButton,
                policyButton, verbosityButton, loadButton, saveButton);
    }

    // Opens a new modal window for the Step command.
    private void openStepWindow() {
        Stage stepStage = new Stage();
        stepStage.setTitle("Step Simulation");
        GridPane grid = createDefaultGrid();

        Label label = new Label("Enter number of steps:");
        Spinner<Integer> stepSpinner = new Spinner<>(1, 100, 1);
        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            int steps = stepSpinner.getValue();
            try {
                commandParser.parseLine("step " + steps);
            } catch(Exception ex) {
                showError("Step error: " + ex.getMessage());
            }
            boardDisplay.refresh();
            stepStage.close();
        });

        grid.add(label, 0, 0);
        grid.add(stepSpinner, 1, 0);
        grid.add(submit, 0, 1, 2, 1);
        showModalWindow(stepStage, grid);
    }

    // Opens a new modal window for the Request Item command.
    private void openRequestWindow() {
        Stage reqStage = new Stage();
        reqStage.setTitle("Request Item");
        GridPane grid = createDefaultGrid();

        Label itemLabel = new Label("Item:");
        // Use a fixed list; you can update this list dynamically if needed.
        ComboBox<String> itemCombo = new ComboBox<>(FXCollections.observableArrayList("door", "handle", "hinge"));
        itemCombo.setPromptText("Select item");

        Label buildingLabel = new Label("From Building:");
        List<String> buildingNames = simulationState.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        ComboBox<String> buildingCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        buildingCombo.setPromptText("Select building");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            String item = itemCombo.getValue();
            String building = buildingCombo.getValue();
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
            reqStage.close();
        });

        grid.add(itemLabel, 0, 0);
        grid.add(itemCombo, 1, 0);
        grid.add(buildingLabel, 0, 1);
        grid.add(buildingCombo, 1, 1);
        grid.add(submit, 0, 2, 2, 1);
        showModalWindow(reqStage, grid);
    }

    // Opens a new modal window for the Connect Buildings command.
    private void openConnectWindow() {
        Stage connStage = new Stage();
        connStage.setTitle("Connect Buildings");
        GridPane grid = createDefaultGrid();

        Label sourceLabel = new Label("Source:");
        List<String> buildingNames = simulationState.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        ComboBox<String> sourceCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        sourceCombo.setPromptText("Select source");

        Label destLabel = new Label("Destination:");
        ComboBox<String> destCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        destCombo.setPromptText("Select destination");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            String source = sourceCombo.getValue();
            String dest = destCombo.getValue();
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
            connStage.close();
        });

        grid.add(sourceLabel, 0, 0);
        grid.add(sourceCombo, 1, 0);
        grid.add(destLabel, 0, 1);
        grid.add(destCombo, 1, 1);
        grid.add(submit, 0, 2, 2, 1);
        showModalWindow(connStage, grid);
    }

    // Opens a new modal window for the Set Policy command.
    private void openPolicyWindow() {
        Stage policyStage = new Stage();
        policyStage.setTitle("Set Policy");
        GridPane grid = createDefaultGrid();

        Label typeLabel = new Label("Policy Type:");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("request", "source"));
        typeCombo.setPromptText("Select type");

        Label valueLabel = new Label("Policy Value:");
        ComboBox<String> valueCombo = new ComboBox<>();
        typeCombo.setOnAction(e -> {
            String type = typeCombo.getValue();
            if ("request".equals(type)) {
                valueCombo.setItems(FXCollections.observableArrayList("fifo", "sjf", "ready", "default"));
            } else if ("source".equals(type)) {
                valueCombo.setItems(FXCollections.observableArrayList("qlen", "simplelat", "recursivelat", "default"));
            } else {
                valueCombo.setItems(FXCollections.observableArrayList());
            }
        });
        valueCombo.setPromptText("Select value");

        Label targetLabel = new Label("Policy Target:");
        List<String> buildingNames = simulationState.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        ComboBox<String> targetCombo = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        // Include "*" and "default" as additional targets.
        targetCombo.getItems().addAll("*", "default");
        targetCombo.setPromptText("Select target");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            String type = typeCombo.getValue();
            String value = valueCombo.getValue();
            String target = targetCombo.getValue();
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
            policyStage.close();
        });

        grid.add(typeLabel, 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(valueLabel, 0, 1);
        grid.add(valueCombo, 1, 1);
        grid.add(targetLabel, 0, 2);
        grid.add(targetCombo, 1, 2);
        grid.add(submit, 0, 3, 2, 1);
        showModalWindow(policyStage, grid);
    }

    // Opens a new modal window for the Set Verbosity command.
    private void openVerbosityWindow() {
        Stage verbStage = new Stage();
        verbStage.setTitle("Set Verbosity");
        GridPane grid = createDefaultGrid();

        Label verbLabel = new Label("Verbosity Level:");
        ComboBox<Integer> verbCombo = new ComboBox<>(FXCollections.observableArrayList(0, 1, 2));
        verbCombo.setPromptText("Select level");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            Integer level = verbCombo.getValue();
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
            verbStage.close();
        });

        grid.add(verbLabel, 0, 0);
        grid.add(verbCombo, 1, 0);
        grid.add(submit, 0, 1, 2, 1);
        showModalWindow(verbStage, grid);
    }

    // Helper method: creates a default GridPane with common settings.
    private GridPane createDefaultGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    // Helper method: configures and shows a window modally.
    private void showModalWindow(Stage stage, GridPane content) {
        Scene scene = new Scene(content);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // Helper method: shows an error alert.
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}