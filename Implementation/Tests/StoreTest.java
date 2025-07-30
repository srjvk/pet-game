package Implementation.Tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import Implementation.Store;

class StoreTest {

    private Store store;
    private static final String TEST_PLAYER_ID = "player123";
    private static final String TEST_ITEM_ID = "item456";

    @TempDir
    static Path tempDir;

    private static File tempFile;

    @BeforeAll
    static void setUpTestFile() throws Exception {
        tempFile = new File(tempDir.toFile(), "test_store.csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            // Create test data assuming store data has playerId, itemId, ..., price at index 5
            writer.write(TEST_PLAYER_ID + "," + TEST_ITEM_ID + ",Item Name,Some description,1,100\n");
            writer.write(TEST_PLAYER_ID + ",item789,Other Item,Another description,2,200\n");
            writer.write("player456," + TEST_ITEM_ID + ",Item Name,Some description,1,150\n");
        }
    }

    @BeforeEach
    void setUp() {
        store = new Store(tempFile.getAbsolutePath());
    }

    @Test
    void getPrice_ExistingPlayerAndItem_ReturnsCorrectPrice() {
        String price = store.getPrice(TEST_PLAYER_ID, TEST_ITEM_ID);
        assertEquals("100", price);
    }

    @Test
    void getPrice_DifferentPlayer_ReturnsDifferentPrice() {
        String price = store.getPrice("player456", TEST_ITEM_ID);
        assertEquals("150", price);
    }

    @Test
    void getPrice_DifferentItem_ReturnsDifferentPrice() {
        String price = store.getPrice(TEST_PLAYER_ID, "item789");
        assertEquals("200", price);
    }

    @Test
    void setPrice_ExistingPlayerAndItem_UpdatesPrice() {
        String newPrice = "125";
        store.setPrice(TEST_PLAYER_ID, TEST_ITEM_ID, newPrice);
        assertEquals(newPrice, store.getPrice(TEST_PLAYER_ID, TEST_ITEM_ID));
    }


    @Test
    void setPrice_NonExistentPlayerOrItem_ThrowsException() {
        assertThrows(Exception.class, () -> {
            store.setPrice("nonexistent", TEST_ITEM_ID, "500");
        });

        assertThrows(Exception.class, () -> {
            store.setPrice(TEST_PLAYER_ID, "nonexistent", "500");
        });
    }
}