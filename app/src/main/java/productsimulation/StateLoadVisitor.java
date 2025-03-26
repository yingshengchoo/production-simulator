package productsimulation;

public class StateLoadVisitor implements StateVisitor {
    
    @Override
    public void visit(LogicTime logicTime) {
        LogicTime.getInstance().loadLogicTime(logicTime); 
    }

    @Override
    public void visit(RequestBroadcaster requestBroadcaster) {
        RequestBroadcaster.getInstance().loadRequestBroadcaster(requestBroadcaster); 
    }
}
