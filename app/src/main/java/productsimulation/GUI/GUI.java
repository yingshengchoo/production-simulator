package productsimulation.GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.State;
import productsimulation.command.FinishCommand;
import productsimulation.command.StepCommand;

import java.net.URL;

public class GUI extends Application {

    private static final int DEFAULT_WIDTH  = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int CONTROL_WIDTH  = 260;

    private State       state;
    private static BoardDisplay  boardDisplay;
    private static FeedbackPane  feedbackPane;
    private static StackPane     rootStack;
    private ControlPanel         controlPanel;

    /** Label to show the current step */
    private Label stepLbl;

    @Override
    public void start(Stage primaryStage) {
        state        = State.getInstance();
        feedbackPane = new FeedbackPane();
        feedbackPane.setContent("Simulation ready.\n");

        // the main drawing area
        boardDisplay = new BoardDisplay(state);

        // move auto-runner (if you like) into the ControlPanel instead of up here
        controlPanel = new ControlPanel(
                boardDisplay,
                feedbackPane,
                this::refreshBoardAndLog
        );
        controlPanel.setMaxWidth(CONTROL_WIDTH);

        // Build the menu bar (always at the very top)
        MenuBar menuBar = buildMenuBar();

        // Build the status label (we'll put it inside the ControlPanel if you want)
        stepLbl = new Label("Step: 0");

        // Layout everything in a BorderPane
        BorderPane main = new BorderPane();
        main.setTop(menuBar);

        // Center: the board
        StackPane canvasHolder = new StackPane(boardDisplay.getCanvasPane());
        canvasHolder.getStyleClass().add("canvas-pane");
        main.setCenter(canvasHolder);

        // Right: your control panel (with whatever buttons / auto‐runner you choose)
        main.setRight(controlPanel);
        BorderPane.setMargin(controlPanel, new Insets(10));

        // Bottom: just the feedback log
        VBox bottomBox = new VBox(feedbackPane);
        bottomBox.setPadding(new Insets(5));
        main.setBottom(bottomBox);

        // Wrap in a StackPane so popups / overlays can go on top
        rootStack = new StackPane(main);
        Scene scene = new Scene(rootStack, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // Load CSS if present
        URL css = getClass().getResource("/productsimulation/GUI/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        else             System.err.println("WARNING: styles.css not found");

        // Keyboard shortcuts
        setupAccelerators(scene);

        primaryStage.setTitle("Production Simulation GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshBoardAndLog();
    }

    private MenuBar buildMenuBar() {
        MenuBar bar = new MenuBar();
        bar.getStyleClass().add("menu-bar");
        bar.getMenus().addAll(
                createFileMenu(),
                createRunMenu(),
                createSettingsMenu()
        );
        return bar;
    }

    private Menu createFileMenu() {
        Menu file = new Menu("File");

        MenuItem save = new MenuItem("Save");
        save.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.S, KeyCombination.CONTROL_DOWN));
        save.setOnAction(e -> SaveWindow.show(this::refreshBoardAndLog));

        MenuItem load = new MenuItem("Load");
        load.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.L, KeyCombination.CONTROL_DOWN));
        load.setOnAction(e -> LoadWindow.show(this::refreshBoardAndLog));

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> System.exit(0));

        file.getItems().addAll(save, load, new SeparatorMenuItem(), exit);
        return file;
    }

    private Menu createRunMenu() {
        Menu run = new Menu("Run");

        MenuItem one = new MenuItem("Go One Step");
        one.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
        one.setOnAction(e -> runOneStep());

        MenuItem many = new MenuItem("Go N Steps");
        many.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.T, KeyCombination.CONTROL_DOWN));
        many.setOnAction(e -> StepWindow.show(this::refreshBoardAndLog));

        MenuItem finish = new MenuItem("Finish");
        finish.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.F, KeyCombination.CONTROL_DOWN));
        finish.setOnAction(e -> {
            String err = new FinishCommand().execute();
            if (err == null) {
                controlPanel.setDisable(true);
                refreshBoardAndLog();
            } else {
                feedbackPane.appendLine("Finish error: " + err);
            }
        });

        run.getItems().addAll(one, many, finish);
        return run;
    }

    private Menu createSettingsMenu() {
        Menu settings = new Menu("Settings");

        MenuItem verbosity = new MenuItem("Set Verbosity…");
        verbosity.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.V, KeyCombination.CONTROL_DOWN));
        verbosity.setOnAction(e ->
                VerbosityWindow.show(level -> {
                    Log.setLogLevel(level);
                    refreshBoardAndLog();
                })
        );

        MenuItem policy = new MenuItem("Set Policy…");
        policy.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.P, KeyCombination.CONTROL_DOWN));
        policy.setOnAction(e ->
                PolicyWindow.show(state, this::refreshBoardAndLog)
        );

        settings.getItems().addAll(verbosity, policy);
        return settings;
    }

    private void setupAccelerators(Scene scene) {
        scene.getAccelerators().put(
                new KeyCodeCombination(
                        javafx.scene.input.KeyCode.R, KeyCombination.CONTROL_DOWN),
                () -> RequestWindow.show(state, this::refreshBoardAndLog)
        );
    }

    private void runOneStep() {
        String err = new StepCommand(1).execute();
        if (err == null) refreshBoardAndLog();
        else            feedbackPane.appendLine("Go One Step error: " + err);
    }

    private void refreshBoardAndLog() {
        // redraw board
        boardDisplay.refresh();
        // reload log
        feedbackPane.setContent(Log.getLogText());
        // update step counter
        stepLbl.setText("Step: " + LogicTime.getInstance().getStep());
    }

    public static BoardDisplay getBoardDisplay() { return boardDisplay; }
    public static StackPane     getRootPane()    { return rootStack;   }
    public static FeedbackPane  getFeedbackPane(){ return feedbackPane;}

    public static void main(String[] args) { launch(args); }
}
