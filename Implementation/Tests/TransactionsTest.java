package Implementation.Tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import Implementation.Transactions;

public class TransactionsTest {

    private Transactions transactions;

    @TempDir
    Path tempDir;

    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary CSV file with test data
        testFile = tempDir.resolve("test-transactions.csv").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("T001,P001,PURCHASE,100,2023-04-01 10:15:30\n");
            writer.write("T002,P002,REFUND,50,2023-04-02 11:20:45\n");
            writer.write("T003,P001,PURCHASE,200,2023-04-03 12:30:15\n");
        }

        transactions = new Transactions(testFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Test getting transaction ID")
    void testGetTransactionId() {
        assertEquals("T001", transactions.getTransactionId(0));
        assertEquals("T002", transactions.getTransactionId(1));
        assertEquals("T003", transactions.getTransactionId(2));
    }

    @Test
    @DisplayName("Test setting transaction ID")
    void testSetTransactionId() {
        transactions.setTransactionId(0, "T004");
        assertEquals("T004", transactions.getTransactionId(0));
    }

    @Test
    @DisplayName("Test getting player ID")
    void testGetPlayerId() {
        assertEquals("P001", transactions.getPlayerId("T001"));
        assertEquals("P002", transactions.getPlayerId("T002"));
    }

    @Test
    @DisplayName("Test setting player ID")
    void testSetPlayerId() {
        transactions.setPlayerId("T001", "P003");
        assertEquals("P003", transactions.getPlayerId("T001"));
    }

    @Test
    @DisplayName("Test getting transaction type")
    void testGetTransactionType() {
        assertEquals("PURCHASE", transactions.getTransactionType("T001"));
        assertEquals("REFUND", transactions.getTransactionType("T002"));
    }

    @Test
    @DisplayName("Test setting transaction type")
    void testSetTransactionType() {
        transactions.setTransactionType("T001", "GIFT");
        assertEquals("GIFT", transactions.getTransactionType("T001"));
    }

    @Test
    @DisplayName("Test getting amount")
    void testGetAmount() {
        assertEquals(100, transactions.getAmount("T001"));
        assertEquals(50, transactions.getAmount("T002"));
        assertEquals(200, transactions.getAmount("T003"));
    }

    @Test
    @DisplayName("Test setting amount")
    void testSetAmount() {
        transactions.setAmount("T001", 150);
        assertEquals(150, transactions.getAmount("T001"));
    }


}