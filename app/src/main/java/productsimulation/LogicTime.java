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
//    private boolean autoFlag;


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

            State state = State.noThrowGetInstance();
            if(state != null) {
                state.updateState();
            }

            TransportQueue.goOneStep();
            //更新建房狀態
            BuildingCostHandler.update();

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

//    public void setRealTimeMode(boolean flag) {
//        autoFlag = flag;
//    }

//    public String realTimeHandler(int speed) {
//        if (speed <= 0) {
//            return "Invalid speed. Speed must be greater than 0.";
//        }
//
//        try {
//            while (autoFlag) { // 持续运行
//                stepNHandler(speed); // 每秒执行 speed 步
//                Thread.sleep(1000); // 暂停 1 秒
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); // 恢复中断状态
//            return e.getClass().getSimpleName() + ": " + e.getMessage();
//        } catch (Exception e) {
//            return e.getClass().getSimpleName() + ": " + e.getMessage();
//        }
//        return null;
//    }

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
