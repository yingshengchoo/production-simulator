package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.request.Request;
import productsimulation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class StorageTest {
  @Test
  public void test_toString() {
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(new Storage("Closet", new Recipe(3, Collections.emptyMap()), Collections.emptyList(), 100, 3, null,null));
    sources.add(new Factory("DoorInc", new FactoryType("Door", Collections.emptyMap()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", new FactoryType("Diamond", Collections.emptyMap()), Collections.emptyList(), null, null));
    Storage s1 = new Storage("Drawer", new Recipe(2, Collections.emptyMap(), "socks"), sources, 150, 10, null, null);
    String expected = "Storage\n{name='Drawer',\n stores='socks',\n sources=[Closet, DoorInc, DiamondMine],\n capacity=150,\n storage=[],\n request queue size=0\n}";

    assertEquals(expected, s1.toString());
  
  }

 @Test
 public void test_goOneStep(){
   //Setup: A Storage
   LogicTime t = LogicTime.getInstance();
   
   ArrayList<Building> sources = new ArrayList<>();
   Recipe socks = new Recipe(1, Collections.emptyMap(), "socks");
   Mine m1 = new Mine("SocksMine1", new FactoryType("SmellySocks", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
   Mine m2 = new Mine("SocksMine2", new FactoryType("SmellySocks", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
   sources.add(m1);
   sources.add(m2);
   Storage s1 = new Storage("Drawer", socks, sources, 100, 100, new SourceQLen(), new FIFOPolicy());
   ArrayList<Building> sources2 = new ArrayList<>();
   sources2.add(s1);
   Recipe pair = new Recipe(1,Map.of("Socks", 2), "pairOfSocks");
   Factory f = new Factory("SocksFactory", new FactoryType("PairOfSocks", Map.of("pairOfSocks", pair)), sources2, new SourceQLen(), new FIFOPolicy());

   t.addObservers(m1);
   t.addObservers(m2);
   t.addObservers(s1);
   t.addObservers(f);

   
   //F = 100*100/(100*100) = 1
   //we expect the storage to request sock at t =0 and t =1;
   
   assertEquals(0, t.getStep());
   f.addRequest(new Request("pairOfSocks", pair, null));
   assertEquals(1, f.getRequestCount());
   assertEquals(2, s1.getRequestCount());
   assertEquals(0, s1.getStockCount());
   assertEquals(0, m1.getRequestCount());
   assertEquals(0, m2.getRequestCount());
   
   t.stepNHandler(1);
   assertEquals(1, t.getStep());
   
   
   t.stepNHandler(1);
   assertEquals(2, t.getStep());

   t.stepNHandler(1);
   assertEquals(2, t.getStep());
 }

}
