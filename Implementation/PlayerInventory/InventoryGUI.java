package Implementation.PlayerInventory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import Implementation.*;

/**
 * The {@code InventoryGUI} class creates a graphical user interface for
 * managing and displaying a player's inventory.
 */
public class InventoryGUI extends JPanel {
    private JPanel inventoryPanel;
    private JButton[][] itemButtons;
    private InventoryManager inventoryManager;
    private Map<String, ImageIcon> itemImages;
    private MainMenu menu;
    private String playerId;
    private String petId;
    private final int GRID_SIZE;
    private final int BUTTON_SIZE = 80;
    
    /**
     * Constructs an {@code InventoryGUI} object with the specified inventory manager.
     *
     * @param inventoryManager The inventory manager to use.
     * @param menu The main menu instance.
     * @param playerId The ID of the current player.
     * @param petId The ID of the current pet.
     */
    public InventoryGUI(InventoryManager inventoryManager, MainMenu menu, String playerId, String petId) {
        this.inventoryManager = inventoryManager;
        this.GRID_SIZE = inventoryManager.getGridSize();
        this.itemButtons = new JButton[GRID_SIZE][GRID_SIZE];
        this.itemImages = new HashMap<>();
        this.menu = menu;
        this.playerId = playerId;
        this.petId = petId;
        initializeGUI();
        loadItemImages();
        displayInventory();
    }
    
    /**
     * Initializes the graphical user interface.
     */
    private void initializeGUI() {
        // Set up this panel with BorderLayout
        this.setLayout(new BorderLayout());
        
        // Create header panel with title and back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Inventory", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JButton backButton = new JButton("Go Back");
        backButton.addActionListener(e -> {
            // Call the correct method in MainMenu
            menu.openPetInteraction(petId);
            //Window window = SwingUtilities.getWindowAncestor(this);
            //if (window instanceof JFrame) {
              //  ((JFrame) window).dispose();
            //} else if (window instanceof JDialog) {
            //    ((JDialog) window).dispose();
            //}
        });
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(backButton, BorderLayout.EAST);
        this.add(headerPanel, BorderLayout.NORTH);
        
        // Create inventory grid panel
        inventoryPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int r = row;
                final int c = col;
                
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                button.addActionListener(e -> displayOptions(r, c));
                
                itemButtons[row][col] = button;
                inventoryPanel.add(button);
            }
        }
        this.add(inventoryPanel, BorderLayout.CENTER);
    }
    
    /**
     * Loads sample item images for the inventory.
     * In a real application, you'd load actual item images.
     */
    private void loadItemImages() {
                // Create sample item icons
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
     * Displays the player's inventory in the GUI.
     */
    public void displayInventory() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                String[] item = inventoryManager.getItem(row, col);
                JButton button = itemButtons[row][col];
                
                if (item[1].isEmpty() || Integer.parseInt(item[2]) <= 0) {
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
                    button.setText("x" + item[2]);
                    button.setHorizontalTextPosition(JButton.CENTER);
                    button.setVerticalTextPosition(JButton.BOTTOM);
                }
            }
        }
    }
    
    /**
     * Gets the position of the currently selected item.
     *
     * @return An int array with {row, col} of the selected position, or null if none selected.
     */
    private int[] getSelectedPosition() {
        // This method would need to track the last clicked item
        // For simplicity, we'll show a dialog to select a position
        String input = JOptionPane.showInputDialog(this, 
                                                "Enter position as row,col (0-" + (GRID_SIZE-1) + "):");
        if (input != null) {
            try {
                String[] parts = input.split(",");
                if (parts.length == 2) {
                    int row = Integer.parseInt(parts[0].trim());
                    int col = Integer.parseInt(parts[1].trim());
                    if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                        return new int[]{row, col};
                    }
                }
            } catch (NumberFormatException e) {
                // Handle parse error
            }
            JOptionPane.showMessageDialog(this, "Invalid position format!");
        }
        return null;
    }
    
    /**
     * Gets the grid position from a mouse click.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     * @return An int array with {row, col} of the clicked position.
     */
    public int[] getClick(int x, int y) {
        // Convert screen coordinates to grid position
        int row = y / BUTTON_SIZE;
        int col = x / BUTTON_SIZE;
        
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            return new int[]{row, col};
        }
        
        return null;
    }
    
    /**
     * Displays options for the item at the specified position.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     */
    public void displayOptions(int row, int col) {
        /*
        String[] item = inventoryManager.getItem(row, col);
        
        if (item[1].isEmpty() || Integer.parseInt(item[2]) <= 0) {
            // Empty slot - do nothing for simplified interface
            JOptionPane.showMessageDialog(this, "Empty slot");
        } else {
            // Show simplified item options (removed Edit option)
            String[] options = {"Use", "Drop", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,
                                    "Item: " + item[1] + "\nQuantity: " + item[2] + "\nCategory: " + item[3],
                                    "Item Options",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE,
                                    null,
                                    options,
                                    options[0]);
            
            switch (choice) {
                case 0: // Use
                    useItem(row, col);
                    break;
                case 1: // Drop
                    inventoryManager.setQuantity(row, col, 0);
                    displayInventory();
                    inventoryManager.saveInventory();
                    break;
                default:
                    break;
            }
        }
        */
    }
    
    /**
     * Handles the use action for an item.
     *
     * @param row The row index of the item.
     * @param col The column index of the item.
     */
    private void useItem(int row, int col) {
        String[] item = inventoryManager.getItem(row, col);
        if (!item[1].isEmpty() && Integer.parseInt(item[2]) > 0) {
            JOptionPane.showMessageDialog(this, "Using " + item[1]);
            inventoryManager.editQuantity(row, col, -1);
            displayInventory();
            inventoryManager.saveInventory();
            inventoryManager.setLastUsedItemId(item[0]);
        }
    }
    
    /**
     * Shows the inventory GUI in a standalone frame.
     */
//    public void show() {
//        JFrame frame = new JFrame("Inventory");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setSize(600, 500);
//        frame.add(this);
//
//        // Load inventory and display
//        inventoryManager.loadInventory();
//        displayInventory();
//
//        frame.setVisible(true);
//    }
}