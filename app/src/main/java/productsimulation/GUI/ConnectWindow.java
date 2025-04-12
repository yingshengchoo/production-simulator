package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.ConnectCommand; // backend

import java.util.List;
import java.util.stream.Collectors;

public class ConnectWindow {

    public static void show(State state, Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Connect Buildings");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Drop-down menus for building names
        List<String> buildingNames = state.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        ComboBox<String> sourceBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        sourceBox.setPromptText("Source building");
        ComboBox<String> destBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        destBox.setPromptText("Destination building");

        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            String source = sourceBox.getValue();
            String dest = destBox.getValue();
            if (source == null || dest == null) {
                showError("Please select both source and destination.");
                return;
            }

            // Connect to backend logic:
            ConnectCommand cmd = new ConnectCommand(source, dest);
            String error = cmd.execute();
            if (error == null) {
                showInfo("Successfully connected " + source + " to " + dest);
                if (onSuccessRefresh != null) onSuccessRefresh.run();
                stage.close();
            } else {
                showError(error);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(new Label("Source:"), 0, 0);
        grid.add(sourceBox, 1, 0);
        grid.add(new Label("Destination:"), 0, 1);
        grid.add(destBox, 1, 1);
        grid.add(submitBtn, 0, 2);
        grid.add(cancelBtn, 1, 2);

        stage.setScene(new Scene(grid, 350, 200));
        stage.showAndWait();
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
