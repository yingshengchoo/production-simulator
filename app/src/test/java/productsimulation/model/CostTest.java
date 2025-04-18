package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class CostTest {
  @Test
  public void test_constructors_and_getters() {
    Map<String, Integer> m = new HashMap<>();
    m.put("item1", 3);
    m.put("item2", 1);
    Cost c1 = new Cost(m);
    assertEquals(3, c1.getItemCost("item1"));
    assertEquals(1, c1.getItemCost("item2"));
    assertEquals(0, c1.getItemCost("DNE"));
    assertEquals(m, c1.getCostMap());
    
    Cost c2 = new Cost();
    assertEquals(0, c2.getItemCost("abc"));
    assertEquals(new HashMap<>(), c2.getCostMap());
    
  }

}
