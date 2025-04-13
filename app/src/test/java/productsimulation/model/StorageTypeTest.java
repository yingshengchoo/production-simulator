package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StorageTypeTest {
  @Test
  public void test_StorageType() {
    StorageType st = new StorageType("Milk Storage", 56.7, 55, "milk");
    assertEquals(55, st.getCapacity());
    assertEquals(56.7, st.getPriority());
    assertEquals("milk", st.getItemToStore());
  }

}
