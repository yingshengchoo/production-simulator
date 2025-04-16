package productsimulation.GUI;

import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Semi-transparent full-screen overlay with centered instruction text.
 * <p>
 * Overlay is mouse-transparent to allow underlying interaction.
 * Supports fade-in and fade-out transitions.
 * <p>
 * Usage:
 * <pre>
 *   ConnectOverlay overlay = new ConnectOverlay("Click target to connect");
 *   rootPane.getChildren().add(overlay);
 *   // ... later to remove:
 *   overlay.fadeOutAndRemove(rootPane);
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public class ConnectOverlay extends StackPane {
    private final Label instruction;

    /**
     * Creates an overlay with the given message, fading in on display.
     * @param message the instruction message to show
     */
    public ConnectOverlay(String message) {
        // background covers the entire area
        Rectangle background = new Rectangle();
        ObjectProperty<Color> overlayColor = new SimpleObjectProperty<>(Color.rgb(255, 165, 0, 0.5));
        background.fillProperty().bind(overlayColor);
        background.widthProperty().bind(widthProperty());
        background.heightProperty().bind(heightProperty());

        instruction = new Label(message);
        instruction.setStyle("-fx-font-size: 26px; -fx-text-fill: white;");
        instruction.getStyleClass().add("overlay-instruction");
        setAlignment(instruction, Pos.CENTER);

        getChildren().addAll(background, instruction);
        setMouseTransparent(true);

        // fade in
        Duration fadeDuration = Duration.millis(300);
        FadeTransition fadeIn = new FadeTransition(fadeDuration, this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

//    /**
//     * Fades out the overlay and removes it from the parent when finished.
//     * @param parent the container this overlay is added to
//     */
//    public void fadeOutAndRemove(StackPane parent) {
//        FadeTransition fadeOut = new FadeTransition(fadeDuration, this);
//        fadeOut.setFromValue(getOpacity());
//        fadeOut.setToValue(0.0);
//        fadeOut.setOnFinished(e -> parent.getChildren().remove(this));
//        fadeOut.play();
//    }

    /**
     * Updates the overlay message.
     * @param message new instruction text
     */
    public void setMessage(String message) {
        instruction.setText(message);
    }

//    /**
//     * Adjusts the overlay background color.
//     * @param color new overlay fill color
//     */
//    public void setOverlayColor(Color color) {
//        overlayColor.set(Objects.requireNonNull(color, "color"));
//    }
}
