package productsimulation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FactoryTypeTest {
  @Test
  public void test_getName() {
    FactoryType t = new FactoryType("type1");
    asertEquals("type1", t.getName());
  }

  @Test
  public void test_toString(){
    FactoryType t = new FactoryType("type1");
    String expected = "Factory Type\n{name='type1',\n sources=[]\n}";
    assertEqauls(expected, t.toString());
  }
}
