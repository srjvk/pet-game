package Implementation.Tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import Implementation.VirtualPets;
import Implementation.VitalStats;
import Implementation.Date;

public class VirtualPetsTest {

    private VirtualPets virtualPets;

    @TempDir
    Path tempDir;

    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary CSV file with test data
        testFile = tempDir.resolve("test-pets.csv").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            // Creating test data with all columns to test the full functionality
            // Format: petId,playerId,petName,age,hunger,happiness,health,sleep,lastPlay,vetCooldown,playCooldown,lastAccessed,petType
            writer.write("P001,U001,Fluffy,2,70,80,90,75,2023-04-01 10:15:30,0,0,2023-04-01,cat\n");
            writer.write("P002,U002,Rex,3,60,85,95,80,2023-04-02 11:20:45,3600,1800,2023-04-02,dog\n");
            writer.write("P003,U001,Sparky,1,65,75,85,70,2023-04-03 12:30:15,0,0,2023-04-03,dragon\n");
        }

        virtualPets = new VirtualPets(testFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Test getting pet ID")
    void testGetPetId() {
        assertEquals("P001", virtualPets.getPetId(0));
        assertEquals("P002", virtualPets.getPetId(1));
        assertEquals("P003", virtualPets.getPetId(2));
    }

    @Test
    @DisplayName("Test setting pet ID")
    void testSetPetId() {
        virtualPets.setPetId(0, "P004");
        assertEquals("P004", virtualPets.getPetId(0));
    }

    @Test
    @DisplayName("Test getting player ID")
    void testGetPlayerId() {
        assertEquals("U001", virtualPets.getPlayerId("P001"));
        assertEquals("U002", virtualPets.getPlayerId("P002"));
    }

    @Test
    @DisplayName("Test setting player ID")
    void testSetPlayerId() {
        virtualPets.setPlayerId("P001", "U003");
        assertEquals("U003", virtualPets.getPlayerId("P001"));
    }

    @Test
    @DisplayName("Test getting pet name")
    void testGetPetName() {
        assertEquals("Fluffy", virtualPets.getPetName("P001"));
        assertEquals("Rex", virtualPets.getPetName("P002"));
    }

    @Test
    @DisplayName("Test setting pet name")
    void testSetPetName() {
        virtualPets.setPetName("P001", "Whiskers");
        assertEquals("Whiskers", virtualPets.getPetName("P001"));
    }

    @Test
    @DisplayName("Test getting age")
    void testGetAge() {
        assertEquals(2, virtualPets.getAge("P001"));
        assertEquals(3, virtualPets.getAge("P002"));
    }

    @Test
    @DisplayName("Test setting age")
    void testSetAge() {
        virtualPets.setAge("P001", 3);
        assertEquals(3, virtualPets.getAge("P001"));
    }

    @Test
    @DisplayName("Test getting hunger")
    void testGetHunger() {
        assertEquals(70, virtualPets.getHunger("P001"));
        assertEquals(60, virtualPets.getHunger("P002"));
    }

    @Test
    @DisplayName("Test setting hunger")
    void testSetHunger() {
        virtualPets.setHunger("P001", 50);
        assertEquals(50, virtualPets.getHunger("P001"));
    }

    @Test
    @DisplayName("Test getting happiness")
    void testGetHappiness() {
        assertEquals(80, virtualPets.getHappiness("P001"));
        assertEquals(85, virtualPets.getHappiness("P002"));
    }

    @Test
    @DisplayName("Test setting happiness")
    void testSetHappiness() {
        virtualPets.setHappiness("P001", 90);
        assertEquals(90, virtualPets.getHappiness("P001"));
    }

    @Test
    @DisplayName("Test getting health")
    void testGetHealth() {
        assertEquals(90, virtualPets.getHealth("P001"));
        assertEquals(95, virtualPets.getHealth("P002"));
    }

    @Test
    @DisplayName("Test setting health")
    void testSetHealth() {
        virtualPets.setHealth("P001", 85);
        assertEquals(85, virtualPets.getHealth("P001"));
    }

    @Test
    @DisplayName("Test getting sleep")
    void testGetSleep() {
        assertEquals(75, virtualPets.getSleep("P001"));
        assertEquals(80, virtualPets.getSleep("P002"));
    }

    @Test
    @DisplayName("Test setting sleep")
    void testSetSleep() {
        virtualPets.setSleep("P001", 85);
        assertEquals(85, virtualPets.getSleep("P001"));
    }

    @Test
    @DisplayName("Test getting vet cooldown")
    void testGetVetCooldown() {
        assertEquals(0, virtualPets.getVetCooldown("P001"));
        assertEquals(3600, virtualPets.getVetCooldown("P002"));
    }

    @Test
    @DisplayName("Test setting vet cooldown")
    void testSetVetCooldown() {
        virtualPets.setVetCooldown("P001", 1800);
        assertEquals(1800, virtualPets.getVetCooldown("P001"));
    }

    @Test
    @DisplayName("Test getting play cooldown")
    void testGetPlayCooldown() {
        assertEquals(0, virtualPets.getPlayCooldown("P001"));
        assertEquals(1800, virtualPets.getPlayCooldown("P002"));
    }

    @Test
    @DisplayName("Test setting play cooldown")
    void testSetPlayCooldown() {
        virtualPets.setPlayCooldown("P001", 900);
        assertEquals(900, virtualPets.getPlayCooldown("P001"));
    }

    @Test
    @DisplayName("Test updating vital stats")
    void testUpdateVitalStats() {
        VitalStats stats = new VitalStats(95, 85, 60, 90, 1);
        virtualPets.updateVitalStats("P001", stats);

        assertEquals(95, virtualPets.getHealth("P001"));
        assertEquals(85, virtualPets.getSleep("P001"));
        assertEquals(60, virtualPets.getHunger("P001"));
        assertEquals(90, virtualPets.getHappiness("P001"));
    }

    @Test
    @DisplayName("Test getting pet type")
    void testGetPetType() {
        assertEquals("cat", virtualPets.getPetType("P001"));
        assertEquals("dog", virtualPets.getPetType("P002"));
        assertEquals("dragon", virtualPets.getPetType("P003"));
    }

    @Test
    @DisplayName("Test getting pet type index")
    void testGetPetTypeIndex() {
        assertEquals(1, virtualPets.getPetTypeIndex("P001")); // cat = 1
        assertEquals(0, virtualPets.getPetTypeIndex("P002")); // dog = 0
        assertEquals(2, virtualPets.getPetTypeIndex("P003")); // dragon = 2
    }

    @Test
    @DisplayName("Test setting pet type")
    void testSetPetType() {
        virtualPets.setPetType("P001", "dog");
        assertEquals("dog", virtualPets.getPetType("P001"));
    }

    @Test
    @DisplayName("Test getting last accessed date")
    void testGetLastAccessed() {
        Date date = virtualPets.getLastAccessed("P001");
        assertEquals("2023-04-01", date.toString());
    }

    @Test
    @DisplayName("Test setting last accessed date")
    void testSetLastAccessed() {
        Date newDate = new Date("2023-04-10");
        virtualPets.setLastAccessed("P001", newDate);
        assertEquals("2023-04-10", virtualPets.getLastAccessed("P001").toString());
    }

    @Test
    @DisplayName("Test default for missing pet type")
    void testMissingPetType() {
        // Create file without pet type column
        try {
            File limitedFile = tempDir.resolve("limited-pets.csv").toFile();
            try (FileWriter writer = new FileWriter(limitedFile)) {
                writer.write("P004,U003,Bubbles,1,60,70,80,65,2023-04-04 13:45:20\n");
            }

            VirtualPets limitedPets = new VirtualPets(limitedFile.getAbsolutePath());

            // Should default to "cat"
            assertEquals("cat", limitedPets.getPetType("P004"));
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test updating last accessed")
    void testUpdateLastAccessed() {
        // Store the current date
        Date beforeUpdate = Date.now();

        // Update the last accessed time
        virtualPets.updateLastAccessed("P001");

        // Get the updated date
        Date afterUpdate = virtualPets.getLastAccessed("P001");

        // The updated date should match today's date
        assertEquals(beforeUpdate.toString(), afterUpdate.toString());
    }
}