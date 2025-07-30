package Implementation.PlayerInventory;
import java.util.*;

import Implementation.Inventory;

/**
 * The {@code InventoryManager} class manages a player's inventory data.
 * It handles inventory loading, saving, and manipulation of inventory items.
 */
public class InventoryManager {
    private final int MAX_SPACE = 9;
    private final int GRID_SIZE = 3; // 3x3 grid
    private Inventory inventory;
    private String[][][] inventoryItems; // [row][col][itemData] where itemData is [itemId, itemName, quantity, category]
    private String playerId;
    private String petId;

    private HashMap<String, Integer> itemCount;
    private String lastUsedFoodId;
    private String lastUsedToyId;
    private String lastUsedGiftId;

    /**
     * Constructs an {@code InventoryManager} object with the specified player ID.
     *
     * @param playerId The ID of the player whose inventory is managed.
     * @param csvFilePath The path to the CSV file containing inventory data.
     */
    public InventoryManager(String playerId, String filepath, String petId) {
        this.playerId = playerId;
        this.petId = petId;
        this.inventory = new Inventory(filepath);
        this.inventoryItems = new String[GRID_SIZE][GRID_SIZE][4]; // 4 attributes per item
        this.itemCount = new HashMap<String, Integer>(); // Initialize itemCount before loading inventory
        loadInventory();
    }

