package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import productsimulation.State;
import productsimulation.model.Building;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConnectDialog {
    public static String[] showConnectDialog(State state) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Connect Buildings");
        dialog.setHeaderText("Select source and destination buildings");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Get building names.
        List<String> buildingNames = state.getBuildings().stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        ComboBox<String> sourceBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        sourceBox.setPromptText("Source building");
        ComboBox<String> destBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        destBox.setPromptText("Destination building");

        grid.add(new Label("Source:"), 0, 0);
        grid.add(sourceBox, 1, 0);
        grid.add(new Label("Destination:"), 0, 1);
        grid.add(destBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return new String[] { sourceBox.getValue(), destBox.getValue() };
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        return result.orElse(null);
    }
}