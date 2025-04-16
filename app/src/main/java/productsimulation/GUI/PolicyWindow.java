package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.SetPolicyCommand;
import productsimulation.model.Building;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Modal dialog to set scheduling (request) or source selection policies.
 * <p>
 * Fields:
 * <ul>
 *   <li>Policy Type: "request" or "source" (required)</li>
 *   <li>Policy Value: depends on type (required)</li>
 *   <li>Target: building name, "*", or "default" (required)</li>
 * </ul>
 * The Submit button is disabled until all selections are made.
 * On success, the provided callback is executed.
 * <p>
 * Usage:
 * <pre>
 *   PolicyWindow.show(state, () -> board.refresh());
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class PolicyWindow {
    private PolicyWindow() { /* prevent instantiation */ }

    /**
     * Displays the policy setting dialog.
     * @param state the simulation state
     * @param onSuccessRefresh callback invoked upon successful policy update
     */
    public static void show(State state, Runnable onSuccessRefresh) {
        Objects.requireNonNull(state, "state");

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Set Policy");

        GridPane grid = createFormGrid();

        // Policy Type
        ComboBox<String> typeBox = new ComboBox<>(
                FXCollections.observableArrayList("request", "source")
        );
        typeBox.setPromptText("Select policy type");
        addRow(grid, 0, "Type:", typeBox);

        // Policy Value
        ComboBox<String> valueBox = new ComboBox<>();
        valueBox.setPromptText("Select policy value");
        addRow(grid, 1, "Value:", valueBox);

        // Target
        List<String> targets = state.getBuildings().stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        targets.add("*");
        targets.add("default");
        ComboBox<String> targetBox = new ComboBox<>(FXCollections.observableArrayList(targets));
        targetBox.setPromptText("Select target");
        addRow(grid, 2, "Target:", targetBox);

        // Buttons
        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");
        submitBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);
        grid.addRow(3, submitBtn, cancelBtn);

        // Disable until all fields are selected
        submitBtn.disableProperty().bind(
                typeBox.valueProperty().isNull()
                        .or(valueBox.valueProperty().isNull())
                        .or(targetBox.valueProperty().isNull())
        );

        // Update valueBox options based on typeBox selection
        typeBox.setOnAction(e -> {
            String sel = typeBox.getValue();
            if ("request".equals(sel)) {
                valueBox.setItems(
                        FXCollections.observableArrayList("fifo", "sjf", "ready", "default")
                );
            } else if ("source".equals(sel)) {
                valueBox.setItems(
                        FXCollections.observableArrayList("qlen", "simplelat", "recursivelat", "default")
                );
            } else {
                valueBox.getItems().clear();
            }
            valueBox.getSelectionModel().clearSelection();
        });

        submitBtn.setOnAction(e -> {
            String type = typeBox.getValue();
            String value = valueBox.getValue();
            String target = targetBox.getValue();
            SetPolicyCommand cmd = new SetPolicyCommand(type, target, value);
            String error = cmd.execute();
            if (error == null || error.isBlank()) {
                showAlert(Alert.AlertType.INFORMATION,
                        String.format("Policy set: %s -> %s on %s", type, value, target)
                );
                if (onSuccessRefresh != null) onSuccessRefresh.run();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error: " + error);
            }
        });
        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid, 450, 220));
        stage.showAndWait();
    }

    private static GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.NEVER);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);
        return grid;
    }

    private static void addRow(GridPane grid, int row, String labelText, Control ctrl) {
        grid.add(new Label(labelText), 0, row);
        grid.add(ctrl, 1, row);
    }

    private static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
