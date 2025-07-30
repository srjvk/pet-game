package Implementation.Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import Implementation.Inventory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class InventoryTest {

    private Inventory inventory;
    private File testFile;

    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("inventory.csv").toFile();


        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("player1,item1,Food,5,Consumable\n");
            writer.write("player1,item2,Toy,2,Playable\n");
            writer.write("player2,item1,Food,3,Consumable\n");
            writer.write("player2,item3,Medicine,1,Consumable\n");
        }

        inventory = new Inventory(testFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        testFile.delete();
    }

    @Test
    @DisplayName("Test getPlayerId returns correct player ID")
    void testGetPlayerId() {
        assertEquals("player1", inventory.getPlayerId(0));
        assertEquals("player1", inventory.getPlayerId(1));
        assertEquals("player2", inventory.getPlayerId(2));
    }

    @Test
    @DisplayName("Test setPlayerId updates player ID correctly")
    void testSetPlayerId() {

        inventory.setPlayerId(1, "newPlayer1");


        assertEquals("newPlayer1", inventory.getPlayerId(1));
    }

    @Test
    @DisplayName("Test getItemId returns correct item ID")
    void testGetItemId() {
        assertEquals("item1", inventory.getItemId(0));
        assertEquals("item2", inventory.getItemId(1));
        assertEquals("item1", inventory.getItemId(2));
    }

    @Test
    @DisplayName("Test setItemId updates item ID correctly")
    void testSetItemId() {

        inventory.setItemId(0, "newItem1");


        assertEquals("newItem1", inventory.getItemId(0));
    }

    @Test
    @DisplayName("Test getItemName returns correct item name")
    void testGetItemName() {
        assertEquals("Food", inventory.getItemName("player1", "item1"));
        assertEquals("Toy", inventory.getItemName("player1", "item2"));
        assertEquals("Medicine", inventory.getItemName("player2", "item3"));
    }

    @Test
    @DisplayName("Test setItemName updates item name correctly")
    void testSetItemName() {

        inventory.setItemName("player1", "item1", "PremiumFood");


        assertEquals("PremiumFood", inventory.getItemName("player1", "item1"));
    }

    @Test
    @DisplayName("Test getQuantity returns correct quantity")
    void testGetQuantity() {
        assertEquals(5, inventory.getQuantity("player1", "item1"));
        assertEquals(2, inventory.getQuantity("player1", "item2"));
        assertEquals(1, inventory.getQuantity("player2", "item3"));
    }

    @Test
    @DisplayName("Test setQuantity updates quantity correctly")
    void testSetQuantity() {

        inventory.setQuantity("player1", "item2", 10);


        assertEquals(10, inventory.getQuantity("player1", "item2"));
    }

    @Test
    @DisplayName("Test getCategory returns correct category")
    void testGetCategory() {
        assertEquals("Consumable", inventory.getCategory("player1", "item1"));
        assertEquals("Playable", inventory.getCategory("player1", "item2"));
        assertEquals("Consumable", inventory.getCategory("player2", "item3"));
    }

    @Test
    @DisplayName("Test setCategory updates category correctly")
    void testSetCategory() {

        inventory.setCategory("player1", "item2", "Special");


        assertEquals("Special", inventory.getCategory("player1", "item2"));
    }

}