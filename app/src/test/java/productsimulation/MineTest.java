package productsimulation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MineTest {
  @Test
  public void test_toString(){
    Mine mine = new Mine("GoldMine", "Gold", Collections.emptyList(), null, null);
    String expected = "Mine\n{name='GoldMine',\n type='Gold',\n sources=[]\n}";
    assertEquals(expected, mine.toString());
  }

  @Test
  public void test_getname(){
    Mine mine = new Mine("GoldMine", "Gold", Collections.emptyList(), null, null);
    assertEquals("GoldMine", mine.getName());
  }
}
