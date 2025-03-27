package productsimulation;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import productsimulation.command.CommandParser;
import productsimulation.command.RequestCommand;
import productsimulation.command.StepCommand;
import productsimulation.model.*;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.*;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.Test;
import productsimulation.setup.SetupParser;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    private String filePathStr = "test.log";
    private Path filePath = Paths.get(filePathStr);

    @BeforeEach
    void setUp() {
        LogicTime.getInstance().reset();
        Request.clearIds();
    }

//    @Test
//    public void minimalE2E() {
//        // step 1: setup
//        // skip parsing the recipe
//        Recipe woodMineRecipe = new Recipe(1, new LinkedHashMap<String, Integer>(), "wood");
//        LinkedHashMap<String, Integer> woodenSwordIngredients = new LinkedHashMap<>();
//        woodenSwordIngredients.put("wood", 2);
//        Recipe woodSwordRecipe = new Recipe(1, woodenSwordIngredients, "wooden_sword");
//        // skip parsing the type
//        Map<String, Recipe> woodMineRecipes = new HashMap<>();
//        woodMineRecipes.put("wood", woodMineRecipe);
//        FactoryType woodMineType = new FactoryType("wood_mine", woodMineRecipes);
//        Map<String, Recipe> woodSwordRecipes = new HashMap<>();
//        woodSwordRecipes.put("wooden_sword", woodSwordRecipe);
//        FactoryType woodSwordType = new FactoryType("sword_factory", woodSwordRecipes);
//        // skip serving policy and request policy
//        SourcePolicy soleSourcePolicy = new SoleSourcePolicy();
//        ServePolicy oneTimeServePolicy = new OneTimeServePolicy();
//        // skip parsing the building
//        Mine woodMine = new Mine("FirstWoodMine", woodMineType,
//                new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);
//        List<Building> sources = new ArrayList<>();
//        sources.add(woodMine);
//        Factory woodSwordFactory = new Factory("FirstWoodenSwordFactory", woodSwordType,
//                sources, soleSourcePolicy, oneTimeServePolicy);
//
//        // step 2: get user prompt and print results
//        // skip parsing the command
//        // skip save/load
//        // skip finish command
//        LogicTime logicTime = LogicTime.getInstance();
//        logicTime.addObservers(woodMine);
//        logicTime.addObservers(woodSwordFactory);
//        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
//        requestBroadcaster.addRecipes(woodMineRecipe);
//        requestBroadcaster.addRecipes(woodSwordRecipe);
//        requestBroadcaster.addBuildings(woodMine);
//        requestBroadcaster.addBuildings(woodSwordFactory);
//
//        RequestCommand requestCommand = new RequestCommand("wooden_sword", "FirstWoodenSwordFactory");
//        requestCommand.execute();
//        StepCommand stepCommand = new StepCommand(1);
//        stepCommand.execute();
//        stepCommand.execute();
//        stepCommand.execute();
//        stepCommand.execute();
//    }

    private String standardizeLineEndings(String content) {
        return content.replace("\r\n", "\n");
    }

    private void testHelper(String testSetupFname, String testInputFname, String testOutputFname) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true);

        InputStream input = getClass().getClassLoader().getResourceAsStream(testInputFname);
        assertNotNull(input);

        InputStream expectedStream = getClass().getClassLoader().getResourceAsStream(testOutputFname);
        assertNotNull(expectedStream);

        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;

        try {
            System.setIn(input);
            System.setOut(out);
            String[] args = { testSetupFname };
            App.main(args);
        }
        finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }

        String expected = standardizeLineEndings(new String(expectedStream.readAllBytes()).trim());
        String actual = standardizeLineEndings(bytes.toString().trim());
        assertEquals(expected, actual);
    }

    private void demoHelper(String setupFileName, String inputFileName) {
        SetupParser parser = new SetupParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(setupFileName);
        assertNotNull(inputStream);
        String error = parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
        assertNull(error);

        App.modelSetup(parser);

        CommandParser cmdParser = new CommandParser();
        assertNotNull(AppTest.class.getResource(inputFileName));
        String filePath = AppTest.class.getResource(inputFileName).getPath();

        try {
            FileReader fileReader = new FileReader(filePath);
            App.readInputCommand(cmdParser, fileReader, false);
        } catch (IOException ignored) {
        }
    }

//    @Test
////    只能gradle test联合运行，不能单独运行
//    public void normalMain() throws IOException {
//        testHelper("json_inputs/doors1.json",
//                "e2e_user_inputs/input1.txt",
//                "e2e_log_outputs/output1.txt");
//    }

    @Test
    public void noSetupMain() throws IOException {
        App.main(new String[]{});
    }

    @Test
    public void wrongSetupMain() throws IOException {
        String[] args = { "non_exist.json" };
        assertThrows(NullPointerException.class, ()->App.main(args));
    }

    @Test
    public void wrongCommandMain() throws IOException {
        CommandParser cmdParser = new CommandParser();
        String predefinedInput = "verbose";
        InputStream inputStream = new ByteArrayInputStream(predefinedInput.getBytes());
        App.readInputCommand(cmdParser, new InputStreamReader(inputStream), true);

        String predefinedInput2 = "set policy request non-exist *";
        InputStream inputStream2 = new ByteArrayInputStream(predefinedInput2.getBytes());
        App.readInputCommand(cmdParser, new InputStreamReader(inputStream2), true);

        String predefinedInput3 = "verbose 1";
        InputStream inputStream3 = new ByteArrayInputStream(predefinedInput3.getBytes());
        App.readInputCommand(cmdParser, new InputStreamReader(inputStream3), true);

        testHelper("json_inputs/doors1.json",
                "e2e_user_inputs/input_wrong.txt",
                "e2e_log_outputs/output_wrong.txt");
    }

