package productsimulation;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import productsimulation.command.CommandParser;
import productsimulation.command.RequestCommand;
import productsimulation.command.StepCommand;
import productsimulation.model.*;
import productsimulation.request.Request;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.Test;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.setup.SetupParser;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    private String filePathStr = "test.log";
    private Path filePath = Paths.get(filePathStr);

    private void cleanUpLogFile(Path p) throws IOException {
        Files.deleteIfExists(p); // 删除旧日志文件
        Files.createFile(p); // 重新创建日志文件
    }

    @BeforeEach
    void setUp() throws IOException {
        LogicTime.getInstance().reset();
        Request.clearIds();
        cleanUpLogFile(filePath);
    }

    @AfterEach
    void tearDown() {
        // 确保日志完全刷新和关闭
        LogManager.shutdown();
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

    private String standardizeLineEndings(String content) {
        return content
                .replace("\r\n", "\n")  // Windows风格换行
                .replace("\r", "\n")    // 老Mac风格换行
                .replaceAll("\\h+$", ""); // 移除每行末尾的水平空白字符
    }

    private void testHelper(String testSetupFname, String testInputFname, String testOutputFname) throws IOException {
        testHelper(testSetupFname, testInputFname, testOutputFname, false);
    }
    private void testHelper(String testSetupFname,
                            String testInputFname,
                            String testOutputFname,
                            boolean isQuickTest) throws IOException {
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
            String[] args = { "-nw", testSetupFname }; //for testing command line only
            App.main(args);
        }
        finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }

        if (isQuickTest) {
            return;
        }

//        这段代码方便我构造expectedOutput时复制粘贴
//        String expected = standardizeLineEndings(new String(expectedStream.readAllBytes()).trim());
//        String actual = standardizeLineEndings(bytes.toString().trim());
//        assertEquals(expected, actual);

        BufferedReader expectedReader = new BufferedReader(new InputStreamReader(expectedStream));
        BufferedReader actualReader = new BufferedReader(new StringReader(bytes.toString()));

        String expectedLine, actualLine;
        int lineNumber = 0;
        while (true) {
            expectedLine = expectedReader.readLine();
            if (expectedLine == null) {
                break;
            }
            actualLine = actualReader.readLine();
            lineNumber++;
            assertEquals(
                    standardizeLineEndings(expectedLine),
                    standardizeLineEndings(actualLine),
                    "Mismatch at line " + lineNumber
            );
        }
    }

    @Test
    public void normalMain() throws IOException {
        testHelper("json_inputs/doors1.json",
                "e2e_user_inputs/input1.txt",
                "e2e_log_outputs/output1.txt");
    }

    @Test
    public void noParameterMain() {
        App.main(new String[]{});
    }

    @Test
    public void noSetupMain() throws IOException {
        testHelper("",
                "e2e_user_inputs/input1.txt",
                "e2e_log_outputs/output_wrong_setup.txt");
    }

    @Test
    public void wrongSetupMain() throws IOException {
        assertThrows(NullPointerException.class, ()->testHelper("non_exist.json",
                "e2e_user_inputs/input1.txt",
                "e2e_log_outputs/output1.txt"));
    }

    @Test
    public void wrongCommandMain() throws IOException {
        testHelper("json_inputs/doors1.json",
                "e2e_user_inputs/input_wrong_command.txt",
                "e2e_log_outputs/output_wrong_command.txt", true);
    }

    @Test
    public void wrongCommandForCoverage() {
        CommandParser cmdParser = new CommandParser();
        String str = "abc";
        Reader stringReader = new StringReader(str);
        App.readInputCommand(cmdParser, stringReader);
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

//    @Test
//    public void policyDemo() {
//        Log.setLogLevel(1);
////        demoHelper("json_inputs/servePolicy.json", "/user_inputs/input_servePolicy1.txt");
//        demoHelper("json_inputs/servePolicy.json", "/user_inputs/input_servePolicy2.txt");
//        demoHelper("json_inputs/servePolicy.json", "/user_inputs/input_servePolicy3.txt");
////        demoHelper("json_inputs/sourcePolicy.json", "/user_inputs/input_sourcePolicy1.txt");
//        demoHelper("json_inputs/sourcePolicy.json", "/user_inputs/input_sourcePolicy2.txt");
//        demoHelper("json_inputs/sourcePolicy.json", "/user_inputs/input_sourcePolicy3.txt");
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
//
//    private void demoHelper(String setupFileName, String inputFileName) {
//        SetupParser parser = new SetupParser();
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(setupFileName);
//        assertNotNull(inputStream);
//        String error = parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
//        assertNull(error);
//
//        App.modelSetup(parser);
//
//        CommandParser cmdParser = new CommandParser();
//        assertNotNull(AppTest.class.getResource(inputFileName));
//        String filePath = AppTest.class.getResource(inputFileName).getPath();
//
//        try {
//            FileReader fileReader = new FileReader(filePath);
//            App.readInputCommand(cmdParser, fileReader);
//        } catch (IOException ignored) {
//        }
//    }
//    private void handTestHelper(int logLevel, String setupFileName, String inputFileName, String outputFileName) throws IOException {
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
//    public void door1LogLevel0() throws IOException {
//        handTestHelper(0, "json_inputs/doors1.json",
//                "/user_inputs/input1.txt",
//                "log_outputs/output1.txt");
//    }
//
//    @Test
//    public void door1LogLevel1() throws IOException {
//        handTestHelper(1, "json_inputs/doors1.json",
//                "/user_inputs/input1.txt",
//                "log_outputs/output2.txt");
//    }
//
//    @Test
//    public void door1LogLevel2() throws IOException {
//        handTestHelper(2, "json_inputs/doors1.json",
//                "/user_inputs/input1.txt",
//                "log_outputs/output3.txt");
//    }
//
//    @Test
//    public void servePolicy1() throws IOException {
//        handTestHelper(1, "json_inputs/servePolicy.json",
//                "/user_inputs/input_servePolicy1.txt",
//                "log_outputs/output_servePolicy1.txt");
//    }
//    @Test
//    public void servePolicy2() throws IOException {
//        handTestHelper(1, "json_inputs/servePolicy.json",
//                "/user_inputs/input_servePolicy2.txt",
//                "log_outputs/output_servePolicy2.txt");
//    }
//
//    @Test
//    public void servePolicy3() throws IOException {
//        handTestHelper(1, "json_inputs/servePolicy.json",
//                "/user_inputs/input_servePolicy3.txt",
//                "log_outputs/output_servePolicy3.txt");
//    }
//
//    @Test
//    public void sourcePolicy1() throws IOException {
//        handTestHelper(1, "json_inputs/sourcePolicy.json",
//                "/user_inputs/input_sourcePolicy1.txt",
//                "log_outputs/output_sourcePolicy1.txt");
//    }
//
//    @Test
//    public void sourcePolicy2() throws IOException {
//        handTestHelper(1, "json_inputs/sourcePolicy.json",
//                "/user_inputs/input_sourcePolicy2.txt",
//                "log_outputs/output_sourcePolicy2.txt");
//    }
//
//    @Test
//    public void sourcePolicy3() throws IOException {
//        handTestHelper(1, "json_inputs/sourcePolicy.json",
//                "/user_inputs/input_sourcePolicy3.txt",
//                "log_outputs/output_sourcePolicy3.txt");
//    }
}
