package Implementation;
import java.util.List;

/**
 * The {@code Player} class extends {@code DataManager} to provide specific
 * methods for retrieving and updating player-related data from a CSV file.
 * This class is designed to manage player information for a video game,
 * including player ID, username, email, join date, last login timestamp,
 * and in-game currency.
 */
public class Players extends DataManager {
    private int score;
    /**
     * Constructs a {@code Player} object with the specified CSV file path.
     *
     * @param filePath The path to the CSV file containing player data.
     */
    public Players(String filePath) {
        super(filePath);
    }

    /**
     * Retrieves the player ID from a specific index in the CSV data.
     *
     * @param index The row index of the player data.
     * @return The player ID as a string.
     */
    public String getPlayerId(int index) {
        return getData(index, 0);
    }
    
    /**
     * Sets the player ID at the specified index.
     *
     * @param index The row index of the player data.	
     * @param data  The new player ID value.
     */
    public void setPlayerId(int index, String data) {
        setData(index, 0, data);
    }
    
    /**
     * Finds the row index of a player based on their player ID.
     *
     * @param playerId The ID of the player.
     * @return The index of the player's row, or -1 if not found.
     */
    private int findRow(String playerId) {
        List<String[]> data = readCSV();
        int index = 0;
        for (String[] row : data) {
            if (row[0].equals(playerId)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Retrieves the username associated with a given player ID.
     *
     * @param playerId The ID of the player.
     * @return The username of the player, or {@code null} if not found.
     */
    public String getUsername(String playerId) {
        return getData(findRow(playerId), 1);
    }
    
    /**
     * Updates the username of a given player ID.
     *
     * @param playerId The ID of the player.
     * @param data     The new username.
     */
    public void setUsername(String playerId, String data) {
        setData(findRow(playerId), 1, data);
    }

    /**
     * Retrieves the email address associated with a given player ID.
     *
     * @param playerId The ID of the player.
     * @return The email address of the player, or {@code null} if not found.
     */
    public String getEmail(String playerId) {
        return getData(findRow(playerId), 2);
    }
    
    /**
     * Updates the email address of a given player ID.
     *
     * @param playerId The ID of the player.
     * @param data     The new email address.
     */
    public void setEmail(String playerId, String data) {
        setData(findRow(playerId), 2, data);
    }

    /**
     * Retrieves the date the player joined the game.
     *
     * @param playerId The ID of the player.
     * @return The join date as a {@code Date} object, or {@code null} if not found.
     */
    public Date getJoinDate(String playerId) {
        return new Date(getData(findRow(playerId), 3));
    }
    
    /**
     * Updates the join date of a given player ID.
     *
     * @param playerId The ID of the player.
     * @param date     The new join date.
     */
    public void setDate(String playerId, Date date) {
        setData(findRow(playerId), 3, date.toString());
    }

    /**
     * Retrieves the last login timestamp of the player.
     *
     * @param playerId The ID of the player.
     * @return The last login timestamp as a {@code Timestamp} object, or {@code null} if not found.
     */
    public Timestamp getLastLogin(String playerId) {
        return new Timestamp(getData(findRow(playerId), 4));
    }
    
    /**
     * Updates the last login timestamp of a given player ID.
     *
     * @param playerId The ID of the player.
     * @param time     The new last login timestamp.
     */
    public void setLastLogin(String playerId, Timestamp time) {
        setData(findRow(playerId), 4, time.toString());
    }

    /**
     * Retrieves the in-game currency balance of the player.
     *
     * @param playerId The ID of the player.
     * @return The amount of in-game currency, or {@code -1} if not found.
     */
    public int getCurrency(String playerId) {
        return Integer.parseInt(getData(findRow(playerId), 5));
    }
    
    /**
     * Updates the in-game currency balance of a given player ID.
     *
     * @param playerId The ID of the player.
     * @param data     The new currency amount as a string.
     */
    public void setCurrency(String playerId, String data) {
        setData(findRow(playerId), 5, data);
    }

    /**
     * Retrieves the in-game score of the player.
     *
     * @param playerId The ID of the player.
     * @return The player's score, or {@code -1} if not found.
     */
    public int getScore(String playerId) {
        return Integer.parseInt(getData(findRow(playerId), 6));
    }

    /**
     * Updates the score of a given player ID.
     *
     * @param playerId The ID of the player.
     * @param data The score of the player as a string.
     */
    public void setScore(String playerId, String data) {
        setData(findRow(playerId), 6, data);
    }
}
