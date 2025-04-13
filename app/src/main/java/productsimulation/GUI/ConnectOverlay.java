package productsimulation.GUI;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * ConnectOverlay creates a semi-transparent overlay with an instruction message.
 * The overlay is mouse transparent so that underlying nodes receive mouse events.
 */
public class ConnectOverlay extends StackPane {

    /**
     * Constructs the overlay.
     *
     * @param message the instruction message to display.
     */
    public ConnectOverlay(String message) {
        Rectangle background = new Rectangle();
        background.setFill(Color.rgb(255, 165, 0, 0.5)); // Semi-transparent orange
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());

        Label instruction = new Label(message);
        instruction.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        setAlignment(instruction, Pos.CENTER);

        getChildren().addAll(background, instruction);

        // Allow mouse events to pass through.
        setMouseTransparent(true);

        FadeTransition ft = new FadeTransition(Duration.millis(300), this);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
}
