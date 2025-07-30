package Implementation;
/**
 * The {@code Commands} class provides various command methods for interacting with the virtual pet game.
 * These commands include actions like feeding the pet, playing with the pet, purchasing items, checking game progress,
 * and managing parental controls. Each command returns a string message indicating the result of the operation.
 */
public class Commands {
    private Players players;
    private VirtualPets virtualPets;
    private Inventory inventory;
    private Store store;
    private GameProgress gameProgress;
    private ParentalControls parentalControls;

    /**
     * Constructs a {@code Commands} object to initialize necessary services for command execution.
     *
     * @param players The {@code Players} object to manage player-related actions.
     * @param virtualPets The {@code VirtualPets} object to manage pet-related actions.
     * @param inventory The {@code Inventory} object to manage the player's inventory.
     * @param store The {@code Store} object to manage store inventory and prices.
     * @param gameProgress The {@code GameProgress} object to manage player progress.
     * @param parentalControls The {@code ParentalControls} object to manage parental settings.
     */
    public Commands(Players players, VirtualPets virtualPets, Inventory inventory, 
                    Store store, GameProgress gameProgress, ParentalControls parentalControls) {
        this.players = players;
        this.virtualPets = virtualPets;
        this.inventory = inventory;
        this.store = store;
        this.gameProgress = gameProgress;
        this.parentalControls = parentalControls;
    }

    /**
     * Purchases an item from the store. If the player has enough currency, the item is added to the inventory.
     * 
     * @param playerId The ID of the player buying the item.
     * @param itemId The ID of the item to purchase.
     * @return A message indicating the result of the operation.
     */
    public String buyItem(String playerId, String itemId) {
        String price = store.getPrice(playerId, itemId);
        int playerCurrency = players.getCurrency(playerId);
        int itemPrice = Integer.parseInt(price);

        if (playerCurrency < itemPrice) {
            return "Not enough currency to buy the item.";
        }

        players.setCurrency(playerId, String.valueOf(playerCurrency - itemPrice)); 
        inventory.setQuantity(playerId, itemId, inventory.getQuantity(playerId, itemId) + 1); 
        return "success";
    }

    /**
     * Checks the player's game progress, including level, experience, quests completed, and badges.
     * 
     * @param playerId The ID of the player whose progress is to be checked.
     * @return A string array containing player ID, level, experience, quests completed, and badges.
     */
    public String[] checkGameProgress(String playerId) {
        int level = gameProgress.getLevel(playerId);
        int experience = gameProgress.getExperience(playerId);
        int questsCompleted = gameProgress.getQuestsCompleted(playerId);
        String[] badges = gameProgress.getBadgesEarned(playerId);

        String[] progress = new String[5];
        progress[0] = playerId;
        progress[1] = String.valueOf(level);
        progress[2] = String.valueOf(experience);
        progress[3] = String.valueOf(questsCompleted);
        progress[4] = String.join(", ", badges);

        return progress;
    }

    /**
     * Sets the playtime limit for the player by updating the parental controls.
     * 
     * @param playerId The ID of the player whose playtime limit is being set.
     * @param minutes The new playtime limit in minutes.
     */
    public void setPlaytimeLimit(String playerId, int minutes) {
        parentalControls.setMaxAllowedPlaytimeMinutes(playerId, minutes);
    }

    /**
     * Enables or disables parental controls for the player by updating the corresponding setting.
     * 
     * @param playerId The ID of the player whose parental controls are being modified.
     * @param isEnabled A boolean indicating whether to enable or disable parental controls.
     * @return The updated status of parental controls for the player.
     */
    public boolean enableParentalControls(String playerId, boolean isEnabled) {
        parentalControls.setPlaytimeLimitEnabled(playerId, isEnabled);
        return isEnabled;
    }
    
    /**
     * Authenticates a parental controls access attempt.
     * 
     * @param password The password entered by the user.
     * @return True if authentication is successful, false otherwise.
     */
    public boolean authenticateParentalControls(String password) {
        // Check if a custom password file exists
        java.io.File passwordFile = new java.io.File("Implementation/parentalPassword.txt");
        if (passwordFile.exists()) {
            try {
                // Read the password from the file
                java.util.Scanner scanner = new java.util.Scanner(passwordFile);
                String storedPassword = scanner.nextLine().trim();
                scanner.close();
                return storedPassword.equals(password);
            } catch (Exception e) {
                System.err.println("Error reading password file: " + e.getMessage());
                // Fall back to default password if there's an error
                return "parent1234".equals(password);
            }
        } else {
            // Use default password if no custom password has been set
            return "parent1234".equals(password);
        }
    }
    
    /**
     * Sets the allowed play time range for a player.
     * 
     * @param playerId The ID of the player.
     * @param startHour The starting hour (0-23) when play is allowed.
     * @param startMinute The starting minute (0-59) when play is allowed.
     * @param endHour The ending hour (0-23) when play is allowed.
     * @param endMinute The ending minute (0-59) when play is allowed.
     * @return A message indicating the result of the operation.
     */
    public String setAllowedPlayTime(String playerId, int startHour, int startMinute, int endHour, int endMinute) {
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23 ||
            startMinute < 0 || startMinute > 59 || endMinute < 0 || endMinute > 59) {
            return "Invalid time range specified.";
        }
        
