package productsimulation.setup;

import productsimulation.LogicTime;
import productsimulation.model.Building;
import productsimulation.model.FactoryType;
import productsimulation.model.Recipe;

import java.io.BufferedReader;
import java.util.Map;

public class SetupParser {
    // <name, abstract data model>
    private Map<String, Building> buildings;
    private Map<String, FactoryType> types;

    private Map<String, Recipe> recipes;

    public SetupParser() {
    }

    public void parse(BufferedReader r) {
    }
}
