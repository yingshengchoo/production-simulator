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
 * BoardDisplay renders the board grid, roads, and buildings on a canvas.
 * It highlights a building on mouse hover and provides a public method to
 * locate a building from canvas coordinates.
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
    private Building hoveredBuilding = null;
    private Coordinate hoveredCoord = null;


    public BoardDisplay(State state, FeedbackPane feedbackPane) {
        this.state = state;
        this.canvas = new Canvas(800, 600);

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleCanvasClick(e.getX(), e.getY()));
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, e -> handleCanvasMove(e.getX(), e.getY()));
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
     * Returns the current scale (pixels per grid unit).
     */
    public double getScale() {
        return scale;
    }

    /**
     * Returns the current horizontal offset.
     */
    public double getOffsetX() {
        return offsetX;
    }

    /**
     * Returns the current vertical offset.
     */
    public double getOffsetY() {
        return offsetY;
    }

    /**
     * Redraws the grid, roads, and buildings on the canvas.
     */
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) {
            return;
        }

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
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
        double extraX = effectiveWidth - boxWidthUnits * scale;
        double extraY = effectiveHeight - boxHeightUnits * scale;
        offsetX = LEFT_PADDING - minX * scale + extraX / 2;
        offsetY = TOP_PADDING - minY * scale + extraY / 2;

        // Draw grid.
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

        // Draw roads.
        Map<Coordinate, RoadTile> roadTileMap = Road.existingRoadTiles;
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.WHITE);

        for (RoadTile roadTile : roadTileMap.values()) {
            Coordinate c = roadTile.getCoordinate();
            if (c.x >= minX && c.x <= maxX && c.y >= minY && c.y <= maxY) {
                double rx = c.x * scale + offsetX;
                double ry = c.y * scale + offsetY;
                gc.fillRect(rx, ry, scale, scale);
                double centerX = rx + scale / 2;
                double centerY = ry + scale / 2;
                double margin = scale / 4;
                double arrowSize = scale / 6; // 箭头大小

                // from 方向的水平连接
                if (roadTile.getFromDirection().hasDirection(Direction.LEFT)) {
                    gc.strokeLine(rx + margin, centerY, centerX, centerY); // 只画左半边
                }
                if (roadTile.getFromDirection().hasDirection(Direction.RIGHT)) {
                    gc.strokeLine(centerX, centerY, rx + scale - margin, centerY); // 只画右半边
                }
                // from 方向的垂直连接
                if (roadTile.getFromDirection().hasDirection(Direction.UP)) {
                    gc.strokeLine(centerX, ry + margin, centerX, centerY); // 只画上半边
                }
                if (roadTile.getFromDirection().hasDirection(Direction.DOWN)) {
                    gc.strokeLine(centerX, centerY, centerX, ry + scale - margin); // 只画下半边
                }

                // to 方向的水平连接（添加箭头）
                if (roadTile.getToDirection().hasDirection(Direction.LEFT)) {
                    // 从中心到左边界，并添加箭头
                    strokeArrow(gc, centerX, centerY, rx + margin, centerY, arrowSize);
                }
                if (roadTile.getToDirection().hasDirection(Direction.RIGHT)) {
                    // 从中心到右边界，并添加箭头
                    strokeArrow(gc, centerX, centerY, rx + scale - margin, centerY, arrowSize);
                }
                // to 方向的垂直连接（添加箭头）
                if (roadTile.getToDirection().hasDirection(Direction.UP)) {
                    // 从中心到上边界，并添加箭头
                    strokeArrow(gc, centerX, centerY, centerX, ry + margin, arrowSize);
                }
                if (roadTile.getToDirection().hasDirection(Direction.DOWN)) {
                    // 从中心到下边界，并添加箭头
                    strokeArrow(gc, centerX, centerY, centerX, ry + scale - margin, arrowSize);
                }

                gc.setLineWidth(2);
            }
        }

        // Draw buildings.
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
            gc.strokeRect(drawX, drawY, scale, scale);
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeRect(drawX, drawY, scale, scale);
                gc.setLineWidth(1);
            }
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

        // Draw a highlight over the hovered building or empty cell, if any.
        if (hoveredCoord != null && !Road.existingRoadTiles.containsKey(hoveredCoord)) {
            double hx = hoveredCoord.x * scale + offsetX;
            double hy = hoveredCoord.y * scale + offsetY;

            if (hoveredBuilding != null) {
                // For building
                gc.setStroke(Color.ORANGE);
                gc.setLineWidth(5);
            } else {
                // For empty cell
                gc.setStroke(Color.CORAL);
                gc.setLineWidth(3);
            }
            gc.strokeRect(hx, hy, scale, scale);
            gc.setLineWidth(1);
        }
    }

    private void strokeArrow(GraphicsContext gc, double startX, double startY, double endX, double endY, double arrowSize) {
        // 绘制主线
        gc.strokeLine(startX, startY, endX, endY);

        // 计算方向向量
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        // 归一化方向向量
        if (length > 0) {
            dx /= length;
            dy /= length;

            // 计算箭头的两个点
            double x1 = endX - arrowSize * dx - arrowSize * dy * 0.5;
            double y1 = endY - arrowSize * dy + arrowSize * dx * 0.5;
            double x2 = endX - arrowSize * dx + arrowSize * dy * 0.5;
            double y2 = endY - arrowSize * dy - arrowSize * dx * 0.5;

            // 绘制箭头
            gc.strokeLine(endX, endY, x1, y1);
            gc.strokeLine(endX, endY, x2, y2);
        }
    }

    private void handleCanvasClick(double pixelX, double pixelY) {
        // Check if a building was clicked.
        Building b = findBuilding(pixelX, pixelY);
        if (b != null) {
            BuildingInfoWindow.show(b);
            return;
        }
        // Check if a road tile was clicked.
        RoadTile tile = findRoadTile(pixelX, pixelY);
        if (tile != null) {
            BuildingInfoWindow.show(tile);
            return;
        }

        // Neither a building nor a road tile was clicked.
        // Determine the grid coordinate for the click.
        int gridX = (int) Math.floor((pixelX - offsetX) / scale);
        int gridY = (int) Math.floor((pixelY - offsetY) / scale);

        // Open the add building window with the fixed coordinate.
        AddBuildingAtCellWindow.show(state, new Coordinate(gridX, gridY), () -> {
            refresh();
        });
    }


    /**
     * Handles mouse movement events and updates the hovered building.
     *
     * @param pixelX the x-coordinate of the mouse
     * @param pixelY the y-coordinate of the mouse
     */
    private void handleCanvasMove(double pixelX, double pixelY) {
        // Calculate gridX, gridY from mouse coords.
        int gridX = (int) Math.floor((pixelX - offsetX) / scale);
        int gridY = (int) Math.floor((pixelY - offsetY) / scale);

        // Always update hoveredCoord to the cell under the mouse.
        Coordinate newCoord = new Coordinate(gridX, gridY);

        // Check if there's a building in that cell.
        Building b = findBuilding(pixelX, pixelY);

        // If building changed or hoveredCoord changed, refresh.
        if ((b != hoveredBuilding) || !newCoord.equals(hoveredCoord)) {
            hoveredBuilding = b;
            hoveredCoord = newCoord;
            refresh();
        }

        // Show a hand only if there's a building:
        if (hoveredBuilding != null) {
            canvas.setCursor(Cursor.HAND);
        } else {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }


    /**
     * Returns the building at the given canvas coordinates.
     *
     * @param pixelX the x-coordinate on the canvas
     * @param pixelY the y-coordinate on the canvas
     * @return the building at the corresponding grid cell, or null if none exists
     */
    public Building findBuilding(double pixelX, double pixelY) {
        int gridX = (int) Math.floor((pixelX - offsetX) / scale);
        int gridY = (int) Math.floor((pixelY - offsetY) / scale);
        for (Building b : state.getBuildings()) {
            if (b.getX() == gridX && b.getY() == gridY) {
                return b;
            }
        }
        return null;
    }

    public RoadTile findRoadTile(double pixelX, double pixelY) {
        int gridX = (int) Math.floor((pixelX - offsetX) / scale);
        int gridY = (int) Math.floor((pixelY - offsetY) / scale);
        for(Map.Entry<Coordinate, RoadTile> entry: Road.existingRoadTiles.entrySet()) {
            Coordinate coordinate = entry.getKey();
            RoadTile roadTile = entry.getValue();
            if(coordinate.x == gridX && coordinate.y == gridY) {
                return roadTile;
            }
        }
        return null;
    }
}
