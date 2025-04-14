package productsimulation.GUI;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.RequestBroadcaster;
import productsimulation.LogicTime;
import productsimulation.model.*;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GUIBasicTest extends ApplicationTest {

    private GUI guiInstance;

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

    @BeforeAll
    static void setupHeadless() {
        // Set system properties for headless JavaFX
        System.setProperty("java.awt.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize state using the dummy state helper.
        // The dummy method creates recipes, building types, and buildings.
        State dummyState = createDummyState();
        // Launch the GUI using the dummy state.
        guiInstance = new GUI();
        guiInstance.start(stage);
    }

    @Test
    void testBoardDisplayExists() {
        BoardDisplay bd = GUI.getBoardDisplay();
        assertNotNull(bd, "BoardDisplay must be initialized by GUI.start");
    }

    @Test
    void testRootPaneIsStackPane() {
        StackPane root = GUI.getRootPane();
        assertNotNull(root, "Root pane must not be null");
        assertTrue(root instanceof StackPane, "Root pane should be a StackPane");
    }

    @Test
    void testFeedbackPaneExists() {
        FeedbackPane fp = GUI.getFeedbackPane();
        assertNotNull(fp, "FeedbackPane must be initialized");
        // Verify that initial text mentions 'Simulation ready'
        String text = fp.getText();
        assertTrue(text.contains("Simulation ready"), "FeedbackPane should contain 'Simulation ready'");
    }

    @Test
    void testControlPanelReflectively() throws Exception {
        // Use reflection to assert that the private controlPanel field in GUI is set.
        Field cpField = GUI.class.getDeclaredField("controlPanel");
        cpField.setAccessible(true);
        Object cpObj = cpField.get(guiInstance);
        assertNotNull(cpObj, "ControlPanel field must be initialized");
    }
}