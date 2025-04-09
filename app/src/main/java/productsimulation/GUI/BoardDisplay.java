package productsimulation.GUI;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import productsimulation.State;
import productsimulation.model.Building;
import java.util.List;

public class BoardDisplay {
    private State state;
    private Canvas canvas;

    // Instead of a fixed cell size, we use scaling computed on the fly.
    private double scale = 40;    // default scale factor (pixels per unit)
    private double offsetX = 0;   // translation offsets to shift coordinates to (0,0)
    private double offsetY = 0;

    public BoardDisplay(State state) {
        this.state = state;
        // Create a canvas sized to match the desired area. You may make these values configurable.
        canvas = new Canvas(800, 600);
    }

    public Node getCanvasPane() {
        return canvas;
    }

    /**
     * Refresh the canvas by computing the extents of the board from the current buildings
     * and then drawing each building proportionately.
     */
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Calculate the coordinate bounds from all buildings.
        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) {
            return;
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Building b : buildings) {
            int x = b.getX();
            int y = b.getY();
            if (x < minX) { minX = x; }
            if (x > maxX) { maxX = x; }
            if (y < minY) { minY = y; }
            if (y > maxY) { maxY = y; }
        }

        // Each building is 1 unit wide.
        double boardWidthUnits = maxX - minX + 1;
        double boardHeightUnits = maxY - minY + 1;

        // Compute scale factors for each dimension.
        double scaleX = canvas.getWidth() / boardWidthUnits;
        double scaleY = canvas.getHeight() / boardHeightUnits;
        // Use the smaller scale factor to preserve the aspect ratio.
        scale = Math.min(scaleX, scaleY);

        // Calculate offsets so that the minimum coordinate maps to the edge (e.g., center vertically/horizontally).
        offsetX = -minX * scale;
        offsetY = -minY * scale;

        // Optionally, you could add margins by reducing the available canvas width/height here.

        // Draw each building proportionately.
        for (Building b : buildings) {
            int x = b.getX();
            int y = b.getY();

            // Compute the drawing position based on scale and offset.
            double drawX = x * scale + offsetX;
            double drawY = y * scale + offsetY;

            // Choose color based on building type.
            String className = b.getClass().getSimpleName();
            if ("Mine".equals(className)) {
                gc.setFill(Color.LIGHTBLUE);
            } else if ("Factory".equals(className)) {
                gc.setFill(Color.LIGHTGREEN);
            } else if ("Storage".equals(className)) {
                gc.setFill(Color.LIGHTYELLOW);
            } else {
                gc.setFill(Color.BEIGE);
            }

            gc.fillRect(drawX, drawY, scale, scale);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(drawX, drawY, scale, scale);
            gc.setFill(Color.BLACK);
            gc.fillText(b.getName(), drawX + 3, drawY + scale / 2);
        }

        // Optionally, draw roads or other simulation details here.
    }
}