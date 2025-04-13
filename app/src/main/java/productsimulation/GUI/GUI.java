package productsimulation.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import productsimulation.State;

public class GUI extends Application {

    private State state;
    private BoardDisplay boardDisplay;
    private ControlPanel controlPanel;
    private FeedbackPane feedbackPane;  // Use our custom feedback pane

    @Override
    public void start(Stage primaryStage) {
        state = State.getInstance();

        // Create the board display.
        boardDisplay = new BoardDisplay(state);

        // Create the custom feedback pane.
        feedbackPane = new FeedbackPane();
        feedbackPane.setText("Simulation ready. Please select a command.\n");

        // Create the control panel, passing the feedbackPane for updates.
        controlPanel = new ControlPanel(state, boardDisplay, feedbackPane);

        // Arrange everything in a BorderPane.
        BorderPane root = new BorderPane();
        root.setCenter(boardDisplay.getCanvasPane());
        root.setRight(controlPanel);
        root.setBottom(feedbackPane);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Production Simulation GUI (Evolution 2)");
        primaryStage.show();

        boardDisplay.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}