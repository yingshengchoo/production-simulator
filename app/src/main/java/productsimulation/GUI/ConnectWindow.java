package productsimulation.GUI;

//import javafx.collections.FXCollections;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Priority;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import productsimulation.State;
//import productsimulation.command.ConnectCommand;
//import productsimulation.model.Building;
//
//import java.util.List;
//import java.util.Objects;
//
///**
// * Modal dialog to establish a directed connection between two buildings.
// * <p>
// * Users select a source and destination building from dropdowns.
// * "Connect" is disabled until both selections are made.
// * On success, the provided callback is invoked.
// *
// * @author Taiyan Liu
// * @version 1.0
// */
//public final class ConnectWindow {
//
//    private ConnectWindow() { /* prevent instantiation */ }
//
//    /**
//     * Shows the connection dialog.
//     * @param state simulation state (must not be null)
//     * @param onSuccessRefresh callback to refresh UI on success
//     */
//    public static void show(State state, Runnable onSuccessRefresh) {
//        Objects.requireNonNull(state, "state");
//
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setTitle("Connect Buildings");
//
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20));
//        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.NEVER);
//        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
//        grid.getColumnConstraints().addAll(col1, col2);
//
//        // Dropdowns for buildings
//        List<Building> allBuildings = state.getBuildings();
//        ComboBox<Building> sourceBox = new ComboBox<>(
//                FXCollections.observableArrayList(allBuildings)
//        );
//        sourceBox.setPromptText("Select source");
//        sourceBox.setCellFactory(cb -> new ListCell<>() {
//            @Override protected void updateItem(Building b, boolean empty) {
//                super.updateItem(b, empty);
//                setText(empty || b==null ? null : b.getName());
//            }
//        });
//        sourceBox.setButtonCell(sourceBox.getCellFactory().call(null));
//        addRow(grid, 0, "Source:", sourceBox);
//
//        ComboBox<Building> destBox = new ComboBox<>(
//                FXCollections.observableArrayList(allBuildings)
//        );
//        destBox.setPromptText("Select destination");
//        destBox.setCellFactory(cb -> new ListCell<>() {
//            @Override protected void updateItem(Building b, boolean empty) {
//                super.updateItem(b, empty);
//                setText(empty || b==null ? null : b.getName());
//            }
//        });
//        destBox.setButtonCell(destBox.getCellFactory().call(null));
//        addRow(grid, 1, "Destination:", destBox);
//
//        Button connectBtn = new Button("Connect");
//        Button cancelBtn  = new Button("Cancel");
//        grid.add(connectBtn, 0, 2);
//        grid.add(cancelBtn,  1, 2);
//
//        // Disable Connect until both source and destination selected and not equal
//        connectBtn.disableProperty().bind(
//                sourceBox.valueProperty().isNull()
//                        .or(destBox.valueProperty().isNull())
//                        .or(sourceBox.valueProperty().isEqualTo(destBox.valueProperty()))
//        );
//
//        connectBtn.setOnAction(e -> {
//            Building source = sourceBox.getValue();
//            Building dest   = destBox.getValue();
//            String err = new ConnectCommand(source.getName(), dest.getName()).execute();
//            if (err == null || err.isBlank()) {
//                showAlert(Alert.AlertType.INFORMATION,
//                        String.format("Connected %s → %s", source.getName(), dest.getName()));
//                if (onSuccessRefresh != null) onSuccessRefresh.run();
//                stage.close();
//            } else {
//                showAlert(Alert.AlertType.ERROR, err);
//            }
//        });
//        cancelBtn.setOnAction(e -> stage.close());
//
//        stage.setScene(new Scene(grid, 400, 160));
//        stage.showAndWait();
//    }
//
//    /**
//     * Adds a label and control to the grid at the given row.
//     */
//    private static <T extends Control> void addRow(GridPane grid, int row, String label, T ctrl) {
//        grid.add(new Label(label), 0, row);
//        grid.add(ctrl, 1, row);
//    }
//
//    /**
//     * Displays an alert with the specified type and message.
//     */
//    private static void showAlert(Alert.AlertType type, String msg) {
//        Alert alert = new Alert(type, msg, ButtonType.OK);
//        alert.setHeaderText(null);
//        alert.showAndWait();
//    }
//}
