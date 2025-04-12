package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.RequestCommand;
import java.util.List;
import java.util.stream.Collectors;

public class RequestWindow {

    public static void show(State state, Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Request Item");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        List<String> buildingNames = state.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());

        Label itemLabel = new Label("Item:");
        TextField itemField = new TextField();
        Label bldLabel = new Label("From building:");
        ComboBox<String> bldBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));

        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");

        // 执行后端
        submitBtn.setOnAction(e -> {
            String item = itemField.getText().trim();
            String building = bldBox.getValue();
            if (item.isEmpty() || building == null) {
                showError("Please enter an item and select a building.");
                return;
            }
            RequestCommand cmd = new RequestCommand(item, building);
            String error = cmd.execute();
            if (error == null || error.trim().isEmpty()) {
                showInfo("Requested " + item + " from " + building + ".");
                if (onSuccessRefresh != null) {
                    onSuccessRefresh.run();
                }
                stage.close();
            } else {
                showError(error);
            }
        });
        cancelBtn.setOnAction(e -> stage.close());

        grid.add(itemLabel, 0, 0); grid.add(itemField, 1, 0);
        grid.add(bldLabel, 0, 1);  grid.add(bldBox, 1, 1);
        grid.add(submitBtn, 0, 2); grid.add(cancelBtn, 1, 2);

        stage.setScene(new Scene(grid, 350, 180));
        stage.showAndWait();
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText(msg);
        a.showAndWait();
    }
    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Success");
        a.setContentText(msg);
        a.showAndWait();
    }
}
