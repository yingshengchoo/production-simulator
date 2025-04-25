package productsimulation.GUI;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import productsimulation.model.drone.Drone;
import productsimulation.model.drone.DronePort;
import productsimulation.model.drone.DroneState;
import productsimulation.model.road.Direction;
import productsimulation.model.road.Road;
import productsimulation.model.road.RoadTile;
import productsimulation.model.waste.WasteDisposal;

import java.util.List;
import java.util.Objects;

/**
 * Visualizes the production simulation world on a JavaFX Canvas.
 * <p>
 * - Shows a grid and axis labels.
 * - Draws directed roads with arrows.
 * - Renders mines, factories, storage, and now drone‐ports (with drone counts).
 * - Animates drones in flight.
 * - Highlights the cell under the mouse.
 * - Right-clicking a DronePort offers “Construct Drone” (up to 10).
 *
 * Based on the original in combined.java :contentReference[oaicite:1]{index=1}.
 */
public final class BoardDisplay {

    // ─── Canvas & Layout ────────────────────────────────────────────

    private static final double CANVAS_WIDTH   = 800;
    private static final double CANVAS_HEIGHT  = 600;
    private static final double TOP_PADDING    = 50;
    private static final double LEFT_PADDING   = 50;
    private static final int    GRID_MARGIN    = 2;

    private static final Color GRID_COLOR       = Color.DARKGRAY;
    private static final Color BUILDING_OUTLINE = Color.BLACK;
    private static final Color BUILDING_NO_SRC  = Color.RED;
    private static final Color ROAD_FILL        = Color.GREY;
    private static final Color ROAD_STROKE      = Color.WHITE;
    private static final Color HIGHLIGHT_BUILD  = Color.ORANGE;
    private static final Color HIGHLIGHT_EMPTY  = Color.CORAL;

    private final State state;
    private final Canvas canvas;
    private double scale = 40.0, offX = 0, offY = 0;
    private int visMinX, visMaxX, visMinY, visMaxY;

    // Hover tracking
    private Building  hoveredBuilding;
    private Coordinate hoveredCoord;

