package productsimulation.GUI;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import productsimulation.model.drone.DronePort;
import productsimulation.model.road.Direction;
import productsimulation.model.road.Road;
import productsimulation.model.road.RoadTile;

import java.util.List;
import java.util.Objects;

/**
 * Visualizes the production simulation world on a JavaFX Canvas.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Compute an appropriate scale and translation to fit all buildings with padding.</li>
 *   <li>Render a coordinate grid with labels.</li>
 *   <li>Draw one-way road segments with directional arrows.</li>
 *   <li>Render buildings with color coding and error highlighting.</li>
 *   <li>Highlight the grid cell under the mouse, distinguishing buildings vs. empty cells.</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 *   BoardDisplay board = new BoardDisplay(state, feedbackPane);
 *   rootPane.setCenter(board.getCanvasPane());
 *   board.refresh();
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 * @since 1.0
 */
public final class BoardDisplay {

    // Canvas dimensions
    private static final double CANVAS_WIDTH  = 800;
    private static final double CANVAS_HEIGHT = 600;

    // Padding around the content
    private static final double TOP_PADDING  = 50;
    private static final double LEFT_PADDING = 50;

    // Extra grid cells margin
    private static final int GRID_MARGIN = 2;

    // Colors for various elements
    private static final Color GRID_COLOR       = Color.DARKGRAY;
    private static final Color BUILDING_OUTLINE = Color.BLACK;
    private static final Color BUILDING_NO_SRC  = Color.RED;
    private static final Color ROAD_FILL        = Color.GREY;
    private static final Color ROAD_STROKE      = Color.WHITE;
    private static final Color HIGHLIGHT_BUILD  = Color.ORANGE;
    private static final Color HIGHLIGHT_EMPTY  = Color.CORAL;

    private final State state;
    private final Canvas canvas;

    // Current scale (pixels per grid cell) and translation offsets
    private double scale = 40.0;
    private double offX  = 0;
    private double offY  = 0;

    // Bounds of the grid currently rendered
    private int visMinX, visMaxX, visMinY, visMaxY;

    // Hover state: which building and cell are under the mouse
    private Building  hoveredBuilding;
    private Coordinate hoveredCoord;

