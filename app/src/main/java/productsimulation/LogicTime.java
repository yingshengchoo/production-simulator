package productsimulation;

import productsimulation.model.*;
import productsimulation.model.road.TransportQueue;

import java.io.Serializable;

public class LogicTime implements Serializable{
//    EV2中，道路可能不通，请求可能无法顺利传达，故大幅下调MAX_STEP，以免误判为死循环
    private final int MAX_STEP = 500;
    private static LogicTime instance = new LogicTime();
    private int currentStep;
    private boolean exitFlag;


    /**
     * private, for singleton
     */
    private LogicTime() {
        this.currentStep = 0;
        this.exitFlag = false;
    }

    /**
     * singleton
     */
    public static LogicTime getInstance() {
        return instance;
    }

    public int getStep() {
        return currentStep;
    }

    public void notifyAll(int timeDiff) {
        boolean finished = true;
        for(int i = 0; i < timeDiff; i++) {
            finished = true;

            State.getInstance().updateState();
            TransportQueue.goOneStep();
            
            // 新一步
            for(Building b: Building.buildingGlobalList) {
                boolean idle = b.notified();
                finished = idle && finished;
            }
            if(finished && timeDiff == MAX_STEP) {
                break;
            }
            //確保 Storage 有先發給Sources request
            for(Building b: Building.buildingGlobalList){
              if(b instanceof Storage){
                Storage s = (Storage) b;
                s.sendRequest();
              }
            }
            currentStep += 1;
            for(Building b: Building.buildingGlobalList) {
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

    public String stepNHandler(int n) {
        try {
            notifyAll(n);
            return null;
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    public String finishHandler() {
        try {
            notifyAll(MAX_STEP);
            return null;
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    public boolean getExitFlag() {
        return exitFlag;
    }

    // only for test
    public void reset() {
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
