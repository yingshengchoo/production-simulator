package productsimulation.model.waste;

import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.model.*;
import productsimulation.model.road.Road;
import productsimulation.model.road.TransportQueue;
import productsimulation.model.waste.WasteDisposal;
import productsimulation.request.Request;
import productsimulation.request.WasteRequest;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildingDisposalTest {

    private static final String ITEM = "plastic";

    /**
     * 一个最小的 Building 子类，用来暴露 disposalWastes()
     */
    static class TestBuilding extends Building {
        TestBuilding(String name) {
            super(name,
                    new BuildingType("n", new HashMap<>(), new Cost()),
                    Collections.emptyList(),
                    null,
                    null,
                    new Coordinate(0, 0));
        }

        @Override
        public Building register() {
            return null;
        }

        @Override
        public boolean goOneStep() {
            return false;
        }
    }

    @BeforeEach
    @AfterEach
    void setUp() {
        Building.buildingGlobalList.clear();
        Recipe.recipeGlobalList.clear();

    }

    /**
     * 快速创建并注册一个 WasteDisposal，用 spy 监听 bookWaste
     */
    private WasteDisposal makeAndRegisterDisposal(int capacity) {
        Map<String, int[]> cfg = new HashMap<>();
        // capacity, rate=capacity, interval=1 方便马上处置
        cfg.put(ITEM, new int[]{capacity, capacity, 1});
        WasteDisposal wd = spy(new WasteDisposal(
                "wd-" + capacity,
                new BuildingType("n", new HashMap<>(), new Cost()),
                Collections.emptyList(),
                cfg,
                new Coordinate(1, 0)
        ));
        wd.register();
        return wd;
    }

    @Test
    void whenCapacitySufficient_thenAllWastesSentAndEntryRemoved() {
        TestBuilding b = new TestBuilding("B1");
        // 手动填入 wastes
        b.getWastes().clear();
        b.getWastes().put(ITEM, 3);

        WasteDisposal wd = makeAndRegisterDisposal(5);

        b.disposalWastes();

        // 应当对容量 5 的处置站预订 3 个
        verify(wd).bookWaste(ITEM, 3);

        // 发送完毕后，wastes map 应当移除了该条目
        assertFalse(b.getWastes().containsKey(ITEM));

    }

    @Test
    void whenCapacityLimited_thenPartialSendAndRemainingUpdated() {
        TestBuilding b = new TestBuilding("B2");
        b.getWastes().clear();
        b.getWastes().put(ITEM, 7);

        WasteDisposal wd = makeAndRegisterDisposal(4);

        b.disposalWastes();

        // 只能发送 4，保留 3
        verify(wd).bookWaste(ITEM, 4);
        assertTrue(b.getWastes().containsKey(ITEM));
        assertEquals(3, b.getWastes().get(ITEM).intValue());
    }

    @Test
    void whenNoDisposalAvailable_thenNothingChanges() {
        TestBuilding b = new TestBuilding("B3");
        b.getWastes().clear();
        b.getWastes().put(ITEM, 5);

        // wastes 保持原样，未调用任何 bookWaste
        assertEquals(1, b.getWastes().size());
        assertEquals(5, b.getWastes().get(ITEM).intValue());

    }

    @Test
    void iteratorRemovesZeroOrNegativeEntries() {
        TestBuilding b = new TestBuilding("B4");
        b.getWastes().clear();
        // 放入一个 non-positive 和一个 positive
        b.getWastes().put("glass", 0);
        b.getWastes().put(ITEM, 2);

        WasteDisposal wd = makeAndRegisterDisposal(10);

        b.disposalWastes();

        // "glass" 应被移除
        assertFalse(b.getWastes().containsKey("glass"));
        // ITEM 被发送完后也移除
        assertFalse(b.getWastes().containsKey(ITEM));

    }

    @Test
    void wholeProcess() {
        Recipe r = new Recipe(1, new HashMap<>());
        r.addWaste(ITEM, 6);

        Map<String, Recipe> rmap = new HashMap<>();
        rmap.put("r1", r);

        Request request = new Request("r1", r, null);

        Building b = new Factory("Test Building",
                new BuildingType("n", rmap, new Cost()),
                Collections.emptyList(),
                new SourceQLen(),
                new FIFOPolicy(),
                new Coordinate(2, 0));

        WasteDisposal wd = makeAndRegisterDisposal(5);
        b.register();
        wd.register();
        Road.generateRoad(b, wd);

        b.addRequest(request);

        // 做好订单，运送废物
        b.goOneStep();
        b.updateNotified();

        // 发送完毕后，wastes map 应当移除了该条目
        assertEquals(1, b.getWastes().get(ITEM).intValue());
        //System.out.println(TransportQueue.queue.size());
        // 运送完毕后,wd的储存应该得到更新
        TransportQueue.goOneStep();
        assertEquals(5, wd.getStored(ITEM));
    }
}
