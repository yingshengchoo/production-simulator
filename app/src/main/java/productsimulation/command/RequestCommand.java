package productsimulation.command;

import productsimulation.RequestBroadcaster;

public class RequestCommand extends Command{
    private String itemName;
    private String buildingName;

    public RequestCommand(String itemName, String buildingName) {
        this.itemName = itemName;
        this.buildingName = buildingName;
    }

    @Override
    public String execute() {
        RequestBroadcaster.getInstance().userRequestHandler(itemName, buildingName);
        return null;
    }

    public String getItem() {
        return itemName;
    }

    public String getBuilding() {
        return buildingName;
    }
}
