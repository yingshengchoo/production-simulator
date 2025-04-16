package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.IntConsumer;

/**
 * Modal dialog to select a verbosity level (0–2) via a Spinner control.
 * Uses a two-column responsive layout, default/cancel buttons, and
 * invokes a callback with the chosen level.
 * <p>
 * Usage:
 * <pre>
 *     VerbosityWindow.show(level -> Log.setLogLevel(level));
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class VerbosityWindow {
    private VerbosityWindow() {
        // prevent instantiation
    }

    /**
     * Shows the verbosity selection dialog.
     * @param onVerbositySet callback receiving the selected level
     */
    public static void show(IntConsumer onVerbositySet) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Set Verbosity Level");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Label and Spinner for levels 0–2
        Label levelLabel = new Label("Verbosity Level:");
        Spinner<Integer> levelSpinner = new Spinner<>();
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2, 1);
        levelSpinner.setValueFactory(valueFactory);
        levelSpinner.setEditable(false);
        grid.addRow(0, levelLabel, levelSpinner);

        // Buttons HBox
        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");
        submitBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);
        HBox buttonBox = new HBox(10, submitBtn, cancelBtn);
        grid.add(buttonBox, 1, 1);

        // Handlers
        submitBtn.setOnAction(e -> {
            int level = levelSpinner.getValue();
            onVerbositySet.accept(level);
            showAlert("Verbosity set to " + level);
            stage.close();
        });
        cancelBtn.setOnAction(e -> stage.close());

        // Display
        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.showAndWait();
    }

    private static void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
