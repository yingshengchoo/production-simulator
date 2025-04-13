package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.FinishCommand;
import productsimulation.command.LoadCommand;
import productsimulation.command.SaveCommand;
import productsimulation.command.VerboseCommand;

import java.io.File;

public class ControlPanel extends VBox {

    private State state;
    private BoardDisplay boardDisplay;
    private FeedbackPane feedbackArea;

    // Store all command buttons as instance variables
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

        // Initialize command buttons

        // 1) Connect Buildings button
        connectBtn = new Button("Connect Buildings");
        connectBtn.setOnAction(e -> {
            ConnectWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 2) Request Item button
        requestBtn = new Button("Request Item");
        requestBtn.setOnAction(e -> {
            RequestWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 3) Step button
        stepBtn = new Button("Step");
        stepBtn.setOnAction(e -> {
            StepWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 4) Finish button
        finishBtn = new Button("Finish");
        finishBtn.setOnAction(e -> {
            // Call backend finish command; if finish completes successfully then disable buttons.
            String error = new FinishCommand().execute();
            if (error == null) {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
                disableAllButtons();
            } else {
                showError("Finish error: " + error);
            }
        });

        // 5) Load button
        loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            LoadWindow.show(() -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // 6) Save button
        saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            SaveWindow.show(() -> {
                setFeedBack(Log.getLogText());
            });
        });

        // 7) Verbosity button
        verbosityBtn = new Button("Set Verbosity");
        verbosityBtn.setOnAction(e -> {
            VerbosityWindow.show(val -> {
                new VerboseCommand(val).execute();
                setFeedBack(Log.getLogText());
            });
        });

        // 8) Policy button
        policyBtn = new Button("Set Policy");
        policyBtn.setOnAction(e -> {
            PolicyWindow.show(state, () -> {
                boardDisplay.refresh();
                setFeedBack(Log.getLogText());
            });
        });

        // Add all buttons to the ControlPanel VBox
        getChildren().addAll(connectBtn, requestBtn, stepBtn, finishBtn,
                loadBtn, saveBtn, verbosityBtn, policyBtn);
    }

    /**
     * Disable all command buttons after finish command has completed.
     */
    private void disableAllButtons() {
        // Disables each button so that no further actions can be performed.
        connectBtn.setDisable(true);
        requestBtn.setDisable(true);
        stepBtn.setDisable(true);
        loadBtn.setDisable(true);
        saveBtn.setDisable(true);
        verbosityBtn.setDisable(true);
        policyBtn.setDisable(true);
        finishBtn.setDisable(true);
    }

    // Utility method to display errors in an alert.
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    // Utility method to update the feedback area (here encapsulated in a custom FeedbackPane).
    private void setFeedBack(String text) {
        feedbackArea.setText("");
        feedbackArea.appendText(text + "\n");
    }
}