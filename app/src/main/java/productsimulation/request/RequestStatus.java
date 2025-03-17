package productsimulation.request;

public class RequestStatus {
    public static final RequestStatus WAITING = new RequestStatus("WAITING");
    public static final RequestStatus READY = new RequestStatus("READY");
    public static final RequestStatus WORKING = new RequestStatus("WORKING");

    private final String name;

    private RequestStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RequestStatus that = (RequestStatus) obj;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

