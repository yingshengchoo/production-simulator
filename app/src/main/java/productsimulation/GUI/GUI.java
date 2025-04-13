package productsimulation.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import productsimulation.State;

/**
 * GUI initializes the graphical user interface.
 * The main content is placed in a BorderPane that is wrapped by a StackPane.
 * The StackPane is used to display overlays (such as the interactive connect overlay).
 */
public class GUI extends Application {

    private static BoardDisplay boardDisplayInstance;
    private static StackPane rootStackPane;
    private static FeedbackPane feedbackPane;
    private State state;
    private ControlPanel controlPanel;

    @Override
    public void start(Stage primaryStage) {
        state = State.getInstance();

        feedbackPane = new FeedbackPane();
        feedbackPane.setText("Simulation ready.\n");

        boardDisplayInstance = new BoardDisplay(state, feedbackPane);
        controlPanel = new ControlPanel(state, boardDisplayInstance, feedbackPane);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(boardDisplayInstance.getCanvasPane());
        mainPane.setRight(controlPanel);
        mainPane.setBottom(feedbackPane);

        rootStackPane = new StackPane();
        rootStackPane.getChildren().add(mainPane);

        Scene scene = new Scene(rootStackPane, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Production Simulation GUI");
        primaryStage.show();

        boardDisplayInstance.refresh();
    }

    /**
     * Returns the active BoardDisplay instance.
     *
     * @return the BoardDisplay used in the GUI.
     */
    public static BoardDisplay getBoardDisplay() {
        return boardDisplayInstance;
    }

    /**
     * Returns the root pane (a StackPane) for overlays.
     *
     * @return the root StackPane.
     */
    public static StackPane getRootPane() {
        return rootStackPane;
    }

    /**
     * Returns the FeedbackPane for displaying log text.
     *
     * @return the current FeedbackPane.
     */
    public static FeedbackPane getFeedbackPane() {
        return feedbackPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
