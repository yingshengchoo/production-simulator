package productsimulation.GUI;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

/**
 * The FeedbackPane class encapsulates a non-editable TextArea wrapped in a ScrollPane.
 * New text appended to the TextArea will automatically scroll to the bottom.
 * This makes it easier to manage and style user feedback.
 */
public class FeedbackPane extends StackPane {
    private final TextArea textArea;
    private final ScrollPane scrollPane;

    public FeedbackPane() {
        // Create a non-editable TextArea.
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Create a ScrollPane that will wrap the TextArea.
        scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        // Always show the vertical scrollbar.
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Add the ScrollPane to this container.
        this.getChildren().add(scrollPane);

        // Add a listener to automatically scroll to the bottom when new text is added.
        textArea.textProperty().addListener((observable, oldText, newText) -> {
            textArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Appends text to the feedback pane and then adds a newline.
     *
     * @param text the text to append.
     */
    public void appendText(String text) {
        textArea.appendText(text + "\n");
    }

    /**
     * Sets the feedback text.
     *
     * @param text the text to set.
     */
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * Retrieves the current feedback text.
     *
     * @return the feedback text.
     */
    public String getText() {
        return textArea.getText();
    }
}