package productsimulation.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.StepCommand;

public class StepWindow {

    public static void show(State state, Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Step Simulation");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label stepLabel = new Label("Number of Steps:");
        TextField stepField = new TextField();
        stepField.setPromptText("Enter integer steps");

        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");

        // 执行后端
        submitBtn.setOnAction(e -> {
            try {
                int steps = Integer.parseInt(stepField.getText().trim());
                StepCommand cmd = new StepCommand(steps);
                String error = cmd.execute();
                if (error == null) {
                    showInfo("Stepped " + steps + " cycles.");
                    if (onSuccessRefresh != null) onSuccessRefresh.run();
                    stage.close();
                } else {
                    showError(error);
                }
            } catch (NumberFormatException ex) {
                showError("Invalid integer for steps.");
            }
        });
        cancelBtn.setOnAction(e -> stage.close());

        grid.add(stepLabel, 0, 0);
        grid.add(stepField, 1, 0);
        grid.add(submitBtn, 0, 1);
        grid.add(cancelBtn, 1, 1);

        stage.setScene(new Scene(grid, 300, 150));
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
