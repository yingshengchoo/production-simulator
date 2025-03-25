package productsimulation;

import productsimulation.command.Command;
import productsimulation.command.CommandParser;
import productsimulation.model.Building;
import productsimulation.model.FactoryType;
import productsimulation.model.Recipe;
import productsimulation.request.OneTimeServePolicy;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SoleSourcePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.setup.SetupParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static void userInteract(CommandParser cmdParser) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (LogicTime.getInstance().getExitFlag()) {
                break;
            }
            Command cmd = cmdParser.parseLine(line);
            cmd.execute();
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
        Map<String, Building> buildings = parser.getBuildingMap();

        // step 2: create LogicTime and RequestBroadcaster
        LogicTime logicTime = LogicTime.getInstance();
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        SourcePolicy sourcePolicy = new SourceQLen();
        ServePolicy servePolicy = new FIFOPolicy();

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
        userInteract(cmdParser);
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
