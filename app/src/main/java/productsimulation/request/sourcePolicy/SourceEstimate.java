package productsimulation.request.sourcePolicy;

import productsimulation.Log;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.IdGenerator;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.sourcePolicy.Estimate.*;

import java.util.*;
import java.util.stream.Collectors;


public class SourceEstimate implements SourcePolicy {
    IdGenerator idGenerator = new IdGenerator();


    @Override
    public Building getSource(List<Building> buildings, String ingredient) {
        // 取目前总estimate time最短的building
        // find the building with minimum estimate time
        int min = Integer.MAX_VALUE;
        Building source = null;
        for (Building b : buildings) {

            if (!b.canProduce(ingredient)) {
                continue;
            }
            int cur = 0;
            UsageSet usageSet = new UsageSet();
            List<Request> requests = b.getRequestQueue();

            // 取building中所有request的总estimate time
            // calculate the total estimate time cost
            for (Request r : requests) {
                cur += estimate(r, b, usageSet, new Path());
            }

            if (cur < min) {
                min = cur;
                source = b;
            }

            idGenerator.reset();

            Log.level2Log("    " + b.getName() + " " + cur);
        }
        return source;
    }

    public int estimate(Request request, Building building, UsageSet usageSet, Path path) {
        // 1. 若在 usageSet 未记录但 building 正在处理Request
        // if the request is not recorded in the usage set and is current processing
        if (isWorkingAndNotRegistered(building, request, usageSet)) {
            usageSet.recordWorking(building, path, request);
            return building.getCurrentRemainTime();
        }

        // 2. 拿到 Request 的配方和基础延迟
        // calculate the Recipe and latency
        Recipe recipe = request.getRecipe();
        int totalTime = recipe.getLatency();

        // 3. 针对每个原料，计算准备原料需要的时间
        // for each ingredient calculate the time for preparation
        for (Map.Entry<String, Integer> ing : recipe.getIngredients().entrySet()) {

            int required = ing.getValue();
            // 先检查库存
            // check storage
            int shortage = getIngredientShortage(required, ing, building, usageSet);

            // record usage
            usageSet.add(new Entry(path, request, building, ing.getKey(), required - shortage));

            // if there is shortage, estimate time for produce shortage(in parallel)
            if (shortage > 0) {
                totalTime += produceShortage(ing.getKey(), building, shortage, usageSet, path);
            }
        }

        return totalTime;
    }

    private int getIngredientShortage(int required,
                                  Map.Entry<String, Integer> ing,
                                  Building building,
                                  UsageSet usageSet) {
        String ingredient = ing.getKey();

        // first check the remaining storage, and get shortage
        int remain = usageSet.getIngredientRemain(building, ingredient);

        return Math.max(0, required - remain);
    }

    private int produceShortage(String item,
                                Building building,
                                int shortage,
                                UsageSet usageSet,
                                Path path) {
        int timeCost = 0;

        while (shortage > 0) {
            int id = idGenerator.nextId();

            // 获取所有来源工厂的估算时间
            // get estimate time from all buildings
            Map<Building, Integer> ts = getEstimatedTimeSet(item, building, usageSet, path, id);

            // 选择 k个最快的
            // choose top k
            int k = Math.min(shortage, ts.size());
            Map<Building, Integer> topK = getTopK(k, ts);

            // 取这 k 个分支的最大时间 => 并行完成时间
            // parallel time = max time in the batch
            int batchTime = topK.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            timeCost += batchTime;

            // 回滚不使用的分支
            // discard unused path
            discard(ts, topK, usageSet, path, id);

            shortage -= k;
        }

        return timeCost;
    }

    // get estimate time map <Building, Integer> for each valid building
    private Map<Building, Integer> getEstimatedTimeSet (String item, Building building, UsageSet usageSet, Path path, int id) {
        Map<Building, Integer> ts = new HashMap<>();

        for (Building source : building.getSources()) {
            if (!source.canProduce(item)) {
                continue;
            }

            // send 'fake request' to upstream buildings
            Segment segment = new Segment(id, source);
            Path newPath = path.append(segment);
            Request newRequest = Request.getDummyRequest(item, path.getLastBuilding());

            // recursive function call, get estimate time for 'fake request'
            int estimation = estimate(newRequest, source, usageSet, newPath);
            ts.put(source, estimation);
        }

        return ts;
    }

    private boolean isWorkingAndNotRegistered(Building building, Request request, UsageSet usageSet) {
        // find building's current request
        Request buildingCurrentRequest = building.getCurrentRequest();

        if (request == null || buildingCurrentRequest == null) {
            return false;
        }

        else {
            // return true if request is working & request is 'same' & not recorded
            return buildingCurrentRequest.getStatus() == RequestStatus.WORKING &&
                    request.isSameItemRequester(buildingCurrentRequest) &&
                    !usageSet.isRecorded(request, building);
        }
    }



    protected static Map<Building, Integer> getTopK(int k, Map<Building, Integer> map) {

        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(k)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private void discard(Map<Building, Integer> ts, Map<Building, Integer> topK, UsageSet usageSet, Path parentPath, int id) {
        List<Building> toRemove = new ArrayList<>();
        for (Building b : ts.keySet()) {
            if (!topK.containsKey(b)) {
                Path p = parentPath.append(new Segment(id, b));

                // discard records with path with prefix p
                usageSet.removeByPath(p);
                toRemove.add(b);
            }
        }
        for (Building b : toRemove) {
            ts.remove(b);
        }
    }

    public String getName() {
        return "source estimate";
    }

}
