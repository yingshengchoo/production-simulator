package productsimulation.request;

import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.util.*;

// Singleton?
public class RequestBuilder {

    final List<Building> buildingList;
    final List<Recipe> recipeList;
    SourcePolicy sourcePolicy;

    public RequestBuilder(List<Building> buildingList, List<Recipe> recipeList, SourcePolicy sourcePolicy) {
        this.buildingList = buildingList;
        this.recipeList = recipeList;
        this.sourcePolicy = sourcePolicy;
    }

    Request BuildRequest(String item) {
        Building building = sourcePolicy.getSource(buildingList);
        if (building == null) {
            throw new IllegalArgumentException("ERROR: 0 Building in list!");
        }
        Recipe recipe = Recipe.getRecipe(item, recipeList);
        Request request = new Request(item, recipe, building);
        building.addRequest(request);
        return request;
    }

    List<Request> parseRequest(String item) {
        List<Request> requests = new LinkedList<>();
        Queue<String> itemQueue = new LinkedList<>();

        // recursive handle request
        itemQueue.add(item);

        while (!itemQueue.isEmpty()) {
            String currentItem = itemQueue.poll();

            Request request = BuildRequest(currentItem);
            requests.add(request);

            Recipe recipe = Recipe.getRecipe(currentItem, recipeList);
            if (recipe != null) {
                for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
                    for (int i = 0; i < entry.getValue(); i++) {
                        itemQueue.add(entry.getKey());
                    }
                }
            }
        }
        return requests;
    }

}
