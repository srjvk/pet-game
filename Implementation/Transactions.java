package Implementation;
import java.util.List;

/**
 * The {@code Transactions} class extends {@code DataManager} to provide methods for
 * retrieving and updating transaction data from a CSV file.
 * This class manages transaction details, including transaction ID, player ID,
 * transaction type, amount, and timestamp.
 */
public class Transactions extends DataManager {

    /**
     * Constructs a {@code Transactions} object with the specified CSV file path.
     *
     * @param filePath The path to the CSV file containing transaction data.
     */
    public Transactions(String filePath) {
        super(filePath);
    }

    /**
     * Finds the row index of a transaction based on its transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @return The index of the transaction's row, or -1 if not found.
     */
    private int findRow(String transactionId) {
        List<String[]> data = readCSV();
        int index = 0;
        for (String[] row : data) {
            if (row[0].equals(transactionId)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    // Getters and setters
    
    /**
     * Retrieves the transaction ID at the specified index.
     *
     * @param index The index of the transaction data.
     * @return The transaction ID at the specified index.
     */
    public String getTransactionId(int index) {
        return getData(index, 0);
    }
    
    /**
     * Sets the transaction ID at the specified index.
     *
     * @param index The index of the transaction data.
     * @param transactionId The new transaction ID to set.
     */
    public void setTransactionId(int index, String transactionId) {
        setData(index, 0, transactionId);
    }
    
    /**
     * Retrieves the player ID for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @return The player ID associated with the transaction.
     */
    public String getPlayerId(String transactionId) {
        return getData(findRow(transactionId), 1);
    }
    
    /**
     * Sets the player ID for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @param playerId The new player ID to set for the transaction.
     */
    public void setPlayerId(String transactionId, String playerId) {
        setData(findRow(transactionId), 1, playerId);
    }
    
    /**
     * Retrieves the transaction type for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @return The type of the transaction.
     */
    public String getTransactionType(String transactionId) {
        return getData(findRow(transactionId), 2);
    }
    
    /**
     * Sets the transaction type for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @param transactionType The new transaction type to set.
     */
    public void setTransactionType(String transactionId, String transactionType) {
        setData(findRow(transactionId), 2, transactionType);
    }
    
    /**
     * Retrieves the amount for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @return The amount of the transaction.
     */
    public int getAmount(String transactionId) {
        return Integer.parseInt(getData(findRow(transactionId), 3));
    }
    
    /**
     * Sets the amount for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @param amount The new amount to set for the transaction.
     */
    public void setAmount(String transactionId, int amount) {
        setData(findRow(transactionId), 3, String.valueOf(amount));
    }
    
    /**
     * Retrieves the timestamp for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @return The timestamp of the transaction.
     */
    public Timestamp getTimestamp(String transactionId) {
        return new Timestamp(getData(findRow(transactionId), 4));
    }
    
    /**
     * Sets the timestamp for the transaction with the given transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @param timestamp The new timestamp to set for the transaction.
     */
    public void setTimestamp(String transactionId, Timestamp timestamp) {
        setData(findRow(transactionId), 4, timestamp.toString());
    }
}
