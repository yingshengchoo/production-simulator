package productsimulation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FactoryTest {
  @Test
  public void test_toString(){
    Factory f = new Factory("PaperInc", "PaperFactory", Collections.emptyList(), null, null);
    String expected = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[]\n}";
    assertEquals(expected, f.toString());
  }

}
