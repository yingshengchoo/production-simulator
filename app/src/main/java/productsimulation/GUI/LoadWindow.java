package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.command.LoadCommand;

/**
 * Modal dialog prompting the user to enter a filename (without extension)
 * to load a saved simulation state. Ensures valid input, disables the
 * Submit button until a filename is provided, and shows alerts for success or error.
 * Usage:
 * <pre>
 *     LoadWindow.show(() -> boardDisplay.refresh());
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class LoadWindow {
    private LoadWindow() { /* prevent instantiation */ }

    /**
     * Displays the Load dialog.
     * @param onSuccessRefresh called after successful load to refresh UI
     */
    public static void show(Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Load Simulation");

        GridPane grid = createFormGrid();

        Label fileLabel = new Label("File name (no ".concat(".ser").concat("):"));
        TextField fileField = new TextField();
        fileField.setPromptText("Enter file name");
        addRow(grid, fileLabel, fileField);

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        submit.setDefaultButton(true);
        cancel.setCancelButton(true);
        submit.disableProperty().bind(fileField.textProperty().isEmpty());

        submit.setOnAction(e -> {
            String fileName = fileField.getText().trim();
            LoadCommand cmd = new LoadCommand(fileName);
            String error = cmd.execute();
            if (error == null || error.isBlank()) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Loaded state from '" + fileName + ".ser'.");
                if (onSuccessRefresh != null) onSuccessRefresh.run();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Load error: " + error);
            }
        });
        cancel.setOnAction(e -> stage.close());

        grid.addRow(1, submit, cancel);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.getScene().getWindow().sizeToScene();
        stage.showAndWait();
    }

    // Creates a GridPane with two columns: label and control
    private static GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setHgrow(Priority.NEVER);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);
        return grid;
    }

    // Adds a label and control to specified row
    private static void addRow(GridPane grid, Label label, Control control) {
        grid.add(label, 0, 0);
        grid.add(control, 1, 0);
    }

    // Shows an alert dialog
    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle("Load Simulation");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
