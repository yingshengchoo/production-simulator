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
import productsimulation.model.road.Direction;
import productsimulation.model.road.Road;
import productsimulation.model.road.RoadTile;

import java.util.List;
import java.util.Map;

/**
 * BoardDisplay draws a grid, roads, and buildings on a Canvas.
 * It highlights any building the mouse hovers over, and changes
 * the cursor to a hand when pointing at a building.
 */
public class BoardDisplay {
    private final State state;
    private final Canvas canvas;
    private double scale = 40.0;
    private double offsetX = 0;
    private double offsetY = 0;

    private static final int MARGIN = 2;
    private static final double TOP_PADDING = 50;
    private static final double LEFT_PADDING = 50;

    // Stores the building under the mouse pointer, if any.
    private Building hoveredBuilding = null;

    public BoardDisplay(State state) {
        this.state = state;
        this.canvas = new Canvas(800, 600);

        // When the user clicks on a building, show the popup with details.
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleCanvasClick(e.getX(), e.getY()));

        // When the mouse moves, update hoveredBuilding, set cursor, and refresh highlight.
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, e -> handleCanvasMove(e.getX(), e.getY()));

        // When the mouse leaves the canvas entirely, clear any hovered building and reset cursor.
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            hoveredBuilding = null;
            canvas.setCursor(Cursor.DEFAULT);
            refresh();
        });
    }

    public Node getCanvasPane() {
        return canvas;
    }

    /**
     * Redraws the board, roads, and buildings. If a building is hovered, that building
     * is highlighted with a thick outline, and the mouse cursor is a hand.
     */
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) {
            return;
        }

        // Determine bounding box with margin.
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Building b : buildings) {
            minX = Math.min(minX, b.getX());
            maxX = Math.max(maxX, b.getX());
            minY = Math.min(minY, b.getY());
            maxY = Math.max(maxY, b.getY());
        }
        minX = Math.max(minX - MARGIN, 0);
        minY = Math.max(minY - MARGIN, 0);
        maxX += MARGIN;
        maxY += MARGIN;

        int boxWidthUnits = maxX - minX + 1;
        int boxHeightUnits = maxY - minY + 1;

        double effectiveWidth = canvas.getWidth() - LEFT_PADDING;
        double effectiveHeight = canvas.getHeight() - TOP_PADDING;
        double scaleX = effectiveWidth / boxWidthUnits;
        double scaleY = effectiveHeight / boxHeightUnits;
        scale = Math.min(scaleX, scaleY);

        double extraX = effectiveWidth - (boxWidthUnits * scale);
        double extraY = effectiveHeight - (boxHeightUnits * scale);

        offsetX = LEFT_PADDING - minX * scale + extraX / 2;
        offsetY = TOP_PADDING - minY * scale + extraY / 2;

        // Draw grid lines
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0);
        for (int i = minX; i <= maxX + 1; i++) {
            double xLine = i * scale + offsetX;
            gc.strokeLine(xLine, offsetY, xLine, offsetY + boxHeightUnits * scale);
            if (i <= maxX) {
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(i), xLine + scale / 2 - 5, offsetY - 10);
            }
        }
        for (int j = minY; j <= maxY + 1; j++) {
            double yLine = j * scale + offsetY;
            gc.strokeLine(offsetX, yLine, offsetX + boxWidthUnits * scale, yLine);
            if (j <= maxY) {
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(j), offsetX - 30, yLine + scale / 2 + 5);
            }
        }

        // Draw roads
        Map<Coordinate, RoadTile> roadTileMap = Road.existingRoadTiles;
        gc.setFill(Color.GRAY);
        for (RoadTile roadTile : roadTileMap.values()) {
            Coordinate c = roadTile.getCoordinate();
            if (c.x >= minX && c.x <= maxX && c.y >= minY && c.y <= maxY) {
                double rx = c.x * scale + offsetX;
                double ry = c.y * scale + offsetY;
                gc.fillRect(rx, ry, scale, scale);

                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                double centerX = rx + scale / 2;
                double centerY = ry + scale / 2;
                double margin = scale / 4;

                Direction d = roadTile.getDirection();
                if (d.hasDirection(Direction.LEFT) || d.hasDirection(Direction.RIGHT)) {
                    gc.strokeLine(rx + margin, centerY, rx + scale - margin, centerY);
                }
                if (d.hasDirection(Direction.UP) || d.hasDirection(Direction.DOWN)) {
                    gc.strokeLine(centerX, ry + margin, centerX, ry + scale - margin);
                }
                gc.setLineWidth(1);
            }
        }

        // Draw buildings
        for (Building b : buildings) {
            double drawX = b.getX() * scale + offsetX;
            double drawY = b.getY() * scale + offsetY;

            if (b instanceof Mine) {
                gc.setFill(Color.LIGHTBLUE);
            } else if (b instanceof Factory) {
                gc.setFill(Color.LIGHTGREEN);
            } else if (b instanceof Storage) {
                gc.setFill(Color.LIGHTYELLOW);
            } else {
                gc.setFill(Color.BEIGE);
            }
            gc.fillRect(drawX, drawY, scale, scale);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            gc.strokeRect(drawX, drawY, scale, scale);

            // Mark a building with no sources (if not a mine) in red
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeRect(drawX, drawY, scale, scale);
                gc.setLineWidth(1.0);
            }

            // Name text
            gc.setFill(Color.BLACK);
            String name = b.getName();
            if (name.length() > 8) {
                int mid = name.length() / 2;
                String line1 = name.substring(0, mid);
                String line2 = name.substring(mid);
                gc.fillText(line1, drawX + 3, drawY + scale / 2 - 5);
                gc.fillText(line2, drawX + 3, drawY + scale / 2 + 10);
            } else {
                gc.fillText(name, drawX + 3, drawY + scale / 2);
            }
        }

        // Highlight hovered building with a thick outline
        if (hoveredBuilding != null) {
            double hx = hoveredBuilding.getX() * scale + offsetX;
            double hy = hoveredBuilding.getY() * scale + offsetY;

            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(5);
            gc.strokeRect(hx, hy, scale, scale);
            gc.setLineWidth(1.0);
        }
    }

    /**
     * Detects which building, if any, was clicked and displays its info.
     */
    private void handleCanvasClick(double pixelX, double pixelY) {
        Building b = findBuilding(pixelX, pixelY);
        if (b != null) {
            BuildingInfoWindow.show(b);
        }
    }

    /**
     * Detects if mouse is over a building and updates hover state.
     * Changes cursor to HAND if a building is hovered, else DEFAULT.
     */
    private void handleCanvasMove(double pixelX, double pixelY) {
        Building b = findBuilding(pixelX, pixelY);
        if (b != hoveredBuilding) {
            hoveredBuilding = b;
            refresh();
        }
        if (b != null) {
            canvas.setCursor(Cursor.HAND);
        } else {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    private Building findBuilding(double pixelX, double pixelY) {
        int gx = (int) Math.floor((pixelX - offsetX) / scale);
        int gy = (int) Math.floor((pixelY - offsetY) / scale);
        for (Building b : state.getBuildings()) {
            if (b.getX() == gx && b.getY() == gy) {
                return b;
            }
        }
        return null;
    }
}
