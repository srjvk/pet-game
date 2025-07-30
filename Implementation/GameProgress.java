package Implementation;
import java.util.List;

/**
 * The {@code GameProgress} class extends {@code DataManager} to provide methods for
 * retrieving and updating game progress data from a CSV file.
 * This class manages player progression, including level, experience, quests completed,
 * and earned badges.
 */
public class GameProgress extends DataManager {
    
    /**
     * Constructs a {@code GameProgress} object with the specified CSV file path.
     *
     * @param filePath The path to the CSV file containing game progress data.
     */
    public GameProgress(String filePath) {
        super(filePath);
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
     * Retrieves the level of the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The level of the player.
     */
    public int getLevel(String playerId) {
        return Integer.parseInt(getData(findRow(playerId), 1));
    }
    
    /**
     * Sets the level of the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param level The new level to set for the player.
     */
    public void setLevel(String playerId, int level) {
        setData(findRow(playerId), 1, String.valueOf(level));
    }
    
    /**
     * Retrieves the experience of the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The experience of the player.
     */
    public int getExperience(String playerId) {
        return Integer.parseInt(getData(findRow(playerId), 2));
    }
    
    /**
     * Sets the experience of the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param experience The new experience to set for the player.
     */
    public void setExperience(String playerId, int experience) {
        setData(findRow(playerId), 2, String.valueOf(experience));
    }
    
    /**
     * Retrieves the number of quests completed by the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The number of quests completed by the player.
     */
    public int getQuestsCompleted(String playerId) {
        return Integer.parseInt(getData(findRow(playerId), 3));
    }
    
    /**
     * Sets the number of quests completed by the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param questsCompleted The new number of quests completed to set for the player.
     */
    public void setQuestsCompleted(String playerId, int questsCompleted) {
        setData(findRow(playerId), 3, String.valueOf(questsCompleted));
    }
    
    /**
     * Retrieves the badges earned by the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return An array of badges earned by the player.
     */
    public String[] getBadgesEarned(String playerId) {
        String badgesString = getData(findRow(playerId), 4);
        return badgesString.isEmpty() ? new String[0] : badgesString.split(",");
    }
    
    /**
     * Sets the badges earned by the player with the given player ID.
     *
     * @param playerId The ID of the player.
     * @param badges An array of badges to set for the player.
     */
    public void setBadgesEarned(String playerId, String[] badges) {
        setData(findRow(playerId), 4, String.join(",", badges));
    }
}
