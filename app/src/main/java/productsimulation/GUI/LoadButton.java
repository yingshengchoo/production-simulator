package productsimulation.GUI;

import javafx.scene.control.Button;

public class LoadButton {

    public static Button create() {
        Button button = new Button("Load");
        button.setOnAction(e -> LoadWindow.show());
        return button;
    }
}