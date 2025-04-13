package productsimulation.GUI;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import productsimulation.State;
import productsimulation.command.ConnectCommand;
import productsimulation.model.Building;

/**
 * InteractiveConnectMode enables a one-time interactive connection mode.
 * An overlay is added to the root pane and a one-time mouse-click event filter is installed on the board's canvas.
 * When a valid target building (different from the source) is clicked, a connection command is executed.
 * After execution, a provided callback is invoked to update the UI (for example, updating the log in the FeedbackPane).
 */
public class InteractiveConnectMode {

    private static boolean active = false;

    /**
     * Returns whether interactive connect mode is active.
     *
     * @return true if active; false otherwise.
     */
    public static boolean isActive() {
        return active;
    }

    /**
     * Activates interactive connect mode.
     *
     * An overlay is added onto the specified root pane and a one-time mouse-click event filter is added to the board's canvas.
     * The next click on a target building (different from the source) executes a ConnectCommand.
     * Afterwards, the provided onConnectComplete callback is run.
     *
     * @param sourceBuildingName the name of the source building.
     * @param state the current simulation state.
     * @param boardDisplay the BoardDisplay instance whose canvas is used for click detection.
     * @param rootPane the root pane (a StackPane) to which the overlay is added.
     * @param onConnectComplete a Runnable callback executed after the connection command.
     */
    public static void activate(String sourceBuildingName, State state, BoardDisplay boardDisplay, Pane rootPane, Runnable onConnectComplete) {
        active = true;
        ConnectOverlay overlay = new ConnectOverlay("Click on the target building to connect.");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(overlay);

        Node canvas = boardDisplay.getCanvasPane();

        EventHandler<MouseEvent> clickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Building target = boardDisplay.findBuilding(event.getX(), event.getY());
                if (target != null && !target.getName().equals(sourceBuildingName)) {
                    ConnectCommand cmd = new ConnectCommand(sourceBuildingName, target.getName());
                    cmd.execute(); // Backend logs connection details.
                    event.consume();
                    canvas.removeEventFilter(MouseEvent.MOUSE_CLICKED, this);
                    rootPane.getChildren().remove(overlay);
                    active = false;
                    if (onConnectComplete != null) {
                        onConnectComplete.run();
                    }
                }
            }
        };

        canvas.setCursor(Cursor.HAND);
        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, clickFilter);
    }
}
