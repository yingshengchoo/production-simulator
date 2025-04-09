package productsimulation.GUI;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileDialogs {
    public static File showLoadDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Simulation Setup");
        return chooser.showOpenDialog(new Stage());
    }

    public static File showSaveDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Simulation State");
        return chooser.showSaveDialog(new Stage());
    }
}