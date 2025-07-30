package Implementation.Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import Implementation.GameProgress;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GameProgressTest {

    private GameProgress gameProgress;
    private File testFile;

    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("gameProgress.csv").toFile();

        // Create test CSV content
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("player1,5,1000,10,Badge1,Badge2\n");
            writer.write("player2,3,500,5,NewbieBadge\n");
            writer.write("player3,10,5000,20,ExpertBadge,GoldBadge,ChampionBadge\n");
        }

        gameProgress = new GameProgress(testFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        testFile.delete();
    }

    @Test
    @DisplayName("Test getPlayerId returns correct player ID")
    void testGetPlayerId() {
        assertEquals("player1", gameProgress.getPlayerId(0));
        assertEquals("player2", gameProgress.getPlayerId(1));
        assertEquals("player3", gameProgress.getPlayerId(2));
    }

    @Test
    @DisplayName("Test setPlayerId updates player ID correctly")
    void testSetPlayerId() {
        // Act
        gameProgress.setPlayerId(1, "newPlayer2");

        // Assert
        assertEquals("newPlayer2", gameProgress.getPlayerId(1));
    }

    @Test
    @DisplayName("Test getLevel returns correct level")
    void testGetLevel() {
        assertEquals(5, gameProgress.getLevel("player1"));
        assertEquals(3, gameProgress.getLevel("player2"));
        assertEquals(10, gameProgress.getLevel("player3"));
    }

    @Test
    @DisplayName("Test setLevel updates level correctly")
    void testSetLevel() {
        // Act
        gameProgress.setLevel("player1", 6);

        // Assert
        assertEquals(6, gameProgress.getLevel("player1"));
    }

    @Test
    @DisplayName("Test getExperience returns correct experience")
    void testGetExperience() {
        assertEquals(1000, gameProgress.getExperience("player1"));
        assertEquals(500, gameProgress.getExperience("player2"));
        assertEquals(5000, gameProgress.getExperience("player3"));
    }

    @Test
    @DisplayName("Test setExperience updates experience correctly")
    void testSetExperience() {
        // Act
        gameProgress.setExperience("player2", 750);

        // Assert
        assertEquals(750, gameProgress.getExperience("player2"));
    }

    @Test
    @DisplayName("Test getQuestsCompleted returns correct number of quests")
    void testGetQuestsCompleted() {
        assertEquals(10, gameProgress.getQuestsCompleted("player1"));
        assertEquals(5, gameProgress.getQuestsCompleted("player2"));
        assertEquals(20, gameProgress.getQuestsCompleted("player3"));
    }

    @Test
    @DisplayName("Test setQuestsCompleted updates quests completed correctly")
    void testSetQuestsCompleted() {
        // Act
        gameProgress.setQuestsCompleted("player3", 25);

        // Assert
        assertEquals(25, gameProgress.getQuestsCompleted("player3"));
    }


}