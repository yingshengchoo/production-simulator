package productsimulation;

import org.junit.jupiter.api.BeforeEach;
import productsimulation.command.CommandParser;
import productsimulation.command.RequestCommand;
import productsimulation.command.StepCommand;
import productsimulation.model.*;
import productsimulation.request.OneTimeServePolicy;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.*;
import productsimulation.request.sourcePolicy.SoleSourcePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.Test;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.setup.SetupParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {
    private String filePathStr = "test.log";
//    private String filePathStr = "~/log/myapp/test.log";
    private Path filePath = Paths.get(filePathStr);

    private void cleanUpLogFile() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
            Files.write(filePath, "".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error when writing to" + filePathStr);
        }
    }

    private String getActualLogFromFile() {
        try (InputStream actualOutputStream = Files.newInputStream(filePath)) {
            return new String(actualOutputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error when reading " + filePathStr);
        }
        return "get actual log failed";
    }

    @BeforeEach
    void setUp() {
        LogicTime.getInstance().reset();
        Request.clearIds();
    }

    private String readResourceFile(String filePath) {
        StringBuilder content = new StringBuilder();
        ClassLoader classLoader = AppTest.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ignore) {
        }
        return content.toString();
    }

    @Test
    public void minimalE2E() {
        // step 1: setup
        // skip parsing the recipe
        Recipe woodMineRecipe = new Recipe(1, new LinkedHashMap<String, Integer>(), "wood");
        LinkedHashMap<String, Integer> woodenSwordIngredients = new LinkedHashMap<>();
        woodenSwordIngredients.put("wood", 2);
        Recipe woodSwordRecipe = new Recipe(1, woodenSwordIngredients, "wooden_sword");
        // skip parsing the type
        Map<String, Recipe> woodMineRecipes = new HashMap<>();
        woodMineRecipes.put("wood", woodMineRecipe);
        FactoryType woodMineType = new FactoryType("wood_mine", woodMineRecipes);
        Map<String, Recipe> woodSwordRecipes = new HashMap<>();
        woodSwordRecipes.put("wooden_sword", woodSwordRecipe);
        FactoryType woodSwordType = new FactoryType("sword_factory", woodSwordRecipes);
        // skip serving policy and request policy
        SourcePolicy soleSourcePolicy = new SoleSourcePolicy();
        ServePolicy oneTimeServePolicy = new OneTimeServePolicy();
        // skip parsing the building
        Mine woodMine = new Mine("FirstWoodMine", woodMineType,
                new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);
        List<Building> sources = new ArrayList<>();
        sources.add(woodMine);
        Factory woodSwordFactory = new Factory("FirstWoodenSwordFactory", woodSwordType,
                sources, soleSourcePolicy, oneTimeServePolicy);

        // step 2: get user prompt and print results
        // skip parsing the command
        // skip save/load
        // skip finish command
        LogicTime logicTime = LogicTime.getInstance();
        logicTime.addObservers(woodMine);
        logicTime.addObservers(woodSwordFactory);
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        requestBroadcaster.addRecipes(woodMineRecipe);
        requestBroadcaster.addRecipes(woodSwordRecipe);
        requestBroadcaster.addBuildings(woodMine);
        requestBroadcaster.addBuildings(woodSwordFactory);

        RequestCommand requestCommand = new RequestCommand("wooden_sword", "FirstWoodenSwordFactory");
        requestCommand.execute();
        StepCommand stepCommand = new StepCommand(1);
        stepCommand.execute();
        stepCommand.execute();
        stepCommand.execute();
        stepCommand.execute();
    }

    private void demoHelper(String setupFileName, String inputFileName) {
        SetupParser parser = new SetupParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(setupFileName);
        assertNotNull(inputStream);
        parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
        Map<String, Recipe> recipes = parser.getRecipeMap();
        Map<String, Building> buildings = parser.getBuildingMap();

        LogicTime logicTime = LogicTime.getInstance();
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        SourcePolicy sourcePolicy = new SourceQLen();
        ServePolicy servePolicy = new FIFOPolicy();

        for(Building b: buildings.values()) {
            b.changeSourcePolicy(sourcePolicy);
            b.changeServePolicy(servePolicy);

            logicTime.addObservers(b);
            requestBroadcaster.addBuildings(b);
        }

        for(Recipe r: recipes.values()) {
            requestBroadcaster.addRecipes(r);
        }

        CommandParser cmdParser = new CommandParser();
        assertNotNull(AppTest.class.getResource(inputFileName));
        String filePath = AppTest.class.getResource(inputFileName).getPath();

        try {
            FileReader fileReader = new FileReader(filePath);
            App.readInputCommand(cmdParser, fileReader, false);
        } catch (IOException ignored) {
        }
    }

    @Test
    public void door1Demo() {
        Log.setLogLevel(3);
        demoHelper("json_inputs/doors1.json", "/user_inputs/input1.txt");
    }

    @Test
    public void door2Demo() {
        Log.setLogLevel(3);
        demoHelper("json_inputs/doors2.json", "/user_inputs/input1.txt");
    }

//    @Test
//    public void door1LogLevel0() {
//        Log.setLogLevel(0);
//
//        cleanUpLogFile();
//        demoHelper("json_inputs/doors1.json", "/user_inputs/input1.txt");
//        Log.debugLog("casual test 0");
//        String actual = getActualLogFromFile();
//
//        String expectedOutput = readResourceFile("log_outputs/output1.txt");
//        assertEquals(expectedOutput, actual);
//    }
//
//    @Test
//    public void door1LogLevel1() {
//        Log.setLogLevel(1);
//
//        cleanUpLogFile();
//        demoHelper("json_inputs/doors1.json", "/user_inputs/input1.txt");
//        Log.debugLog("casual test 1");
//        String actual = getActualLogFromFile();
//
//        String expectedOutput = readResourceFile("log_outputs/output2.txt");;
//        assertEquals(expectedOutput, actual);
//    }
}