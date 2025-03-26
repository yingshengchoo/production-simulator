package productsimulation;

public interface StateVisitor {
  void visit(LogicTime logicTime);
  void visit(RequestBroadcaster requestBroadcaster);
}
