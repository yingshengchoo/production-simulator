package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import productsimulation.State;
import productsimulation.command.CommandParser;
import java.io.File;

public class ControlPanel extends VBox {

    private CommandParser commandParser;
    private BoardDisplay boardDisplay;
    private State simulationState;

    public ControlPanel(CommandParser commandParser, BoardDisplay boardDisplay, State simulationState) {
        this.commandParser = commandParser;
        this.boardDisplay = boardDisplay;
        this.simulationState = simulationState;

        setPadding(new Insets(10));
        setSpacing(10);

        // STEP and FINISH controls.
        Button stepButton = new Button("Step");
        Button finishButton = new Button("Finish");
        HBox stepBox = new HBox(5, stepButton, finishButton);
        getChildren().add(stepBox);

        // Other command buttons.
        Button requestButton = new Button("Request Item");
        Button connectButton = new Button("Connect Buildings");
        Button policyButton = new Button("Set Policy");
        Button verbosityButton = new Button("Set Verbosity");
        getChildren().addAll(requestButton, connectButton, policyButton, verbosityButton);

        // LOAD and SAVE controls.
        Button loadButton = new Button("Load Setup");
        Button saveButton = new Button("Save Simulation");
        HBox loadSaveBox = new HBox(5, loadButton, saveButton);
        getChildren().add(loadSaveBox);

        // --- Event Handlers with error alerts ---
        stepButton.setOnAction(e -> {
            int steps = DialogUtils.promptForInt("Step Command", "Enter number of steps:", 1);
            if (steps > 0) {
                try {
                    commandParser.parseLine("step " + steps);
                } catch (Exception ex) {
                    showError("Step error: " + ex.getMessage());
                }
                boardDisplay.refresh();
            }
        });

        finishButton.setOnAction(e -> {
            try {
                commandParser.parseLine("finish");
            } catch (Exception ex) {
                showError("Finish error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });

        requestButton.setOnAction(e -> {
            String[] input = RequestDialog.showRequestDialog(simulationState);
            if (input != null) {
                String cmd = String.format("request '%s' from '%s'", input[0], input[1]);
                try {
                    commandParser.parseLine(cmd);
                } catch (Exception ex) {
                    showError("Request error: " + ex.getMessage());
                }
                boardDisplay.refresh();
            }
        });

        connectButton.setOnAction(e -> {
            String[] input = ConnectDialog.showConnectDialog(simulationState);
            if (input != null) {
                String cmd = String.format("connect '%s' to '%s'", input[0], input[1]);
                try {
                    commandParser.parseLine(cmd);
                } catch (Exception ex) {
                    showError("Connect error: " + ex.getMessage());
                }
                boardDisplay.refresh();
            }
        });

        policyButton.setOnAction(e -> {
            String[] input = PolicyDialog.showPolicyDialog();
            if (input != null) {
                String cmd = String.format("set policy %s '%s' on %s", input[0], input[1], input[2]);
                try {
                    commandParser.parseLine(cmd);
                } catch (Exception ex) {
                    showError("Policy error: " + ex.getMessage());
                }
                boardDisplay.refresh();
            }
        });

        verbosityButton.setOnAction(e -> {
            int level = DialogUtils.promptForInt("Verbosity", "Enter verbosity level (0-2):", 0);
            try {
                commandParser.parseLine("verbose " + level);
            } catch (Exception ex) {
                showError("Verbosity error: " + ex.getMessage());
            }
            boardDisplay.refresh();
        });

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
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}