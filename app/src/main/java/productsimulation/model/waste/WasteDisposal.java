package productsimulation.model.waste;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WasteDisposal represents a facility that accepts waste items, reserves capacity,
 * and destroys stored waste over time.
 */
public class WasteDisposal extends Building {

    public static class WasteConfig {
        final int capacity;
        public final int rate;       // 每 interval 处置数量
        public final int interval;   // 处置间隔
        public int stored = 0;
        public int booked = 0;       //在途
        public int timer = 0;        // 自上次处置以来的计时器

        WasteConfig(int capacity, int rate, int interval) {
            this.capacity = capacity;
            this.rate = rate;
            this.interval = interval;
        }
    }

    public Map<String, WasteConfig> getConfigs() {
        return configs;
    }

    // 每种废物类型的配置
    public final Map<String, WasteConfig> configs = new HashMap<>();

    /**
     * @param name        建筑名称
     * @param type        BuildingType.WASTE_DISPOSAL
     * @param configMap   key:废物名称, value:int[]{capacity, rate, interval}
     * @param coordinate  所在坐标
     */
    public WasteDisposal(String name,
                         BuildingType type,
                         List<Building> sources,
                         Map<String, int[]> configMap,
                         Coordinate coordinate) {
        super(name, type, sources,null, null, coordinate);
        for (Map.Entry<String, int[]> e : configMap.entrySet()) {
            int[] arr = e.getValue();
            configs.put(e.getKey(), new WasteConfig(arr[0], arr[1], arr[2]));
        }
    }

    public WasteDisposal(String name,
                         BuildingType type,
                         List<Building> sources,
                         Coordinate coordinate) {
        super(name, type, sources,null, null, coordinate);
        if (type instanceof WasteDisposalType) {
            for (Map.Entry<String, WasteConfig> e : ((WasteDisposalType) type).configs.entrySet()) {
                configs.put(e.getKey(), e.getValue());
            }
        }
    }

    /**
     * 预订空间用于在途废物（transport departure 时调用）
     * @return 是否预订成功
     */
    public boolean bookWaste(String item, int amount) {
        WasteConfig cfg = configs.get(item);
        if (cfg == null) return false;
        if (cfg.stored + cfg.booked + amount > cfg.capacity) return false;
        cfg.booked += amount;
        Log.debugLog("[WasteDisposal] Booked " + amount + " " + item + " in " + name + " (booked=" + cfg.booked + ")");
        return true;
    }

    /**
     * 确认废物到达：减少 booked，增加 stored（transport arrival 时调用）
     * @return 是否提交成功
     */
    public boolean commitWaste(String item, int amount) {
        WasteConfig cfg = configs.get(item);
        if (cfg == null || cfg.booked < amount) return false;
        cfg.booked -= amount;
        cfg.stored += amount;
        Log.debugLog("[WasteDisposal] Committed arrival of " + amount + " "
                + item + " at " + name + " (stored=" + cfg.stored + ")");
        return true;
    }


    public int getStored(String item) {
        WasteConfig cfg = configs.get(item);
        return cfg == null ? 0 : cfg.stored;
    }

    /**
     * 获取可用容量：capacity - stored - booked
     */
    public int getAvailableCapacity(String item) {
        WasteConfig cfg = configs.get(item);
        return cfg == null ? 0 : (cfg.capacity - cfg.stored - cfg.booked);
    }

    /**
     * 注册到全局列表并添加到地图
     */
    @Override
    public WasteDisposal register() {
        buildingGlobalList.add(this);
        Board.getBoard().addBuilding(this);
        return this;
    }

    /**
     * 每个 timestep 进行废物处置：按 interval 和 rate 扣除 stored
     */
    @Override
    public boolean goOneStep() {
        for (Map.Entry<String, WasteConfig> e : configs.entrySet()) {
            WasteConfig cfg = e.getValue();
            if (cfg.stored > 0) {
                cfg.timer++;
                if (cfg.timer >= cfg.interval) {
                    int toDispose = Math.min(cfg.rate, cfg.stored);
                    cfg.stored -= toDispose;
                    Log.debugLog("[WasteDisposal] Disposed " + toDispose + " "
                            + e.getKey() + " at " + name + " (remaining=" + cfg.stored + ")");
                    cfg.timer = 0;
                }
            }
        }
        return true;
    }

    /**
     * 查找能预订指定废物数量的设施
     * todo: 检查是否连接
     */
    public static WasteDisposal findEligibleDisposal(String item) {
        for (Building b : buildingGlobalList) {
            if (b instanceof WasteDisposal) {
                WasteDisposal wd = (WasteDisposal) b;
                if (wd.getAvailableCapacity(item) > 0) {
                    return wd;
                }
            }
        }
        return null;
    }
}
