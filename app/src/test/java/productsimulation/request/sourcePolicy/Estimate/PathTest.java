package productsimulation.request.sourcePolicy.Estimate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import productsimulation.model.Building;

import java.util.List;

class PathTest {

    private Building createBuildingMock() {
        return mock(Building.class, new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if ("equals".equals(invocation.getMethod().getName())) {
                    return true;
                }
                return RETURNS_DEFAULTS.answer(invocation);
            }
        });
    }

    @Test
    void testIsPrefixOfWithMatchingPaths() {

        Building buildingMock1 = createBuildingMock();
        Building buildingMock2 = createBuildingMock();
        Building buildingMock3 = createBuildingMock();

        Path path1 = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(2, buildingMock2)
        ));

        Path path2 = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(2, buildingMock2),
                new Segment(3, buildingMock3)
        ));
        assertTrue(path1.isPrefixOf(path2));
    }

    @Test
    void testIsPrefixOfWithNonPrefixPaths() {
        Building buildingMock1 = createBuildingMock();
        Building buildingMock2 = createBuildingMock();
        Building buildingMock3 = createBuildingMock();

        Path path1 = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(3, buildingMock3)
        ));
        Path path2 = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(2, buildingMock2),
                new Segment(3, buildingMock3)
        ));
        assertFalse(path1.isPrefixOf(path2));
    }

    @Test
    void testIsPrefixOfWithEmptyPaths() {
        Path emptyPath = new Path();
        Building buildingMock1 = createBuildingMock();
        Building buildingMock2 = createBuildingMock();
        Path fullPath = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(2, buildingMock2)
        ));
        assertTrue(emptyPath.isPrefixOf(fullPath));
        assertTrue(emptyPath.isPrefixOf(emptyPath));
        assertFalse(fullPath.isPrefixOf(emptyPath));
    }

    @Test
    void testLastBuilding() {
        Building buildingMock1 = createBuildingMock();
        Building buildingMock2 = createBuildingMock();
        Building buildingMock3 = createBuildingMock();
        Path path  = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(2, buildingMock2),
                new Segment(3, buildingMock3)
        ));

        assertEquals(path.getLastBuilding(), buildingMock3);
    }



    @Test
    void testAppend() {
        Building buildingMock1 = createBuildingMock();
        Building buildingMock2 = createBuildingMock();
        Building buildingMock3 = createBuildingMock();

        Path path1 = new Path(List.of(
                new Segment(1, buildingMock1),
                new Segment(2, buildingMock2)
        ));

        Path path2 = path1.append(new Segment(3, buildingMock3));
        assertTrue(path1.isPrefixOf(path2));
    }
}