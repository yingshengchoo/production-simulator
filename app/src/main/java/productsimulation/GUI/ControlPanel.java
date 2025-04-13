package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.FinishCommand;
import productsimulation.command.LoadCommand;
import productsimulation.command.SaveCommand;
import productsimulation.command.VerboseCommand;
import productsimulation.GUI.ConnectWindow;
import productsimulation.GUI.RequestWindow;
import productsimulation.GUI.StepWindow;
import productsimulation.GUI.PolicyWindow;
import productsimulation.GUI.VerbosityWindow;

/**
 * ControlPanel displays command buttons on the GUI.
 * Buttons include functionalities such as adding a building,
 * connecting buildings, requesting items, stepping, finishing,
 * loading, saving, changing verbosity, setting policies, and a new "Show Log" button.
 * When "Show Log" is pressed, the log text is retrieved and updated in the FeedbackPane.
 */
public class ControlPanel extends VBox {

    private State state;
    private BoardDisplay boardDisplay;
    private FeedbackPane feedbackArea;

    // Command buttons.
    private Button addBuildingBtn;
    private Button connectBtn;
    private Button requestBtn;
    private Button stepBtn;
    private Button finishBtn;
    private Button loadBtn;
    private Button saveBtn;
    private Button verbosityBtn;
    private Button policyBtn;
    private Button showLogBtn;  // New "Show Log" button

    /**
     * Constructs the ControlPanel with all command buttons.
     *
     * @param state         the current simulation state
     * @param boardDisplay  the board display component
     * @param feedbackArea  the pane for displaying logs and feedback
     */
    public ControlPanel(State state, BoardDisplay boardDisplay, FeedbackPane feedbackArea) {
        this.state = state;
        this.boardDisplay = boardDisplay;
        this.feedbackArea = feedbackArea;

        setPadding(new Insets(10));
        setSpacing(10);

        addBuildingBtn = new Button("Add Building");
        addBuildingBtn.setOnAction(e -> {
            AddBuildingWindow.show(state, () -> {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
            });
        });

        connectBtn = new Button("Connect Buildings");
        connectBtn.setOnAction(e -> {
            ConnectWindow.show(state, () -> {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
            });
        });

        requestBtn = new Button("Request Item");
        requestBtn.setOnAction(e -> {
            RequestWindow.show(state, () -> {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
            });
        });

        stepBtn = new Button("Step");
        stepBtn.setOnAction(e -> {
            StepWindow.show(state, () -> {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
            });
        });

        finishBtn = new Button("Finish");
        finishBtn.setOnAction(e -> {
            String error = new FinishCommand().execute();
            if (error == null) {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
                disableAllButtons();
            } else {
                showError("Finish error: " + error);
            }
        });

        loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            LoadWindow.show(() -> {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
            });
        });

        saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            SaveWindow.show(() -> {
                feedbackArea.setText(Log.getLogText());
            });
        });

        verbosityBtn = new Button("Set Verbosity");
        verbosityBtn.setOnAction(e -> {
            VerbosityWindow.show(val -> {
                new VerboseCommand(val).execute();
                feedbackArea.setText(Log.getLogText());
            });
        });

        policyBtn = new Button("Set Policy");
        policyBtn.setOnAction(e -> {
            PolicyWindow.show(state, () -> {
                boardDisplay.refresh();
                feedbackArea.setText(Log.getLogText());
            });
        });

        // New "Show Log" button logic:
        showLogBtn = new Button("Show Log");
        showLogBtn.setOnAction(e -> {
            // Retrieve the log text and display it in the FeedbackPane.
            feedbackArea.setText(Log.getLogText());
        });

        getChildren().addAll(
                addBuildingBtn,
                connectBtn,
                requestBtn,
                stepBtn,
                finishBtn,
                loadBtn,
                saveBtn,
                verbosityBtn,
                policyBtn,
                showLogBtn
        );
    }

    private void disableAllButtons() {
        addBuildingBtn.setDisable(true);
        connectBtn.setDisable(true);
        requestBtn.setDisable(true);
        stepBtn.setDisable(true);
        finishBtn.setDisable(true);
        loadBtn.setDisable(true);
        saveBtn.setDisable(true);
        verbosityBtn.setDisable(true);
        policyBtn.setDisable(true);
        showLogBtn.setDisable(true);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
