package Implementation.StoreInventory;
import java.util.*;

import Implementation.Players;
import Implementation.Store;
import Implementation.PlayerInventory.InventoryManager;

/**
 * The {@code StoreManager} class manages a store's data.
 * It handles store loading, saving, and manipulation of store items.
 * It also handles player purchases and currency management.
 */
public class StoreManager {
    private final int MAX_SPACE = 9;
    private final int GRID_SIZE = 3; // 3x3 grid
    private Store store;
    private String[][][] storeItems; // [row][col][itemData] where itemData is [itemId, itemName, quantity, category, price]
    private Players player;
    private String playerId;
    private String petId;
    private InventoryManager inventoryManager;

    /**
     * Constructs a {@code StoreManager} object with the specified filepath.
     *
     * @param storeFilepath The path to the CSV file containing store data.
     * @param playerFilepath The path to the player data file.
     * @param inventoryManager The inventory manager for the player.
     */
    public StoreManager(String playerIdString, String storeFilepath, String playerFilepath, InventoryManager inventoryManager, String petId) {
        this.store = new Store(storeFilepath);
        this.storeItems = new String[GRID_SIZE][GRID_SIZE][5]; // 5 attributes per item (including price)
        this.player = new Players(playerFilepath);
        this.inventoryManager = inventoryManager;
        this.playerId = playerIdString;
        this.petId = petId;
        initializeEmptyStore();
        loadStore();
        populateStore();
    }

