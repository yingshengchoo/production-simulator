package productsimulation.GUI;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import productsimulation.Board;
import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;
import java.util.List;

public class BoardDisplay {
//    Use Board for model, instead of State.
//    private State state;
    private Board board;
    private Canvas canvas;

    // Dynamic scaling variables.
    private double scale = 40;
    private double offsetX = 0;
    private double offsetY = 0;

    public BoardDisplay() {
        this.board = Board.getBoard();
        canvas = new Canvas(800, 600);
    }

    public Node getCanvasPane() {
        return canvas;
    }

    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Building> buildings = Building.buildings;
        if (buildings.isEmpty()) return;

        // Calculate the bounding box of all building coordinates.
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

        // Draw grid lines with a thicker stroke.
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0);
        for (int i = minX; i <= maxX + 1; i++) {
            double xLine = i * scale + offsetX;
            gc.strokeLine(xLine, offsetY, xLine, offsetY + boardHeightUnits * scale);
            // Label column numbers above each cell.
            if (i <= maxX) {
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Arial", 10));
                gc.fillText(String.valueOf(i), xLine + 2, offsetY - 2);
            }
        }
        for (int j = minY; j <= maxY + 1; j++) {
            double yLine = j * scale + offsetY;
            gc.strokeLine(offsetX, yLine, offsetX + boardWidthUnits * scale, yLine);
            // Label row numbers to the left of each cell.
            if (j <= maxY) {
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Arial", 10));
                gc.fillText(String.valueOf(j), offsetX - 15, yLine + 10);
            }
        }

        // Now label each cell with its coordinate.
        gc.setFill(Color.DARKGRAY);
        gc.setFont(Font.font("Arial", 10));
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                double cellCenterX = i * scale + offsetX + scale / 2;
                double cellCenterY = j * scale + offsetY + scale / 2;
                // Adjust text positioning if necessary.
                gc.fillText(i + "," + j, cellCenterX - 12, cellCenterY + 4);
            }
        }

        // Draw each building.
        for (Building b : buildings) {
            int x = b.getX();
            int y = b.getY();
            double drawX = x * scale + offsetX;
            double drawY = y * scale + offsetY;

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

            // Mark nonviable buildings (assumes non-mines with empty sources are nonviable).
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