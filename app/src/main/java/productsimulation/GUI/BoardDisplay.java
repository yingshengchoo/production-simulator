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

    // Called after each user action to redraw the board
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = state.getBuildings();
        if (buildings.isEmpty()) return;

        // 1) Find bounding box
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

        double boardWidthUnits = maxX - minX + 1;
        double boardHeightUnits = maxY - minY + 1;
        double scaleX = canvas.getWidth() / boardWidthUnits;
        double scaleY = canvas.getHeight() / boardHeightUnits;
        scale = Math.min(scaleX, scaleY);

        offsetX = -minX * scale;
        offsetY = -minY * scale;

        // 2) draw a grid
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0);
        for (int i = minX; i <= maxX + 1; i++) {
            double xLine = i * scale + offsetX;
            gc.strokeLine(xLine, offsetY, xLine, offsetY + boardHeightUnits * scale);
        }
        for (int j = minY; j <= maxY + 1; j++) {
            double yLine = j * scale + offsetY;
            gc.strokeLine(offsetX, yLine, offsetX + boardWidthUnits * scale, yLine);
        }

        // 3) Draw buildings
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

            // Mark nonviable (not a mine & no sources)
            if (!(b instanceof Mine) && (b.getSources() == null || b.getSources().isEmpty())) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeRect(drawX, drawY, scale, scale);
                gc.setLineWidth(1);
            }

            gc.setFill(Color.BLACK);
            gc.fillText(b.getName(), drawX + 3, drawY + scale / 2);
        }

        // If roads are stored in State, draw them here
        // e.g. for each Road, fillRect or strokeLine for each path tile
    }
}
