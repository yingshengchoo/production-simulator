package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.command.SaveCommand;

/**
 * Modal dialog prompting the user to enter a filename (without extension)
 * to save the current simulation state. Disables the Submit button until
 * a non-empty filename is provided. Shows alerts on success or error.
 **/
public final class SaveWindow {
    private SaveWindow() { /* prevent instantiation */ }

    /**
     * Displays the save dialog.
     * @param onSuccessRefresh callback after successful save to refresh UI
     */
    public static void show(Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Save Simulation");

        GridPane grid = createFormGrid();

        Label fileLabel = new Label("File name (no .ser):");
        TextField fileField = new TextField();
        fileField.setPromptText("Enter file name");
        addRow(grid, fileLabel, fileField);

        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");
        submitBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);

        // Disable Submit until filename is non-empty
        submitBtn.disableProperty().bind(fileField.textProperty().isEmpty());

        submitBtn.setOnAction(e -> {
            String fileName = fileField.getText().trim();
            SaveCommand cmd = new SaveCommand(fileName);
            String error = cmd.execute();
            if (error == null || error.isBlank()) {
                showAlert(Alert.AlertType.INFORMATION,
                        String.format("Saved state to '%s.ser'.", fileName)
                );
                if (onSuccessRefresh != null) onSuccessRefresh.run();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Save error: " + error);
            }
        });
        cancelBtn.setOnAction(e -> stage.close());

        grid.addRow(1, submitBtn, cancelBtn);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.getScene().getWindow().sizeToScene();
        stage.showAndWait();
    }

    // Creates a two-column GridPane with responsive sizing
    private static GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    // Helper to add a label and control in the specified row
    private static <T extends Control> void addRow(GridPane grid, Label label, T control) {
        grid.add(label, 0, 0);
        grid.add(control, 1, 0);
    }

    // Shows an alert dialog
    private static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
