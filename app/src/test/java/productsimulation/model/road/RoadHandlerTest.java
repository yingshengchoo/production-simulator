package productsimulation.model.road;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoadHandlerTest {
    @Test
    void removeHandler_invalid() {
        assertNotNull(RoadHandler.removeHandler("notExist1", "notExist2"));
    }
}