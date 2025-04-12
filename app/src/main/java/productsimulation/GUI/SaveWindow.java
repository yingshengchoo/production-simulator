package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.command.SaveCommand;

/**
 * A modal window that prompts the user to enter a file name (without extension)
 * to save the current simulation.
 * When the user clicks Submit, a SaveCommand is created and executed.
 */
public class SaveWindow {

    public static void show(Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Save Simulation");

        GridPane grid = createGrid();
        Label fileLabel = new Label("File name (no extension):");
        TextField fileField = new TextField();
        fileField.setPromptText("Enter file name");

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");

        // When submit is pressed, connect to backend SaveCommand.
        submit.setOnAction(e -> {
            String fileName = fileField.getText().trim();
            if (fileName.isEmpty()) {
                showError("Please enter a file name.");
                return;
            }
            // BACKEND: Create and execute the SaveCommand here.
            SaveCommand cmd = new SaveCommand(fileName);
            String error = cmd.execute();
            if (error == null || error.trim().isEmpty()) {
                showInfo("Simulation saved to " + fileName + ".ser");
                if (onSuccessRefresh != null) {
                    onSuccessRefresh.run();
                }
                stage.close();
            } else {
                showError("Save error: " + error);
            }
        });

        cancel.setOnAction(e -> stage.close());

        grid.add(fileLabel, 0, 0);
        grid.add(fileField, 1, 0);
        grid.add(submit, 0, 1);
        grid.add(cancel, 1, 1);

        Scene scene = new Scene(grid, 350, 150);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // Helper to create a default GridPane with padding and gaps.
    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Save Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Save Successful");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
