package Implementation;
import java.util.List;

/**
 * The {@code ParentalControls} class extends {@code DataManager} to manage parental control settings
 * for players, including playtime limits and playtime statistics.
 */
public class ParentalControls extends DataManager {
    
    /**
     * Constructs a {@code ParentalControls} object with the specified CSV file path.
     *
     * @param filePath The path to the CSV file containing parental control data.
     */
    public ParentalControls(String filePath) {
        super(filePath);
    }
    
    /**
     * Constructs a {@code ParentalControls} object with the default file path.
     */
    public ParentalControls() {
        super("Implementation/playtime_tracking.csv");
    }

    /**
     * Finds the row index of a player based on their player ID.
     * If multiple rows have the same ID, returns the last one (most recently added).
     *
     * @param playerId The ID of the player.
     * @return The index of the player's row, or -1 if not found.
     */
    public int findRow(String playerId) {
        List<String[]> data = readCSV();
        int index = 0;
        int foundIndex = -1;
        
        for (String[] row : data) {
            if (row.length > 0 && row[0].equals(playerId)) {
                foundIndex = index; // Keep updating - will end up with the last match
            }
            index++;
        }
        
        return foundIndex;
    }

    // Getters and setters
    
    /**
     * Retrieves the player ID at the specified index.
     *
     * @param index The index of the player data.
     * @return The player ID at the specified index.
     */
    public String getPlayerId(int index) {
        return getData(index, 0);
    }
    
    /**
     * Sets the player ID at the specified index.
     *
     * @param index The index of the player data.
     * @param playerId The new player ID to set.
     */
    public void setPlayerId(int index, String playerId) {
        setData(index, 0, playerId);
    }
    
    /**
     * Retrieves whether playtime limits are enforced for the player.
     *
     * @param playerId The ID of the player.
     * @return {@code true} if the player has a playtime limit, {@code false} otherwise.
     */
    public boolean getPlaytimeLimitEnabled(String playerId) {
        try {
            return Boolean.parseBoolean(getData(findRow(playerId), 1));
        } catch (Exception e) {
            System.err.println("Error getting playtime limit enabled status: " + e.getMessage());
            return false; // Default to no limits if there's an error
        }
    }
    
    /**
     * Sets whether playtime limits are enforced for the player.
     *
     * @param playerId The ID of the player.
     * @param enabled Whether playtime limits should be enforced.
     */
    public void setPlaytimeLimitEnabled(String playerId, boolean enabled) {
        try {
            setData(findRow(playerId), 1, String.valueOf(enabled));
        } catch (Exception e) {
            System.err.println("Error setting playtime limit enabled status: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the total playtime of the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The total playtime of the player in minutes.
     */
    public int getPlaytimeMinutes(String playerId) {
        try {
            String value = getData(findRow(playerId), 2);
            return safeParseInt(value, 0);
        } catch (Exception e) {
            System.err.println("Error getting playtime minutes: " + e.getMessage());
            return 0; // Return 0 if there's an error
        }
    }
    
    /**
     * Safely parses an integer with a default value if parsing fails
     */
    private int safeParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format '" + value + "', using default value: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Sets the total playtime for the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param minutes The total playtime to set for the player in minutes.
     */
    public void setPlaytimeMinutes(String playerId, int minutes) {
        try {
            setData(findRow(playerId), 2, String.valueOf(minutes));
        } catch (Exception e) {
            System.err.println("Error setting playtime minutes: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the average playtime per session of the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The average playtime per session in minutes for the player.
     */
    public int getAveragePlaytimeMinutes(String playerId) {
        try {
            String value = getData(findRow(playerId), 3);
            return safeParseInt(value, 0);
        } catch (Exception e) {
            System.err.println("Error getting average playtime minutes: " + e.getMessage());
            return 0; // Return 0 if there's an error
        }
    }
    
    /**
     * Sets the average playtime per session for the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param minutes The average playtime per session to set for the player in minutes.
     */
    public void setAveragePlaytimeMinutes(String playerId, int minutes) {
        try {
            setData(findRow(playerId), 3, String.valueOf(minutes));
        } catch (Exception e) {
            System.err.println("Error setting average playtime minutes: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the maximum allowed playtime limit in minutes.
     *
     * @param playerId The ID of the player.
     * @return The maximum allowed playtime in minutes. Returns 0 if no limit is set.
     */
    public int getMaxAllowedPlaytimeMinutes(String playerId) {
        try {
            String value = getData(findRow(playerId), 4);
            return safeParseInt(value, 60); // Default to 60 minutes
        } catch (Exception e) {
            System.err.println("Error getting max allowed playtime: " + e.getMessage());
            return 60; // Return a reasonable default if there's an error
        }
    }
    
    /**
     * Sets the maximum allowed playtime limit in minutes.
     *
     * @param playerId The ID of the player.
     * @param minutes The maximum allowed playtime in minutes.
     */
    public void setMaxAllowedPlaytimeMinutes(String playerId, int minutes) {
        try {
            setData(findRow(playerId), 4, String.valueOf(minutes));
        } catch (Exception e) {
            System.err.println("Error setting max allowed playtime: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the session count for the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The number of gaming sessions.
     */
    public int getSessionCount(String playerId) {
        try {
            String value = getData(findRow(playerId), 5);
            return safeParseInt(value, 0);
        } catch (Exception e) {
            System.err.println("Error getting session count: " + e.getMessage());
            return 0; // Return 0 if there's an error
        }
    }
    
    /**
     * Sets the session count for the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param sessionCount The number of gaming sessions.
     */
    public void setSessionCount(String playerId, int sessionCount) {
        try {
            setData(findRow(playerId), 5, String.valueOf(sessionCount));
        } catch (Exception e) {
            System.err.println("Error setting session count: " + e.getMessage());
        }
    }
}
