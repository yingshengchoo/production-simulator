package productsimulation;

public interface BuildingVisitor {
  void visit(Mine mine);
  void visit(Factory factory);
}
