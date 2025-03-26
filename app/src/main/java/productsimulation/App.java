package productsimulation;

import productsimulation.command.Command;
import productsimulation.command.CommandParser;
import productsimulation.model.Building;
import productsimulation.model.FactoryType;
import productsimulation.model.Recipe;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.setup.SetupParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void readInputCommand(CommandParser cmdParser, Readable inputSource, boolean isInteractive) {
        Scanner scanner = new Scanner(inputSource);
        if (isInteractive) {
            System.out.print("> ");
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (LogicTime.getInstance().getExitFlag()) {
                break;
            }
            Command cmd = cmdParser.parseLine(line);
            try {
                cmd.execute();
            } catch (Exception e) {
//                e.printStackTrace();
                Log.level0Log(e.getMessage());
            }

            if (isInteractive) {
                System.out.print("> ");
            }
        }
        scanner.close();
    }

    private static void initialize(String setupFilePath) {
        Log.debugLog("initializing");
        // step 1: read the json, apply check rules
        SetupParser parser = new SetupParser();
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(setupFilePath);
        parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
        Map<String, Recipe> recipes = parser.getRecipeMap();
        Map<String, FactoryType> types = parser.getTypeMap();
        Map<String, Building> buildings = parser.getBuildingMap();

        // step 2: create LogicTime, RequestBroadcaster, State
        LogicTime logicTime = LogicTime.getInstance();
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        SourcePolicy sourcePolicy = new SourceQLen();
        ServePolicy servePolicy = new FIFOPolicy();
        State.initialize(new ArrayList<>(buildings.values()), new ArrayList<>(types.values()),
                new ArrayList<>(recipes.values()), requestBroadcaster, logicTime);

        // step 3: fill out the data model
        for(Building b: buildings.values()) {
            b.changeSourcePolicy(sourcePolicy);
            b.changeServePolicy(servePolicy);

            logicTime.addObservers(b);
            requestBroadcaster.addBuildings(b);
        }

        for(Recipe r: recipes.values()) {
            requestBroadcaster.addRecipes(r);
        }
    }

    private static void play(String setupFilePath) {
        //setup
        initialize(setupFilePath);
        // enter interaction phase
        CommandParser cmdParser = new CommandParser();
        readInputCommand(cmdParser, new InputStreamReader(System.in), true);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ./app <setup_json_file_path>");
            System.exit(1);
        }
        String filePath = args[0];
        play(filePath);
    }
}
