package productsimulation.model.road;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {

    @Test
    public void testConstructor() {
        // 测试默认构造（无方向）
        Direction direction = new Direction(Direction.UNDEFINED);
        assertEquals("", direction.getDirections());

        // 测试初始化为单个方向
        direction = new Direction(Direction.UP);
        assertEquals("UP", direction.getDirections());

        // 测试初始化为多个方向
        direction = new Direction(Direction.UP | Direction.RIGHT);
        assertEquals("UP RIGHT", direction.getDirections());
    }

    @Test
    public void testHasDirection() {
        Direction direction = new Direction(Direction.UP | Direction.RIGHT);

        // 测试存在方向
        assertTrue(direction.hasDirection(Direction.UP));
        assertTrue(direction.hasDirection(Direction.RIGHT));

        // 测试不存在方向
        assertFalse(direction.hasDirection(Direction.DOWN));
        assertFalse(direction.hasDirection(Direction.LEFT));
    }

    @Test
    public void testAddDirection() {
        Direction direction = new Direction(Direction.UNDEFINED);

        // 测试添加单个方向
        direction.addDirection(Direction.UP);
        assertEquals("UP", direction.getDirections());

        // 测试添加多个方向
        direction.addDirection(Direction.RIGHT);
        assertEquals("UP RIGHT", direction.getDirections());

        // 测试重复添加方向（不应改变 mask）
        direction.addDirection(Direction.UP);
        assertEquals("UP RIGHT", direction.getDirections());
    }

    @Test
    public void testRemoveDirection() {
        Direction direction = new Direction(Direction.UP | Direction.RIGHT);

        // 测试移除单个方向
        direction.removeDirection(Direction.UP);
        assertEquals("RIGHT", direction.getDirections());

        // 测试移除不存在的方向（不应改变 mask）
        direction.removeDirection(Direction.DOWN);
        assertEquals("RIGHT", direction.getDirections());
    }

    @Test
    public void testInvalidDirection() {
        Direction direction = new Direction(Direction.UNDEFINED);

        // 测试非法方向（UNDEFINED）
        assertFalse(direction.hasDirection(Direction.UNDEFINED));
        direction.addDirection(Direction.UNDEFINED);
        assertEquals("", direction.getDirections()); // 添加非法方向不应改变 mask
    }
}