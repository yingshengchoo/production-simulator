package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.FinishCommand;
import productsimulation.command.StepCommand;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Control panel down the right-hand side with quick-access buttons.
 *
 * @author Taiyan
 * @version 1.3 – removed Connect/Disconnect buttons
 */
public class ControlPanel extends VBox {

    private final BoardDisplay boardDisplay;
    private final FeedbackPane feedback;
    private final Runnable uiRefresher;

    public ControlPanel(BoardDisplay boardDisplay,
                        FeedbackPane feedback,
                        Runnable uiRefresher) {

        super(12);
        setPadding(new Insets(15));

        this.boardDisplay = boardDisplay;
        this.feedback     = feedback;
        this.uiRefresher  = uiRefresher;

        State state = State.getInstance();

        /* ---------- label → action map (display order) ---------------- */
        Map<String,Runnable> actions = new LinkedHashMap<>();

        actions.put("Add Building",
                () -> AddBuildingWindow.show(state, this::postUpdate));

        actions.put("Go One Step", () -> {
            String err = new StepCommand(1).execute();
            if (err == null) postUpdate();
            else showError("Go One Step error: " + err);
        });

        actions.put("Go N Steps", () ->
                StepWindow.show(this::postUpdate));

        actions.put("Finish", this::onFinish);

        /* -------------------------------------------------------------- */
        Map<String,String> shortcuts = Map.of(
                "Go One Step", "Ctrl+1",
                "Go N Steps",  "Ctrl+T",
                "Finish",      "Ctrl+F"
        );

        actions.forEach((label,action) -> {
            String hint = shortcuts.get(label);
            String text = (hint == null) ? label : label + "  ("+hint+')';

            Button btn = new Button(text);
            btn.setMaxWidth(Double.MAX_VALUE);
            if (hint != null) btn.setTooltip(new Tooltip("Shortcut: "+hint));
            btn.getStyleClass().add("control-button");

            btn.setOnAction(e -> runSafely(label, action));
            getChildren().add(btn);
        });
    }

    /* ---------------- helpers --------------------------------------- */
    private void runSafely(String label, Runnable r) {
        try { r.run(); }
        catch (Exception ex) { showError(label + " failed: " + ex.getMessage()); }
    }

    private void postUpdate() {
        boardDisplay.refresh();
        feedback.setContent(Log.getLogText());
        if (uiRefresher != null) uiRefresher.run();
    }

    private void onFinish() {
        String err = new FinishCommand().execute();
        if (err == null) {
            postUpdate();
            getChildren().forEach(n -> n.setDisable(true));
        } else {
            showError("Finish error: " + err);
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
