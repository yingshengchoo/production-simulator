package productsimulation;

import productsimulation.model.Building;

import java.util.ArrayList;
import java.util.List;

public class LogicTime {
    private static final LogicTime instance = new LogicTime();
    private int currentStep;
    private List<Building> observers;


    /**
     * private, for singleton
     */
    private LogicTime() {
        this.currentStep = 0;
        this.observers = new ArrayList<>();
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
            for(Building b: observers) {
                finished = finished && b.notified();
            }
            if(finished && timeDiff == Integer.MAX_VALUE) {
                break;
            }
            currentStep += 1;
        }
        if(finished && timeDiff == Integer.MAX_VALUE) {
            quitGame();
        }
    }

    private void quitGame() {
        System.exit(0);
    }

    public void stepNHandler(int n) {
        notifyAll(n);
    }

    public void finishHandler() {
        notifyAll(Integer.MAX_VALUE);
    }
}
