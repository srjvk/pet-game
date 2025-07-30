package Implementation;
import java.util.List;

/**
 * The {@code Inventory} class extends {@code DataManager} to provide methods for
 * retrieving and updating inventory data from a CSV file.
 * This class manages player inventory, including item ID, item name, quantity,
 * and category.
 */
public class Inventory extends DataManager {
    
    /**
     * Constructs an {@code Inventory} object with the specified CSV file path.
     *
     * @param filePath The path to the CSV file containing inventory data.
     */
    public Inventory(String filePath) {
        super(filePath);
    }

    /**
     * Finds the row index of an item based on player ID and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @return The index of the item's row, or -1 if not found.
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
     * @param data The new player ID to set.
     */
    public void setPlayerId(int index, String data) {
        setData(index, 0, data);
    }
    
    /**
     * Retrieves the item ID at the specified index.
     *
     * @param index The index of the item data.
     * @return The item ID at the specified index.
     */
    public String getItemId(int index) {
        return getData(index, 1);
    }
    
    /**
     * Sets the item ID at the specified index.
     *
     * @param index The index of the item data.
     * @param data The new item ID to set.
     */
    public void setItemId(int index, String data) {
        setData(index, 1, data);
    }
    
    /**
     * Retrieves the item name for a given player and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @return The name of the item.
     */
    public String getItemName(String playerId, String itemId) {
        return getData(findRow(playerId, itemId), 2);
    }
    
    /**
     * Sets the item name for a given player and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @param itemName The new item name to set.
     */
    public void setItemName(String playerId, String itemId, String itemName) {
        setData(findRow(playerId, itemId), 2, itemName);
    }
    
    /**
     * Retrieves the quantity of the item for a given player and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @return The quantity of the item.
     */
    public int getQuantity(String playerId, String itemId) {
        return Integer.parseInt(getData(findRow(playerId, itemId), 3));
    }
    
    /**
     * Sets the quantity of the item for a given player and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @param quantity The new quantity to set for the item.
     */
    public void setQuantity(String playerId, String itemId, int quantity) {
        setData(findRow(playerId, itemId), 3, String.valueOf(quantity));
    }
    
    /**
     * Retrieves the category of the item for a given player and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @return The category of the item.
     */
    public String getCategory(String playerId, String itemId) {
        return getData(findRow(playerId, itemId), 4);
    }
    
    /**
     * Sets the category of the item for a given player and item ID.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item.
     * @param category The new category to set for the item.
     */
    public void setCategory(String playerId, String itemId, String category) {
        setData(findRow(playerId, itemId), 4, category);
    }
    
    /**
     * Removes the item for a given player and item ID from the inventory.
     *
     * @param playerId The ID of the player.
     * @param itemId   The ID of the item to remove.
     */
    public void removeItem(String playerId, String itemId) {
        removeRow(findRow(playerId, itemId));
    }
}
