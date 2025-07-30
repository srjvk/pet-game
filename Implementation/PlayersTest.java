package Implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayersTest {
    private Players players;
    private static final String HEADER = "PlayerID,Username,Email,JoinDate,LastLogin,Currency,Score\n";
    private static final String PLAYER1 = "P001,testUser1,test1@example.com,01/01/2023,2023-01-01T10:30:00,1000,500\n";
    private static final String PLAYER2 = "P002,testUser2,test2@example.com,02/02/2023,2023-02-02T14:45:00,2000,750\n";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary CSV file with test data
        File csvFile = tempDir.resolve("players_test.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(HEADER);
            writer.write(PLAYER1);
            writer.write(PLAYER2);
        }

        // Initialize Players with the temp file
        players = new Players(csvFile.getAbsolutePath());
    }

    @Test
    void testGetPlayerId() {
        assertEquals("P001", players.getPlayerId(1));
        assertEquals("P002", players.getPlayerId(2));
    }

    @Test
    void testSetPlayerId() {
        players.setPlayerId(1, "P999");
        assertEquals("P999", players.getPlayerId(1));
    }

    @Test
    void testGetUsername() {
        assertEquals("testUser1", players.getUsername("P001"));
        assertEquals("testUser2", players.getUsername("P002"));
    }

    @Test
    void testSetUsername() {
        players.setUsername("P001", "updatedUser1");
        assertEquals("updatedUser1", players.getUsername("P001"));
    }

    @Test
    void testGetEmail() {
        assertEquals("test1@example.com", players.getEmail("P001"));
        assertEquals("test2@example.com", players.getEmail("P002"));
    }

    @Test
    void testSetEmail() {
        players.setEmail("P001", "updated1@example.com");
        assertEquals("updated1@example.com", players.getEmail("P001"));
    }

    @Test
    void testGetJoinDate() {
        Date expectedDate = new Date("01/01/2023");
        Date actualDate = players.getJoinDate("P001");

        assertEquals(expectedDate.toString(), actualDate.toString());
    }

    @Test
    void testSetDate() {
        Date newDate = new Date("15/07/2023");
        players.setDate("P001", newDate);

        Date retrievedDate = players.getJoinDate("P001");
        assertEquals(newDate.toString(), retrievedDate.toString());
    }



    @Test
    void testGetCurrency() {
        assertEquals(1000, players.getCurrency("P001"));
        assertEquals(2000, players.getCurrency("P002"));
    }

    @Test
    void testSetCurrency() {
        players.setCurrency("P001", "5000");
        assertEquals(5000, players.getCurrency("P001"));
    }

    @Test
    void testGetScore() {
        assertEquals(500, players.getScore("P001"));
        assertEquals(750, players.getScore("P002"));
    }

    @Test
    void testSetScore() {
        players.setScore("P001", "1500");
        assertEquals(1500, players.getScore("P001"));
    }



}