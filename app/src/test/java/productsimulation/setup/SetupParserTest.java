package productsimulation.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.model.*;
import productsimulation.model.road.Road;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SetupParserTest {
    private SetupParser parser;

    @BeforeEach
    void setUp() {
        Board.getBoard().cleanup();
        Road.cleanup();
        Building.buildingGlobalList.clear();
        parser = new SetupParser();
    }

    @Test
    void testParseDoors1Json() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/doors1.json")) {
            assertNotNull(is, "Resource doors1.json not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String error = parser.parse(reader);
                assertNull(error, "doors1.json should parse successfully without errors.");
            }
        }

        Map<String, Recipe> recipeMap = parser.getRecipeMap();
        Map<String, BuildingType> typeMap = parser.getTypeMap();
        Map<String, Building> buildingMap = parser.getBuildingMap();

        assertEquals(5, recipeMap.size(), "Should have 5 recipes in doors1.json");
        assertEquals(3, typeMap.size(), "Should have 3 types in doors1.json");
        assertEquals(5, buildingMap.size(), "Should have 5 buildings in doors1.json");
    }

    @Test
    void testParseDoors2Json() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/doors2.json")) {
            assertNotNull(is, "Resource doors2.json not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String error = parser.parse(reader);
                assertNull(error, "doors2.json should parse successfully without errors.");
            }
        }

        Map<String, Recipe> recipeMap = parser.getRecipeMap();
        Map<String, BuildingType> typeMap = parser.getTypeMap();
        Map<String, Building> buildingMap = parser.getBuildingMap();

        assertEquals(5, recipeMap.size());
        assertEquals(2, typeMap.size());
        assertEquals(6, buildingMap.size());
    }

    @Test
    void testParseDoors3Json() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/doors3.json")) {
            assertNotNull(is, "Resource doors3.json not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String error = parser.parse(reader);
                assertEquals("Input validation error: Building 'D' has unknown type: door", error);
            }
        }
    }

    @Test
    void testParseDoors4Json() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/notJSON.json")) {
            assertNotNull(is, "Resource illegal not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                assertEquals("Failed to parse JSON file.", parser.parse(reader));
            }
        }
    }

    @Test
    void testIllegalStorageJson() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/bad_storage.json")) {
            assertNotNull(is, "Resource bad_storage.json not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String error = parser.parse(reader);
                assertNotNull( error);
            }
        }
    }

    @Test
    void testStorageJson() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/storage1.json")) {
            assertNotNull(is, "Resource bad_storage.json not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String error = parser.parse(reader);
                assertNull( error);
            }
        }
    }

    @Test
    void testComprehensive() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/storage2.json")) {
            assertNotNull(is, "Resource bad_storage.json not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String error = parser.parse(reader);
                assertNull( error);
            }
        }
    }
 }
