package Implementation;
import java.util.List;
import Implementation.Date;

/**
 * The {@code VirtualPets} class extends {@code DataManager} to provide methods for
 * retrieving and updating pet-related data from a CSV file.
 * This class manages pet information, including pet ID, owner (player ID),
 * pet name, age, hunger, happiness, health, and last fed timestamp.
 */
public class VirtualPets extends DataManager {

    /**
     * Constructs a {@code VirtualPets} object with the specified CSV file path.
     *
     * @param filePath The path to the CSV file containing pet data.
     */
    public VirtualPets(String filePath) {
        super(filePath);
    }

    /**
     * Finds the row index of a pet based on its pet ID.
     *
     * @param petId The ID of the pet.
     * @return The index of the pet's row, or -1 if not found.
     */
    private int findRow(String petId) {
        List<String[]> data = readCSV();
        int index = 0;
        for (String[] row : data) {
            if (row[0].equals(petId)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Retrieves the pet ID at the specified index.
     *
     * @param index The index of the pet data.
     * @return The pet ID at the specified index.
     */
    public String getPetId(int index) {
        return getData(index, 0);
    }

    /**
     * Sets the pet ID at the specified index.
     *
     * @param index The index of the pet data.
     * @param petId The new pet ID to set.
     */
    public void setPetId(int index, String petId) {
        setData(index, 0, petId);
    }

    /**
     * Retrieves the player ID associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The player ID associated with the pet.
     */
    public String getPlayerId(String petId) {
        return getData(findRow(petId), 1);
    }

    /**
     * Sets the player ID for the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param playerId The new player ID to set.
     */
    public void setPlayerId(String petId, String playerId) {
        setData(findRow(petId), 1, playerId);
    }

    /**
     * Retrieves the pet name associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The name of the pet.
     */
    public String getPetName(String petId) {
        return getData(findRow(petId), 2);
    }

    /**
     * Sets the pet name for the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param petName The new pet name to set.
     */
    public void setPetName(String petId, String petName) {
        setData(findRow(petId), 2, petName);
    }

    /**
     * Retrieves the age of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The age of the pet.
     */
    public int getAge(String petId) {
        return Integer.parseInt(getData(findRow(petId), 3));
    }

    /**
     * Sets the age of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param age The new age to set for the pet.
     */
    public void setAge(String petId, int age) {
        setData(findRow(petId), 3, String.valueOf(age));
    }

    /**
     * Retrieves the hunger level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The hunger level of the pet.
     */
    public int getHunger(String petId) {
        return Integer.parseInt(getData(findRow(petId), 4));
    }

    /**
     * Sets the hunger level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param hunger The new hunger level to set for the pet.
     */
    public void setHunger(String petId, int hunger) {
        setData(findRow(petId), 4, String.valueOf(hunger));
    }

    /**
     * Retrieves the happiness level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The happiness level of the pet.
     */
    public int getHappiness(String petId) {
        return Integer.parseInt(getData(findRow(petId), 5));
    }

    /**
     * Sets the happiness level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param happiness The new happiness level to set for the pet.
     */
    public void setHappiness(String petId, int happiness) {
        setData(findRow(petId), 5, String.valueOf(happiness));
    }

    /**
     * Retrieves the health level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The health level of the pet.
     */
    public int getHealth(String petId) {
        return Integer.parseInt(getData(findRow(petId), 6));
    }

    /**
     * Sets the health level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param health The new health level to set for the pet.
     */
    public void setHealth(String petId, int health) {
        setData(findRow(petId), 6, String.valueOf(health));
    }

    /**
     * Retrieves the sleep level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The sleep level of the pet.
     */
    public int getSleep(String petId) {
        return Integer.parseInt(getData(findRow(petId), 7));
    }

    /**
     * Sets the sleep level of the pet associated with the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param sleep The new sleep level to set for the pet.
     */
    public void setSleep(String petId, int sleep) {
        setData(findRow(petId), 7, String.valueOf(sleep));
    }

    /**
     * Retrieves the last play timestamp for the pet.
     *
     * @param petId The ID of the pet.
     * @return The timestamp of the last play session.
     */
    public Implementation.Timestamp getLastPlay(String petId) {
        return new Implementation.Timestamp(getData(findRow(petId), 8));
    }

    /**
     * Sets the last play timestamp for the pet.
     *
     * @param petId The ID of the pet.
     * @param timestamp The timestamp of the last play session.
     */
    public void setLastPlay(String petId, Implementation.Timestamp timestamp) {
        setData(findRow(petId), 8, timestamp.toString());
    }

    /**
     * Retrieves the remaining vet cooldown time in seconds.
     *
     * @param petId The ID of the pet.
     * @return The remaining cooldown time in seconds.
     */
    public int getVetCooldown(String petId) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0 && data.get(row).length > 9) {
            try {
                return Integer.parseInt(getData(row, 9));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Sets the remaining vet cooldown time in seconds.
     *
     * @param petId The ID of the pet.
     * @param seconds The remaining cooldown time in seconds.
     */
    public void setVetCooldown(String petId, int seconds) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0) {
            String[] petData = data.get(row);
            if (petData.length <= 9) {
                // Expand the array if needed to size 13
                String[] newData = new String[13];
                System.arraycopy(petData, 0, newData, 0, petData.length);
                data.set(row, newData);
            }
            setData(row, 9, String.valueOf(seconds));
        }
    }

    /**
     * Retrieves the remaining play cooldown time in seconds.
     *
     * @param petId The ID of the pet.
     * @return The remaining cooldown time in seconds.
     */
    public int getPlayCooldown(String petId) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0 && data.get(row).length > 10) {
            try {
                return Integer.parseInt(getData(row, 10));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Sets the remaining play cooldown time in seconds.
     *
     * @param petId The ID of the pet.
     * @param seconds The remaining cooldown time in seconds.
     */
    public void setPlayCooldown(String petId, int seconds) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0) {
            String[] petData = data.get(row);
            if (petData.length <= 10) {
                // Expand the array if needed to size 13
                String[] newData = new String[13];
                System.arraycopy(petData, 0, newData, 0, petData.length);
                data.set(row, newData);
            }
            setData(row, 10, String.valueOf(seconds));
        }
    }

    /**
     * Retrieves a VitalStats object representing the pet's current vital statistics.
     * Assumes that hunger, happiness, health, and sleep are stored in columns 4, 5, 6, and 7 respectively.
     * @param petId The ID of the pet.
     */
    public VitalStats getVitalStats(String petId) {
        int hunger = getHunger(petId);
        int happiness = getHappiness(petId);
        int health = getHealth(petId);
        int sleep = getSleep(petId);
        int type = getPetTypeIndex(petId);
        return new VitalStats(health, sleep, hunger, happiness, type);
    }

    /**
     * Updates the CSV record for a pet with the latest vital statistics.
     * @param petId The ID of the pet.
     * @param stats The stats of the pet, as a VitalStats object
     */
    public void updateVitalStats(String petId, VitalStats stats) {
        int row = findRow(petId);
        setHunger(petId, stats.getHunger());
        setHappiness(petId, stats.getHappiness());
        setHealth(petId, stats.getHealth());
        setSleep(petId, stats.getSleep());
    }

    /**
     * Convenience method to update a pet's vital stats in a game tick.
     * This method retrieves the current stats, applies the update cycle, and saves the new values.
     * @param petId The ID of the pet.
     */
    public void updatePetVitals(String petId) {
        VitalStats stats = getVitalStats(petId);
        stats.updateStats();
        updateVitalStats(petId, stats);
    }

    /**
     * Retrieves the last accessed date for the given pet ID.
     *
     * @param petId The ID of the pet.
     * @return The last accessed date as a Date object.
     */
    public Date getLastAccessed(String petId) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0 && data.get(row).length > 11) {
            return new Date(getData(row, 11));
        }
        return Date.now(); // Return current date if not found
    }

    /**
     * Sets the last accessed date for the given pet ID.
     *
     * @param petId The ID of the pet.
     * @param date The new last accessed date.
     */
    public void setLastAccessed(String petId, Date date) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0) {
            String[] petData = data.get(row);
            if (petData.length <= 11) {
                // Expand the array if needed to size 13
                String[] newRow = new String[13];
                System.arraycopy(petData, 0, newRow, 0, petData.length);
                data.set(row, newRow);
            }
            setData(row, 11, date.toString());
        }
    }

    /**
     * Updates the last accessed date for the given pet ID to the current time.
     *
     * @param petId The ID of the pet.
     */
    public void updateLastAccessed(String petId) {
        setLastAccessed(petId, Date.now());
    }

    /**
     * Retrieves the pet type string (e.g., "dog", "cat", "dragon") for the given pet ID.
     * Reads from the new pet type column (index 12).
     *
     * @param petId The ID of the pet.
     * @return The pet type as a lowercase String, or "cat" as a default if not found or column missing.
     */
    public String getPetType(String petId) {
        int row = findRow(petId);
        if (row == -1) {
            System.err.println("Pet not found for ID: " + petId + ". Defaulting to 'cat'.");
            return "cat";
        }
        List<String[]> data = readCSV();
        if (row >= data.size() || data.get(row).length <= 12) {
            System.err.println("Pet type column (12) missing or invalid for petId: " + petId + ". Defaulting to 'cat'.");
            // Attempt to fallback to index 9 if column 12 is missing (for transition)
            if (row < data.size() && data.get(row).length > 9) {
                try {
                    int typeIndex = Integer.parseInt(getData(row, 9));
                    switch (typeIndex) {
                        case 0: return "dog";
                        case 1: return "cat";
                        case 2: return "dragon";
                    }
                } catch (NumberFormatException e) { /* ignore, proceed to default */ }
            }
            return "cat"; // Default if index 12 is missing and fallback fails
        }
        String petType = getData(row, 12);
        if (petType == null || petType.trim().isEmpty()) {
            System.err.println("Pet type column (12) is empty for petId: " + petId + ". Defaulting to 'cat'.");
            return "cat"; // Default if the type string is missing/empty
        }
        return petType.toLowerCase(); // Return the string from column 12, ensuring lowercase
    }

    /**
     * Retrieves the pet type index for the given pet ID.
     * Reads from the new pet type column (index 12).
     *
     * @param petId The ID of the pet.
     * @return The pet type index as an integer.
    */
    public int getPetTypeIndex(String petId) {
        String petType = getPetType(petId);
        switch (petType) {
            case "dog": return 0;
            case "cat": return 1;
            case "dragon": return 2;
        }
        return 1;
    }
    /**
     * Sets the pet type string for the given pet ID in the new column (index 12).
     *
     * @param petId The ID of the pet.
     * @param petType The pet type string (e.g., "dog", "cat", "dragon").
     */
    public void setPetType(String petId, String petType) {
        int row = findRow(petId);
        List<String[]> data = readCSV();
        if (row >= 0) {
            String[] petData = data.get(row);
            if (petData.length <= 12) {
                // Expand the array if needed to size 13
                String[] newRow = new String[13];
                System.arraycopy(petData, 0, newRow, 0, petData.length);
                data.set(row, newRow);
            }
            setData(row, 12, petType.toLowerCase()); // Set data at index 12
        }
    }
}
