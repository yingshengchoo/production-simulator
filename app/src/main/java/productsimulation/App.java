package productsimulation;

import productsimulation.GUI.GUI;
import productsimulation.command.Command;
import productsimulation.command.CommandParser;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.model.Recipe;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.setup.SetupParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;

public class App {
    private static final String beginPrompt = "Welcome to product simulation, you can:\n" +
            "request 'itemName' from 'buildingName'\n" +
            "step 1\n" +
            "verbose 1\n" +
            "finish\n" +
            "set policy request on target\n" +
            "save saveFileName\n" +
            "load loadFileName\n" +
            "connect 'buildingName1' 'buildingName2'\n";

    /**
     * Reads and processes input commands.
     * <p>
     * This method reads from the given input source, parses each line using the provided CommandParser,
     * and executes the resulting commands. If interactive mode is enabled, it will print a prompt after each input line.
     * The loop will terminate when the exit flag from LogicTime is set to true.
     *
     * @param cmdParser     The command parser used to parse input lines into commands.
     * @param inputSource   The input source to read from, which can be any object implementing the Readable interface.
     * @see CommandParser#parseLine(String)
     * @see LogicTime#getExitFlag()
     */
//    public static void readInputCommand(CommandParser cmdParser, Readable inputSource, boolean isInteractive) {
    public static void readInputCommand(CommandParser cmdParser, Readable inputSource) {
        Scanner scanner = new Scanner(inputSource);
        System.out.print(beginPrompt);
        System.out.print("> ");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (LogicTime.getInstance().getExitFlag()) {
                break;
            }
            Command cmd = cmdParser.parseLine(line);
            if (cmd == null) {
                System.err.println("You enter an invalid command");
            }
            try {
                cmd.execute();
            } catch (Exception e) {
                Log.debugLog(e.getClass().getName());
            }

            System.out.print("> ");
        }
        scanner.close();
    }

    // todo 目前，全局的recipes，types，buildings在setupParser中首次解析完毕之后，
    // todo 分散到了LogicTime，RequestBroadcaster，Recipe，State各个地方。
    // todo 值得注意的是，目前的setupParser所返回的，是unmodifiable map
    // todo 为了支持编辑功能，重构时直接将setupParser的get方法改为set ModelManager，上述四个类删除相应field
    public static void modelSetup(SetupParser parser) {
        Map<String, Recipe> recipes = parser.getRecipeMap();
        Map<String, BuildingType> types = parser.getTypeMap();
        Map<String, Building> buildings = parser.getBuildingMap();

        LogicTime logicTime = LogicTime.getInstance();
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        SourcePolicy sourcePolicy = new SourceQLen();
        ServePolicy servePolicy = new FIFOPolicy();
        State.initialize(new ArrayList<>(buildings.values()), new ArrayList<>(types.values()),
                new ArrayList<>(recipes.values()), requestBroadcaster, logicTime);

        for(Building b: buildings.values()) {
            b.changeSourcePolicy(sourcePolicy);
            b.changeServePolicy(servePolicy);

            logicTime.addObservers(b);
            requestBroadcaster.addBuildings(b);
        }

        ArrayList<Recipe> recipeList = new ArrayList<>();
        for(Recipe r: recipes.values()) {
            requestBroadcaster.addRecipes(r);
            recipeList.add(r);
        }
        Recipe.setRecipeList(recipeList);
    }

    private static boolean initialize(String setupFilePath) {
        Log.debugLog("initializing with " + setupFilePath);
        // step 1: read the json, apply check rules
        SetupParser parser = new SetupParser();
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(setupFilePath);
        String error = parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
        if(error == null) {
            Log.debugLog("The setup json is valid!");
        } else {
            Log.level0Log(error);
            return false;
        }

        modelSetup(parser);
        return true;
    }

    //If GUI is True, open GUI
    private static void play(String setupFilePath, boolean useGUI) {
        //setup
        boolean ret = initialize(setupFilePath);
        if(!ret) {
            return;
        }
        // enter interaction phase
        if(useGUI){
          Application.launch(GUI.class);
        } else {
          CommandParser cmdParser = new CommandParser();
          readInputCommand(cmdParser, new InputStreamReader(System.in));
        }
    }

    public static void main(String[] args) {

        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java App [-nw] <setup_json_file_path>");
            return;
        }

        boolean useGUI = true;
        String filePath;
        if (args[0].equals("-nw")) {
            useGUI = false;
            if (args.length < 2) {
                System.err.println("Usage: java App -nw <setup_json_file_path>");
                return;
            }
            filePath = args[1];
        } else {
            filePath = args[0];
        }

        play(filePath, useGUI);
    }
}
