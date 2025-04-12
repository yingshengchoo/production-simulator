package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import productsimulation.Log;
import productsimulation.LogicTime;
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

    public ControlPanel(State state, BoardDisplay boardDisplay, FeedbackPane feedbackArea) {
        this.state = state;
        this.boardDisplay = boardDisplay;
        this.feedbackArea = feedbackArea;

        setPadding(new Insets(10));
        setSpacing(10);

        // 1) Add Building
//        Button addBuildingBtn = new Button("Add Building");
//        addBuildingBtn.setOnAction(e -> {
//            // Open the window that collects building name, type, coords
//            AddBuildingWindow.show(state, () -> {
//                // Refresh the board
//                boardDisplay.refresh();
//                // Show feedback
//                appendFeedback("Building added successfully.");
//            });
//        });

        // 2) Connect Buildings
        Button connectBtn = new Button("Connect Buildings");
        connectBtn.setOnAction(e -> {
            ConnectWindow.show(state, () -> {
                boardDisplay.refresh();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Buildings connected successfully.");
            });
        });

        Button requestBtn = new Button("Request Item");
        requestBtn.setOnAction(e -> {
            RequestWindow.show(state, () -> {
                boardDisplay.refresh();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Request item succeeded.");
            });
        });

        Button stepBtn = new Button("Step");
        stepBtn.setOnAction(e -> {
            StepWindow.show(state, () -> {
                boardDisplay.refresh();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Stepped the simulation.");
            });
        });

        Button finishBtn = new Button("Finish");
        finishBtn.setOnAction(e -> {
            String error = new FinishCommand().execute();
            if (error == null) {
                boardDisplay.refresh();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Finish: all requests completed.");
            } else {
                showError("Finish error: " + error);
            }
        });

        // In ControlPanel.java
        Button loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            LoadWindow.show(() -> {
                boardDisplay.refresh();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Loaded simulation successfully.");
            });
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            SaveWindow.show(() -> {
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Simulation saved successfully.");
            });
        });


        // 8) Verbosity
        Button verbosityBtn = new Button("Set Verbosity");
        verbosityBtn.setOnAction(e -> {
            VerbosityWindow.show(val -> {
                new VerboseCommand(val).execute();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Verbosity set to " + val);
            });
        });

        // 9) Policy
        Button policyBtn = new Button("Set Policy");
        policyBtn.setOnAction(e -> {
            PolicyWindow.show(state, () -> {
                boardDisplay.refresh();
//                setFeedback(Log.getLogText());
                setFeedBack(Log.getLogText());
//                appendFeedback("Policy updated.");
            });
        });

        getChildren().addAll(
//                addBuildingBtn,
                connectBtn, requestBtn, stepBtn, finishBtn,
                loadBtn, saveBtn, verbosityBtn, policyBtn
        );
    }


    // Implementation for load
    private void handleLoad() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Load Simulation");
        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        // Connect to the backend load logic
        String error = new LoadCommand(file.getAbsolutePath()).execute();
        if (error == null) {
            boardDisplay.refresh();
//            setFeedback(Log.getLogText());
            setFeedBack(Log.getLogText());
//            appendFeedback("Loaded from " + file.getName());
        } else {
            showError(error);
        }
    }

    // Utility
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void setFeedBack(String text) {
        feedbackArea.setText("");
        feedbackArea.appendText(text + "\n");
    }
}