        String timeRange = String.format("%02d:%02d-%02d:%02d", startHour, startMinute, endHour, endMinute);
        parentalControls.setMaxAllowedPlaytimeMinutes(playerId, calculateMinutes(startHour, startMinute, endHour, endMinute));
        return "Maximum playtime set successfully.";
    }

    /**
     * Helper method to calculate total minutes between two time points
     */
    private int calculateMinutes(int startHour, int startMinute, int endHour, int endMinute) {
        int startTotalMinutes = startHour * 60 + startMinute;
        int endTotalMinutes = endHour * 60 + endMinute;
        
        // Handle cases where end time is earlier than start time (spans midnight)
        if (endTotalMinutes < startTotalMinutes) {
            return (24 * 60) - startTotalMinutes + endTotalMinutes;
        } else {
            return endTotalMinutes - startTotalMinutes;
        }
    }

    /**
     * Checks if the player has exceeded their maximum allowed playtime.
     * 
     * @param playerId The ID of the player.
     * @return True if the player is allowed to play, false if they've reached their limit.
     */
    public boolean isPlayTimeAllowed(String playerId) {
        // If playtime limits are not enabled, always allow play
        if (!parentalControls.getPlaytimeLimitEnabled(playerId)) {
            return true;
        }
        
        // Get current playtime and the maximum allowed
        int currentPlaytime = parentalControls.getPlaytimeMinutes(playerId);
        int maxAllowedPlaytime = parentalControls.getMaxAllowedPlaytimeMinutes(playerId);
        
        // Check if the current playtime is less than the maximum allowed
        return currentPlaytime < maxAllowedPlaytime;
    }

    /**
     * Helper method to check if the current time is within a specified range.
     * This method is deprecated as we now use total minutes played instead of time frames.
     * 
     * @param timeRange Time range string in format "HH:MM-HH:MM".
     * @return True if the current time is within the range, false otherwise.
     * @deprecated Use isPlayTimeAllowed instead which checks against maximum allowed minutes.
     */
    @Deprecated
    private boolean isCurrentTimeInRange(String timeRange) {
        // This method is kept for backward compatibility but should not be used
        return true;
    }
    
    /**
     * Updates play time statistics for a player session.
     * This should be called when a gaming session ends.
     * 
     * @param playerId The ID of the player.
     * @param sessionDurationInMinutes The duration of the current session in minutes.
     */
    public void updatePlayTimeStatistics(String playerId, int sessionDurationInMinutes) {
        
        try {
            // Update total play time
            int currentTotalPlayTime = parentalControls.getPlaytimeMinutes(playerId);
            
            int newTotalPlaytime = currentTotalPlayTime + sessionDurationInMinutes;
            parentalControls.setPlaytimeMinutes(playerId, newTotalPlaytime);
            
            // Update average play time
            int currentAveragePlayTime = parentalControls.getAveragePlaytimeMinutes(playerId);
            int sessionCount = parentalControls.getSessionCount(playerId);
            
            // Calculate new average play time
            int newAveragePlayTime;
            if (sessionCount == 0) {
                newAveragePlayTime = sessionDurationInMinutes;
            } else {
                newAveragePlayTime = ((currentAveragePlayTime * sessionCount) + sessionDurationInMinutes) / (sessionCount + 1);
            }
            
            parentalControls.setAveragePlaytimeMinutes(playerId, newAveragePlayTime);
            parentalControls.setSessionCount(playerId, sessionCount + 1);
            
            // Verify the changes were saved
            int verifiedPlaytime = parentalControls.getPlaytimeMinutes(playerId);
        } catch (Exception e) {
            System.err.println("Error in updatePlayTimeStatistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Resets play time statistics for a player.
     * 
     * @param playerId The ID of the player.
     * @return A message indicating the result of the operation.
     */
    public String resetPlayTimeStatistics(String playerId) {
        parentalControls.setPlaytimeMinutes(playerId, 0);
        parentalControls.setAveragePlaytimeMinutes(playerId, 0);
        parentalControls.setSessionCount(playerId, 0);
        return "Play time statistics reset successfully.";
    }
    
    /**
     * Revives a pet and restores all its statistics to maximum values.
     * 
     * @param petId The ID of the pet to revive.
     * @return A message indicating the result of the operation.
     */
    public String revivePet(String petId) {
        // Set all pet stats to maximum
        virtualPets.setHunger(petId, 100);
        virtualPets.setHappiness(petId, 100);
        virtualPets.setHealth(petId, 100);
        virtualPets.setSleep(petId, 100);
        
        return "Pet revived successfully with all stats restored to maximum.";
    }

    /**
     * Updates the pet's vital statistics in the backend.
     * 
     * @param petId The ID of the pet.
     * @param stats The new vital statistics for the pet.
     */
    public void updatePetStats(String petId, VitalStats stats) {
        virtualPets.updateVitalStats(petId, stats);
    }

    /**
     * Sets the maximum allowed playtime in minutes for a player.
     * 
     * @param playerId The ID of the player.
     * @param minutes The maximum allowed playtime in minutes.
     * @return A message indicating the result of the operation.
     */
    public String setMaxPlaytimeMinutes(String playerId, int minutes) {
        if (minutes < 0) {
            return "Invalid playtime limit specified.";
        }
        
        parentalControls.setMaxAllowedPlaytimeMinutes(playerId, minutes);
        return "Maximum playtime limit set successfully.";
    }
}
