package productsimulation;

import productsimulation.model.Building;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogicTime {
    private static final LogicTime instance = new LogicTime();
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
            // 新一步
            for(Building b: observers) {
                boolean idle = b.notified();
                finished = idle && finished;
            }
            if(finished && timeDiff == Integer.MAX_VALUE) {
                break;
            }
            currentStep += 1;
            for(Building b: observers) {
                b.updateNotified();
            }
//            Log.debugLog("currentStep:" + (currentStep-1) + " to " + currentStep + "\n");
        }
        if(finished && timeDiff == Integer.MAX_VALUE) {
            quitGame();
        }
    }

    private void quitGame() {
        Log.debugLog("The simulation is ended.");
        exitFlag = true;
    }

    public void stepNHandler(int n) {
        notifyAll(n);
    }

    public void finishHandler() {
        notifyAll(Integer.MAX_VALUE);
    }

    public boolean getExitFlag() {
        return exitFlag;
    }

    // only for test
    public void reset() {
        observers.clear();
        currentStep = 0;
    }
}
