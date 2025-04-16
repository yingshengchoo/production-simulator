package productsimulation.GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.FinishCommand;
import productsimulation.command.StepCommand;

import java.net.URL;

/**
 * Main entry point for the production simulation GUI.
 * <p>
 * Layout:
 * <ul>
 *   <li>Top:    MenuBar (File, Run, Settings)</li>
 *   <li>Center: Canvas wrapped in styled StackPane</li>
 *   <li>Right:  ControlPanel</li>
 *   <li>Bottom: FeedbackPane</li>
 * </ul>
 * Ctrl‑based shortcuts everywhere.
 *
 * @author Taiyan Liu
 * @version 1.0
 * @since 1.0
 */
public class GUI extends Application {
    private static final int DEFAULT_WIDTH  = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int CONTROL_WIDTH  = 260;

    private State        state;
    private static BoardDisplay  boardDisplay;
    private static FeedbackPane  feedbackPane;
    private static StackPane     rootStack;
    private ControlPanel         controlPanel;

    @Override
    public void start(Stage primaryStage) {
        state        = State.getInstance();
        feedbackPane = new FeedbackPane();
        feedbackPane.setContent("Simulation ready.\n");

        boardDisplay = new BoardDisplay(state);
        controlPanel = new ControlPanel(boardDisplay, feedbackPane);
        controlPanel.setMaxWidth(CONTROL_WIDTH);

        MenuBar menuBar = buildMenuBar();
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(menuBar);

        StackPane canvasContainer = new StackPane(boardDisplay.getCanvasPane());
        canvasContainer.getStyleClass().add("canvas-pane");
        mainPane.setCenter(canvasContainer);

        mainPane.setRight(controlPanel);
        BorderPane.setMargin(controlPanel, new Insets(10));
        mainPane.setBottom(feedbackPane);
        BorderPane.setMargin(feedbackPane, new Insets(5));

        rootStack = new StackPane(mainPane);

        Scene scene = new Scene(rootStack, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        URL css = getClass().getResource("/productsimulation/GUI/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("WARNING: styles.css not found on classpath");
        }

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

        MenuItem goOne = new MenuItem("Go One Step");
        goOne.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
        goOne.setOnAction(e -> {
            String err = new StepCommand(1).execute();
            if (err == null) refreshBoardAndLog();
            else feedbackPane.appendLine("Go One Step error: " + err);
        });

        MenuItem goN = new MenuItem("Go N Steps");
        goN.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.T, KeyCombination.CONTROL_DOWN));
        goN.setOnAction(e -> StepWindow.show(this::refreshBoardAndLog));

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

        run.getItems().addAll(goOne, goN, finish);
        return run;
    }

    private Menu createSettingsMenu() {
        Menu settings = new Menu("Settings");
        MenuItem verbosity = new MenuItem("Set Verbosity...");
        verbosity.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.V, KeyCombination.CONTROL_DOWN));
        verbosity.setOnAction(e -> VerbosityWindow.show(level -> {
            Log.setLogLevel(level);
            refreshBoardAndLog();
        }));

        MenuItem policy = new MenuItem("Set Policy...");
        policy.setAccelerator(new KeyCodeCombination(
                javafx.scene.input.KeyCode.P, KeyCombination.CONTROL_DOWN));
        policy.setOnAction(e -> PolicyWindow.show(state, this::refreshBoardAndLog));

        settings.getItems().addAll(verbosity, policy);
        return settings;
    }

    private void setupAccelerators(Scene scene) {
        scene.getAccelerators().put(
                new KeyCodeCombination(
                        javafx.scene.input.KeyCode.R, KeyCombination.CONTROL_DOWN),
                () -> RequestWindow.show(state, this::refreshBoardAndLog)
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(
                        javafx.scene.input.KeyCode.C, KeyCombination.CONTROL_DOWN),
                () -> InteractiveConnectMode.activate(
                        null, state, boardDisplay, rootStack, this::refreshBoardAndLog
                )
        );
    }

    private void refreshBoardAndLog() {
        boardDisplay.refresh();
        feedbackPane.setContent(Log.getLogText());
    }

    public static BoardDisplay getBoardDisplay() { return boardDisplay; }
    public static StackPane   getRootPane()      { return rootStack;    }
    public static FeedbackPane getFeedbackPane() { return feedbackPane; }

    public static void main(String[] args) {
        launch(args);
    }
}
