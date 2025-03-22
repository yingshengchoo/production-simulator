package productsimulation.model;

public interface BuildingVisitor {
  void visit(Mine mine);
  void visit(Factory factory);
}
