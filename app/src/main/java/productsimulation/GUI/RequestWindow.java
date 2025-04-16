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
import productsimulation.command.RequestCommand;
import productsimulation.model.Building;
import productsimulation.model.Factory;
import productsimulation.model.Mine;
import productsimulation.model.Storage;

import java.util.List;
import java.util.Objects;

/**
 * Modal dialog to submit an item request from a selected building.
 * <p>
 * The Submit button is disabled until both a building and an item are selected.
 * Uses ComboBox<Building> for type-safe selection and a helper method for alerts.
 * <p>
 * Usage:
 * <pre>
 *     RequestWindow.show(state, () -> boardDisplay.refresh());
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class RequestWindow {
    private RequestWindow() { /* prevent instantiation */ }

    public static void show(State state, Runnable onSuccessRefresh) {
        Objects.requireNonNull(state, "state");

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Request Item");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints labelCol = new ColumnConstraints(); labelCol.setHgrow(Priority.NEVER);
        ColumnConstraints fieldCol = new ColumnConstraints(); fieldCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        // Building selector
        ComboBox<Building> buildingBox = new ComboBox<>(FXCollections.observableArrayList(state.getBuildings()));
        buildingBox.setPromptText("Select building");
        buildingBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Building b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null : b.getName());
            }
        });
        buildingBox.setButtonCell(buildingBox.getCellFactory().call(null));
        addRow(grid, 0, "From building:", buildingBox);

        // Item selector
        ComboBox<String> itemBox = new ComboBox<>();
        itemBox.setPromptText("Select item");
        addRow(grid, 1, "Item:", itemBox);

        // Populate items when building changes
        buildingBox.setOnAction(e -> {
            Building b = buildingBox.getValue();
            if (b == null) {
                itemBox.getItems().clear();
            } else {
                List<String> items;
                if (b instanceof Mine) {
                    items = List.copyOf(b.getBuildingType().getAllRecipes().keySet());
                } else if (b instanceof Storage) {
                    items = List.of(((Storage)b).getRecipeOutput());
                } else if (b instanceof Factory) {
                    items = List.copyOf(b.getBuildingType().getAllRecipes().keySet());
                } else {
                    items = List.of();
                }
                itemBox.setItems(FXCollections.observableArrayList(items));
            }
            itemBox.getSelectionModel().clearSelection();
        });

        // Buttons
        Button submitBtn = new Button("Submit");
        submitBtn.setDefaultButton(true);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setCancelButton(true);
        grid.addRow(2, submitBtn, cancelBtn);

        // Disable until both selections made
        submitBtn.disableProperty().bind(
                buildingBox.valueProperty().isNull()
                        .or(itemBox.valueProperty().isNull())
        );

        // Handlers
        submitBtn.setOnAction(e -> {
            Building b = buildingBox.getValue();
            String item = itemBox.getValue();
            String error = new RequestCommand(item, b.getName()).execute();
            if (error == null || error.isBlank()) {
                showAlert(Alert.AlertType.INFORMATION,
                        String.format("Requested %s from %s.", item, b.getName()));
                if (onSuccessRefresh != null) onSuccessRefresh.run();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, error);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid, 400, 180));
        stage.showAndWait();
    }

    private static <T extends Control> void addRow(GridPane grid, int row, String label, T ctrl) {
        grid.add(new Label(label), 0, row);
        grid.add(ctrl, 1, row);
    }

    private static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
