package Implementation;
import java.io.*;
import java.util.*;

/**
 * A base class for managing data operations on CSV files.
 * This class provides basic functionality for reading and writing CSV data,
 * as well as accessing and modifying specific data points within the CSV structure.
 */
public class DataManager {
    protected String filePath;
    protected String resolvedPath;

    /**
     * Constructs a DataManager with the given file path.
     * 
     * @param filePath The path to the CSV file to manage
     */
    public DataManager(String filePath) {
        this.filePath = filePath;
        this.resolvedPath = DataPathManager.resolveDataPath(filePath);
        System.out.println("DataManager initialized with path: " + filePath);
        System.out.println("Resolved to: " + resolvedPath);
    }

    /**
     * Reads the CSV file and returns its contents as a list of string arrays.
     * Each array represents a row in the CSV file, with each element representing a column.
     * 
     * @return A list of string arrays representing CSV rows
     * @throws IOException if there is an error reading the file
     */
    public List<String[]> readCSV() {
        List<String[]> data = new ArrayList<>();
        
        // First try to read from the resolved path (external data directory)
        if (!resolvedPath.equals(filePath)) {
            try (BufferedReader br = new BufferedReader(new FileReader(resolvedPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    data.add(line.split(","));
                }
                System.out.println("Successfully read from resolved path: " + resolvedPath);
                return data;
            } catch (IOException e) {
                System.err.println("Failed to read from resolved path, trying alternative methods: " + e.getMessage());
                // Fall through to try alternative methods
            }
        }
        
        // Then attempt to use ResourceLoader to get the stream
        try (InputStream is = ResourceLoader.getResourceAsStream(filePath);
             BufferedReader br = (is != null) ? new BufferedReader(new InputStreamReader(is)) : null) {
             
             if (br != null) {
                 String line;
                 while ((line = br.readLine()) != null) {
                     data.add(line.split(","));
                 }
                 // If we read from ResourceLoader but should be writing to a data directory,
                 // immediately save to the resolved path for future use
                 if (!resolvedPath.equals(filePath)) {
                     writeCSV(data);
                 }
                 System.out.println("Successfully read from ResourceLoader: " + filePath);
                 return data;
             }
        } catch (Exception e) {
            System.err.println("Failed to read CSV via ResourceLoader: " + e.getMessage());
            // Fall back to direct file access if ResourceLoader fails
        }
        
        // Fall back to the original direct file access method
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
            System.out.println("Successfully read from original path: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to read from original path: " + e.getMessage());
            
            // If everything fails, try to create a default file structure
            try {
                File file = new File(resolvedPath);
                file.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("id,name,value\n");
                }
                data.add(new String[]{"id", "name", "value"});
                System.out.println("Created default file at: " + resolvedPath);
            } catch (Exception ex) {
                System.err.println("Failed to create default file: " + ex.getMessage());
            }
        }
        return data;
    }

    /**
     * Writes the given data to the CSV file.
     * Each array in the list represents a row, with each element representing a column.
     * 
     * @param data A list of string arrays representing CSV rows
     * @throws IOException if there is an error writing to the file
     */
    public void writeCSV(List<String[]> data) {
        try {
            // Always write to the resolved path
            File file = new File(resolvedPath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            
            // Write to file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(resolvedPath))) {
                for (String[] row : data) {
                    bw.write(String.join(",", row));
                    bw.newLine();
                }
                System.out.println("Successfully wrote to: " + resolvedPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves data from a specific row and column index in the CSV file.
     * 
     * @param row The row index (0-based)
     * @param index The column index (0-based)
     * @return The data at the specified position, or null if the position is invalid
     */
    public String getData(int row, int index) {
        List<String[]> data = readCSV();
        if (row >= 0 && row < data.size()) {
            String[] rowData = data.get(row);
            if (index >= 0 && index < rowData.length) {
                return rowData[index];
            }
        }
        return null;
    }
    
    /**
     * Sets data at a specific row and column index in the CSV file.
     * 
     * @param row The row index (0-based)
     * @param index The column index (0-based)
     * @param data The new data to set at the specified position
     */
    public void setData(int row, int index, String data) {
    	List<String[]> file = readCSV();
    	String[] currRow = file.get(row);
    	currRow[index] = data;
        file.set(row, currRow);
        writeCSV(file);
    }
    
    /**
     * Removes a row from the CSV file.
     * 
     * @param index The index of the row to remove (0-based)
     */
    public void removeRow(int index) {
    	List<String[]> file = readCSV();
    	file.remove(index);
    	writeCSV(file);
    }
    
    /**
     * Adds a new row to the CSV file.
     * 
     * @param rowData A string array representing the new row
     */
    public void addRow(String[] rowData) {
    	List<String[]> file = readCSV();
    	file.add(rowData);
    	writeCSV(file);
    }
} 