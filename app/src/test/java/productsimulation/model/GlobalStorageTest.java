package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GlobalStorageTest {
  @Test
  public void test_globalStorageMethods() {
    GlobalStorage.globalStorageMap = new HashMap<>();    
    String expectedString1 = "Global Storage:\n";
    assertEquals(expectedString1 , GlobalStorage.globalStorageToString());
    assertEquals(new HashMap<>(), GlobalStorage.globalStorageMap);
    for(int i = 0; i < 5; i++){
      GlobalStorage.addItemToStorage("book");
    }
    GlobalStorage.addItemToStorage("pencil");
    assertEquals(5, GlobalStorage.getItemCount("book"));
    assertEquals(1, GlobalStorage.getItemCount("pencil"));
    assertEquals(0, GlobalStorage.getItemCount("eraser"));

    HashMap<String, Integer> expectedMap = new HashMap<>();
    expectedMap.put("book", 5);
    expectedMap.put("pencil", 1);
    assertEquals(expectedMap, GlobalStorage.getGlobalStorageMap());

    String expectedString2 = "Global Storage:\n- book: 5\n- pencil: 1\n";
    assertEquals(expectedString2, GlobalStorage.globalStorageToString());

    int amountStillNeeded;
    amountStillNeeded = GlobalStorage.useStorageItem("book", 2);
    assertEquals(0, amountStillNeeded);
    assertEquals(3, GlobalStorage.getItemCount("book"));
    amountStillNeeded = GlobalStorage.useStorageItem("pencil", 3);
    assertEquals(2, amountStillNeeded);
    assertEquals(0, GlobalStorage.getItemCount("pencil"));
    amountStillNeeded = GlobalStorage.useStorageItem("eraser", 1);
    assertEquals(1, amountStillNeeded);
    assertEquals(0, GlobalStorage.getItemCount("eraser"));
  }
}
