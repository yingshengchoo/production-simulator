package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.sourcePolicy.Estimate.*;

import java.util.*;
import java.util.stream.Collectors;


public class SourceEstimate implements SourcePolicy {

    @Override
    public Building getSource(List<Building> buildings, String ingredient) {
        int min = Integer.MAX_VALUE;
        Building source = null;
        for (Building b : buildings) {
            int cur = 0;
            List<Request> requests = b.getRequestQueue();
            for (Request r : requests) {
                cur += estimate(r.getRecipe().getOutput(), b, new UsageSet(), new Path());
            }
            if (cur < min) {
                min = cur;
                source = b;
            }
        }
        return source;
    }

    public int estimate(String item, Building building, UsageSet usageSet, Path path) {
        // 1. 若在 usageSet 未记录但 building 正在生产 item
        if (isWorkingAndNotRegistered(building, item, usageSet)) {
            usageSet.addRecord(building, path, item);
            return building.getCurrentRemainTime();
        }

        // 2. 拿到 item 的配方和基础延迟
        Recipe recipe = Recipe.getRecipe(item);
        int totalTime = recipe.getLatency();

        // 3. 针对每个原料，调用 acquireIngredient(...)
        for (Map.Entry<String, Integer> ing : recipe.getIngredients().entrySet()) {
            int shortage = getIngredientShortage(item, ing, building, usageSet, path);
            if (shortage > 0) {
                totalTime += produceShortage(ing.getKey(), shortage, usageSet, path);
            }
        }

        return totalTime;
    }

    private int getIngredientShortage(String parentItem,
                                  Map.Entry<String, Integer> ing,
                                  Building building,
                                  UsageSet usageSet,
                                  Path path) {
        String ingredient = ing.getKey();
        int required = ing.getValue();

        // 先看看可用量
        int remain = usageSet.getIngredientRemain(building, ingredient);
        int shortage = Math.max(0, required - remain);

        // 在 usageSet 中记录占用 (哪怕只用了一部分，也记下)
        int usedNow = required - shortage; // 实际从本地拿到的量
        usageSet.add(new Entry(path, parentItem, building, ingredient, usedNow));

        return shortage;
    }

    /**
     * 使用并行策略在能生产 ingredient 的子工厂中试探，
     * 并行选出 k 个最快的分支，回滚其他分支占用。
     */
    private int produceShortage(String ingredient,
                                int shortage,
                                UsageSet usageSet,
                                Path path) {
        int timeCost = 0;

        while (shortage > 0) {
            int id = IdGenerator.nextId();

            // 获取所有来源工厂的估算时间
            Map<Building, Integer> ts = getEstimatedTime(ingredient, usageSet, path, id);

            // 选择 k个最快的
            int k = Math.min(shortage, ts.size());
            Map<Building, Integer> topK = getTopK(k, ts);

            // 取这 k 个分支的最大时间 => 并行完成时间
            int batchTime = topK.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            timeCost += batchTime;

            // 回滚不使用的分支
            discard(ts, topK, usageSet, path, id);

            // 已经生产了k个
            shortage -= k;
        }

        return timeCost;
    }

    private Map<Building, Integer> getEstimatedTime(String item, UsageSet usageSet, Path path, int id) {
        Map<Building, Integer> ts = new HashMap<>();

        for (Building source : Building.getBuildings()) {
            if (!source.canProduce(item)) {
                continue;
            }
            Segment segment = new Segment(id, source);
            Path newPath = path.append(segment);
            int estimation = estimate(item, source, usageSet, newPath);
            ts.put(source, estimation);
        }

        return ts;
    }

    private boolean isWorkingAndNotRegistered(Building building, String item, UsageSet usageSet) {
        Request request = building.getCurrentRequest();

        if (request == null) {
            return false;
        }

        else return request.getStatus() == RequestStatus.WORKING &&
                request.getRecipe().getOutput().equals(item) &&
                !usageSet.isRecorded(building, item);
    }

    private Map<Building, Integer> getTopK(int k, Map<Building, Integer> map) {

        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(k)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private void discard(Map<Building, Integer> ts, Map<Building, Integer> topK, UsageSet usageSet, Path path, int id) {
        List<Building> toRemove = new ArrayList<>();
        for (Building b : ts.keySet()) {
            if (!topK.containsKey(b)) {
                Path p = path.append(new Segment(id, b));
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
