package productsimulation.GUI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.function.IntSupplier;

/**
 * Utility to run steps automatically at a variable speed.
 */
public final class AutoRunner {
    private static Timeline timeline;

    private AutoRunner() { /* no instances */ }

    /**
     * Starts automatic execution of the given action at the rate provided by stepsPerSecond.
     */
    public static void start(IntSupplier stepsPerSecond, Runnable action) {
        stop();
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1000.0 / stepsPerSecond.getAsInt()), e -> action.run())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /** Stops any ongoing automatic execution. */
    public static void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }
}