package productsimulation;

import productsimulation.GUI.GUI;
import productsimulation.command.Command;
import productsimulation.command.CommandParser;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.model.Storage;
import productsimulation.model.Recipe;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.setup.SetupParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import productsimulation.setup.TypeParser;

public class App {
    private static final String beginPrompt = "Welcome to product simulation, you can:\n" +
            "request 'itemName' from 'buildingName'\n" +
            "step 1\n" +
            "verbose 1\n" +
            "finish\n" +
            "set policy request on target\n" +
            "save saveFileName\n" +
            "load loadFileName\n" +
            "connect 'buildingName1' to 'buildingName2'\n";

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

        ArrayList<Building> buildingList = new ArrayList<>();
        for(Building b: buildings.values()) {
            b.changeSourcePolicy(sourcePolicy);
            b.changeServePolicy(servePolicy);

//            logicTime.addObservers(b);
//            requestBroadcaster.addBuildings(b);
            buildingList.add(b);
        }
        Building.buildingGlobalList = buildingList;

        //            requestBroadcaster.addRecipes(r);
        ArrayList<Recipe> recipeList = new ArrayList<>(recipes.values());

        for(Building b: buildings.values()){
          if(b instanceof Storage){
            Storage s = (Storage) b;
            s.initializeStorageType();
          }
        }

        Recipe.setRecipeGlobalList(recipeList);
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

    private static boolean initBuildingTypes(String setupFilePath) {
        if(setupFilePath.isEmpty()) {
            Log.debugLog("The simulation will go on without building type json");
            return true;
        }
        TypeParser parser = new TypeParser();
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(setupFilePath);
        if (inputStream== null) {
            Log.debugLog("Error when reading building type json file");
            return false;
        }
        String error = parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
        if(error == null) {
            Log.debugLog("The setup json is valid!");
        } else {
            Log.level0Log(error);
            return false;
        }
        BuildingType.setBuildingTypeGlobalList(parser.getTypeMap());
        return true;
    }

    //If GUI is True, open GUI
    private static void play(String setupFilePath, String addBuildingFilePath, boolean useGUI) {
        //setup
        boolean ret = initialize(setupFilePath) && initBuildingTypes(addBuildingFilePath);
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
        try {
            Files.writeString(Paths.get("test.log"), "");
        } catch(IOException e) {
            System.err.println("Error when clean up log file");
        }

        if (args.length < 1 || args.length > 3) {
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

        String addBuildingFilePath = "";
        // init buildingType List
        if (args.length == 3 ) {
//            initBuildingTypes(args[2]);
            addBuildingFilePath = args[2];
        }
        else if (!args[0].equals("-nw") && args.length == 2){
//            initBuildingTypes(args[1]);
            addBuildingFilePath = args[1];
        }

        play(filePath, addBuildingFilePath, useGUI);
    }
}
