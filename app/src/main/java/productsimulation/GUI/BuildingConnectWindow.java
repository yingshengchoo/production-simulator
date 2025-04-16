package productsimulation.GUI;
//
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
//import java.util.stream.Collectors;
//
///**
// * Modal dialog to establish a connection from one building to another.
// * <p>
// * The source building name is pre-filled and non-editable; the user selects
// * a destination from a dropdown. The Connect button is enabled only when
// * a valid destination is chosen.
// * <p>
// * Example usage:
// * <pre>
// *     BuildingConnectWindow.show(sourceName, state);
// * </pre>
// *
// * @author Taiyan Liu
// * @version 1.0
// */
//public final class BuildingConnectWindow {
//
//    private BuildingConnectWindow() { /* prevent instantiation */ }
//
//    /**
//     * Displays the connection dialog for a given source building.
//     *
//     * @param sourceBuildingName the non-null name of the source building
//     * @param state              the simulation state (non-null)
//     */
//    public static void show(String sourceBuildingName, State state) {
//        Objects.requireNonNull(sourceBuildingName, "sourceBuildingName");
//        Objects.requireNonNull(state, "state");
//
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setTitle("Connect: " + sourceBuildingName);
//
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20));
//        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.NEVER);
//        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
//        grid.getColumnConstraints().addAll(col1, col2);
//
//        // Source label and disabled field
//        TextField sourceField = new TextField(sourceBuildingName);
//        sourceField.setEditable(false);
//        addRow(grid, 0, "Source:", sourceField);
//
//        // Destination combo box
//        List<String> allNames = state.getBuildings().stream()
//                .map(Building::getName)
//                .filter(name -> !name.equals(sourceBuildingName))
//                .collect(Collectors.toList());
//        ComboBox<String> destCombo = new ComboBox<>(FXCollections.observableArrayList(allNames));
//        destCombo.setPromptText("Select destination");
//        addRow(grid, 1, "Destination:", destCombo);
//
//        // Buttons
//        Button connectBtn = new Button("Connect");
//        Button cancelBtn  = new Button("Cancel");
//        grid.add(connectBtn, 0, 2);
//        grid.add(cancelBtn,  1, 2);
//
//        // Enable Connect only when a destination is selected
//        connectBtn.disableProperty().bind(destCombo.valueProperty().isNull());
//
//        connectBtn.setOnAction(e -> {
//            String dest = destCombo.getValue();
//            ConnectCommand cmd = new ConnectCommand(sourceBuildingName, dest);
//            String err = cmd.execute();
//            if (err == null || err.isBlank()) {
//                showAlert(Alert.AlertType.INFORMATION,
//                        String.format("Connected %s → %s", sourceBuildingName, dest));
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
//     * Adds a label-control pair to the GridPane at the specified row.
//     */
//    private static void addRow(GridPane grid, int row, String labelText, Control control) {
//        grid.add(new Label(labelText), 0, row);
//        grid.add(control, 1, row);
//    }
//
//    /**
//     * Helper to show an alert and wait.
//     */
//    private static void showAlert(Alert.AlertType type, String msg) {
//        Alert alert = new Alert(type, msg, ButtonType.OK);
//        alert.setHeaderText(null);
//        alert.showAndWait();
//    }
//}
