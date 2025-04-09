package productsimulation.GUI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;

import java.util.List;

/**
 * MapView displays the simulation's buildings on an interactive map.
 * It computes the bounding box from building coordinates and scales the view.
 * Users can pan (drag) and zoom (scroll) to examine different parts of the simulation.
 */
public class MapView extends Pane {
    private final Group contentGroup = new Group(); // Group holding building nodes
    private final State state;
    private final DoubleProperty scaleFactor = new SimpleDoubleProperty(1.0);

    // Variables for panning
    private double mouseAnchorX, mouseAnchorY;
    private double translateAnchorX, translateAnchorY;

    public MapView(State state) {
        this.state = state;
        getChildren().add(contentGroup);
        setPrefSize(800, 600);

        setupInteraction();

        // Listen to size changes so we can refresh the view when the Pane is sized
        widthProperty().addListener((obs, oldVal, newVal) -> refresh());
        heightProperty().addListener((obs, oldVal, newVal) -> refresh());

        refresh();
    }

    /**
     * Refreshes the map view by recalculating the bounding box and re-drawing building nodes.
     */
    public void refresh() {
        if (getWidth() <= 0 || getHeight() <= 0) return;

        contentGroup.getChildren().clear();
        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

        // Calculate the bounding box of building coordinates
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (Building b : buildings) {
            int x = b.getX();
            int y = b.getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        // Add a 1-unit margin on each side
        minX--; maxX++;
        minY--; maxY++;

        double worldWidth = maxX - minX;
        double worldHeight = maxY - minY;

        // Create and position each building node in world coordinates
        for (Building b : buildings) {
            Node node = createBuildingNode(b);
            double worldX = b.getX() - minX;
            double worldY = b.getY() - minY;
            node.setLayoutX(worldX);
            // Flip the y-axis so the bottom of the world is at the bottom of the Pane
            node.setLayoutY(worldHeight - worldY - 1);
            contentGroup.getChildren().add(node);
        }

        // Determine an initial scale so that all buildings fit (with some margin)
        double scaleX = getWidth() / worldWidth;
        double scaleY = getHeight() / worldHeight;
        double initialScale = Math.min(scaleX, scaleY) * 0.9;
        if (Double.isInfinite(initialScale) || initialScale <= 0) {
            initialScale = 10.0; // Fallback scale
        }
        scaleFactor.set(initialScale);
        contentGroup.setScaleX(scaleFactor.get());
        contentGroup.setScaleY(scaleFactor.get());

        // Center the content in the Pane
        double extraX = (getWidth() - worldWidth * scaleFactor.get()) / 2.0;
        double extraY = (getHeight() - worldHeight * scaleFactor.get()) / 2.0;
        contentGroup.setTranslateX(extraX);
        contentGroup.setTranslateY(extraY);
    }

    /**
     * Creates a Node (shape) representing a building.
     * The node's fill color and shape depend on the building type.
     * It is set to be mouse-transparent so that only the map handles drag/zoom.
     */
    private Node createBuildingNode(Building b) {
        double size = 1.0; // world unit size
        Node node;

        if (b instanceof Mine) {
            Circle c = new Circle(size / 2);
            c.setFill(Color.LIGHTBLUE);
            c.setStroke(Color.BLACK);
            c.setStyle("-fx-fill: lightblue; -fx-stroke: black;");
            node = c;
        } else if (b instanceof Factory) {
            Rectangle r = new Rectangle(size, size);
            r.setFill(Color.LIGHTGREEN);
            r.setStroke(Color.BLACK);
            r.setStyle("-fx-fill: lightgreen; -fx-stroke: black;");
            node = r;
        } else if (b instanceof Storage) {
            Rectangle r = new Rectangle(size, size);
            r.setFill(Color.LIGHTYELLOW);
            r.setStroke(Color.BLACK);
            r.setRotate(45);
            r.setStyle("-fx-fill: lightyellow; -fx-stroke: black;");
            node = r;
        } else {
            Rectangle r = new Rectangle(size, size);
            r.setFill(Color.BEIGE);
            r.setStroke(Color.BLACK);
            r.setStyle("-fx-fill: beige; -fx-stroke: black;");
            node = r;
        }

        node.setMouseTransparent(true); // Prevent individual dragging
        Tooltip.install(node, new Tooltip(b.getName()));
        return node;
    }

    /**
     * Sets up mouse event handlers on the MapView to support panning and zooming.
     * The entire map (contentGroup) is moved/scaled without altering individual building positions.
     */
    private void setupInteraction() {
        // Handle mouse press for panning
        setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mouseAnchorX = event.getSceneX();
                mouseAnchorY = event.getSceneY();
                translateAnchorX = contentGroup.getTranslateX();
                translateAnchorY = contentGroup.getTranslateY();
                setCursor(Cursor.MOVE);
            }
        });
        // Handle mouse drag for panning
        setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double deltaX = event.getSceneX() - mouseAnchorX;
                double deltaY = event.getSceneY() - mouseAnchorY;
                contentGroup.setTranslateX(translateAnchorX + deltaX);
                contentGroup.setTranslateY(translateAnchorY + deltaY);
            }
        });
        setOnMouseReleased(event -> setCursor(Cursor.DEFAULT));

        // Handle scroll events for zooming
        setOnScroll((ScrollEvent event) -> {
            double delta = 1.2;
            double scale = contentGroup.getScaleX();
            if (event.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }
            // Clamp the scale value to prevent excessive zoom
            if (scale < 0.01) scale = 0.01;
            if (scale > 100) scale = 100;

            contentGroup.setScaleX(scale);
            contentGroup.setScaleY(scale);
            scaleFactor.set(scale);
            event.consume();
        });
    }
}