    public BoardDisplay(State state) {
        this.state  = Objects.requireNonNull(state, "state cannot be null");
        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        // Move, click, exit for hover / info windows
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED,   this::onMouseMove);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED,  e -> clearHover());

        // Right-click on a DronePort → “Construct Drone” menu
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Building b = findBuilding(e.getX(), e.getY());
                if (b instanceof DronePort port) {
                    ContextMenu menu = new ContextMenu();
                    MenuItem mi = new MenuItem("Construct Drone");
                    mi.setOnAction(ev -> {
                        if (!port.constructDrone()) {
                            new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.WARNING,
                                    "Port full (max 10 drones)"
                            ).showAndWait();
                        }
                        refresh();
                    });
                    menu.getItems().add(mi);
                    menu.show(canvas, e.getScreenX(), e.getScreenY());
                }
            }
        });
    }

    public Node getCanvasPane() {
        return canvas;
    }

    // ─── Main Refresh ───────────────────────────────────────────────

    public void refresh() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

        computeTransform(buildings);
        drawGrid(g);
        drawRoads(g);
        drawBuildings(g, buildings);
        drawDrones(g);               // ← new
        drawHighlight(g);
    }

    // ─── Coordinate Conversion ──────────────────────────────────────

    public Coordinate screenToGrid(double px, double py) {
        int gx = (int)Math.floor((px - offX) / scale);
        int gy = (int)Math.floor((py - offY) / scale);
        return new Coordinate(gx, gy);
    }

    public Building findBuilding(double px, double py) {
        Coordinate c = screenToGrid(px, py);
        return state.getBuildings().stream()
                .filter(b -> b.getX() == c.x && b.getY() == c.y)
                .findFirst().orElse(null);
    }

    public List<RoadTile> findRoadTile(double px, double py) {
        return Road.existingRoadTiles.get(screenToGrid(px, py));
    }

    // ─── Layout Computation ─────────────────────────────────────────

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

    // ─── Drawing Helpers ────────────────────────────────────────────

    private void drawGrid(GraphicsContext g) {
        g.setStroke(GRID_COLOR);
        g.setLineWidth(1);

        int wUnits = visMaxX - visMinX + 1;
        int hUnits = visMaxY - visMinY + 1;
        for (int i = visMinX; i <= visMaxX + 1; i++) {
            double x = i * scale + offX;
            g.strokeLine(x, offY, x, offY + hUnits * scale);
            if (i <= visMaxX) {
                g.fillText(String.valueOf(i), x + scale/2 - 5, offY - 10);
            }
        }
        for (int j = visMinY; j <= visMaxY + 1; j++) {
            double y = j * scale + offY;
            g.strokeLine(offX, y, offX + wUnits * scale, y);
            if (j <= visMaxY) {
                g.fillText(String.valueOf(j), offX - 30, y + scale/2 + 5);
            }
        }
    }

    private void drawRoads(GraphicsContext g) {
        g.setFill(ROAD_FILL);
        g.setStroke(ROAD_STROKE);
        g.setLineWidth(2);
        for (List<RoadTile> tilesAtC : Road.existingRoadTiles.values()) {
            RoadTile t = tilesAtC.get(0);
            Coordinate c = t.getCoordinate();
            if (c.x < visMinX || c.x > visMaxX || c.y < visMinY || c.y > visMaxY) continue;
            double x = c.x * scale + offX;
            double y = c.y * scale + offY;
            g.fillRect(x, y, scale, scale);
            // 将一个坐标上的所有tile合成一块临时tile，临时tile仅用于GUI绘制
            RoadTile tmp = new RoadTile(c);
            Direction fromDirection = tmp.getFromDirection();
            Direction toDirection = tmp.getToDirection();
            for(RoadTile tile: tilesAtC) {
                fromDirection.addDirection(tile.getFromDirection());
                toDirection.addDirection(tile.getToDirection());
            }
            drawRoadConnections(g, tmp, x, y);
        }
    }

    private void drawRoadConnections(GraphicsContext g, RoadTile t, double x, double y) {
        double cx = x + scale/2, cy = y + scale/2;
        double m  = scale/4, a = scale/6;
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
            double x = b.getX()*scale + offX, y = b.getY()*scale + offY;

            if (b instanceof DronePort port) {
                // DronePort: light‐blue circle + drone count
                g.setFill(Color.LIGHTBLUE);
                g.fillOval(x, y, scale, scale);
                g.setStroke(BUILDING_OUTLINE);
                g.strokeOval(x, y, scale, scale);
                g.setFill(Color.BLACK);
                g.fillText(
                        String.valueOf(port.getName()),
                        x + scale*0.3,
                        y + scale*0.7
                );
            }
            else {
                // original rect‐based buildings :contentReference[oaicite:2]{index=2}&#8203;:contentReference[oaicite:3]{index=3}
                g.setFill(buildingFill(b));
                g.fillRect(x, y, scale, scale);
                g.setStroke(BUILDING_OUTLINE);
                g.strokeRect(x, y, scale, scale);
                if (!(b instanceof Mine) && (b.getSources()==null || b.getSources().isEmpty())) {
                    g.setStroke(BUILDING_NO_SRC);
                    g.setLineWidth(3);
                    g.strokeRect(x, y, scale, scale);
                    g.setLineWidth(1);
                }
                drawName(g, b.getName(), x, y);
            }
        }
    }

    private void drawDrones(GraphicsContext g) {
        // Animate each drone from each port :contentReference[oaicite:4]{index=4}&#8203;:contentReference[oaicite:5]{index=5}
        for (Building b : state.getBuildings()) {
            if (!(b instanceof DronePort port)) continue;
            for (Drone d : port.getDrones()) {
                Coordinate p = d.getPosition();
                double x = offX + p.x*scale + scale/4;
                double y = offY + p.y*scale + scale/4;

                // choose color by state
                DroneState st = d.getState();
                Color c = st.equals(DroneState.TO_SOURCE)   ? Color.ORANGE
                        : st.equals(DroneState.TO_DEST)     ? Color.GREEN
                        : st.equals(DroneState.RETURNING)   ? Color.PURPLE
                        : Color.GRAY;

                g.setFill(c);
                g.fillOval(x, y, scale/2, scale/2);
            }
        }
    }

    private void drawHighlight(GraphicsContext g) {
        if (hoveredCoord == null) return;
        int gx = hoveredCoord.x, gy = hoveredCoord.y;
        if (gx < visMinX || gx > visMaxX || gy < visMinY || gy > visMaxY) return;
        if (Road.existingRoadTiles.containsKey(hoveredCoord)) return;

        double x = gx*scale + offX, y = gy*scale + offY;
        g.setStroke(hoveredBuilding!=null ? HIGHLIGHT_BUILD : HIGHLIGHT_EMPTY);
        g.setLineWidth(hoveredBuilding!=null ? 5 : 3);
        g.strokeRect(x, y, scale, scale);
        g.setLineWidth(1);
    }

    // ─── Input Handlers ─────────────────────────────────────────────

    private void onMouseMove(MouseEvent e) {
        var grid = screenToGrid(e.getX(), e.getY());
        if (grid.x<visMinX||grid.x>visMaxX||grid.y<visMinY||grid.y>visMaxY) {
            clearHover(); return;
        }
        var b = findBuilding(e.getX(), e.getY());
        if (!Objects.equals(b, hoveredBuilding) || !grid.equals(hoveredCoord)) {
            hoveredBuilding = b;
            hoveredCoord    = grid;
            refresh();
        }
        canvas.setCursor(b!=null ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void onMouseClick(MouseEvent e) {
        Coordinate grid = screenToGrid(e.getX(), e.getY());
        if (grid.x<visMinX||grid.x>visMaxX||grid.y<visMinY||grid.y>visMaxY) return;
        Building b  = findBuilding(e.getX(), e.getY());
        if (b != null) {
            if (b instanceof DronePort) {
                new DronePortWindow((DronePort) b).show();
                return;
            } else if (b instanceof WasteDisposal) {
                new WasteDisposalWindow((WasteDisposal) b).show();
                return;
            }
            BuildingInfoWindow.show(b);
            return;
        }
        List<RoadTile> rts = findRoadTile(e.getX(), e.getY());
        if (rts != null) {
            BuildingInfoWindow.show(rts);
            BuildingInfoWindow.show(b);
            return;
        }
        AddBuildingAtCellWindow.show(state, grid, this::refresh);
    }

    private void clearHover() {
        if (hoveredCoord!=null || hoveredBuilding!=null) {
            hoveredCoord    = null;
            hoveredBuilding = null;
            canvas.setCursor(Cursor.DEFAULT);
            refresh();
        }
    }

    // ─── Misc Drawing Utilities ────────────────────────────────────

    private static Color buildingFill(Building b) {
        if (b instanceof Mine)    return Color.LIGHTBLUE;
        if (b instanceof Factory) return Color.LIGHTGREEN;
        if (b instanceof Storage) return Color.LIGHTYELLOW;
        return Color.BEIGE;
    }

    private void drawName(GraphicsContext g, String name, double x, double y) {
        g.setFill(Color.BLACK);
        double baseY = y + scale*0.5;
        if (name.length() <= 8) {
            g.fillText(name, x+3, baseY);
        } else {
            int mid = name.length()/2;
            g.fillText(name.substring(0, mid), x+3, baseY - scale*0.15);
            g.fillText(name.substring(mid),     x+3, baseY + scale*0.15);
        }
    }

    private static void strokeArrow(GraphicsContext g,
                                    double sx, double sy,
                                    double ex, double ey,
                                    double size) {
        g.strokeLine(sx, sy, ex, ey);
        double dx = ex - sx, dy = ey - sy, len = Math.hypot(dx, dy);
        if (len==0) return;
        dx/=len; dy/=len;
        double x1 = ex - size*dx - size*dy*0.5;
        double y1 = ey - size*dy + size*dx*0.5;
        double x2 = ex - size*dx + size*dy*0.5;
        double y2 = ey - size*dy - size*dx*0.5;
        g.strokeLine(ex, ey, x1, y1);
        g.strokeLine(ex, ey, x2, y2);
    }
}
