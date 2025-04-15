package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.SetPolicyCommand;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A modal window to let the user set a scheduling or source policy
 * (request vs. source) with a chosen policy value (fifo, sjf, etc.)
 * and a target (building name, '*', or 'default').
 */
public class PolicyWindow {

    /**
     * Shows the policy window modally. On success, calls onSuccessRefresh.
     */
    public static void show(State state, Runnable onSuccessRefresh) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Set Policy");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label typeLabel = new Label("Policy Type:");
        // A drop-down for "request" or "source"
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("request", "source"));
        typeBox.setPromptText("Pick request or source");

        Label valueLabel = new Label("Policy Value:");
        // Another combo that updates based on which type is chosen
        ComboBox<String> valueBox = new ComboBox<>();

        Label targetLabel = new Label("Target:");
        // The user can pick a building or '*' or 'default'
        List<String> buildingNames = state.getBuildings().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        buildingNames.add("*");
        buildingNames.add("default");
        ComboBox<String> targetBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        targetBox.setPromptText("Pick building, '*' or 'default'");

        // When type changes, update the valueBox items
        typeBox.setOnAction(e -> {
            String selection = typeBox.getValue();
            if ("request".equals(selection)) {
                valueBox.setItems(FXCollections.observableArrayList("fifo", "sjf", "ready", "default"));
            } else if ("source".equals(selection)) {
                valueBox.setItems(FXCollections.observableArrayList("qlen", "simplelat", "recursivelat", "default"));
            } else {
                valueBox.setItems(FXCollections.observableArrayList());
            }
        });
        valueBox.setPromptText("Pick a policy value");

        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");

        submitBtn.setOnAction(e -> {
            String pType = typeBox.getValue();
            String pValue = valueBox.getValue();
            String pTarget = targetBox.getValue();
            if (pType == null || pValue == null || pTarget == null) {
                showError("Please select a policy type, a value, and a target.");
                return;
            }

            SetPolicyCommand cmd = new SetPolicyCommand(pType, pTarget, pValue);
            String error = cmd.execute();
            if (error == null) {
                showInfo("Policy set: " + pType + " -> " + pValue + " on " + pTarget);
                if (onSuccessRefresh != null) {
                    onSuccessRefresh.run();
                }
                stage.close();
            } else {
                showError(error);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        grid.add(typeLabel, 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(valueLabel, 0, 1);
        grid.add(valueBox, 1, 1);
        grid.add(targetLabel, 0, 2);
        grid.add(targetBox, 1, 2);
        grid.add(submitBtn, 0, 3);
        grid.add(cancelBtn, 1, 3);

        stage.setScene(new Scene(grid, 420, 220));
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