    /**
     * Initializes the inventory array with empty values.
     */
    private void initializeEmptyInventory() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // Set consistent empty values for all inventory slots
                inventoryItems[row][col] = new String[]{"" + (row * GRID_SIZE + col), "", "0", ""};
            }
        }
    }

    /**
     * Creates a new empty inventory and saves it to the CSV file.
     */
    public void newInventory() {
        List<String[]> inventoryData = new ArrayList<>();
        
        for (int i = 0; i < MAX_SPACE; i++) {
            String[] item = {playerId, String.valueOf(i), "", "0", ""};
            inventoryData.add(item);
        }
        
        inventory.writeCSV(inventoryData);
        initializeEmptyInventory();
    }

    /**
     * Loads inventory data from the CSV file.
     */
    public void loadInventory() {
        List<String[]> data = inventory.readCSV();
        initializeEmptyInventory(); // Reset current inventory
        // Clear itemCount before reloading
        itemCount.clear();
        
        int position = 0;
        for (String[] row : data) {
            if (row.length == 5 && row[0].equals(playerId) && position <= 8) {
                if (position < MAX_SPACE) {
                    int gridRow = position / GRID_SIZE;
                    int gridCol = position % GRID_SIZE;
                    
                    String itemId = row[1]; // itemId
                    String itemName = row[2]; // itemName
                    String quantity = row[3]; // quantity
                    String category = row[4]; // category
                    
                    inventoryItems[gridRow][gridCol][0] = itemId;
                    inventoryItems[gridRow][gridCol][1] = itemName;
                    inventoryItems[gridRow][gridCol][2] = quantity;
                    inventoryItems[gridRow][gridCol][3] = category;
                    
                    // Update itemCount with this item's quantity
                    if (itemName != null && !itemName.isEmpty() && !quantity.equals("0")) {
                        int quantityInt = Integer.parseInt(quantity);
                        itemCount.put(itemId, quantityInt);
                    }
                }
            }
            position++;
        }
    }

    /**
     * Saves the current inventory to the CSV file.
     */
    public void saveInventory() {
        List<String[]> inventoryData = new ArrayList<>();
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int index = row * GRID_SIZE + col;
                String itemId = String.valueOf(index); // Use positional index as ID
                String itemName = inventoryItems[row][col][1];
                String quantity = inventoryItems[row][col][2];
                String category = inventoryItems[row][col][3];
                
                // Ensure empty slots have consistent empty values
                if (itemName == null || itemName.isEmpty() || quantity.equals("0")) {
                    itemName = "";
                    quantity = "0";
                    category = "";
                }
                
                String[] item = {playerId, itemId, itemName, quantity, category};
                inventoryData.add(item);
            }
        }
        
        inventory.writeCSV(inventoryData);
    }
    
    
    /**
     * Adds a new item to the inventory. If the item already exists, increase its quantity.
     *
     * @param itemId The ID of the item.
     * @param itemName The name of the item.
     * @param quantity The quantity of the item.
     * @param category The category of the item.
     * @return true if the item was added successfully, false if inventory is full.
     */
    public boolean addItem(String itemId, String itemName, int quantity, String category) {
        // Check if the item already exists in inventory by name (more reliable than ID)
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (inventoryItems[row][col][1] != null && 
                    !inventoryItems[row][col][1].isEmpty() && 
                    inventoryItems[row][col][1].equals(itemName)) {
                    // Item already exists, increase its quantity
                    int existingQuantity = Integer.parseInt(inventoryItems[row][col][2]);
                    inventoryItems[row][col][2] = String.valueOf(existingQuantity + quantity);
                    Integer currentCount = itemCount.get(itemId);
                    if (currentCount != null) {
                        itemCount.put(itemId, currentCount + quantity);
                    } else {
                        itemCount.put(itemId, quantity);
                    }
                    return true;
                }
            }
        }
        
        // If item is not found, look for the first empty slot

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (inventoryItems[row][col][1] == null || 
                    inventoryItems[row][col][1].isEmpty() || 
                    Integer.parseInt(inventoryItems[row][col][2]) <= 0) {
                    // Found an empty slot, add the item
                    inventoryItems[row][col][0] = itemId;
                    inventoryItems[row][col][1] = itemName;
                    inventoryItems[row][col][2] = String.valueOf(quantity);
                    inventoryItems[row][col][3] = category;
                    itemCount.put(itemId, quantity);
                    return true;
                }
            }
        }
        
        // No empty slots found
        return false;
    }

    
    /**
     * Retrieves an item from the inventory at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @return A String array containing the item data (itemId, itemName, quantity, category).
     */
    public String[] getItem(int row, int col) {
        if (isValidPosition(row, col)) {
            return inventoryItems[row][col];
        }
        return new String[]{"", "", "0", ""};
    }

    /**
     * Gets the total quantity of an item in the inventory
     * @param itemId
     * @return the total overall count of the item
     */
    public Integer getItemCount(String itemId){
        return itemCount.getOrDefault(itemId, 0);
    }
    /**
     * Updates an item in the inventory at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param itemId The ID of the item.
     * @param itemName The name of the item.
     * @param quantity The quantity of the item.
     * @param category The category of the item.
     */
    public void updateItem(int row, int col, String itemId, String itemName, int quantity, String category) {
        if (isValidPosition(row, col)) {
            inventoryItems[row][col][0] = itemId;
            inventoryItems[row][col][1] = itemName;
            inventoryItems[row][col][2] = String.valueOf(quantity);
            inventoryItems[row][col][3] = category;
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
            inventoryItems[row][col][0] = id;
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
            inventoryItems[row][col][1] = name;
        }
    }

    /**
     * Edits the quantity of the item at the specified location by adding the given amount.
     * If the result is zero or negative, the item is removed.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param amount The amount to add (can be negative).
     */
    public void editQuantity(int row, int col, int amount) {
        if (isValidPosition(row, col)) {
            int currentQuantity = Integer.parseInt(inventoryItems[row][col][2]);
            int newQuantity = currentQuantity + amount;
            
            if (newQuantity <= 0) {
                // Remove the item if quantity becomes zero or negative
                inventoryItems[row][col] = new String[]{"", "", "0", ""};
                itemCount.put(inventoryItems[row][col][0], 0);
            } else {
                inventoryItems[row][col][2] = String.valueOf(newQuantity);
                itemCount.put(inventoryItems[row][col][0], newQuantity);
            }

        }
    }

    /**
     * Sets the quantity of the item at the specified location.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     * @param quantity The new quantity to set.
     */
    public void setQuantity(int row, int col, int quantity) {
        if (isValidPosition(row, col)) {
            if (quantity <= 0) {
                // Remove the item if quantity becomes zero or negative
                inventoryItems[row][col] = new String[]{"", "", "0", ""};
                itemCount.put(inventoryItems[row][col][0], 0);
            } else {
                inventoryItems[row][col][2] = String.valueOf(quantity);
                itemCount.put(inventoryItems[row][col][0], quantity);
            }
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
            inventoryItems[row][col][3] = category;
        }
    }
    
    /**
     * Checks if the given position is valid within the inventory grid.
     *
     * @param row The row index to check.
     * @param col The column index to check.
     * @return true if the position is valid, false otherwise.
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }
    
    /**
     * Gets the player ID.
     *
     * @return The player ID.
     */
    public String getPlayerId() {
        return playerId;
    }
    
    /**
     * Sets the player ID.
     *
     * @param playerId The new player ID to set.
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    /**
     * Gets the maximum number of items that can be stored in the inventory.
     *
     * @return The maximum number of items.
     */
    public int getMaxSpace() {
        return MAX_SPACE;
    }
    
    /**
     * Gets the grid size of the inventory.
     *
     * @return The grid size.
     */
    public int getGridSize() {
        return GRID_SIZE;
    }

    public String getLastUsedFoodId(){
        return this.lastUsedFoodId;
    }
    public void setLastUsedFoodId(String itemId){
        this.lastUsedFoodId = itemId;
    }
    public String getLastUsedToyId(){
        return this.lastUsedToyId;
    }
    public void setLastUsedToyId(String itemId){
        this.lastUsedToyId = itemId;
    }
    public String getLastUsedGiftId(){
        return this.lastUsedGiftId;
    }
    public void setLastUsedGiftId(String itemId){
        this.lastUsedGiftId = itemId;
    }

    public void setLastUsedItemId(String itemId){
        if(itemId.equals("1") || itemId.equals("2") || itemId.equals("10")){
            setLastUsedFoodId(itemId);
        }
        else if(itemId.equals("3") || itemId.equals("4")){
            setLastUsedToyId(itemId);
        }
        else{
            setLastUsedGiftId(itemId);
        }
    }
}