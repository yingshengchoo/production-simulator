package productsimulation.GUI;

import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class PolicyDialog {
    public static String[] showPolicyDialog() {
        TextInputDialog typeDialog = new TextInputDialog();
        typeDialog.setTitle("Set Policy");
        typeDialog.setHeaderText("Enter policy type (request or source):");
        Optional<String> policyType = typeDialog.showAndWait();
        if (!policyType.isPresent()) return null;

        TextInputDialog valueDialog = new TextInputDialog();
        valueDialog.setTitle("Set Policy");
        valueDialog.setHeaderText("Enter policy value (e.g., fifo, sjf, ready, qlen, simplelat, recursivelat, or default):");
        Optional<String> policyValue = valueDialog.showAndWait();
        if (!policyValue.isPresent()) return null;

        TextInputDialog targetDialog = new TextInputDialog();
        targetDialog.setTitle("Set Policy");
        targetDialog.setHeaderText("Enter target (a building name in quotes, '*' or default):");
        Optional<String> target = targetDialog.showAndWait();
        if (!target.isPresent()) return null;

        return new String[] { policyType.get(), policyValue.get(), target.get() };
    }
}