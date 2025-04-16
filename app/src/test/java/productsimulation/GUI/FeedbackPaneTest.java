package productsimulation.GUI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackPaneTest extends ApplicationTest {

    private FeedbackPane pane;

    @BeforeAll
    public static void setupSpec() {
        // Initialize JavaFX toolkit
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("java.awt.headless", "true");
    }

    @Override
    public void start(Stage stage) {
        pane = new FeedbackPane();
    }


    @Test
    public void setText_shouldReplaceContent() {
        interact(() -> {
            pane.setText("Initial text");
            pane.setText("New text");
        });
        assertEquals("New text", pane.getText());
    }
}