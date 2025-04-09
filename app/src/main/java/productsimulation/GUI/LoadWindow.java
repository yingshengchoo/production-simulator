package productsimulation.GUI;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadWindow {

    public static void show() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Drag and Drop a File");

        Label dropLabel = new Label("Drop a file here");
        dropLabel.setStyle("-fx-border-color: #aaa; -fx-border-width: 2px; -fx-padding: 50px;");

        StackPane dropPane = new StackPane(dropLabel);
        dropPane.setPrefSize(300, 200);

        dropPane.setOnDragOver(event -> {
            if (event.getGestureSource() != dropPane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropPane.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                String filePath = db.getFiles().get(0).getAbsolutePath();
                System.out.println("File dropped: " + filePath);
                dropLabel.setText("Loaded: " + filePath);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        Scene popupScene = new Scene(dropPane);
        popupStage.setScene(popupScene);
        popupStage.show();
    }
}