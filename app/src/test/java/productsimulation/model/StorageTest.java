package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import productsimulation.model.road.Road;
import productsimulation.model.road.RoadHandler;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.request.Request;
import productsimulation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;


public class StorageTest {

  @BeforeEach
  public void setup(){
    LogicTime.getInstance().reset();
    Board.getBoard().cleanup();
    Building.buildingGlobalList.clear();
    Recipe.recipeGlobalList.clear();
  }

  @Test
  public void test_addStorage() {
    Recipe recipe = new Recipe(1, new HashMap<>(), "thing").register();
    Storage.addStorage("storage1", new ArrayList<>(), null, null,
            new Coordinate(0, 0), new StorageType("storageType1", 10, 10, "thing"));
    assertThrows(RuntimeException.class, ()->Storage.addStorage("storage2", new ArrayList<>(), null, null,
            new Coordinate(0, 0), new StorageType("storageType1", 10, 10, "thing")));
  }
  
  @Test
  public void test_toString() {
    ArrayList<Building> sources = new ArrayList<>();
    Recipe r1 = new Recipe(3, Collections.emptyMap(), "a").register();
    Recipe r2 = new Recipe(2, Collections.emptyMap(), "socks").register();
    sources.add(new Storage("Closet", "a" , Collections.emptyList(), 100, 3, null,null));
    sources.add(new Factory("DoorInc", new BuildingType("Door", Collections.emptyMap()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", new BuildingType("Diamond", Collections.emptyMap()), Collections.emptyList(), null, null));
    Storage s1 = new Storage("Drawer", "socks", sources, 150, 10, null, null, new Coordinate(1,1));
    s1.initializeStorageType();
    String expected = "Storage\n{name='Drawer',\n stores='socks',\n sources=[Closet, DoorInc, DiamondMine],\n capacity=150,\n storage=[socks: 0],\n request queue size=0\n}";

    assertEquals(expected, s1.toString());
  
  }
  @Test
  public void test_goOneStep(){
    //Setup: A Storage
    //
    // Recipe: pairOfSocks <-- 2 x socks
    //
    //         SocksFactory(f)               
    //               |
    //           Drawer(s1)               
    //          |          |
    //SocksMine1(m1)     SocksMine2(m2)   
    
    LogicTime t = LogicTime.getInstance();
   
    ArrayList<Building> sources = new ArrayList<>();
    Recipe socks = new Recipe(1, Collections.emptyMap(), "socks").register();
    Recipe pair = new Recipe(1,Map.of("socks", 2), "pairOfSocks").register();
   
    Mine m1 = new Mine("SocksMine1", new BuildingType("SmellySocks", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy(), new Coordinate(1, 2)).register();
    Mine m2 = new Mine("SocksMine2", new BuildingType("SmellySocks", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy(), new Coordinate(2, 1)).register();
    sources.add(m1);
    sources.add(m2);
    Storage s1 = new Storage("Drawer", "socks", sources, 100, 102, new SourceQLen(), new FIFOPolicy(), new Coordinate(1,1)).register();
   
    ArrayList<Building> sources2 = new ArrayList<>();
    sources2.add(s1);
    Factory f = new Factory("SocksFactory", new BuildingType("PairOfSocks", Map.of("pairOfSocks", pair)), sources2, new SourceQLen(), new FIFOPolicy(), new Coordinate(0, 1)).register();

    assertEquals("socks", s1.getRecipeOutput());

    // connect the buildings
    RoadHandler.connectHandler(m1.getName(), s1.getName());
    RoadHandler.connectHandler(m2.getName(), s1.getName());
    RoadHandler.connectHandler(s1.getName(), f.getName());

    //F = 100*100/(100*100) = 1
    //we expect the storage to request sock at t =0 and t =1;

    assertEquals(100, s1.getR());
    assertEquals(1, s1.getFrequency());
   
    assertEquals(0, t.getStep());
    Request.userRequestHandler(pair.getOutput(),f.getName());

    //DFS propagates the requests down
    //Factory has 1 request, Storage: 2 request, M1: 1 request, M2: 1 request
    //M1 and M2 Makes the socks
    assertEquals(100, s1.getR());
    assertEquals(102, s1.getPriority());
    assertEquals(100, s1.getTotalCapacity());

    assertEquals(1, s1.getFrequency());
    assertEquals(1, f.getRequestCount());
    assertEquals(2, s1.getRequestCount());
    assertEquals(2, s1.getReqCount());
    assertEquals(0, s1.getReadyQueueCount());
    assertEquals(0, s1.getStockCount());
    assertEquals(1, m1.getRequestCount());
    assertEquals(1, m2.getRequestCount());
   
    t.stepNHandler(1);
    assertEquals(1, t.getStep());
    assertEquals(99, s1.getR());

    //M1 and M2 finishes making the requests and sends ingredient to storage
    //As Step 1 % frequency 1 = 0, storage sends a request to M1
    assertEquals(1, s1.getFrequency());
    assertTrue(LogicTime.getInstance().getStep() % s1.getFrequency() == 0); 
    assertEquals(socks, m1.type.getRecipeByProductName(socks.getOutput()));
    assertEquals(1, s1.getFrequency());
    assertEquals(1, f.getRequestCount());
    assertEquals(2, s1.getRequestCount());
    assertEquals(2, s1.getReqCount());
    assertEquals(0, s1.getReadyQueueCount());
    assertEquals(0, s1.getStockCount());
    assertEquals(1, m1.getRequestCount());
    assertEquals(0, m2.getRequestCount());
    assertEquals(1, s1.getStorage().size());

    //Storage complets the requests sending the ingredients to Factory
    //M1 finishes making the ingredient and sends it to storage.
    //Now, storage has 1 item
    //Since frequency is still 1, it sends a request to m2.
    t.stepNHandler(1);
    assertEquals(2, t.getStep());
    assertEquals(2, s1.getFrequency());
    assertEquals(1, f.getRequestCount());
    assertEquals(-1, s1.getRequestCount());
    assertEquals(0, s1.getReqCount());
    assertEquals(0, s1.getReadyQueueCount());
    assertEquals(1, s1.getStockCount());
    assertEquals(0, m1.getRequestCount());
    assertEquals(0, m2.getRequestCount());
    assertEquals(1, s1.getStorage().size());

    //Factory finishes the pair of socks and the request is done.
    //M2 finishes and sends the ingredient to storage increasing it to 2.

    t.stepNHandler(1);
    assertEquals(3, t.getStep());
    assertEquals(2, s1.getFrequency());
    assertEquals(0, f.getRequestCount());
    assertEquals(-1, s1.getRequestCount());
    assertEquals(1, s1.getStorage().size());
    assertEquals(0, s1.getReadyQueueCount());
    assertEquals(0, s1.getReqCount());
    assertEquals(1, s1.getStockCount());
    assertEquals(1, m1.getRequestCount());
    assertEquals(0, m2.getRequestCount());

    t.stepNHandler(1);
    assertEquals(4, t.getStep());
    assertEquals(2, s1.getFrequency());
    assertEquals(0, f.getRequestCount());
    assertEquals(-2, s1.getRequestCount());
    assertEquals(0, s1.getReqCount());
    assertEquals(0, s1.getReadyQueueCount());
    assertEquals(2, s1.getStockCount());
    assertEquals(0, m1.getRequestCount());
    assertEquals(0, m2.getRequestCount());
    assertEquals(1, s1.getStorage().size());

    t.stepNHandler(1);
    assertEquals(5, t.getStep());
    assertEquals(2, s1.getFrequency());
    assertEquals(0, f.getRequestCount());
    assertEquals(-2, s1.getRequestCount());
    assertEquals(0, s1.getReqCount());
    assertEquals(0, s1.getReadyQueueCount());
    assertEquals(2, s1.getStockCount());
    assertEquals(1, m1.getRequestCount());
    assertEquals(0, m2.getRequestCount());
    assertEquals(1, s1.getStorage().size());

    Request.userRequestHandler(pair.getOutput(),f.getName());
    t.stepNHandler(3);

  }

  @Test
  public void test_updateFrequency(){
    Recipe r1 = new Recipe(3, Collections.emptyMap(), "a").register();
    Recipe r2 = new Recipe(2, Collections.emptyMap(), "socks").register();

    Storage s1 = new Storage("Drawer", "socks", 150, 10, null, null, new Coordinate(4,2));
    s1.initializeStorageType();
    assertEquals((int)Math.ceil((double)(s1.getTotalCapacity() * s1.getTotalCapacity()) / (double)(s1.getR() * s1.getPriority())), s1.getFrequency());


    Storage s2 = new Storage("closet", "socks", 0, 10, null, null, new Coordinate(1,2));
    s2.initializeStorageType();
    assertEquals(0, s2.getR());
    assertEquals(-1, s2.getFrequency());
  }

  @Test
  public void test_noSendRequest(){
    //Setup: A Storage
   
    ArrayList<Building> sources = new ArrayList<>();
    Recipe socks = new Recipe(100, Collections.emptyMap(), "socks").register();
    Recipe pair = new Recipe(1,Map.of("socks", 2), "pairOfSocks").register();
   
    Mine m1 = new Mine("SocksMine1", new BuildingType("SmellySocks", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
    Mine m2 = new Mine("SocksMine2", new BuildingType("SmellySocks", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
    sources.add(m1);
    sources.add(m2);
    Storage s1 = new Storage("Drawer", "socks", sources, 0, 102, new SourceQLen(), new FIFOPolicy(), new Coordinate(3,3));

    s1.initializeStorageType();
   
    assertEquals("socks", s1.getRecipeOutput());

    //frequency is -1, so no requests should be sent to sources
    assertEquals(-1, s1.getFrequency());
    s1.sendRequest();
    s1.sendRequest();
    s1.sendRequest();
    assertEquals(0, m1.getRequestCount());
    assertEquals(0, m2.getRequestCount());
  }

  @Test
  public void test_getTotalLatency(){
    ArrayList<Building> sources = new ArrayList<>();
    Recipe socks = new Recipe(2, Collections.emptyMap(), "socks").register();
    Recipe pair = new Recipe(1,Map.of("socks", 2), "pairOfSocks").register();
   
    Mine m1 = new Mine("SocksMine1", new BuildingType("SockTree", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy()).register();
    Mine m2 = new Mine("SocksMine2", new BuildingType("SockOre", Map.of("socks", socks)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy()).register();
    sources.add(m1);
    sources.add(m2);
    Storage s1 = new Storage("Drawer", "socks", sources, 100, 102, new SourceQLen(), new FIFOPolicy(), new Coordinate(1,0)).register();

    RoadHandler.connectHandler(m1.getName(), s1.getName());
    RoadHandler.connectHandler(m2.getName(), s1.getName());

    assertEquals(0, s1.getTotalRemainTime());
    s1.addRequest(Request.getDummyRequest("socks", s1));
    assertEquals(2, s1.getTotalRemainTime());
    s1.addRequest(Request.getDummyRequest("socks", s1));
    assertEquals(4, s1.getTotalRemainTime());

    Storage s2 = new Storage("Drawer", "socks", sources, 100, 102, new SourceQLen(), new FIFOPolicy(), new Coordinate(5,4)).register();
   
    assertEquals(0, s2.getTotalRemainTime());
    s2.updateStorage("socks");
    assertEquals(-2, s2.getTotalRemainTime());
    s2.updateStorage("socks");
    assertEquals(-4, s2.getTotalRemainTime());
  }

  @Test
  public void test_getCurrentRemainTime(){
    ArrayList<Building> sources = new ArrayList<>();
    Recipe socks1 = new Recipe(10, Collections.emptyMap(), "socks").register();
    Recipe socks2 = new Recipe(2, Collections.emptyMap(), "socks").register();
   
    Recipe pair = new Recipe(1,Map.of("socks", 2), "pairOfSocks").register();
   
    Mine m1 = new Mine("SocksMine1", new BuildingType("SockED", Map.of("socks", socks1)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
    Mine m2 = new Mine("SocksMine2", new BuildingType("SockET", Map.of("socks", socks2)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
    sources.add(m1);
    sources.add(m2);
    Storage s1 = new Storage("Drawer", "socks", sources, 100, 102, new SourceQLen(), new FIFOPolicy(), new Coordinate(7,7));

    s1.initializeStorageType();
   
    assertEquals(1, s1.getCurrentRemainTime());
   
    s1.updateStorage("socks");
    assertEquals(0, s1.getCurrentRemainTime());
   
  }

  
  @Test
  public void test_emptySource(){
    ArrayList<Building> sources = new ArrayList<>();
    Recipe socks1 = new Recipe(10, Collections.emptyMap(), "socks").register();
    Recipe socks2 = new Recipe(2, Collections.emptyMap(), "socks").register();
   
    Recipe pair = new Recipe(1,Map.of("socks", 2), "pairOfSocks").register();
   
    Mine m1 = new Mine("SocksMine1", new BuildingType("SockED", Map.of("socks", socks1)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
    Mine m2 = new Mine("SocksMine2", new BuildingType("SockET", Map.of("socks", socks2)), Collections.emptyList(), new SourceQLen(), new FIFOPolicy());
    sources.add(m1);
    sources.add(m2);
    Storage s2 = new Storage("Drawer", "pairOfSocks", sources, 100, 102, new SourceQLen(), new FIFOPolicy(), new Coordinate(7,7));

    Storage s1 =new Storage("Drawer", "socks", null, 100, 100, null, null, new Coordinate(100, 100));
    assertDoesNotThrow(()->s1.sendRequest());

    s2.initializeStorageType();
    assertDoesNotThrow(()->s2.sendRequest());
  }
}
