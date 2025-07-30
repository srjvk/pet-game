package Implementation.StoreInventory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import Implementation.*;
import Implementation.PlayerInventory.*;

/**
 * The {@code StoreGUI} class creates a graphical user interface for
 * displaying and interacting with a store.
 */
public class StoreGUI extends JPanel {
    private JPanel storePanel;
    private JButton[][] itemButtons;
    private StoreManager storeManager;
    private Map<String, ImageIcon> itemImages;
    private JLabel currencyLabel;
    private MainMenu menu;
    private String playerId;
    private String petId;
    private final int GRID_SIZE;
    private final int BUTTON_SIZE = 80;
    /**
     * Constructs a {@code StoreGUI} object with the specified store manager.
     *
     * @param storeManager The store manager to use.
     */
    public StoreGUI(StoreManager storeManager, MainMenu menu, String playerId, String petId) {
        this.storeManager = storeManager;
        this.GRID_SIZE = storeManager.getGridSize();
        this.itemButtons = new JButton[GRID_SIZE][GRID_SIZE];
        this.itemImages = new HashMap<>();
        this.playerId = playerId;
        this.petId = petId;
        this.menu = menu;
        
        initializeGUI();
        loadItemImages();
        displayStore();
    }
    
    /**
     * Initializes the graphical user interface.
     */
    private void initializeGUI() {
        // Set up this panel with BorderLayout
        this.setLayout(new BorderLayout());
        
        // Create header panel with title, currency display, and back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Title in the center
        JLabel titleLabel = new JLabel("Store", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Currency display on the left
        currencyLabel = new JLabel("Currency: " + storeManager.getPlayerCurrency(), JLabel.LEFT);
        currencyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        currencyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Back button on the right
        JButton backButton = new JButton("Go Back");
        backButton.addActionListener(e -> {
            // Corrected method call to navigate back
            menu.openPetInteraction(petId);
            //Window window = SwingUtilities.getWindowAncestor(this);
            //if (window instanceof JFrame) {
            //   ((JFrame) window).dispose();
            //} else if (window instanceof JDialog) {
            //    ((JDialog) window).dispose();
            //}
        });
        
        // Add components to header panel
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(currencyLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        this.add(headerPanel, BorderLayout.NORTH);
        
        // Create store grid panel
        storePanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int r = row;
                final int c = col;
                
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                button.addActionListener(e -> displayOptions(r, c));
                
                itemButtons[row][col] = button;
                storePanel.add(button);
            }
        }
        this.add(storePanel, BorderLayout.CENTER);
    }
    
    /**
     * Loads sample item images for the store.
     * In a real application, you'd load actual item images.
     */
    private void loadItemImages() {
        ImageIcon acorn = new ImageIcon("Implementation/Icons/acorn.png");
        ImageIcon treats = new ImageIcon("Implementation/Icons/treats.png");
        ImageIcon feast = new ImageIcon("Implementation/Icons/feast.jpg");
        itemImages.put("acorn", acorn);
        itemImages.put("treats", treats);
        itemImages.put("feast", feast);

        ImageIcon bandaid = new ImageIcon("Implementation/Icons/bandaid.jpg");
        ImageIcon needle = new ImageIcon("Implementation/Icons/needle.png");
        ImageIcon firstaidkit = new ImageIcon("Implementation/Icons/firstaidkit.jpg");
        itemImages.put("bandaid", bandaid);
        itemImages.put("needle", needle);
        itemImages.put("firstaidkit", firstaidkit);

        ImageIcon tennis = new ImageIcon("Implementation/Icons/tennis.png");
        ImageIcon mouse = new ImageIcon("Implementation/Icons/mouse.png");
        ImageIcon toys = new ImageIcon("Implementation/Icons/toys.jpg");
        itemImages.put("tennis", tennis);
        itemImages.put("mouse", mouse);
        itemImages.put("toys", toys);
    }
    
    /**
     * Creates a simple icon with the specified color and label.
     *
     * @param color The color of the icon.
     * @param label The label for the icon.
     * @return An ImageIcon with the specified color and label.
     */
    private ImageIcon createColorIcon(Color color, String label) {
        // Create a simple colored square icon
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(10, 10, 44, 44);
        g.setColor(Color.WHITE);
        g.drawString(label, 15, 35);
        g.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Displays the store items in the GUI.
     */
    public void displayStore() {
        // Update currency display
        currencyLabel.setText("Currency: " + storeManager.getPlayerCurrency());
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                String[] item = storeManager.getItem(row, col);
                JButton button = itemButtons[row][col];
                
                if (item[1].isEmpty()) {
                    // Empty slot
                    button.setIcon(null);
                    button.setText("");
                } else {
                    // Set icon based on item name if available, otherwise use a default
                    String itemName = item[1].toLowerCase();
                    if (itemImages.containsKey(itemName)) {
                        button.setIcon(itemImages.get(itemName));
                    } else {
                        button.setIcon(createColorIcon(Color.GRAY, itemName));
                    }
                    
                    // Display price
                    int price = storeManager.getPrice(row, col);
                    button.setText("$" + price);
                    button.setHorizontalTextPosition(JButton.CENTER);
                    button.setVerticalTextPosition(JButton.BOTTOM);
                }
            }
        }
    }
    
    /**
     * Displays options for the item at the specified position.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     */
    public void displayOptions(int row, int col) {
        String[] item = storeManager.getItem(row, col);
        
        if (item[1].isEmpty()) {
            // Empty slot - do nothing
            JOptionPane.showMessageDialog(this, "Empty slot");
        } else {
            int price = storeManager.getPrice(row, col);
            int playerCurrency = storeManager.getPlayerCurrency();
            
            String[] options = {"Buy", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,
                                "Item: " + item[1] + 
                                "\nCategory: " + item[3] + 
                                "\nPrice: $" + price + 
                                "\nYour Currency: $" + playerCurrency,
                                "Item Purchase",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                options[0]);
            
            if (choice == 0) { // Buy
                boolean purchased = storeManager.purchaseItem(row, col);
                if (purchased) {
                    JOptionPane.showMessageDialog(this, 
                                    "You purchased " + item[1] + " for $" + price);
                    // Update currency display and ensure inventory is reloaded and refreshed
                    storeManager.getInventoryManager().loadInventory();
                    displayStore();
                } else {
                    if (playerCurrency < price) {
                        JOptionPane.showMessageDialog(this, 
                                        "Not enough currency to purchase this item!",
                                        "Purchase Failed",
                                        JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                                        "Your inventory is full!",
                                        "Purchase Failed",
                                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    /**
     * Shows the store GUI in a standalone frame.
     */
//    public void show() {
//        JFrame frame = new JFrame("Store");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setSize(600, 500);
//        frame.add(this);
//
//        // Load store and display
//        storeManager.loadStore();
//        displayStore();
//
//        frame.setVisible(true);
//    }
}