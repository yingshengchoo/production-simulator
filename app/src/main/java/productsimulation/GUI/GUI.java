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
    private FeedbackPane feedbackPane;

    @Override
    public void start(Stage primaryStage) {
        state = State.getInstance();

        // Custom feedback pane for logs & info
        feedbackPane = new FeedbackPane();
        feedbackPane.setText("Ready.\n");

        // Pass feedbackPane to the BoardDisplay constructor
        boardDisplay = new BoardDisplay(state);

        controlPanel = new ControlPanel(state, boardDisplay, feedbackPane);

        BorderPane root = new BorderPane();
        root.setCenter(boardDisplay.getCanvasPane());
        root.setRight(controlPanel);
        root.setBottom(feedbackPane);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Production Simulation GUI");
        primaryStage.show();

        boardDisplay.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
