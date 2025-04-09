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

public class RequestDialog {
    // Pass the simulation state to get a list of building names.
    public static String[] showRequestDialog(State state) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Request Item");
        dialog.setHeaderText("Enter item name and select building");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField itemField = new TextField();
        itemField.setPromptText("Item (e.g., door)");

        // Create a ComboBox with building names from the state.
        List<String> buildingNames = state.getBuildings().stream()
                .map(Building::getName)
                .collect(Collectors.toList());
        ComboBox<String> buildingBox = new ComboBox<>(FXCollections.observableArrayList(buildingNames));
        buildingBox.setPromptText("Select building");

        grid.add(new Label("Item:"), 0, 0);
        grid.add(itemField, 1, 0);
        grid.add(new Label("Building:"), 0, 1);
        grid.add(buildingBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return new String[] { itemField.getText(), buildingBox.getValue() };
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        return result.orElse(null);
    }
}