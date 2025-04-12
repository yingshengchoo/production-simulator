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

    // Base scale (size of one cell in pixels; will be recomputed dynamically).
    private double scale = 40;
    // Offsets to translate coordinates to canvas pixels.
    private double offsetX = 0;
    private double offsetY = 0;

    // Margin added in grid units.
    private static final int MARGIN = 2;
    // Fixed pixel paddings to ensure that the grid does not touch the canvas edges.
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
     * Steps:
     * 1. Compute a bounding box from the building coordinates (with added MARGIN), but clamp the minimums to zero.
     * 2. Calculate a scale factor that fits the effective area (canvas width & height minus fixed paddings) and then center the drawing.
     * 3. Draw grid lines along with column and row numbers (the column numbers appear along the top and row numbers along the left).
     * 4. Draw road cells by iterating over Road.existingRoadTiles (cells with road weight, i.e. weight==1).
     * 5. Draw the buildings on top.
     */
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

        // 1) Compute bounding box over building coordinates.
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
        // Add margin; clamp minimum to 0 so grid labels are always nonnegative.
        minX = Math.max(minX - MARGIN, 0);
        minY = Math.max(minY - MARGIN, 0);
        maxX += MARGIN;
        maxY += MARGIN;

        int boxWidthUnits = maxX - minX + 1;
        int boxHeightUnits = maxY - minY + 1;

        // 2) Compute effective drawing area by subtracting fixed paddings.
        double effectiveWidth = canvas.getWidth() - LEFT_PADDING;
        double effectiveHeight = canvas.getHeight() - TOP_PADDING;
        double scaleX = effectiveWidth / boxWidthUnits;
        double scaleY = effectiveHeight / boxHeightUnits;
        scale = Math.min(scaleX, scaleY);

        // Compute extra space remaining after drawing the grid.
        double extraX = effectiveWidth - boxWidthUnits * scale;
        double extraY = effectiveHeight - boxHeightUnits * scale;
        // Offsets: add fixed paddings and distribute extra space evenly.
        offsetX = LEFT_PADDING - minX * scale + extraX / 2;
        offsetY = TOP_PADDING - minY * scale + extraY / 2;

        // 3) Draw grid lines with column and row numbers.
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0);
        for (int i = minX; i <= maxX + 1; i++) {
            double xLine = i * scale + offsetX;
            gc.strokeLine(xLine, offsetY, xLine, offsetY + boxHeightUnits * scale);
            if (i <= maxX) {  // Draw column numbers at the top of each cell.
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(i), xLine + scale / 2 - 5, offsetY - 10);
            }
        }
        for (int j = minY; j <= maxY + 1; j++) {
            double yLine = j * scale + offsetY;
            gc.strokeLine(offsetX, yLine, offsetX + boxWidthUnits * scale, yLine);
            if (j <= maxY) {  // Draw row numbers at the left of each cell.
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(j), offsetX - 30, yLine + scale / 2 + 5);
            }
        }

        // 4) Draw roads from Road.existingRoadTiles.
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

        // 5) Draw buildings on top of the grid.
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

            // Highlight nonviable buildings (those that aren't mines and have no sources).
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeRect(drawX, drawY, scale, scale);
                gc.setLineWidth(1);
            }

            // Draw the building's name inside the cell.
            gc.setFill(Color.BLACK);
            gc.fillText(b.getName(), drawX + 3, drawY + scale / 2);
        }
    }
}