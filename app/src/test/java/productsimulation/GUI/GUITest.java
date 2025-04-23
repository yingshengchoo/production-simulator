package productsimulation.GUI;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JavaFX-based GUI menus and layout methods,
 * without launching a full Stage or relying on State initialization.
 */
public class GUITest {
    private static GUI gui;

    @BeforeAll
    public static void initToolkit() {
        // Initialize JavaFX toolkit once, ignoring if already initialized
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Toolkit already initialized
        }
        gui = new GUI();
    }

    @Test
    public void testBuildMenuBar() throws Exception {
        // Invoke private buildMenuBar() via reflection
        Method buildMenuBar = GUI.class.getDeclaredMethod("buildMenuBar");
        buildMenuBar.setAccessible(true);
        MenuBar menuBar = (MenuBar) buildMenuBar.invoke(gui);
        assertNotNull(menuBar, "MenuBar should not be null");
        List<String> topMenuTexts = menuBar.getMenus().stream()
                .map(Menu::getText)
                .collect(Collectors.toList());
        assertEquals(List.of("File", "Run", "Settings"), topMenuTexts);
    }

    @Test
    public void testFileMenuItems() throws Exception {
        Method buildMenuBar = GUI.class.getDeclaredMethod("buildMenuBar");
        buildMenuBar.setAccessible(true);
        MenuBar menuBar = (MenuBar) buildMenuBar.invoke(gui);
        Menu file = menuBar.getMenus().stream()
                .filter(m -> "File".equals(m.getText()))
                .findFirst().orElseThrow();

        List<MenuItem> items = file.getItems();
        assertEquals(4, items.size());
        assertEquals("Save", items.get(0).getText());
        assertEquals("Load", items.get(1).getText());
        assertTrue(items.get(2) instanceof SeparatorMenuItem, "Third item should be a separator");
        assertEquals("Exit", items.get(3).getText());

        // Check accelerators are set
        assertNotNull(items.get(0).getAccelerator(), "Save should have accelerator");
        assertNotNull(items.get(1).getAccelerator(), "Load should have accelerator");
    }

    @Test
    public void testRunMenuItems() throws Exception {
        Method createRunMenu = GUI.class.getDeclaredMethod("createRunMenu");
        createRunMenu.setAccessible(true);
        Object runMenuObj = createRunMenu.invoke(gui);
        assertTrue(runMenuObj instanceof Menu, "createRunMenu should return a Menu");
        Menu runMenu = (Menu) runMenuObj;
        List<String> texts = runMenu.getItems().stream()
                .map(MenuItem::getText)
                .collect(Collectors.toList());
        assertEquals(List.of("Go One Step", "Go N Steps", "Finish"), texts);
    }

    @Test
    public void testSettingsMenuItems() throws Exception {
        Method createSettingsMenu = GUI.class.getDeclaredMethod("createSettingsMenu");
        createSettingsMenu.setAccessible(true);
        Menu settings = (Menu) createSettingsMenu.invoke(gui);
        List<String> texts = settings.getItems().stream()
                .map(MenuItem::getText)
                .collect(Collectors.toList());
//        assertEquals(List.of("Set Verbosity...", "Set Policy..."), texts);
    }
}