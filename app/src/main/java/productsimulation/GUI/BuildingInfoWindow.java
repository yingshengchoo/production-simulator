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
import productsimulation.command.RemoveBuildingCommand;
import productsimulation.model.*;
import productsimulation.model.road.Direction;
import productsimulation.model.road.RoadTile;
import productsimulation.model.waste.WasteDisposal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Modal dialog displaying information for a Building or RoadTile,
 * with context-specific actions (request items, connect, disconnect, or remove).
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
     * Show info for a Building, with request, connect, disconnect, and remove actions.
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
        root.getChildren().add(createDisconnectButton(b));
        root.getChildren().add(createRemoveButton(b));

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
                new Label("From: " + from.getDirections()),
                new Label("To:   " + to.getDirections())
        );

        stage.setScene(new Scene(root, 300, 120));
        stage.showAndWait();
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

    // Disconnect button
    private static Button createDisconnectButton(Building b) {
        Button disc = new Button("Disconnect");
        disc.setMaxWidth(Double.MAX_VALUE);
        disc.setOnAction(e -> {
            Stage stage = (Stage)disc.getScene().getWindow();
            stage.close();
            InteractiveDisconnectMode.activate(
                    b.getName(),
                    State.getInstance(),
                    GUI.getBoardDisplay(),
                    GUI.getRootPane(),
                    () -> GUI.getFeedbackPane().setContent(Log.getLogText())
            );
        });
        return disc;
    }

    // Remove button
    private static Button createRemoveButton(Building b) {
        Button rm = new Button("Remove");
        rm.setMaxWidth(Double.MAX_VALUE);
        rm.setOnAction(e -> {
            String err = new RemoveBuildingCommand(b.getName()).execute();
            if (err == null) {
                GUI.getBoardDisplay().refresh();
                GUI.getFeedbackPane().appendLine("Removed " + b.getName());
                Stage stage = (Stage)rm.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, err);
            }
        });
        return rm;
    }

    // Helpers for options and labels
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

    private static TitledPane createInfoSection(Building b) {
        VBox box = new VBox(8);
        box.getChildren().addAll(
                new Label("Step: " + LogicTime.getInstance().getStep()),
                new Label("Name: " + b.getName()),
                new Label("Coord: (" + b.getX() + "," + b.getY() + ")"),
                new Label("Queue size: " + b.getRequestQueue().size())
        );

        if (b instanceof Mine) {
            box.getChildren().addAll(mineLabels((Mine) b));
            Map<String, Integer> wastes = b.getWastes();
            if (!wastes.isEmpty()) {
                box.getChildren().add(new Label("Waste Info:"));
                for (Map.Entry<String, Integer> e : wastes.entrySet()) {
                    box.getChildren().add(new Label(
                            String.format("%s: %d", e.getKey(), e.getValue())
                    ));
                }
            }
        } else if (b instanceof Storage) {
            box.getChildren().addAll(storageLabels((Storage) b));
        } else if (b instanceof Factory) {
            box.getChildren().addAll(factoryLabels((Factory) b));
            // 展示 Factory 产生的 waste
            Map<String, Integer> wastes = b.getWastes();
            if (!wastes.isEmpty()) {
                box.getChildren().add(new Label("Waste Info:"));
                for (Map.Entry<String, Integer> e : wastes.entrySet()) {
                    box.getChildren().add(new Label(
                            String.format("%s: %d", e.getKey(), e.getValue())
                    ));
                }
            }
        }
        // 如果有其他类型，不做额外展示

        TitledPane pane = new TitledPane("Details", box);
        pane.setCollapsible(false);
        return pane;
    }
}
