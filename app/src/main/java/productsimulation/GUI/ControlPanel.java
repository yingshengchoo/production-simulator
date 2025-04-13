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

/**
 * ControlPanel is the right-side pane in the GUI that contains all the command buttons.
 * It includes buttons for connecting buildings, requesting items, stepping, finishing,
 * loading, saving, setting verbosity, setting policy, and now adding a building.
 *
 * When the "Finish" command is executed successfully, all buttons are disabled so that no further
 * actions are permitted.
 */
public class ControlPanel extends VBox {

    private State state;
    private BoardDisplay boardDisplay;
    private FeedbackPane feedbackArea;

    // Store all command buttons as instance variables
    private Button addBuildingBtn;
    private Button connectBtn;
    private Button requestBtn;
    private Button stepBtn;
    private Button finishBtn;
    private Button loadBtn;
    private Button saveBtn;
    private Button verbosityBtn;
    private Button policyBtn;

    public ControlPanel(State state, BoardDisplay boardDisplay, FeedbackPane feedbackArea) {
        this.state = state;
        this.boardDisplay = boardDisplay;
        this.feedbackArea = feedbackArea;

        setPadding(new Insets(10));
        setSpacing(10);

        // 1) Add Building button (new)
        addBuildingBtn = new Button("Add Building");
        addBuildingBtn.setOnAction(e -> {
            // BACKEND LOGIC:
            // Here we pass the current state and a callback to refresh the board and update feedback.
            AddBuildingWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 2) Connect Buildings button
        connectBtn = new Button("Connect Buildings");
        connectBtn.setOnAction(e -> {
            ConnectWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 3) Request Item button
        requestBtn = new Button("Request Item");
        requestBtn.setOnAction(e -> {
            RequestWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 4) Step button
        stepBtn = new Button("Step");
        stepBtn.setOnAction(e -> {
            StepWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 5) Finish button
        finishBtn = new Button("Finish");
        finishBtn.setOnAction(e -> {
            String error = new FinishCommand().execute();
            if (error == null) {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
                disableAllButtons();  // Disable all buttons when finish is executed.
            } else {
                showError("Finish error: " + error);
            }
        });

        // 6) Load button
        loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            LoadWindow.show(() -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 7) Save button
        saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            SaveWindow.show(() -> {
                setFeedBack(Log.getLogText());
            });
        });

        // 8) Verbosity button
        verbosityBtn = new Button("Set Verbosity");
        verbosityBtn.setOnAction(e -> {
            VerbosityWindow.show(val -> {
                new VerboseCommand(val).execute();
                setFeedBack(Log.getLogText());
            });
        });

        // 9) Policy button
        policyBtn = new Button("Set Policy");
        policyBtn.setOnAction(e -> {
            PolicyWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // Add all buttons to the ControlPanel VBox (order can be adjusted as needed)
        getChildren().addAll(
                addBuildingBtn, connectBtn, requestBtn, stepBtn, finishBtn,
                loadBtn, saveBtn, verbosityBtn, policyBtn
        );
    }

    /**
     * Disables all command buttons after finish command has completed.
     */
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
    }

    // Utility method to show an error alert.
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    // Utility method to update feedback in the feedback area.
    private void setFeedBack(String text) {
        feedbackArea.setText("");
        feedbackArea.appendText(text + "\n");
    }
}
