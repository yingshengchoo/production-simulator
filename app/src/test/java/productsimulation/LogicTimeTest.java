package productsimulation;

import org.junit.jupiter.api.Test;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.model.Mine;
import productsimulation.model.Recipe;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class LogicTimeTest {
    @BeforeEach
    void setUp() {
        LogicTime.getInstance().reset();
        Building.buildingGlobalList.clear();
    }

    @Test
    void test_singleton() {
        // multiple getInstance should be the same object
        LogicTime t1 = LogicTime.getInstance();
        t1.stepNHandler(1);
        LogicTime t2 = LogicTime.getInstance();
        assertEquals(t1, t2);
    }

    @Test
    void test_finishHandler() {
        LogicTime t = LogicTime.getInstance();
        t.finishHandler();
        assertEquals(t.getStep(), 0);
    }

    @Test
    void test_autoRun() {
        LogicTime t = LogicTime.getInstance();

        int speed = 3;
        int testDuration = 5;

        // 开启自动模式
        t.setRealTimeMode(true);
        // 在单独的线程中运行 realTimeHandler
        Thread realTimeThread = new Thread(() -> {
            String result = t.realTimeHandler(speed);
            assertNull(result);
        });

        realTimeThread.start();

        // 模拟运行一段时间后停止
        try {
            Thread.sleep(testDuration * 1000); // 运行指定时间
            t.setRealTimeMode(false); // 停止自动模式
            realTimeThread.join(); // 等待线程结束
        } catch (InterruptedException e) {
        }

        // 检查当前步数
        int expectedSteps = speed * testDuration;
        int actualSteps = t.getStep();
        assertEquals(actualSteps, expectedSteps);
    }
}