package productsimulation.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import productsimulation.App;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.CommandParser;

public class GUI extends Application {

    // Simulation engine objects.
    private State simulationState;
    private CommandParser commandParser;

    // UI components.
    private BoardDisplay boardDisplay;
    private TextArea logArea;

    // For future updates: if you add current cycle tracking to your simulation engine,
    // you can uncomment and update the cycleLabel below.
    // private Label cycleLabel;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the simulation engine and command parser.
        simulationState = State.getInstance();
        commandParser = new CommandParser();

        // Create the board display adapter.
        boardDisplay = new BoardDisplay(simulationState);

        // For now, we omit the cycle display.
        // If simulationState had a getCurrentCycle() method, you could initialize and update a cycle label.
        // cycleLabel = new Label("Cycle: " + simulationState.getCurrentCycle());
        // cycleLabel.getStyleClass().add("cycle-label");

        // Set up the main layout.
        BorderPane root = new BorderPane();
        // root.setTop(cycleLabel);  // Uncomment this if you add current cycle tracking later.
        root.setCenter(boardDisplay.getCanvasPane());

        // Create the control panel on the right.
        ControlPanel controlPanel = new ControlPanel(commandParser, boardDisplay, simulationState);
        root.setRight(controlPanel);

        // Create and add a log area at the bottom.
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(6);
        root.setBottom(logArea);

        // Create the scene.
        Scene scene = new Scene(root, 1200, 800);
        // Optionally add a stylesheet:
        // scene.getStylesheets().add(getClass().getResource("gui.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Production Simulation");
        primaryStage.show();

        // Use a Timeline to periodically update the UI.
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateUI();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateUI() {
        boardDisplay.refresh();
        // For now, we skip updating the cycle label.
        // If you add a getCurrentCycle(), you could update it here:
        // cycleLabel.setText("Cycle: " + simulationState.getCurrentCycle());
//        logArea.setText(Log.getLogText());
    }

    public static void main(String[] args) {
        // If the -nw flag is specified, launch the textual interface (via App.java).
        for (String arg : args) {
            if (arg.equals("-nw")) {
                App.main(args);
                return;
            }
        }
        launch(args);
    }
}