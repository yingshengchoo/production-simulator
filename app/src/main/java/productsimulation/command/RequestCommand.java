package productsimulation.command;

import productsimulation.request.Request;

public class RequestCommand extends Command{
    private String itemName;
    private String buildingName;

    public RequestCommand(String itemName, String buildingName) {
        this.itemName = itemName;
        this.buildingName = buildingName;
    }

    @Override
    public String execute() {
        return Request.userRequestHandler(itemName, buildingName);
    }

    public String getItem() {
        return itemName;
    }

    public String getBuilding() {
        return buildingName;
    }
}
