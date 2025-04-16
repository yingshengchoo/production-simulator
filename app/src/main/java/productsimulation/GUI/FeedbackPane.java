package productsimulation.GUI;

import javafx.scene.control.TextArea;

/**
 * FeedbackPane is a styled, read‑only text area for simulation logs.
 * <ul>
 *   <li>Non-editable, word‑wrapped</li>
 *   <li>Auto-scrolls when content changes</li>
 * </ul>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public class FeedbackPane extends TextArea {
    public FeedbackPane() {
        super();
        setEditable(false);
        setWrapText(true);
        getStyleClass().add("feedback-pane");
    }

    /** Replaces all content and scrolls to bottom. */
    public void setContent(String text) {
        setText(text);
        positionCaret(getText().length());
    }

    /** Appends a line and scrolls down. */
    public void appendLine(String line) {
        appendText(line + System.lineSeparator());
        positionCaret(getText().length());
    }
}
