package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import productsimulation.Log;
import productsimulation.command.FinishCommand;
import productsimulation.command.StepCommand;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ControlPanel hosts all simulation command buttons, each annotated with its Ctrl+ shortcut.
 * Buttons:
 * <ul>
 *   <li>Add Building</li>
 *   <li>Connect Buildings (Ctrl+C)</li>
 *   <li>Request Item (Ctrl+R)</li>
 *   <li>Go One Step (Ctrl+1)</li>
 *   <li>Go N Steps (Ctrl+T)</li>
 *   <li>Finish (Ctrl+F)</li>
 *   <li>Load (Ctrl+L)</li>
 *   <li>Save (Ctrl+S)</li>
 *   <li>Set Verbosity (Ctrl+V)</li>
 *   <li>Set Policy (Ctrl+P)</li>
 *   <li>Show Log</li>
 * </ul>
 * After each action, the board and feedback pane refresh; on Finish, all buttons disable.
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public class ControlPanel extends VBox {
    private final FeedbackPane feedback;
    BoardDisplay boardDisplay;
    public ControlPanel(BoardDisplay boardDisplay, FeedbackPane feedback) {
        this.feedback     = feedback;
        this.boardDisplay = boardDisplay;
        setPadding(new Insets(15));
        setSpacing(12);

        Map<String, Runnable> actions = new LinkedHashMap<>();
//        actions.put("Add Building",      () -> AddBuildingWindow.show(state, this::postUpdate));
//        actions.put("Connect Buildings", () -> ConnectWindow.show(state, this::postUpdate));
//        actions.put("Request Item",      () -> RequestWindow.show(state, this::postUpdate));
        actions.put("Go One Step",       () -> {
            String err = new StepCommand(1).execute();
            if (err == null) postUpdate();
            else showError("Go One Step error: " + err);
        });
        actions.put("Go N Steps",        () -> StepWindow.show(this::postUpdate));
        actions.put("Finish",            this::onFinish);
//        actions.put("Load",              () -> LoadWindow.show(this::postUpdate));
//        actions.put("Save",              () -> SaveWindow.show(this::postUpdate));
//        actions.put("Set Verbosity",     () -> VerbosityWindow.show(level -> {
//            Log.setLogLevel(level);
//            postUpdate();
//        }));
//        actions.put("Set Policy",        () -> PolicyWindow.show(state, this::postUpdate));
        actions.put("Show Log",          () -> feedback.appendLine(Log.getLogText()));

        Map<String, String> shortcuts = Map.of(
                "Connect Buildings", "Ctrl+C",
                "Request Item",      "Ctrl+R",
                "Go One Step",       "Ctrl+1",
                "Go N Steps",        "Ctrl+T",
                "Finish",            "Ctrl+F",
                "Load",              "Ctrl+L",
                "Save",              "Ctrl+S",
                "Set Verbosity",     "Ctrl+V",
                "Set Policy",        "Ctrl+P"
        );

        actions.forEach((label, action) -> {
            String hint = shortcuts.get(label);
            String text = (hint != null) ? String.format("%s (%s)", label, hint) : label;
            Button btn = new Button(text);
            btn.getStyleClass().add("control-button");
            btn.setMaxWidth(Double.MAX_VALUE);
            if (hint != null) {
                btn.setTooltip(new Tooltip("Shortcut: " + hint));
            }
            btn.setOnAction(e -> runAction(label, action));
            getChildren().add(btn);
        });
    }

    private void runAction(String label, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            showError(label + " failed: " + ex.getMessage());
        }
    }

    private void postUpdate() {
        boardDisplay.refresh();
        feedback.setContent(Log.getLogText());
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
