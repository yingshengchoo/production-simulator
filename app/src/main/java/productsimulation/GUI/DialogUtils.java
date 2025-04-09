package productsimulation.GUI;

import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class DialogUtils {
    public static int promptForInt(String title, String header, int defaultVal) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(defaultVal));
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(defaultVal);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}