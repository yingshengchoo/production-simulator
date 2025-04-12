package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.IntConsumer;

/**
 * A small modal window that lets the user pick a verbosity level (0-2) from a drop-down
 * and calls a callback (onVerbositySet) with that integer.
 */
public class VerbosityWindow {

    public static void show(IntConsumer onVerbositySet) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Set Verbosity");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label label = new Label("Verbosity (0–2):");
        // Use a drop-down with 0, 1, 2
        ComboBox<Integer> combo = new ComboBox<>(FXCollections.observableArrayList(0, 1, 2));
        combo.setPromptText("Select level");

        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            Integer val = combo.getValue();
            if (val == null) {
                showError("Please select a verbosity level.");
                return;
            }
            onVerbositySet.accept(val);
            showInfo("Verbosity set to " + val);
            stage.close();
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(label, 0, 0);
        grid.add(combo, 1, 0);
        grid.add(submitBtn, 0, 1);
        grid.add(cancelBtn, 1, 1);

        stage.setScene(new Scene(grid, 300, 150));
        stage.showAndWait();
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
