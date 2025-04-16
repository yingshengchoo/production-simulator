package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.command.StepCommand;

/**
 * Modal dialog to advance the simulation by a specified number of steps.
 * Uses a Spinner<Integer> control for intuitive step selection,
 * avoiding manual text parsing. On success, invokes a callback to refresh the UI.
 * <p>
 * Usage:
 * <pre>
 *     StepWindow.show(state, () -> boardDisplay.refresh());
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class StepWindow {
    private StepWindow() {
        // prevent instantiation
    }

    /**
     * Shows the step dialog.
     *
     * @param onSuccessRefresh callback after stepping to refresh UI
     */
    public static void show(Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Step Simulation");

        // Two-column form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Spinner for number of steps (min 1)
        Label stepLabel = new Label("Number of Steps:");
        Spinner<Integer> stepSpinner = new Spinner<>();
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        stepSpinner.setValueFactory(valueFactory);
        stepSpinner.setEditable(false);
        grid.addRow(0, stepLabel, stepSpinner);

        // Buttons
        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");
        submitBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);
        grid.addRow(1, submitBtn, cancelBtn);

        // Handlers
        submitBtn.setOnAction(e -> {
            int steps = stepSpinner.getValue();
            StepCommand cmd = new StepCommand(steps);
            String error = cmd.execute();
            if (error == null || error.isBlank()) {
                showAlert(Alert.AlertType.INFORMATION,
                        String.format("Advanced %d steps.", steps)
                );
                if (onSuccessRefresh != null) onSuccessRefresh.run();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, error);
            }
        });
        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid));
        stage.sizeToScene();
        stage.showAndWait();
    }

    private static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}