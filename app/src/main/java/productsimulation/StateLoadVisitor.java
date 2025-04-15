package productsimulation;

public class StateLoadVisitor implements StateVisitor {
    
    @Override
    public void visit(LogicTime logicTime) {
        LogicTime.getInstance().loadLogicTime(logicTime); 
    }
}
