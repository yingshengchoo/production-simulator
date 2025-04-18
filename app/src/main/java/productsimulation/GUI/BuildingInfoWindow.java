package productsimulation.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import productsimulation.LogicTime;
import productsimulation.Log;
import productsimulation.State;
import productsimulation.command.RequestCommand;
import productsimulation.model.*;
import productsimulation.model.road.Direction;
import productsimulation.model.road.RoadTile;

import java.util.List;
import java.util.Objects;

/**
 * Modal dialog displaying information for a Building or RoadTile,
 * with context-specific actions (request items or connect mode).
 * Uses static GUI getters for board display, root pane, and feedback pane.
 *
 * <p>Usage:
 * <pre>
 *   BuildingInfoWindow.show(building);
 *   BuildingInfoWindow.show(roadTile);
 * </pre>
 *
 * @author Taiyan Liu
 * @version 1.0
 */
public final class BuildingInfoWindow {
    private BuildingInfoWindow() { /* no instances */ }

    /**
     * Show info for a Building, with request and connect actions.
     * @param b the target Building
     */
    public static void show(Building b) {
        Objects.requireNonNull(b, "Building");
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Building: " + b.getName());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        root.getChildren().add(createInfoSection(b));
        root.getChildren().add(createRequestSection(b));
        root.getChildren().add(createConnectButton(b));

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    /**
     * Show info for a RoadTile (read-only).
     * @param tiles the RoadTile
     */
    public static void show(List<RoadTile> tiles) {
        Objects.requireNonNull(tiles, "RoadTile");
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Road Info");

        Direction from = new Direction(Direction.UNDEFINED);
        Direction to = new Direction(Direction.UNDEFINED);
        for(RoadTile t: tiles) {
            from.addDirection(t.getFromDirection());
            to.addDirection(t.getToDirection());
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
                new Label("From: " + from),
                new Label("To:   " + to)
        );

        stage.setScene(new Scene(root, 300, 120));
        stage.showAndWait();
    }

    // Info section
    private static TitledPane createInfoSection(Building b) {
        VBox box = new VBox(8);
        box.getChildren().addAll(
                new Label("Step: " + LogicTime.getInstance().getStep()),
                new Label("Name: " + b.getName()),
                new Label("Coord: (" + b.getX() + "," + b.getY() + ")"),
                new Label("Queue size: " + b.getRequestQueue().size())
        );
        if (b instanceof Mine)      box.getChildren().addAll(mineLabels((Mine)b));
        else if (b instanceof Storage) box.getChildren().addAll(storageLabels((Storage)b));
        else if (b instanceof Factory) box.getChildren().addAll(factoryLabels((Factory)b));

        TitledPane pane = new TitledPane("Details", box);
        pane.setCollapsible(false);
        return pane;
    }

    // Request section
    private static TitledPane createRequestSection(Building b) {
        ObservableList<String> options = recipeOptions(b);
        ComboBox<String> combo = new ComboBox<>(options);
        combo.setPromptText("Select item");
        combo.setMaxWidth(Double.MAX_VALUE);

        Button btn = new Button("Request");
        btn.setOnAction(e -> {
            String item = combo.getValue();
            if (item == null) {
                showAlert(Alert.AlertType.WARNING, "Select an item.");
                return;
            }
            String err = new RequestCommand(item, b.getName()).execute();
            if (err == null || err.isBlank()) {
                showAlert(Alert.AlertType.INFORMATION, "Requested " + item);
            } else {
                showAlert(Alert.AlertType.ERROR, err);
            }
            GUI.getFeedbackPane().setContent(Log.getLogText());
        });

        HBox hbox = new HBox(8, combo, btn);
        hbox.setAlignment(Pos.CENTER_LEFT);

        TitledPane pane = new TitledPane("Request Item", new VBox(10, hbox));
        pane.setCollapsible(false);
        return pane;
    }

    // Connect button
    private static Button createConnectButton(Building b) {
        Button connect = new Button("Connect");
        connect.setMaxWidth(Double.MAX_VALUE);
        connect.setOnAction(e -> {
            Stage stage = (Stage)connect.getScene().getWindow();
            stage.close();
            InteractiveConnectMode.activate(
                    b.getName(),
                    State.getInstance(),
                    GUI.getBoardDisplay(),
                    GUI.getRootPane(),
                    () -> GUI.getFeedbackPane().setContent(Log.getLogText())
            );
        });
        return connect;
    }

    // Helpers
    private static ObservableList<String> recipeOptions(Building b) {
        if (b instanceof Mine)    return FXCollections.observableArrayList(
                b.getBuildingType().getAllRecipes().keySet());
        if (b instanceof Storage) return FXCollections.observableArrayList(
                List.of(((Storage)b).getRecipeOutput()));
        if (b instanceof Factory) return FXCollections.observableArrayList(
                b.getBuildingType().getAllRecipes().keySet());
        return FXCollections.emptyObservableList();
    }

    private static List<Label> mineLabels(Mine m) {
        return List.of(
                new Label("Type: Mine"),
                new Label("Produces: " + m.getBuildingType().getName())
        );
    }
    private static List<Label> storageLabels(Storage s) {
        return List.of(
                new Label("Type: Storage"),
                new Label("Stores: " + s.getRecipeOutput()),
                new Label("Capacity: " + s.getTotalCapacity()),
                new Label("Stock:    " + s.getStockCount())
        );
    }
    private static List<Label> factoryLabels(Factory f) {
        String recipes = String.join(", ", f.getBuildingType().getAllRecipes().keySet());
        String status  = f.getCurrentRequest() == null
                ? "Idle"
                : f.getCurrentRequest().getIngredient() + " (" + f.getCurrentRemainTime() + " steps)";
        return List.of(
                new Label("Type: Factory"),
                new Label("Recipes: " + recipes),
                new Label("Status:  " + status)
        );
    }

    private static void showAlert(Alert.AlertType type, String text) {
        Alert a = new Alert(type, text, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
