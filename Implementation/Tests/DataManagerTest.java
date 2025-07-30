package Implementation.Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import Implementation.DataManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class DataManagerTest {

    private DataManager dataManager;
    private File testFile;

    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test.csv").toFile();

        // Create test CSV content
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("1,John,25,Developer\n");
            writer.write("2,Jane,30,Designer\n");
            writer.write("3,Bob,28,Manager\n");
        }

        dataManager = new DataManager(testFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        testFile.delete();
    }

    @Test
    @DisplayName("Test readCSV reads all rows correctly")
    void testReadCSV() {
        // Act
        List<String[]> data = dataManager.readCSV();

        // Assert
        assertEquals(3, data.size());
        assertArrayEquals(new String[]{"1", "John", "25", "Developer"}, data.get(0));
        assertArrayEquals(new String[]{"2", "Jane", "30", "Designer"}, data.get(1));
        assertArrayEquals(new String[]{"3", "Bob", "28", "Manager"}, data.get(2));
    }

    @Test
    @DisplayName("Test writeCSV writes all rows correctly")
    void testWriteCSV() {
        // Arrange
        List<String[]> newData = Arrays.asList(
                new String[]{"4", "Alice", "32", "Tester"},
                new String[]{"5", "Mike", "27", "Analyst"}
        );

        // Act
        dataManager.writeCSV(newData);
        List<String[]> readData = dataManager.readCSV();

        // Assert
        assertEquals(2, readData.size());
        assertArrayEquals(new String[]{"4", "Alice", "32", "Tester"}, readData.get(0));
        assertArrayEquals(new String[]{"5", "Mike", "27", "Analyst"}, readData.get(1));
    }

    @Test
    @DisplayName("Test getData returns correct data for valid indices")
    void testGetDataWithValidIndices() {
        // Act & Assert
        assertEquals("1", dataManager.getData(0, 0));
        assertEquals("John", dataManager.getData(0, 1));
        assertEquals("30", dataManager.getData(1, 2));
        assertEquals("Manager", dataManager.getData(2, 3));
    }

    @Test
    @DisplayName("Test getData returns null for invalid row index")
    void testGetDataWithInvalidRowIndex() {
        // Act & Assert
        assertNull(dataManager.getData(5, 0));
    }

    @Test
    @DisplayName("Test setData updates data correctly")
    void testSetData() {
        // Act
        dataManager.setData(1, 1, "Janet");
        dataManager.setData(2, 2, "35");

        // Assert
        List<String[]> data = dataManager.readCSV();
        assertEquals("Janet", data.get(1)[1]);
        assertEquals("35", data.get(2)[2]);
    }

}