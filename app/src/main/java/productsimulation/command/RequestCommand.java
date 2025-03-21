package productsimulation.command;

import productsimulation.Log;
import productsimulation.RequestBroadcaster;
import productsimulation.request.Request;

public class RequestCommand extends Command{
    private String itemName;
    private String buildingName;

    public RequestCommand(String itemName, String buildingName) {
        this.itemName = itemName;
        this.buildingName = buildingName;
    }

    @Override
    public void execute() {
        RequestBroadcaster.getInstance().userRequestHandler(itemName, buildingName);
    }
}
