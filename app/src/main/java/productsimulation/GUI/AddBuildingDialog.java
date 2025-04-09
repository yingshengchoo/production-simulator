package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.State;

public class AddBuildingDialog {

    /**
     * Opens a modal dialog to add a new building.
     * @param state The simulation state (can be used for dynamic data or validation).
     * @return A command string for creating the building, or null if cancelled/invalid.
     */
    public static String showAddBuildingDialog(State state) {
        Stage addStage = new Stage();
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.setTitle("Add New Building");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Building Name
        Label nameLabel = new Label("Building Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter unique building name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // Building Type (using a fixed list as an example; in a real project, load this from the JSON 'types')
        Label typeLabel = new Label("Building Type:");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Bolt Storage (100)", "Door Factory", "Metal Mine"
        ));
        typeCombo.setPromptText("Select building type");
        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1);

        // Coordinates (optional)
        Label xLabel = new Label("X Coordinate (optional):");
        TextField xField = new TextField();
        xField.setPromptText("E.g., 5");
        grid.add(xLabel, 0, 2);
        grid.add(xField, 1, 2);

        Label yLabel = new Label("Y Coordinate (optional):");
        TextField yField = new TextField();
        yField.setPromptText("E.g., 3");
        grid.add(yLabel, 0, 3);
        grid.add(yField, 1, 3);

        // Extra parameters for storage buildings.
        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Positive integer");
        Label priorityLabel = new Label("Priority:");
        TextField priorityField = new TextField();
        priorityField.setPromptText("Positive float");
        capacityLabel.setVisible(false);
        capacityField.setVisible(false);
        priorityLabel.setVisible(false);
        priorityField.setVisible(false);
        grid.add(capacityLabel, 0, 4);
        grid.add(capacityField, 1, 4);
        grid.add(priorityLabel, 0, 5);
        grid.add(priorityField, 1, 5);

        // Show extra fields only if the selected type indicates a storage building.
        typeCombo.setOnAction(e -> {
            String selected = typeCombo.getValue();
            if (selected != null && selected.toLowerCase().contains("storage")) {
                capacityLabel.setVisible(true);
                capacityField.setVisible(true);
                priorityLabel.setVisible(true);
                priorityField.setVisible(true);
            } else {
                capacityLabel.setVisible(false);
                capacityField.setVisible(false);
                priorityLabel.setVisible(false);
                priorityField.setVisible(false);
            }
        });

        // Submit Button to close dialog.
        Button submit = new Button("Add Building");
        submit.setOnAction(e -> addStage.close());
        grid.add(submit, 0, 6, 2, 1);

        Scene scene = new Scene(grid);
        addStage.setScene(scene);
        addStage.showAndWait();

        // Build the command string based on entered inputs.
        String name = nameField.getText().trim();
        String type = typeCombo.getValue();
        if (name.isEmpty() || type == null) {
            return null; // Mandatory fields missing.
        }
        StringBuilder command = new StringBuilder();
        // Example command syntax; adjust to match your simulation engine:
        command.append("create building '").append(name)
                .append("' type '").append(type).append("'");

        // Append coordinates if provided.
        if (!xField.getText().trim().isEmpty() && !yField.getText().trim().isEmpty()) {
            try {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                command.append(" at ").append(x).append(" ").append(y);
            } catch (NumberFormatException ex) {
                // If coordinates are invalid, skip them for auto-placement.
            }
        }
        // Append extra storage parameters if applicable.
        if (type.toLowerCase().contains("storage")) {
            if (!capacityField.getText().trim().isEmpty() && !priorityField.getText().trim().isEmpty()) {
                command.append(" capacity ").append(capacityField.getText().trim())
                        .append(" priority ").append(priorityField.getText().trim());
            }
        }
        return command.toString();
    }
}