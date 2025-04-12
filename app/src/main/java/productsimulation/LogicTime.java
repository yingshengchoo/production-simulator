package productsimulation;

import productsimulation.model.*;
import productsimulation.model.road.RequestQueue;

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

public class LogicTime implements Serializable{
//    EV2中，道路可能不通，请求可能无法顺利传达，故大幅下调MAX_STEP，以免误判为死循环
    private final int MAX_STEP = 500;
    private static LogicTime instance = new LogicTime();
    private int currentStep;
    private final Set<Building> observers;
    private boolean exitFlag;


    /**
     * private, for singleton
     */
    private LogicTime() {
        this.currentStep = 0;
        this.observers = new HashSet<>();
        this.exitFlag = false;
    }

    /**
     * singleton
     */
    public static LogicTime getInstance() {
        return instance;
    }

    public void addObservers(Building b) {
        observers.add(b);
    }

    public void removeObservers(Building b) {
        observers.remove(b);
    }

    // only for test
    public int getObserversSize() {
        return observers.size();
    }

    public int getStep() {
        return currentStep;
    }

    public void notifyAll(int timeDiff) {
        boolean finished = true;
        for(int i = 0; i < timeDiff; i++) {
            finished = true;

            RequestQueue.goOneStep();
            
            // 新一步
            for(Building b: observers) {
                boolean idle = b.notified();
                finished = idle && finished;
            }
            if(finished && timeDiff == MAX_STEP) {
                break;
            }
            //確保 Storage 有先發給Sources request 
            for(Building b: observers){
              if(b instanceof Storage){
                Storage s = (Storage) b;
                s.sendRequest();
              }
            }
            currentStep += 1;
            for(Building b: observers) {
                b.updateNotified();
            }
            Log.level2Log("==========Step from " + (currentStep-1) + " to " + currentStep + " end==========\n");
        }
        if(finished && timeDiff == MAX_STEP) {
            quitGame();
        }
    }

    private void quitGame() {
        Log.level0Log("Simulation completed at time-step " + currentStep);
        exitFlag = true;
    }

    public void stepNHandler(int n) {
        notifyAll(n);
    }

    public void finishHandler() {
        notifyAll(MAX_STEP);
    }

    public boolean getExitFlag() {
        return exitFlag;
    }

    // only for test
    public void reset() {
        observers.clear();
        currentStep = 0;
    }

    public void loadLogicTime(LogicTime logicTime){
      instance = logicTime;
    }
  
    public void accept(StateVisitor v){
      v.visit(this);
    }

  
    @Override
    public String toString(){
      return "Current Step: " + currentStep;
    }
}
