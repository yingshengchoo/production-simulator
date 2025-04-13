package productsimulation.GUI;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.junit.jupiter.api.Assertions.*;

import productsimulation.State;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Recipe;
import productsimulation.model.BuildingType;
import productsimulation.model.Storage;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.RequestBroadcaster;
import productsimulation.LogicTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RobotGUITest extends ApplicationTest {

    private State state;
    private BoardDisplay boardDisplay;
    private ControlPanel controlPanel;
    private FeedbackPane feedbackPane; // Custom pane that extends TextArea and supports scrolling

    @Override
    public void start(Stage stage) throws Exception {
        state = createDummyState();

        boardDisplay = new BoardDisplay(state);
        feedbackPane = new FeedbackPane();
        controlPanel = new ControlPanel(state, boardDisplay, feedbackPane);

        BorderPane root = new BorderPane();
        root.setCenter(boardDisplay.getCanvasPane());
        root.setRight(controlPanel);
        root.setBottom(feedbackPane);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Production Simulation GUI Test");
        stage.show();

        boardDisplay.refresh();
    }

    /**
     * Creates a dummy state with several recipes, building types, and buildings.
     */
    public static State createDummyState() {
        Recipe door = new Recipe(10, Map.of("wood", 1, "handle", 1, "hinge", 3), "door");
        Recipe handle = new Recipe(5, Map.of("metal", 1), "handle");
        Recipe hinge = new Recipe(1, Map.of("metal", 1), "hinge");
        Recipe wood = new Recipe(1, Collections.emptyMap(), "wood");
        Recipe metal = new Recipe(1, Collections.emptyMap(), "metal");

        List<Recipe> recipeList = new ArrayList<>();
        recipeList.add(door);
        recipeList.add(handle);
        recipeList.add(hinge);
        recipeList.add(wood);
        recipeList.add(metal);

        BuildingType doorFactory = new BuildingType("Door Factory", Map.of("door", door));
        BuildingType componentFactory = new BuildingType("Component Factory", Map.of("handle", handle, "hinge", hinge));
        BuildingType woodMineType = new BuildingType("wood", Map.of("wood", wood));
        BuildingType metalMineType = new BuildingType("metal", Map.of("metal", metal));

        List<BuildingType> typeList = new ArrayList<>();
        typeList.add(doorFactory);
        typeList.add(componentFactory);
        typeList.add(woodMineType);
        typeList.add(metalMineType);

        Factory factoryA = new Factory("Factory A", doorFactory, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(10, 10));
        Factory compFactoryA = new Factory("Component Factory A", componentFactory, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(8, 9));
        Mine woodMine = new Mine("Wood Mine", woodMineType, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(5, 10));
        Mine metalMine = new Mine("Metal Mine", metalMineType, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(4, 11));

        factoryA.setSources(List.of(woodMine, compFactoryA));
        compFactoryA.setSources(List.of(metalMine));
        woodMine.setSources(Collections.emptyList());
        metalMine.setSources(Collections.emptyList());

        List<Building> buildingList = new ArrayList<>();
        buildingList.add(factoryA);
        buildingList.add(compFactoryA);
        buildingList.add(woodMine);
        buildingList.add(metalMine);

        RequestBroadcaster rb = RequestBroadcaster.getInstance();
        LogicTime lt = LogicTime.getInstance();

        State.initialize(buildingList, typeList, recipeList, rb, lt);
        return State.getInstance();
    }

    @Test
    public void testControlPanelButtonsVisible() {
        verifyThat("Connect Buildings", isVisible());
        verifyThat("Request Item", isVisible());
        verifyThat("Step", isVisible());
        verifyThat("Finish", isVisible());
        verifyThat("Load", isVisible());
        verifyThat("Save", isVisible());
        verifyThat("Set Verbosity", isVisible());
        verifyThat("Set Policy", isVisible());
    }





}
