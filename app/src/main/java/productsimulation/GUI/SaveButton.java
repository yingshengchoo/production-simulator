package productsimulation.GUI;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SaveButton {

    public static Button create(Label targetLabel) {
        Button button = new Button("Save");
        button.setOnAction(e -> {
            System.out.println("Save clicked!");
            targetLabel.setText("You clicked Save!");
            // Add real save logic here
        });
        return button;
    }
}