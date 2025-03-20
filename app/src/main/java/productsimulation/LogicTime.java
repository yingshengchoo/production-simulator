package productsimulation;

import productsimulation.model.Building;

import java.util.List;

public class LogicTime {
  private int currentStep;
  private List<Building> observers;


  /**
   * Constructs a LogicTime at time step 0.
   */
  public LogicTime(){
    this.currentStep = 0;
  }

  public void addObservers(Building b){
  }

  public void removeObservers(Building b){
  }

  public void notifyAll(int timeDiff){
  }

}
