package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import productsimulation.Log;
import productsimulation.command.FinishCommand;
import productsimulation.command.StepCommand;
import productsimulation.GUI.AutoRunner;
import productsimulation.State;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Right-hand control panel: quick-access buttons + auto-run slider.
 */
public class ControlPanel extends VBox {
    private final BoardDisplay boardDisplay;
    private final FeedbackPane feedback;
    private final Runnable     uiRefresher;

    public ControlPanel(BoardDisplay boardDisplay,
                        FeedbackPane feedback,
                        Runnable uiRefresher) {
        super(12);
        setPadding(new Insets(15));

        this.boardDisplay = boardDisplay;
        this.feedback     = feedback;
        this.uiRefresher  = uiRefresher;

        // ── Static action buttons ─────────────────────────────
        Map<String,Runnable> actions = new LinkedHashMap<>();
        actions.put("Add Building", () ->
                AddBuildingWindow.show(State.getInstance(), this::postUpdate));
        actions.put("Go One Step", () -> {
            String err = new StepCommand(1).execute();
            if (err == null) postUpdate();
            else feedback.appendLine("Step error: " + err);
        });
        actions.put("Go N Steps", () ->
                StepWindow.show(this::postUpdate));
        actions.put("Finish", this::onFinish);

        actions.forEach((label, action) -> {
            Button btn = new Button(label);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> runSafely(label, action));
            getChildren().add(btn);
        });

        // ── Auto-Run Controls ─────────────────────────────────
        Label autoLabel    = new Label("Auto-Run Speed (steps/sec):");
        Slider speedSlider = new Slider(1, 20, 5);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(5);
        speedSlider.setMinorTickCount(4);
        speedSlider.setBlockIncrement(1);
        speedSlider.setMaxWidth(Double.MAX_VALUE);

        ToggleButton autoBtn = new ToggleButton("Start");
        autoBtn.setMaxWidth(Double.MAX_VALUE);
        autoBtn.setOnAction(e -> {
            if (autoBtn.isSelected()) {
                autoBtn.setText("Stop");
                // ▶ Here’s the fix: on each tick, run one StepCommand then refresh UI
                AutoRunner.start(
                        () -> (int)Math.max(1, speedSlider.getValue()),
                        () -> {
                            String err = new StepCommand(1).execute();
                            if (err != null && !err.isBlank()) {
                                feedback.appendLine("Auto-run step error: " + err);
                            }
                            // then refresh board/log and step counter
                            boardDisplay.refresh();
                            feedback.setContent(Log.getLogText());
                            uiRefresher.run();
                        }
                );
            } else {
                autoBtn.setText("Start");
                AutoRunner.stop();
            }
        });

        getChildren().addAll(
                new Separator(),
                autoLabel,
                speedSlider,
                autoBtn
        );
    }

    private void postUpdate() {
        boardDisplay.refresh();
        feedback.setContent(Log.getLogText());
        if (uiRefresher != null) uiRefresher.run();
    }

    private void onFinish() {
        String err = new FinishCommand().execute();
        if (err == null) {
            feedback.appendLine("Simulation finished.");
        } else {
            feedback.appendLine("Finish error: " + err);
        }
    }

    private void runSafely(String label, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, label + " failed: " + ex.getMessage())
                    .showAndWait();
        }
    }
}
