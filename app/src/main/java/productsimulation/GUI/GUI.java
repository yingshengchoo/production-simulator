package productsimulation.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import productsimulation.State;

public class GUI extends Application {

    private State state;
    private BoardDisplay boardDisplay;
    private ControlPanel controlPanel;
    private TextArea feedbackArea;

    @Override
    public void start(Stage primaryStage) {
        state = State.getInstance();

        // A text area at the bottom for user-visible logs or results.
        feedbackArea = new TextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setPrefRowCount(30);

        // The BoardDisplay: draws buildings
        boardDisplay = new BoardDisplay(state);

        // The ControlPanel: has all the command buttons, referencing boardDisplay + feedbackArea
        controlPanel = new ControlPanel(state, boardDisplay, feedbackArea);

        // Layout in a BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(boardDisplay.getCanvasPane());
        root.setRight(controlPanel);
        root.setBottom(feedbackArea);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Production Simulation GUI (Evolution 2)");
        primaryStage.show();

        // Initial refresh
        boardDisplay.refresh();
//        feedbackArea.appendText("Simulation ready. Please select a command.\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
