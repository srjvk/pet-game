package Implementation;

import java.io.File;

/**
 * Utility class for managing data file paths.
 * This class resolves paths for CSV and other data files based on the app.data.dir
 * system property, enabling the application to save data outside of the JAR file.
 */
public class DataPathManager {
    
    private static final String DATA_DIR_PROPERTY = "app.data.dir";
    
    /**
     * Resolves a data file path with consideration to the app.data.dir system property.
     * If the property is set, it will use that as the base directory for data files.
     * If not, it falls back to the original file path.
     *
     * @param filePath The original file path, relative to the workspace
     * @return The resolved file path, potentially adjusted to use the data directory
     */
    public static String resolveDataPath(String filePath) {
        String dataDir = System.getProperty(DATA_DIR_PROPERTY);
        
        // If data directory is specified and the file is a CSV (or other data file)
        if (dataDir != null && !dataDir.isEmpty() && 
            (filePath.endsWith(".csv") || filePath.endsWith(".data"))) {
            
            // Create full path in the data directory
            File dataFile = new File(dataDir, filePath);
            
            // Return the absolute path
            return dataFile.getAbsolutePath();
        }
        
        // For non-data files or when no data directory is specified, return the original path
        return filePath;
    }
} 