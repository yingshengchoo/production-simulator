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
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.model.*;

import java.util.List;
import java.util.Objects;

/**
 * Dialog for adding a building at a fixed grid coordinate.
 * <p>
 * Uses a ComboBox of BuildingType and a ListView of Building for sources,
 * avoiding string lookups and ensuring type safety. Disables the Add button
 * until required fields (name and type) are provided.
 *<p>
 * Usage:
 * <pre>
 *   AddBuildingAtCellWindow.show(state, coord, () -> board.refresh());
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class AddBuildingAtCellWindow {

    private AddBuildingAtCellWindow() {
        // prevent instantiation
    }

    /**
     * Shows the modal form.
     * @param state simulation state (not null)
     * @param coord fixed grid coordinate (not null)
     * @param onSuccess callback on successful creation
     */
    public static void show(State state,
                            Coordinate coord,
                            Runnable onSuccess) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(coord, "coord");

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(String.format("Add Building at (%d, %d)", coord.x, coord.y));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.NEVER);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);

        // Name field
        TextField nameField = new TextField();
        nameField.setPromptText("Enter unique name");
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        // Type selector
        ComboBox<BuildingType> typeCombo = new ComboBox<>(
                FXCollections.observableArrayList(BuildingType.getBuildingTypeGlobalList())
        );
        typeCombo.setPromptText("Select type");
        typeCombo.setCellFactory(cb -> new ListCell<>() {
            @Override public void updateItem(BuildingType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        typeCombo.setButtonCell(typeCombo.getCellFactory().call(null));
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);

        // Sources list
        ListView<Building> sourcesView = new ListView<>(
                FXCollections.observableArrayList(state.getBuildings())
        );
        sourcesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourcesView.setCellFactory(lv -> new ListCell<>() {
            @Override public void updateItem(Building b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null : b.getName());
            }
        });
        grid.add(new Label("Sources:"), 0, 2);
        grid.add(sourcesView, 1, 2);

        // Buttons
        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);
        grid.add(addBtn, 0, 3);
        grid.add(cancelBtn, 1, 3);

        // Disable Add until name and type are provided
        addBtn.disableProperty().bind(
                nameField.textProperty().isEmpty()
                        .or(typeCombo.valueProperty().isNull())
        );

        addBtn.setOnAction(e -> handleAdd(state, coord,
                nameField.getText().trim(),
                typeCombo.getValue(),
                sourcesView.getSelectionModel().getSelectedItems(),
                onSuccess,
                stage
        ));
        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid));
        stage.showAndWait();
    }

    private static void handleAdd(State state,
                                  Coordinate coord,
                                  String name,
                                  BuildingType type,
                                  List<Building> sources,
                                  Runnable onSuccess,
                                  Stage stage) {
        if (name.isEmpty() || type == null) {
            showAlert(Alert.AlertType.ERROR, "Name and type are required.");
            return;
        }
        if (state.getBuildings().stream().anyMatch(b ->
                b.getName().equalsIgnoreCase(name))) {
            showAlert(Alert.AlertType.ERROR, "Name already exists.");
            return;
        }
        try {
            Building created = createBuilding(state, coord, name, type, sources);
            showAlert(Alert.AlertType.INFORMATION,
                    String.format("Added %s at (%d,%d)",
                            created.getName(), created.getX(), created.getY())
            );
            if (onSuccess != null) onSuccess.run();
            stage.close();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Creation failed: " + ex.getMessage());
        }
    }

    private static Building createBuilding(State state,
                                           Coordinate coord,
                                           String name,
                                           BuildingType type,
                                           List<Building> sources) {
        if (type instanceof StorageType) {
            return Storage.addStorage(name, sources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord, (StorageType) type);
        } else if (type.getName().toLowerCase().contains("mine")) {
            return Mine.addMine(name, sources,
                    state.getDefaultSourcePolicy(),
                    state.getDefaultServePolicy(),
                    coord, type);
        }
        return Factory.addFactory(name, sources,
                state.getDefaultSourcePolicy(),
                state.getDefaultServePolicy(),
                coord, type);
    }

    private static void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