//    @Test
//    public void door1Demo() {
//        Log.setLogLevel(3);
//        demoHelper("json_inputs/doors1.json", "/user_inputs/input1.txt");
//    }
//
//    @Test
//    public void door2Demo() {
//        Log.setLogLevel(3);
//        demoHelper("json_inputs/doors2.json", "/user_inputs/input1.txt");
//    }

    @Test
    public void policyDemo() {
        Log.setLogLevel(1);
//        demoHelper("json_inputs/servePolicy.json", "/user_inputs/input_servePolicy1.txt");
        demoHelper("json_inputs/servePolicy.json", "/user_inputs/input_servePolicy2.txt");
        demoHelper("json_inputs/servePolicy.json", "/user_inputs/input_servePolicy3.txt");
//        demoHelper("json_inputs/sourcePolicy.json", "/user_inputs/input_sourcePolicy1.txt");
        demoHelper("json_inputs/sourcePolicy.json", "/user_inputs/input_sourcePolicy2.txt");
        demoHelper("json_inputs/sourcePolicy.json", "/user_inputs/input_sourcePolicy3.txt");
    }

//    private void cleanUpLogFile(Path p) {
//        try {
//            Files.deleteIfExists(p); // 删除旧日志文件
//            Files.createFile(p); // 重新创建日志文件
//            LogManager.shutdown();
//        } catch (IOException e) {
//            System.err.println("Error cleaning up " + filePathStr);
//        }
//    }
//
//    private String getActualLogFromFile(Path p) {
//        try (InputStream actualOutputStream = Files.newInputStream(p)) {
//            return new String(actualOutputStream.readAllBytes(), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            System.err.println("Error when reading " + filePathStr);
//        }
//        return "get actual log failed";
//    }
//
//    private String readResourceFile(String filePath) {
//        StringBuilder content = new StringBuilder();
//        ClassLoader classLoader = AppTest.class.getClassLoader();
//        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        } catch (IOException ignore) {
//        }
//        return content.toString();
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.door1LogLevel0")
////     todo AppTest中所有disable的test，都是同一个原因：gradle test --tests能跑，而gradle test不能跑
//    public void door1LogLevel0() {
////        System.setProperty("logFilename", "door1LogLevel0.log");
////        LoggerContext context = (LoggerContext) LogManager.getContext(false);
////        File file = new File("src/test/resources/log4j2.xml");
////        context.setConfigLocation(file.toURI());
////        cleanUpLogFile(Paths.get("door1LogLevel0.log"));
//        cleanUpLogFile(Paths.get("test.log"));
//
//        Log.setLogLevel(0);
//        demoHelper("json_inputs/doors1.json", "/user_inputs/input1.txt");
////        String actual = getActualLogFromFile(Paths.get("door1LogLevel0.log"));
//        String actual = getActualLogFromFile(Paths.get("test.log"));
//
//        String expectedOutput = readResourceFile("log_outputs/output1.txt");
//        assertEquals(expectedOutput, actual);
//    }
//
//    private void handTestHelper(int logLevel, String setupFileName, String inputFileName, String outputFileName) {
//        Path path = Paths.get("test.log");
//        cleanUpLogFile(path);
//
//        Log.setLogLevel(logLevel);
//        demoHelper(setupFileName, inputFileName);
//        String actual = getActualLogFromFile(path);
//
//        String expectedOutput = readResourceFile(outputFileName);;
//        assertEquals(expectedOutput, actual);
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.door1LogLevel1")
//    public void door1LogLevel1() {
//        handTestHelper(1, "json_inputs/doors1.json",
//                "/user_inputs/input1.txt",
//                "log_outputs/output2.txt");
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.door1LogLevel2")
//    public void door1LogLevel2() {
//        handTestHelper(2, "json_inputs/doors1.json",
//                "/user_inputs/input1.txt",
//                "log_outputs/output3.txt");
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.servePolicy1")
//    public void servePolicy1() {
//        handTestHelper(1, "json_inputs/servePolicy.json",
//                "/user_inputs/input_servePolicy1.txt",
//                "log_outputs/output_servePolicy1.txt");
//    }
//    @Test
//    @Disabled("gradle clean test --tests AppTest.servePolicy2")
//    public void servePolicy2() {
//        handTestHelper(1, "json_inputs/servePolicy.json",
//                "/user_inputs/input_servePolicy2.txt",
//                "log_outputs/output_servePolicy2.txt");
//    }
//
//    @Test
//    @Disabled("wgradle clean test --tests AppTest.servePolicy3")
//    public void servePolicy3() {
//        handTestHelper(1, "json_inputs/servePolicy.json",
//                "/user_inputs/input_servePolicy3.txt",
//                "log_outputs/output_servePolicy3.txt");
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.sourcePolicy1")
//    public void sourcePolicy1() {
//        handTestHelper(1, "json_inputs/sourcePolicy.json",
//                "/user_inputs/input_sourcePolicy1.txt",
//                "log_outputs/output_sourcePolicy1.txt");
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.sourcePolicy2")
//    public void sourcePolicy2() {
//        handTestHelper(1, "json_inputs/sourcePolicy.json",
//                "/user_inputs/input_sourcePolicy2.txt",
//                "log_outputs/output_sourcePolicy2.txt");
//    }
//
//    @Test
//    @Disabled("gradle clean test --tests AppTest.sourcePolicy3")
//    public void sourcePolicy3() {
//        handTestHelper(1, "json_inputs/sourcePolicy.json",
//                "/user_inputs/input_sourcePolicy3.txt",
//                "log_outputs/output_sourcePolicy3.txt");
//    }
}
