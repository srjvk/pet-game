package Implementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import Implementation.PlayerInventory.*;
import Implementation.StoreInventory.*;

/**
 * MainMenu represents the primary interface of the Virtual Pet Game.
 * This class handles the creation and management of the main menu window,
 * including navigation between different game screens and management of game state.
 * 
 * The main menu provides options for:
 * - Starting a new game
 * - Loading an existing game
 * - Accessing game instructions
 * - Managing parental controls
 * - Exiting the game
 */
public class MainMenu extends JFrame {
    private JButton newGameButton, loadGameButton, parentalControlsButton, exitButton, infoButton;
    private JLabel titleLabel;
    private JPanel buttonPanel;
    private JPanel contentPanel;
    private JPanel mainContentPanel;
    private ParentalControlsGUI parentalControlsPanel;
    private PetSelectionGUI petSelectionPanel;
    private LoadGameGUI loadGamePanel;
    private PetInteractionWindow petInteractionWindow;
    private InventoryGUI inventoryPanel;
    private StoreGUI storePanel;
    private JLabel gameImageLabel;
    
    // Global playtime tracker
    private PlaytimeTracker playtimeTracker;
    // private ScoreTracker scoreTracker;

    /**
     * Constructs a new MainMenu window with all necessary components and event handlers.
     * Initializes the playtime tracker and sets up the main window properties.
     */
    public MainMenu() {
        setTitle("Virtual Pet Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);   // Prevent resizing


        // Initialize the global playtime tracker
        playtimeTracker = PlaytimeTracker.getInstance();
        System.out.println("MainMenu initialized global playtime tracker");

        // Main content panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE); // Or your desired background color

        // Create the main menu panel
        mainContentPanel = createMainMenuPanel();
        
        // Add main content panel to the card layout
        contentPanel.add(mainContentPanel, "MAIN_MENU");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Add window closing event to properly shut down the playtime tracker
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save any game state
                if (petInteractionWindow != null) {
                    petInteractionWindow.saveGame();
                }
                
                // Save playtime statistics and shut down the tracker
                playtimeTracker.shutdown();
                System.out.println("MainMenu closing, playtime tracker shut down");
            }
        });
        
        setVisible(true);
    }
    
    /**
     * Creates and returns the main menu panel containing all buttons and visual elements.
     * 
     * @return JPanel containing the main menu interface
     */
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Title Label
        titleLabel = new JLabel("Virtual Pet Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new GridBagLayout());
        centerContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        try {
            // Use the ResourceLoader to load and resize the image
            ImageIcon gameIcon = ResourceLoader.loadAndResizeImage("Implementation/pet_game_icon.png", 300, 300);
            
            if (gameIcon != null) {
                gameImageLabel = new JLabel(gameIcon);
            } else {
                throw new Exception("Icon not found");
            }
        } catch (Exception e) {
            // Fallback if image not found
            gameImageLabel = new JLabel("Game Logo");
            gameImageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            gameImageLabel.setPreferredSize(new Dimension(300, 300));
            gameImageLabel.setHorizontalAlignment(JLabel.CENTER);
            gameImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        centerContent.add(gameImageLabel, gbc);

        // Button Panel (centered)
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create and add buttons
        newGameButton = createButton("New Game");
        loadGameButton = createButton("Load Game");
        infoButton = createButton("Instructions");
        parentalControlsButton = createButton("Parental Controls");
        exitButton = createButton("Exit");

        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(loadGameButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(infoButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(parentalControlsButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(exitButton);
        
        gbc.gridx = 1;
        centerContent.add(buttonPanel, gbc);
        
        panel.add(centerContent, BorderLayout.CENTER);
        
        // Team Information Panel
        JPanel teamInfoPanel = new JPanel();
        teamInfoPanel.setLayout(new BoxLayout(teamInfoPanel, BoxLayout.Y_AXIS));
        teamInfoPanel.setBackground(new Color(240, 240, 240));
        teamInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel developersLabel = new JLabel("Developed by: Edison Qu, Eric Jiang, Andrew Durnford, Calvin Lo, Surajj Vinodh Kumar");
        JLabel teamLabel = new JLabel("Team #81");
        JLabel termLabel = new JLabel("Fall 2024");
        JLabel courseLabel = new JLabel("Created as part of CS2212 at Western University");
        
        Font infoFont = new Font("Arial", Font.PLAIN, 14);
        developersLabel.setFont(infoFont);
        teamLabel.setFont(infoFont);
        termLabel.setFont(infoFont);
        courseLabel.setFont(infoFont);
        
        developersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        teamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        termLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        courseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        teamInfoPanel.add(developersLabel);
        teamInfoPanel.add(Box.createVerticalStrut(5));
        teamInfoPanel.add(teamLabel);
        teamInfoPanel.add(Box.createVerticalStrut(5));
        teamInfoPanel.add(termLabel);
        teamInfoPanel.add(Box.createVerticalStrut(5));
        teamInfoPanel.add(courseLabel);
        
        panel.add(teamInfoPanel, BorderLayout.SOUTH);
        
        // Button Actions
        newGameButton.addActionListener(e -> {
            showPetSelection();
        });

        loadGameButton.addActionListener(e -> {
            showLoadGame();
        });

        infoButton.addActionListener(e -> {
            InfoScreen infoScreen = new InfoScreen(this);
            infoScreen.setVisible(true);
        });

        parentalControlsButton.addActionListener(e -> {
            showParentalControls();
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Bind the Escape key to an action named "goToMainMenu"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goToMainMenu");
        actionMap.put("goToMainMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call your method to show the main menu.
                // For example, if your parent frame has a method called showMainContent(), do:
                showMainContent();
            }
        });

        return panel;
    }
    
    /**
     * Displays the pet selection screen where players can choose their virtual pet.
     * Creates a new PetSelectionGUI if one doesn't exist and switches to that view.
     */
    private void showPetSelection() {
        // Create pet selection panel if it doesn't exist
        if (petSelectionPanel == null) {
            petSelectionPanel = new PetSelectionGUI(this);
            contentPanel.add(petSelectionPanel, "PET_SELECTION");
        }
        
        // Switch to the pet selection card
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "PET_SELECTION");
    }
    
    /**
     * Displays the parental controls panel after password authentication.
     * Prompts the user for a password and only shows the controls if authentication is successful.
     */
    private void showParentalControls() {
        // Prompt for password before showing parental controls
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, 
                                                  passwordField, 
                                                  "Enter Parental Controls Password", 
                                                  JOptionPane.OK_CANCEL_OPTION, 
                                                  JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            
            // Create needed objects for authentication
            ParentalControls controls = new ParentalControls();
            VirtualPets virtualPets = new VirtualPets("Implementation/pets.csv");
            Players players = new Players("Implementation/player_data.csv");
            Inventory inventory = new Inventory("Implementation/inventory.csv");
            Store store = new Store("Implementation/store.csv");
            GameProgress gameProgress = new GameProgress("Implementation/progress.csv");
            Commands commands = new Commands(players, virtualPets, inventory, store, gameProgress, controls);
            
            // Authenticate password
            if (commands.authenticateParentalControls(password)) {
                // Create parental controls panel if it doesn't exist
                if (parentalControlsPanel == null) {
                    parentalControlsPanel = new ParentalControlsGUI(controls, "player", this);
                    contentPanel.add(parentalControlsPanel, "PARENTAL_CONTROLS");
                }
                
                // Switch to the parental controls card
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "PARENTAL_CONTROLS");
            } else {
                JOptionPane.showMessageDialog(this, 
                                              "Incorrect password. Access denied.", 
                                              "Authentication Failed", 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Displays the load game screen where players can load their saved games.
     * Creates a new LoadGameGUI if one doesn't exist and switches to that view.
     */
    private void showLoadGame() {
        // Create load game panel if it doesn't exist
        if (loadGamePanel == null) {
            loadGamePanel = new LoadGameGUI(this);
            contentPanel.add(loadGamePanel, "LOAD_GAME");
        }
        
        // Switch to the load game card
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "LOAD_GAME");
    }
    
    /**
     * Returns to the main menu view and updates all playtime statistics.
     */
    public void showMainContent() {
        // When returning to main menu, update all playtime statistics
        playtimeTracker.forceUpdateAllStatistics();
        
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "MAIN_MENU");
    }

    /**
     * Opens the pet interaction window for a specific pet.
     * 
     * @param petId The unique identifier of the pet to interact with
     */
    public void openPetInteraction(String petId) {
        try {
            // Fixed player ID for all pets
            String playerId = "player";
            
            petInteractionWindow = new PetInteractionWindow(this, petId, playerId);
            contentPanel.add(petInteractionWindow, "PET_INTERACTION");
            
            // Switch to the pet interaction card
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "PET_INTERACTION");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error opening pet interaction window: " + e.getMessage());
        }
    }

    /**
     * Displays the inventory window for the specified player and pet.
     * 
     * @param playerId The unique identifier of the player
     * @param petId The unique identifier of the pet
     */
    public void showInventory(String playerId, String petId){
        // Always recreate the inventory panel to ensure the data is fresh
        InventoryManager invManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);
        inventoryPanel = new InventoryGUI(invManager, this, playerId, petId);
        contentPanel.add(inventoryPanel, "INVENTORY");
        
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "INVENTORY");
    }

    /**
     * Displays the store window where players can purchase items.
     * 
     * @param playerId The unique identifier of the player
     * @param petId The unique identifier of the pet
     */
    public void showStore(String playerId, String petId){
        // Always recreate the store panel to ensure it has fresh inventory data
        InventoryManager invManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);
        StoreManager sm = new StoreManager(playerId, "Implementation/store.csv", "Implementation/player_data.csv", invManager, petId);
        storePanel = new StoreGUI(sm, this, playerId, petId);
        contentPanel.add(storePanel, "STORE");
        
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "STORE");
    }

    /**
     * Displays an error dialog with the specified message.
     * 
     * @param message The error message to display in the dialog
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Creates and returns a styled button with the specified text.
     * 
     * @param text The text to display on the button
     * @return A styled JButton with the specified text
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50)); // Set fixed width but adjustable height
        return button;
    }

    /**
     * Main entry point for the Virtual Pet Game.
     * Creates and displays the main menu window using Swing's event dispatch thread.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}