    public BoardDisplay(State state) {
        this.state  = Objects.requireNonNull(state, "state cannot be null");
        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMove);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, e -> clearHover());
    }

    public Node getCanvasPane() {
        return canvas;
    }

    public void refresh() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

        computeTransform(buildings);
        drawGrid(g);
        drawRoads(g);
        drawBuildings(g, buildings);
        drawHighlight(g);
    }

    public Coordinate screenToGrid(double px, double py) {
        int gx = (int) Math.floor((px - offX) / scale);
        int gy = (int) Math.floor((py - offY) / scale);
        return new Coordinate(gx, gy);
    }

    public Building findBuilding(double px, double py) {
        Coordinate c = screenToGrid(px, py);
        return state.getBuildings().stream()
                .filter(b -> b.getX() == c.x && b.getY() == c.y)
                .findFirst().orElse(null);
    }

    public RoadTile findRoadTile(double px, double py) {
        return Road.existingRoadTiles.get(screenToGrid(px, py));
    }

    private void computeTransform(List<Building> buildings) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (Building b : buildings) {
            minX = Math.min(minX, b.getX());
            maxX = Math.max(maxX, b.getX());
            minY = Math.min(minY, b.getY());
            maxY = Math.max(maxY, b.getY());
        }
        minX = Math.max(minX - GRID_MARGIN, 0);
        minY = Math.max(minY - GRID_MARGIN, 0);
        maxX += GRID_MARGIN;
        maxY += GRID_MARGIN;

        visMinX = minX; visMaxX = maxX;
        visMinY = minY; visMaxY = maxY;

        int wUnits = maxX - minX + 1;
        int hUnits = maxY - minY + 1;
        double wAvail = canvas.getWidth()  - LEFT_PADDING;
        double hAvail = canvas.getHeight() - TOP_PADDING;
        scale = Math.min(wAvail / wUnits, hAvail / hUnits);

        offX = LEFT_PADDING - minX * scale + (wAvail - wUnits * scale) / 2;
        offY = TOP_PADDING  - minY * scale + (hAvail - hUnits * scale) / 2;
    }

    private void drawGrid(GraphicsContext g) {
        g.setStroke(GRID_COLOR);
        g.setLineWidth(1);
        int wUnits = visMaxX - visMinX + 1;
        int hUnits = visMaxY - visMinY + 1;
        for (int i = visMinX; i <= visMaxX + 1; i++) {
            double x = i * scale + offX;
            g.strokeLine(x, offY, x, offY + hUnits * scale);
            if (i <= visMaxX) {
                g.fillText(String.valueOf(i), x + scale / 2 - 5, offY - 10);
            }
        }
        for (int j = visMinY; j <= visMaxY + 1; j++) {
            double y = j * scale + offY;
            g.strokeLine(offX, y, offX + wUnits * scale, y);
            if (j <= visMaxY) {
                g.fillText(String.valueOf(j), offX - 30, y + scale / 2 + 5);
            }
        }
    }

    private void drawRoads(GraphicsContext g) {
        g.setFill(ROAD_FILL);
        g.setStroke(ROAD_STROKE);
        g.setLineWidth(2);
        for (RoadTile t : Road.existingRoadTiles.values()) {
            var c = t.getCoordinate();
            if (c.x < visMinX || c.x > visMaxX || c.y < visMinY || c.y > visMaxY) continue;
            double x = c.x * scale + offX;
            double y = c.y * scale + offY;
            g.fillRect(x, y, scale, scale);
            drawRoadConnections(g, t, x, y);
        }
    }

    private void drawRoadConnections(GraphicsContext g, RoadTile t, double x, double y) {
        double cx = x + scale / 2;
        double cy = y + scale / 2;
        double m  = scale / 4;
        double a  = scale / 6;
        if (t.getFromDirection().hasDirection(Direction.LEFT))  g.strokeLine(x + m, cy, cx, cy);
        if (t.getFromDirection().hasDirection(Direction.RIGHT)) g.strokeLine(cx, cy, x + scale - m, cy);
        if (t.getFromDirection().hasDirection(Direction.UP))    g.strokeLine(cx, y + m, cx, cy);
        if (t.getFromDirection().hasDirection(Direction.DOWN))  g.strokeLine(cx, cy, cx, y + scale - m);
        if (t.getToDirection().hasDirection(Direction.LEFT))  strokeArrow(g, cx, cy, x + m, cy, a);
        if (t.getToDirection().hasDirection(Direction.RIGHT)) strokeArrow(g, cx, cy, x + scale - m, cy, a);
        if (t.getToDirection().hasDirection(Direction.UP))    strokeArrow(g, cx, cy, cx, y + m, a);
        if (t.getToDirection().hasDirection(Direction.DOWN))  strokeArrow(g, cx, cy, cx, y + scale - m, a);
    }

    private void drawBuildings(GraphicsContext g, List<Building> bs) {
        for (Building b : bs) {
            double x = b.getX() * scale + offX;
            double y = b.getY() * scale + offY;
            g.setFill(buildingFill(b));
            g.fillRect(x, y, scale, scale);
            g.setStroke(BUILDING_OUTLINE);
            g.strokeRect(x, y, scale, scale);
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                g.setStroke(BUILDING_NO_SRC);
                g.setLineWidth(3);
                g.strokeRect(x, y, scale, scale);
                g.setLineWidth(1);
            }
            drawName(g, b.getName(), x, y);
        }
    }

    private void drawHighlight(GraphicsContext g) {
        if (hoveredCoord == null) return;
        int gx = hoveredCoord.x, gy = hoveredCoord.y;
        if (gx < visMinX || gx > visMaxX || gy < visMinY || gy > visMaxY) return;
        if (Road.existingRoadTiles.containsKey(hoveredCoord)) return;
        double x = gx * scale + offX;
        double y = gy * scale + offY;
        g.setStroke(hoveredBuilding != null ? HIGHLIGHT_BUILD : HIGHLIGHT_EMPTY);
        g.setLineWidth(hoveredBuilding != null ? 5 : 3);
        g.strokeRect(x, y, scale, scale);
        g.setLineWidth(1);
    }

    private void onMouseMove(MouseEvent e) {
        var grid = screenToGrid(e.getX(), e.getY());
        if (grid.x < visMinX || grid.x > visMaxX || grid.y < visMinY || grid.y > visMaxY) {
            clearHover();
            return;
        }
        var b = findBuilding(e.getX(), e.getY());
        if (!Objects.equals(b, hoveredBuilding) || !grid.equals(hoveredCoord)) {
            hoveredBuilding = b;
            hoveredCoord    = grid;
            refresh();
        }
        canvas.setCursor(b != null ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void onMouseClick(MouseEvent e) {
        Coordinate grid = screenToGrid(e.getX(), e.getY());
        if (grid.x < visMinX || grid.x > visMaxX || grid.y < visMinY || grid.y > visMaxY) {
            return;
        }
        Building b  = findBuilding(e.getX(), e.getY());
        if (b != null) {
            if (b instanceof DronePort) {
                new DronePortWindow((DronePort) b).show();
                return;
            }
            BuildingInfoWindow.show(b);
            return;
        }
        RoadTile rt = findRoadTile(e.getX(), e.getY());
        if (rt != null) {
            BuildingInfoWindow.show(rt);
            return;
        }
        AddBuildingAtCellWindow.show(state, screenToGrid(e.getX(), e.getY()), this::refresh);
    }

    private void clearHover() {
        if (hoveredCoord != null || hoveredBuilding != null) {
            hoveredCoord    = null;
            hoveredBuilding = null;
            canvas.setCursor(Cursor.DEFAULT);
            refresh();
        }
    }

    private static Color buildingFill(Building b) {
        if (b instanceof Mine)    return Color.LIGHTBLUE;
        if (b instanceof Factory) return Color.LIGHTGREEN;
        if (b instanceof Storage) return Color.LIGHTYELLOW;
        return Color.BEIGE;
    }

    private void drawName(GraphicsContext g, String name, double x, double y) {
        g.setFill(Color.BLACK);
        double baseY = y + scale * 0.5;
        if (name.length() <= 8) {
            g.fillText(name, x + 3, baseY);
        } else {
            int mid = name.length() / 2;
            g.fillText(name.substring(0, mid), x + 3, baseY - scale * 0.15);
            g.fillText(name.substring(mid),     x + 3, baseY + scale * 0.15);
        }
    }

    private static void strokeArrow(GraphicsContext g,
                                    double sx, double sy,
                                    double ex, double ey,
                                    double size) {
        g.strokeLine(sx, sy, ex, ey);
        double dx = ex - sx, dy = ey - sy;
        double len = Math.hypot(dx, dy);
        if (len == 0) return;
        dx /= len; dy /= len;
        double x1 = ex - size * dx - size * dy * 0.5;
        double y1 = ey - size * dy + size * dx * 0.5;
        double x2 = ex - size * dx + size * dy * 0.5;
        double y2 = ey - size * dy - size * dx * 0.5;
        g.strokeLine(ex, ey, x1, y1);
        g.strokeLine(ex, ey, x2, y2);
    }
}