package productsimulation.GUI;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import productsimulation.Board;
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

public class BoardDisplay {
    private State state;
    private Canvas canvas;
    // Base scale, in pixels per grid unit; recalculated dynamically.
    private double scale = 40;
    // Offsets for translating board coordinates to canvas coordinates.
    private double offsetX = 0;
    private double offsetY = 0;
    // Margin (in grid units) added around the bounding box.
    private static final int MARGIN = 2;
    // Fixed pixel paddings to keep the grid from touching the canvas edges.
    private static final double TOP_PADDING = 50;
    private static final double LEFT_PADDING = 50;

    public BoardDisplay(State state) {
        this.state = state;
        canvas = new Canvas(800, 600);
    }

    public Node getCanvasPane() {
        return canvas;
    }

    /**
     * Refreshes the board display.
     * <p>
     * This method computes the bounding box of building coordinates (with margin),
     * calculates an appropriate scale factor for the canvas, draws grid lines with labels,
     * renders road tiles from Road.existingRoadTiles, and finally draws buildings.
     * The building names are drawn inside the cell; if the name is too long it is split into
     * two lines to avoid overlap.
     */
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

        // 1. Compute the bounding box over building coordinates.
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (Building b : buildings) {
            int bx = b.getX();
            int by = b.getY();
            if (bx < minX) minX = bx;
            if (bx > maxX) maxX = bx;
            if (by < minY) minY = by;
            if (by > maxY) maxY = by;
        }
        // Add margin and ensure minimum is zero.
        minX = Math.max(minX - MARGIN, 0);
        minY = Math.max(minY - MARGIN, 0);
        maxX += MARGIN;
        maxY += MARGIN;
        int boxWidthUnits = maxX - minX + 1;
        int boxHeightUnits = maxY - minY + 1;

        // 2. Compute effective drawing area and determine scale.
        double effectiveWidth = canvas.getWidth() - LEFT_PADDING;
        double effectiveHeight = canvas.getHeight() - TOP_PADDING;
        double scaleX = effectiveWidth / boxWidthUnits;
        double scaleY = effectiveHeight / boxHeightUnits;
        scale = Math.min(scaleX, scaleY);
        double extraX = effectiveWidth - boxWidthUnits * scale;
        double extraY = effectiveHeight - boxHeightUnits * scale;
        offsetX = LEFT_PADDING - minX * scale + extraX / 2;
        offsetY = TOP_PADDING - minY * scale + extraY / 2;

        // 3. Draw grid lines and labels.
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

        // 4. Draw roads from Road.existingRoadTiles.
        Map<Coordinate, RoadTile> roadTileMap = Road.existingRoadTiles;
        gc.setFill(Color.GRAY);
        for (RoadTile roadTile : roadTileMap.values()) {
            Coordinate coord = roadTile.getCoordinate();
            if (coord.x >= minX && coord.x <= maxX && coord.y >= minY && coord.y <= maxY) {
                double rx = coord.x * scale + offsetX;
                double ry = coord.y * scale + offsetY;
                gc.fillRect(rx, ry, scale, scale);
                // Draw directional indicators.
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

        // 5. Draw buildings.
        for (Building b : buildings) {
            int bx = b.getX();
            int by = b.getY();
            double drawX = bx * scale + offsetX;
            double drawY = by * scale + offsetY;
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
            gc.strokeRect(drawX, drawY, scale, scale);
            // Highlight nonviable buildings (those that are not mines and have no sources).
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeRect(drawX, drawY, scale, scale);
                gc.setLineWidth(1);
            }
            // Draw the building's name.
            gc.setFill(Color.BLACK);
            String name = b.getName();
            // If the name is longer than 8 characters, split into two lines.
            if (name.length() > 8) {
                int mid = name.length() / 2;
                String firstLine = name.substring(0, mid);
                String secondLine = name.substring(mid);
                gc.fillText(firstLine, drawX + 3, drawY + scale / 2 - 5);
                gc.fillText(secondLine, drawX + 3, drawY + scale / 2 + 10);
            } else {
                gc.fillText(name, drawX + 3, drawY + scale / 2);
            }
        }
    }
}