package productsimulation.GUI;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import productsimulation.Log;
import productsimulation.command.DisconnectCommand;
import productsimulation.model.Building;
import productsimulation.model.road.Road;
import productsimulation.State;
import javafx.util.Pair;

/**
 * InteractiveDisconnectMode enables a one-time interactive disconnect mode,
 * mirroring Connect behavior: shows a full-screen instruction overlay,
 * then lets the user click to sever a connection, consuming the click so
 * the BuildingInfoWindow does not appear.
 */
public class InteractiveDisconnectMode {
    private static boolean active = false;

    /**
     * Returns whether disconnect mode is active.
     */
    public static boolean isActive() {
        return active;
    }

    /**
     * Activates disconnect mode. Displays an overlay instructing the user,
     * then lets them click a target building to sever the existing connection.
     * If no connection exists, a warning is shown. The click is consumed to
     * prevent the normal BuildingInfoWindow from popping up.
     * @param sourceName the name of the source building
     * @param state      simulation state
     * @param board      the BoardDisplay to detect building clicks
     * @param rootPane   the root StackPane to host the overlay
     * @param onComplete callback to refresh UI after action
     */
    public static void activate(
            String sourceName,
            State state,
            BoardDisplay board,
            StackPane rootPane,
            Runnable onComplete) {
        if (active) return;
        active = true;

        // Show instruction overlay
        ConnectOverlay overlay = new ConnectOverlay("Click on the target building to disconnect.");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(overlay);

        // Change cursor
        board.getCanvasPane().setCursor(Cursor.HAND);

        // Single-click handler
        EventHandler<MouseEvent> clickHandler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                // consume the event immediately to prevent BuildingInfoWindow
                event.consume();

                Building target = board.findBuilding(event.getX(), event.getY());
                // Clean up
                board.getCanvasPane().removeEventFilter(MouseEvent.MOUSE_CLICKED, this);
                rootPane.getChildren().remove(overlay);
                board.getCanvasPane().setCursor(Cursor.DEFAULT);
                active = false;

                if (target == null || target.getName().equals(sourceName)) {
                    // nothing to disconnect
                } else {
                    // Check existing connection
                    Building src = state.getBuildings(sourceName);
                    boolean exists = state.getBuildings(sourceName) != null
                            && state.getBuildings(sourceName).equals(src)
                            && Road.roadMap.containsKey(new Pair<>(src, target));
                    if (!exists) {
                        Alert alert = new Alert(AlertType.WARNING,
                                "No existing connection from '" + sourceName + "' to '" + target.getName() + "'."
                        );
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    } else {
                        String err = new DisconnectCommand(sourceName, target.getName()).execute();
                        if (err != null && !err.isBlank()) {
                            GUI.getFeedbackPane().appendLine(err);
                        }
                    }
                }
                // Refresh the board, feedback, and status bar
                board.refresh();
                GUI.getFeedbackPane().setContent(Log.getLogText());
                GUI.getRootPane().requestLayout();
                if (onComplete != null) onComplete.run();
            }
        };

        board.getCanvasPane().addEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);
    }
}
