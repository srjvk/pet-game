package Implementation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParentalControls}.
 */
class ParentalControlsTest {

    @TempDir
    static Path tempDir;
    private static File tempFile;
    private ParentalControls parentalControls;
    private static final String PLAYER_ID = "player123";

    @BeforeAll
    static void setUpTestFile() throws IOException {
        tempFile = tempDir.resolve("test_playtime_tracking.csv").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            // CSV Header
            writer.write("playerId,limitEnabled,playtimeMinutes,avgPlaytimeMinutes,maxAllowedMinutes,sessionCount\n");
            // Test data
            writer.write("player123,true,120,30,180,4\n");
            writer.write("player456,false,60,15,120,5\n");
            // Duplicate player ID (to test findRow returns last occurrence)
            writer.write("player123,true,150,40,200,6\n");
        }
    }

    @BeforeEach
    void setUp() {
        parentalControls = new ParentalControls(tempFile.getAbsolutePath());
    }

    @Test
    void testConstructorWithFilePath() {
        // Test that constructor works with custom file path
        ParentalControls customControls = new ParentalControls(tempFile.getAbsolutePath());
        assertNotNull(customControls);
        assertEquals(150, customControls.getPlaytimeMinutes(PLAYER_ID));
    }

    @Test
    void testDefaultConstructor() {
        // This mainly checks that the default constructor doesn't throw exceptions

        assertDoesNotThrow(() -> new ParentalControls());
    }

    @Test
    void testFindRow() {

        // Should find the last instance of player123
        assertEquals(3, parentalControls.findRow(PLAYER_ID));

        // Player that doesn't exist
        assertEquals(-1, parentalControls.findRow("nonexistentPlayer"));
    }

    @Test
    void testGetPlayerId() {

        // Testing with correct index values (accounting for header row)
        assertEquals("player456", parentalControls.getPlayerId(2));
        assertEquals(PLAYER_ID, parentalControls.getPlayerId(3));
    }

    @Test
    void testSetPlayerId() {
        String newId = "newPlayerId";
        parentalControls.setPlayerId(3, newId);
        assertEquals(newId, parentalControls.getPlayerId(3));
        parentalControls.setPlayerId(3, PLAYER_ID);
    }

    @Test
    void testGetPlaytimeLimitEnabled() {
        assertTrue(parentalControls.getPlaytimeLimitEnabled(PLAYER_ID));
        assertFalse(parentalControls.getPlaytimeLimitEnabled("player456"));

        // Test with nonexistent player
        assertFalse(parentalControls.getPlaytimeLimitEnabled("nonexistentPlayer"));
    }

    @Test
    void testSetPlaytimeLimitEnabled() {
        assertTrue(parentalControls.getPlaytimeLimitEnabled(PLAYER_ID));

        parentalControls.setPlaytimeLimitEnabled(PLAYER_ID, false);
        assertFalse(parentalControls.getPlaytimeLimitEnabled(PLAYER_ID));

        parentalControls.setPlaytimeLimitEnabled(PLAYER_ID, true);
    }

    @Test
    void testGetPlaytimeMinutes() {
        assertEquals(150, parentalControls.getPlaytimeMinutes(PLAYER_ID));
        assertEquals(60, parentalControls.getPlaytimeMinutes("player456"));

        // Test with nonexistent player
        assertEquals(0, parentalControls.getPlaytimeMinutes("nonexistentPlayer"));
    }

    @Test
    void testSetPlaytimeMinutes() {
        int newMinutes = 200;
        parentalControls.setPlaytimeMinutes(PLAYER_ID, newMinutes);
        assertEquals(newMinutes, parentalControls.getPlaytimeMinutes(PLAYER_ID));
        parentalControls.setPlaytimeMinutes(PLAYER_ID, 150);
    }

    @Test
    void testGetAveragePlaytimeMinutes() {
        assertEquals(40, parentalControls.getAveragePlaytimeMinutes(PLAYER_ID));
        assertEquals(15, parentalControls.getAveragePlaytimeMinutes("player456"));
        assertEquals(0, parentalControls.getAveragePlaytimeMinutes("nonexistentPlayer"));
    }

    @Test
    void testSetAveragePlaytimeMinutes() {
        int newAvg = 45;
        parentalControls.setAveragePlaytimeMinutes(PLAYER_ID, newAvg);
        assertEquals(newAvg, parentalControls.getAveragePlaytimeMinutes(PLAYER_ID));
        parentalControls.setAveragePlaytimeMinutes(PLAYER_ID, 40);
    }

    @Test
    void testGetMaxAllowedPlaytimeMinutes() {
        assertEquals(200, parentalControls.getMaxAllowedPlaytimeMinutes(PLAYER_ID));
        assertEquals(120, parentalControls.getMaxAllowedPlaytimeMinutes("player456"));
        assertEquals(60, parentalControls.getMaxAllowedPlaytimeMinutes("nonexistentPlayer"));
    }

    @Test
    void testSetMaxAllowedPlaytimeMinutes() {
        int newMax = 240;
        parentalControls.setMaxAllowedPlaytimeMinutes(PLAYER_ID, newMax);
        assertEquals(newMax, parentalControls.getMaxAllowedPlaytimeMinutes(PLAYER_ID));
        parentalControls.setMaxAllowedPlaytimeMinutes(PLAYER_ID, 200);
    }

    @Test
    void testGetSessionCount() {
        assertEquals(6, parentalControls.getSessionCount(PLAYER_ID));
        assertEquals(5, parentalControls.getSessionCount("player456"));
        assertEquals(0, parentalControls.getSessionCount("nonexistentPlayer"));
    }

    @Test
    void testSetSessionCount() {
        int newCount = 10;
        parentalControls.setSessionCount(PLAYER_ID, newCount);
        assertEquals(newCount, parentalControls.getSessionCount(PLAYER_ID));
        parentalControls.setSessionCount(PLAYER_ID, 6);
    }

    @Test
    void testSafeParseInt() {
        // Test the safeParseInt method through a method that uses it
        try {
            File invalidFile = tempDir.resolve("invalid_data.csv").toFile();
            try (FileWriter writer = new FileWriter(invalidFile)) {
                writer.write("playerId,limitEnabled,playtimeMinutes,avgPlaytimeMinutes,maxAllowedMinutes,sessionCount\n");
                writer.write("playerInvalid,true,notANumber,30,180,four\n");
            }

            ParentalControls invalidControls = new ParentalControls(invalidFile.getAbsolutePath());

            // Should return 0 for invalid playtime minutes
            assertEquals(0, invalidControls.getPlaytimeMinutes("playerInvalid"));
            assertEquals(0, invalidControls.getSessionCount("playerInvalid"));

        } catch (IOException e) {
            fail("Failed to create test file with invalid data: " + e.getMessage());
        }
    }

    @Test
    void testErrorHandling() {

        // Test behavior when file doesn't exist or is inaccessible
        ParentalControls nonExistentControls = new ParentalControls("nonexistent/path.csv");
        assertEquals(0, nonExistentControls.getPlaytimeMinutes("anyPlayer"));
        assertEquals(60, nonExistentControls.getMaxAllowedPlaytimeMinutes("anyPlayer"));
        assertFalse(nonExistentControls.getPlaytimeLimitEnabled("anyPlayer"));
    }
}