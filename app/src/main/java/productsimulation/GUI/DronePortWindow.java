package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import productsimulation.Coordinate;
import productsimulation.model.drone.Drone;
import productsimulation.model.drone.DronePort;

/**
 * DronePortWindow 用于展示指定 DronePort 的所有无人机及其状态，并可添加新无人机。
 */
public class DronePortWindow extends Stage {
    private final DronePort dronePort;
    private final ObservableList<Drone> droneList;
    private final TableView<Drone> tableView;

    public DronePortWindow(DronePort port) {
        this.dronePort = port;
        this.droneList = FXCollections.observableArrayList(port.getDrones());

        setTitle("DronePort 管理 - " + port.getName());
        initModality(Modality.APPLICATION_MODAL);

        // 表格视图
        tableView = new TableView<>();

        // 位置列，使用 SimpleStringProperty
        TableColumn<Drone, String> posCol = new TableColumn<>("位置");
        posCol.setCellValueFactory(cell -> {
            Coordinate c = cell.getValue().getPosition();
            String posText = c.x + ", " + c.y;
            return new SimpleStringProperty(posText);
        });
        posCol.setPrefWidth(120);

        // 状态列
        TableColumn<Drone, String> stateCol = new TableColumn<>("状态");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateCol.setPrefWidth(100);

        tableView.getColumns().addAll(posCol, stateCol);
        tableView.setItems(droneList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 添加无人机按钮
        Button addBtn = new Button("添加无人机");
        addBtn.setOnAction(e -> onAddDrone());

        VBox root = new VBox(10, tableView, addBtn);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 300, 400);
        setScene(scene);
    }

    private void onAddDrone() {
        boolean ok = dronePort.constructDrone();
        if (!ok) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "已达最大容量，无法再添加无人机。");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            // 刷新列表
            droneList.setAll(dronePort.getDrones());
        }
    }
}
