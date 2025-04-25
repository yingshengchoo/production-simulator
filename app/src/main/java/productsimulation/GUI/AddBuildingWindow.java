package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.model.*;
import productsimulation.model.drone.DronePort;
import productsimulation.model.waste.WasteDisposal;
import productsimulation.model.waste.WasteDisposalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Dialog for adding a new building with optional coordinates and source selection.
 * <p>
 * Fields:
 *   • Name (required)
 *   • Type (required)
 *   • X and Y coordinates (optional – both must be entered together)
 *   • Source buildings (optional, multi‑select)
 * <p>
 * If both X and Y are blank, a valid coordinate is auto‑assigned. If one is entered without the other,
 * the “Add” button stays disabled and the dialog cannot be submitted until corrected.
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class AddBuildingWindow {
    private AddBuildingWindow() { /* no instances */ }

    public static void show(State state, Runnable onSuccess) {
        Objects.requireNonNull(state, "state");
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Building");

        // two‑column responsive grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        ColumnConstraints labelCol = new ColumnConstraints(); labelCol.setHgrow(Priority.NEVER);
        ColumnConstraints fieldCol = new ColumnConstraints(); fieldCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        // --- Name ---
        TextField nameField = new TextField();
        nameField.setPromptText("Unique building name");
        addRow(grid, 0, "Name:", nameField);

        // --- Type ---
        ComboBox<BuildingType> typeCombo = new ComboBox<>(
                FXCollections.observableArrayList(BuildingType.getBuildingTypeGlobalList())
        );
        typeCombo.setPromptText("Select type");
        typeCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(BuildingType bt, boolean empty) {
                super.updateItem(bt, empty);
                setText(empty || bt == null ? null : bt.getName());
            }
        });
        typeCombo.setButtonCell(typeCombo.getCellFactory().call(null));
        addRow(grid, 1, "Type:", typeCombo);

        // --- Coordinates ---
        TextField xField = new TextField(); xField.setPromptText("optional");
        TextField yField = new TextField(); yField.setPromptText("optional");
        addRow(grid, 2, "X Coord:", xField);
        addRow(grid, 3, "Y Coord:", yField);

        // --- Sources ---
        ListView<Building> sourcesList = new ListView<>(
                FXCollections.observableArrayList(state.getBuildings())
        );
        sourcesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourcesList.setCellFactory(lv -> {
            ListCell<Building> cell = new ListCell<>() {
                @Override protected void updateItem(Building b, boolean empty) {
                    super.updateItem(b, empty);
                    setText(empty || b==null ? null : b.getName());
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, ev -> {
                lv.requestFocus();
                if (!cell.isEmpty()) {
                    int idx = cell.getIndex();
                    if (sourcesList.getSelectionModel().isSelected(idx))
                        sourcesList.getSelectionModel().clearSelection(idx);
                    else
                        sourcesList.getSelectionModel().select(idx);
                    ev.consume();
                }
            });
            return cell;
        });
        addRow(grid, 4, "Sources:", sourcesList);

        // Clear Sources button
        Button clearBtn = new Button("Clear Sources");
        clearBtn.setOnAction(e -> sourcesList.getSelectionModel().clearSelection());
        addRow(grid, 5, "", clearBtn);

        // --- Buttons ---
        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);
        grid.addRow(6, addBtn, cancelBtn);

        // Disable Add whenever: name empty OR type null OR partial coords
        var nameOrTypeMissing =
                nameField.textProperty().isEmpty()
                        .or(typeCombo.valueProperty().isNull());
        var coordPartial =
                xField.textProperty().isEmpty().and(yField.textProperty().isNotEmpty())
                        .or(xField.textProperty().isNotEmpty().and(yField.textProperty().isEmpty()));
        addBtn.disableProperty().bind(nameOrTypeMissing.or(coordPartial));

        // --- Handlers ---
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String xs = xField.getText().trim();
            String ys = yField.getText().trim();
            Coordinate coord;
            if (xs.isEmpty()) coord = BuildingHandler.getValidCoordinate();
            else {
                try { coord = new Coordinate(Integer.parseInt(xs), Integer.parseInt(ys)); }
                catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "X and Y must be valid integers.");
                    return;
                }
            }
            // Duplicate name guard
            if (state.getBuildings().stream()
                    .anyMatch(b -> b.getName().equalsIgnoreCase(name))) {
                showAlert(Alert.AlertType.ERROR, "A building named \""+name+"\" already exists.");
                return;
            }
            List<Building> selSources = new ArrayList<>(sourcesList.getSelectionModel().getSelectedItems());
            try {
                Building created = createBuilding(name, typeCombo.getValue(), coord, selSources, state);
                showAlert(Alert.AlertType.INFORMATION,
                        String.format("Added \"%s\" at (%d,%d).", created.getName(), created.getX(), created.getY())
                );
                if (onSuccess != null) onSuccess.run();
                stage.close();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Creation failed: " + ex.getMessage());
            }
        });
        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid));
        stage.showAndWait();
    }

    private static <T extends javafx.scene.Node> void addRow(GridPane grid, int row, String label, T ctrl) {
        grid.add(new Label(label), 0, row);
        grid.add(ctrl, 1, row);
    }

    private static Building createBuilding(
            String name, BuildingType type, Coordinate coord,
            List<Building> sources, State state
    ) {
        if (type instanceof StorageType) {
            return Storage.addStorage(name, sources,
                    state.getDefaultSourcePolicy(), state.getDefaultServePolicy(),
                    coord, (StorageType) type);
        } else if (type.getName().toLowerCase().contains("mine")) {
            return Mine.addMine(name, sources,
                    state.getDefaultSourcePolicy(), state.getDefaultServePolicy(),
                    coord, type);
        } else if (type instanceof DronePortType) {
            DronePort dp = new DronePort(name, type, null, null, coord);
            dp.register();
            return dp;
        } else if (type instanceof WasteDisposalType) {
            WasteDisposal wd = new WasteDisposal(name, type, new ArrayList<>(), coord);
            wd.register();
            return wd;
        }

        else {
            return Factory.addFactory(name, sources,
                    state.getDefaultSourcePolicy(), state.getDefaultServePolicy(),
                    coord, type);
        }
    }

    private static void showAlert(Alert.AlertType t, String msg) {
        Alert a = new Alert(t, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