    /**
     * Initializes the store array with empty values.
     */
    private void initializeEmptyStore() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                storeItems[row][col] = new String[]{"", "", "1", "", "0"};
            }
        }
    }

    /**
     * Creates a new empty store and saves it to the CSV file.
     */
    public void newStore() {
        List<String[]> storeData = new ArrayList<>();
        
        for (int i = 0; i < MAX_SPACE; i++) {
            String[] item = {String.valueOf(i), "", "1", "", "0"};
            storeData.add(item);
        }
        
        store.writeCSV(storeData);
        initializeEmptyStore();
    }

    /**
     * Loads store data from the CSV file.
     */
    public void loadStore() {
        List<String[]> data = store.readCSV();
        initializeEmptyStore(); // Reset current store
        int position = 0;
        for (String[] row : data) {
            if (row.length == 5 && position <= 8) {
                if (position < MAX_SPACE) {
                    int gridRow = position / GRID_SIZE;
                    int gridCol = position % GRID_SIZE;
                    
                    storeItems[gridRow][gridCol][0] = row[0]; // itemId
                    storeItems[gridRow][gridCol][1] = row[1]; // itemName
                    storeItems[gridRow][gridCol][2] = "1";    // quantity (always 1 for store)
                    storeItems[gridRow][gridCol][3] = row[3]; // category
                    storeItems[gridRow][gridCol][4] = row[4]; // price
                }
            }
            position++;
        }
    }

    /**
     * Saves the current store to the CSV file.
     */
    public void saveStore() {
        List<String[]> storeData = new ArrayList<>();
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int index = row * GRID_SIZE + col;
                String itemId = storeItems[row][col][0].isEmpty() ? String.valueOf(index) : storeItems[row][col][0];
                String itemName = storeItems[row][col][1];
                String quantity = "1"; // Always 1 for store items
                String category = storeItems[row][col][3];
                String price = storeItems[row][col][4];
                
                String[] item = {itemId, itemName, quantity, category, price};
                storeData.add(item);
            }
        }
        
        store.writeCSV(storeData);
    }
    
    /**
     * Adds a new item to the first available empty slot in the store.
     *
     * @param itemId The ID of the item.
     * @param itemName The name of the item.
     * @param category The category of the item.
     * @param price The price of the item.
     * @return true if the item was added successfully, false if store is full.
     */
    public boolean addItem(String itemId, String itemName, String category, int price) {
        // Look for the first empty slot
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // Check if slot is empty
                if (storeItems[row][col][1].isEmpty()) {
                    // Found an empty slot, add the item
                    storeItems[row][col][0] = itemId;
                    storeItems[row][col][1] = itemName;
                    storeItems[row][col][2] = "1"; // Always 1 for store
                    storeItems[row][col][3] = category;
                    storeItems[row][col][4] = String.valueOf(price);
                    return true;
                }
            }
        }
        // No empty slots found
        return false;
    }
    
    public void populateStore(){
        this.addItem("1", "acorn", "food", 50);
        this.addItem("2", "avocado", "food", 75);
        this.addItem("3", "bear", "toy", 150);
        this.addItem("4", "dreidel", "toy", 120);
        this.addItem("5", "bandaid", "equipment", 30);
        this.addItem("6", "dumbbell", "equipment", 200);
        this.addItem("7", "potion", "consumable", 100);
        this.addItem("8", "sword", "weapon", 300);
        this.addItem("9", "shield", "armor", 250);
        this.addItem("10", "feast", "food", 125);
    }
    /**
     * Retrieves an item from the store at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @return A String array containing the item data (itemId, itemName, quantity, category, price).
     */
    public String[] getItem(int row, int col) {
        if (isValidPosition(row, col)) {
            return storeItems[row][col];
        }
        return new String[]{"", "", "1", "", "0"};
    }

    /**
     * Updates an item in the store at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param itemId The ID of the item.
     * @param itemName The name of the item.
     * @param category The category of the item.
     * @param price The price of the item.
     */
    public void updateItem(int row, int col, String itemId, String itemName, String category, int price) {
        if (isValidPosition(row, col)) {
            storeItems[row][col][0] = itemId;
            storeItems[row][col][1] = itemName;
            storeItems[row][col][2] = "1"; // Always 1 for store
            storeItems[row][col][3] = category;
            storeItems[row][col][4] = String.valueOf(price);
        }
    }

    /**
     * Edits the ID of the item at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param id The new ID to set.
     */
    public void editItemId(int row, int col, String id) {
        if (isValidPosition(row, col)) {
            storeItems[row][col][0] = id;
        }
    }

    /**
     * Edits the name of the item at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param name The new name to set.
     */
    public void editItemName(int row, int col, String name) {
        if (isValidPosition(row, col)) {
            storeItems[row][col][1] = name;
        }
    }

    /**
     * Edits the category of the item at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param category The new category to set.
     */
    public void editCategory(int row, int col, String category) {
        if (isValidPosition(row, col)) {
            storeItems[row][col][3] = category;
        }
    }
    
    /**
     * Edits the price of the item at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param price The new price to set.
     */
    public void editPrice(int row, int col, int price) {
        if (isValidPosition(row, col)) {
            storeItems[row][col][4] = String.valueOf(price);
        }
    }
    
    /**
     * Gets the price of the item at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @return The price of the item, or 0 if the position is invalid.
     */
    public int getPrice(int row, int col) {
        if (isValidPosition(row, col)) {
            try {
                return Integer.parseInt(storeItems[row][col][4]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Processes a purchase of an item from the store.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @return true if the purchase was successful, false otherwise.
     */
    public boolean purchaseItem(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }
        
        String[] item = storeItems[row][col];
        
        // Check if item exists
        if (item[1].isEmpty()) {
            return false;
        }
        
        // Get price
        int price;
        try {
            price = Integer.parseInt(item[4]);
        } catch (NumberFormatException e) {
            return false;
        }
        
        // Check if player has enough currency
        int playerCurrency = getPlayerCurrency();
        if (playerCurrency < price) {
            return false;
        }
        
        // Deduct currency from player
        player.setCurrency(playerId, String.valueOf(playerCurrency - price));
        
        // Add item to player's inventory
        boolean added = inventoryManager.addItem(item[0], item[1], 1, item[3]);
        
        // If item was added to inventory, save changes
        if (added) {
            inventoryManager.saveInventory();
            inventoryManager.setLastUsedItemId(item[0]);
            return true;
        }
        
        // If inventory is full, refund the currency
        player.setCurrency(playerId, String.valueOf(playerCurrency));
        return false;
    }
    
    /**
     * Checks if the given position is valid within the store grid.
     *
     * @param row The row index to check.
     * @param col The column index to check.
     * @return true if the position is valid, false otherwise.
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }
    
    /**
     * Gets the player's current currency.
     *
     * @return The player's currency.
     */
    public int getPlayerCurrency() {
        return player.getCurrency(playerId);
    }
    
    /**
     * Gets the grid size of the store.
     *
     * @return The grid size.
     */
    public int getGridSize() {
        return GRID_SIZE;
    }
    
    /**
     * Gets the inventory manager used by this store.
     *
     * @return The inventory manager.
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}