package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;
import productsimulation.command.ConnectCommand;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BuildingConnectWindow provides a pop-up window that enables establishing a connection
 * from a specific source building to another destination building. The source building's
 * name is pre-populated and disabled, and the user selects a destination from a drop-down list.
 */
public class BuildingConnectWindow {

    /**
     * Displays the quick connect window with the source building locked.
     *
     * @param sourceBuildingName the name of the source building
     * @param state the current state used to retrieve available buildings
     */
    public static void show(String sourceBuildingName, State state) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Quick Connect");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Source field pre-filled and disabled.
        Label sourceLabel = new Label("Source:");
        TextField sourceField = new TextField(sourceBuildingName);
        sourceField.setDisable(true);

        // Destination selection combo box.
        Label destLabel = new Label("Destination:");
        List<String> destNames = state.getBuildings().stream()
                .map(b -> b.getName())
                .filter(name -> !name.equals(sourceBuildingName))
                .collect(Collectors.toList());
        ComboBox<String> destComboBox = new ComboBox<>(FXCollections.observableArrayList(destNames));
        destComboBox.setPromptText("Select destination building");

        // Connect and Cancel buttons.
        Button connectBtn = new Button("Connect");
        Button cancelBtn = new Button("Cancel");

        connectBtn.setOnAction(e -> {
            String dest = destComboBox.getValue();
            if (dest == null) {
                showError("Destination building is required.");
                return;
            }
            // Execute the command to connect.
            ConnectCommand cmd = new ConnectCommand(sourceBuildingName, dest);
            String error = cmd.execute();
            if (error == null || error.trim().isEmpty()) {
                showInfo("Connection established from " + sourceBuildingName + " to " + dest + ".");
                popup.close();
            } else {
                showError(error);
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        grid.add(sourceLabel, 0, 0);
        grid.add(sourceField, 1, 0);
        grid.add(destLabel, 0, 1);
        grid.add(destComboBox, 1, 1);
        grid.add(connectBtn, 0, 2);
        grid.add(cancelBtn, 1, 2);

        Scene scene = new Scene(grid, 400, 200);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
