package productsimulation.model.waste;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import productsimulation.model.Building;
import productsimulation.Coordinate;

import java.util.*;

class WasteDisposalTest {

    private static final String ITEM = "plastic";

    @BeforeEach
    void setUp() {
        // 清空全局列表，避免测试间互相干扰
        Building.buildingGlobalList.clear();
        // 如果 Board 对象会抛空指针或有副作用，这里可用 Mockito.mock(Board.class) 进行模拟
        // Board.setBoard(mockBoard);
    }

    private WasteDisposal createDisposal(int capacity, int rate, int interval) {
        Map<String, int[]> cfg = new HashMap<>();
        cfg.put(ITEM, new int[]{capacity, rate, interval});
        return new WasteDisposal(
                "wd-" + capacity,
                null,
                Collections.emptyList(),
                cfg,
                new Coordinate(0, 0)
        );
    }

    @Test
    void testBookAndAvailableCapacity() {
        WasteDisposal wd = createDisposal(10, 2, 5);
        // 成功预订
        assertTrue(wd.bookWaste(ITEM, 4));
        assertEquals(10 - 0 /*stored*/ - 4 /*booked*/, wd.getAvailableCapacity(ITEM));

        // 超出容量时预订失败
        assertFalse(wd.bookWaste(ITEM, 7));
        // booked 保持不变
        assertEquals(4, wd.configs.get(ITEM).booked);
    }

    @Test
    void testCommitWaste() {
        WasteDisposal wd = createDisposal(10, 2, 5);
        // 未预订时提交应失败
        assertFalse(wd.commitWaste(ITEM, 1));

        // 先预订再提交
        assertTrue(wd.bookWaste(ITEM, 3));
        assertTrue(wd.commitWaste(ITEM, 2));
        // booked 减少，stored 增加
        assertEquals(1, wd.configs.get(ITEM).booked);
        assertEquals(2, wd.getStored(ITEM));

        // 多提交超过 booked 部分失败
        assertFalse(wd.commitWaste(ITEM, 5));
    }

    @Test
    void testGoOneStepDisposal() {
        // rate=3, interval=2
        WasteDisposal wd = createDisposal(100, 3, 2);
        wd.configs.get(ITEM).stored = 8;
        // timer=1，存量不变
        wd.goOneStep();
        assertEquals(8, wd.getStored(ITEM));
        // timer=2 -> 触发处置，减少 3
        wd.goOneStep();
        assertEquals(5, wd.getStored(ITEM));
        // timer 重置为 0
        assertEquals(0, wd.configs.get(ITEM).timer);

        // 再来几轮
        wd.goOneStep();
        wd.goOneStep();
        wd.goOneStep();
        wd.goOneStep();
        wd.goOneStep();
        assertEquals(0, wd.getStored(ITEM));
    }

    @Test
    void testFindEligibleDisposal() {
        // 第一个容量已满
        WasteDisposal full = createDisposal(5, 1, 1);
        full.configs.get(ITEM).stored = 5;
        Building.buildingGlobalList.add(full);

        // 第二个还有空间
        WasteDisposal spare = createDisposal(10, 1, 1);
        Building.buildingGlobalList.add(spare);

        WasteDisposal found = WasteDisposal.findEligibleDisposal(ITEM);
        assertNotNull(found);
        assertEquals(spare, found);
    }
}