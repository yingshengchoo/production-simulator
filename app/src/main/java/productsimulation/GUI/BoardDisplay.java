package productsimulation.GUI;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;

import java.util.List;

public class BoardDisplay {
    private State state;
    private Canvas canvas;

    // Dynamic scaling variables.
    private double scale = 40;
    private double offsetX = 0;
    private double offsetY = 0;

    public BoardDisplay(State state) {
        this.state = state;
        canvas = new Canvas(800, 600);
    }

    public Node getCanvasPane() {
        return canvas;
    }

    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

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

        double boardWidthUnits = maxX - minX + 1;
        double boardHeightUnits = maxY - minY + 1;
        double scaleX = canvas.getWidth() / boardWidthUnits;
        double scaleY = canvas.getHeight() / boardHeightUnits;
        scale = Math.min(scaleX, scaleY);
        offsetX = -minX * scale;
        offsetY = -minY * scale;

        for (Building b : buildings) {
            int x = b.getX();
            int y = b.getY();
            double drawX = x * scale + offsetX;
            double drawY = y * scale + offsetY;

            if (b instanceof Mine) {
                gc.setFill(Color.LIGHTBLUE);
            } else if ((b instanceof Factory)){
                gc.setFill(Color.LIGHTGREEN);
            } else if ((b instanceof Storage)) {
                gc.setFill(Color.LIGHTYELLOW);
            } else {
                gc.setFill(Color.BEIGE);
            }
            gc.fillRect(drawX, drawY, scale, scale);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(drawX, drawY, scale, scale);

            // Mark building as nonviable if it is not a mine and it has no sources.
            // (Assumes Building.getSources() returns a List<String>).
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeRect(drawX, drawY, scale, scale);
                gc.setLineWidth(1); // Reset line width.
            }

            gc.setFill(Color.BLACK);
            gc.fillText(b.getName(), drawX + 3, drawY + scale / 2);
        }

        // TODO: Optionally, draw roads/connections if available.
    }
}