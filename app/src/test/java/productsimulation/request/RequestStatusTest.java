package productsimulation.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestStatusTest {

    @Test
    void testToString() {
        // Test WAITING status
        RequestStatus status1 = RequestStatus.WAITING;
        assertEquals("WAITING", status1.toString(), "toString should return the correct name for WAITING");
    }

    @Test
    void testEquals() {
        // Test with the same object
        RequestStatus status1 = RequestStatus.WAITING;
        assertEquals(status1, status1, "Same object should be equal");

        // Test with equivalent objects
        RequestStatus status2 = RequestStatus.WAITING;
        assertEquals(status1, status2, "Equivalent objects should be equal");

        // Test with different objects
        RequestStatus status3 = RequestStatus.READY;
        assertNotEquals(status1, status3, "Different objects should not be equal");

        // Test with null
        assertNotEquals(status1, null, "Object should not be equal to null");

        // Test with a different type
        String differentType = "WAITING";
        assertNotEquals(differentType, status1, "Object should not be equal to a different type");
    }

    @Test
    void testHashCode() {
        // Test the hash code consistency
        RequestStatus status1 = RequestStatus.WAITING;
        int hashCode1 = status1.hashCode();
        assertEquals(hashCode1, status1.hashCode(), "Hash code should remain consistent");
    }
}