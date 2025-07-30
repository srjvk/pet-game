package Implementation;
import java.util.List;

/**
 * The {@code Store} class extends {@code Inventory} and provides methods to manage 
 * a player's store inventory and pricing data. It allows retrieving and updating the 
 * price of items in the store based on the player ID and item ID.
 */
public class Store extends Inventory {

    /**
     * Constructs a {@code Store} object with the specified file path for the store inventory data.
     *
     * @param filepath The path to the CSV file containing store inventory data.
     */
    public Store(String filepath) {
        super(filepath);
    }
    
    /**
     * Finds the row index of a player and item based on their player ID and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId The ID of the item.
     * @return The index of the row containing the player and item, or -1 if not found.
     */
    private int findRow(String playerId, String itemId) {
        List<String[]> data = readCSV();
        int index = 0;
        for (String[] row : data) {
            if (row[0].equals(playerId) && row[1].equals(itemId)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Retrieves the price of the specified item for the given player.
     *
     * @param playerId The ID of the player.
     * @param itemId The ID of the item.
     * @return The price of the item as a {@code String}.
     */
    public String getPrice(String playerId, String itemId) {
        return getData(findRow(playerId, itemId), 5);
    }

    /**
     * Sets the price of the specified item for the given player.
     *
     * @param playerId The ID of the player.
     * @param itemId The ID of the item.
     * @param data The new price to set for the item.
     */
    public void setPrice(String playerId, String itemId, String data) {
        setData(findRow(playerId, itemId), 5, data);
    }
}
