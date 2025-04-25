package productsimulation.GUI;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.model.waste.WasteDisposal;

import java.util.Map;

/**
 * 窗口：展示 WasteDisposal 的各种废物处理配置
 */
public class WasteDisposalWindow extends Stage {
    private final WasteDisposal disposal;
    private final ObservableList<ConfigRow> rows;

    public WasteDisposalWindow(WasteDisposal disposal) {
        this.disposal = disposal;
        setTitle("Waste Disposal 管理 - " + disposal.getName());
        initModality(Modality.APPLICATION_MODAL);

        // 生成表格行
        rows = FXCollections.observableArrayList();
        for (Map.Entry<String, WasteDisposal.WasteConfig> entry : disposal.configs.entrySet()) {
            String type = entry.getKey();
            WasteDisposal.WasteConfig cfg = entry.getValue();
            int nextTime = cfg.interval - cfg.timer; // 下次处理时间
            rows.add(new ConfigRow(
                    type,
                    cfg.stored,
                    cfg.booked,
                    nextTime,
                    cfg.rate
            ));
        }

        // 表格
        TableView<ConfigRow> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ConfigRow, String> typeCol = new TableColumn<>("废物类型");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<ConfigRow, Number> storedCol = new TableColumn<>("存量");
        storedCol.setCellValueFactory(new PropertyValueFactory<>("stored"));

        TableColumn<ConfigRow, Number> bookedCol = new TableColumn<>("在途");
        bookedCol.setCellValueFactory(new PropertyValueFactory<>("booked"));

        TableColumn<ConfigRow, Number> nextCol = new TableColumn<>("下次处理时间");
        nextCol.setCellValueFactory(new PropertyValueFactory<>("nextTime"));

        TableColumn<ConfigRow, Number> rateCol = new TableColumn<>("处理数量");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));

        table.getColumns().addAll(typeCol, storedCol, bookedCol, nextCol, rateCol);

        VBox root = new VBox(10, table);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 500, 400);
        setScene(scene);
    }

    /**
     * 行数据模型
     */
    public static class ConfigRow {
        private final SimpleStringProperty type;
        private final SimpleIntegerProperty stored;
        private final SimpleIntegerProperty booked;
        private final SimpleIntegerProperty nextTime;
        private final SimpleIntegerProperty rate;

        public ConfigRow(String type, int stored, int booked, int nextTime, int rate) {
            this.type = new SimpleStringProperty(type);
            this.stored = new SimpleIntegerProperty(stored);
            this.booked = new SimpleIntegerProperty(booked);
            this.nextTime = new SimpleIntegerProperty(nextTime);
            this.rate = new SimpleIntegerProperty(rate);
        }

        public String getType() { return type.get(); }
        public int getStored() { return stored.get(); }
        public int getBooked() { return booked.get(); }
        public int getNextTime() { return nextTime.get(); }
        public int getRate() { return rate.get(); }
    }
}

