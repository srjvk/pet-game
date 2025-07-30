package Implementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.sql.Timestamp;
import java.awt.image.BufferedImage;
import Implementation.PlayerInventory.InventoryManager;
import java.util.Map;
import java.util.HashMap;



import Implementation.PlayerInventory.*;

/**
 * A comprehensive GUI panel for interacting with virtual pets in the game.
 * This class provides a user interface for players to care for their virtual pets,
 * including feeding, playing, medical care, and other interactions.
 * 
 * The interface includes:
 * <ul>
 *   <li>Pet display with animated sprites that change based on pet state</li>
 *   <li>Vital statistics monitoring (hunger, happiness, health, sleep)</li>
 *   <li>Action buttons for various pet interactions</li>
 *   <li>Inventory management integration</li>
 *   <li>Playtime tracking for parental controls</li>
 * </ul>
 * 
 * The class integrates with various backend systems for:
 * <ul>
 *   <li>Virtual pet data management</li>
 *   <li>Inventory management</li>
 *   <li>Player statistics</li>
 *   <li>Parental controls</li>
 * </ul>
 * 
 * @see Implementation.VitalStats
 * @see Implementation.VirtualPets
 * @see Implementation.PlayerInventory.InventoryManager
 * @see Implementation.Players
 */
public class PetInteractionWindow extends JPanel {
    // Constants
    /** Width of the main window */
    private static final int WINDOW_WIDTH = 800;
    /** Height of the main window */
    private static final int WINDOW_HEIGHT = 600;
    /** Height of the stat bars */
    private static final int STAT_BAR_HEIGHT = 40;
    /** Width of action buttons */
    private static final int ACTION_BUTTON_WIDTH = 150;
    /** Height of action buttons */
    private static final int ACTION_BUTTON_HEIGHT = 50;

    // Components
    /** Main panel containing all other components */
    private JPanel mainPanel;
    /** Panel for displaying the pet sprite */
    private JPanel petDisplayPanel;
    /** Panel for displaying pet statistics */
    private JPanel statsPanel;
    /** Panel containing action buttons */
    private JPanel actionsPanel;
    /** Panel for displaying inventory items */
    private JPanel inventoryPanel;
    /** Label displaying the pet's name */
    private JLabel petNameLabel;
    /** Label displaying the pet's sprite */
    private JLabel petSpriteLabel;
    /** Label displaying the pet's speech bubble */
    private JLabel speechBubbleLabel;
    /** Label for hunger stat */
    private JLabel hungerLabel;
    /** Label for happiness stat */
    private JLabel happinessLabel;
    /** Label for health stat */
    private JLabel healthLabel;
    /** Label for sleep stat */
    private JLabel sleepLabel;
    /** Label for overall stat */
    private JLabel overallLabel;
    /** Progress bar for hunger stat */
    private JProgressBar hungerBar;
    /** Progress bar for happiness stat */
    private JProgressBar happinessBar;
    /** Progress bar for health stat */
    private JProgressBar healthBar;
    /** Progress bar for sleep stat */
    private JProgressBar sleepBar;
    /** Progress bar for overall stat */
    private JProgressBar overallBar;
    /** Label displaying food count */
    private JLabel foodCountLabel;
    /** Label displaying gift count */
    private JLabel giftCountLabel;
    /** Label displaying player score */
    private JLabel scoreLabel;

    /** Button for feeding the pet */
    private JButton feedButton;
    /** Button for playing with the pet */
    private JButton playButton;
    /** Button for taking pet to vet */
    private JButton vetButton;
    /** Button for pet workout */
    private JButton workoutButton;
    /** Button for giving medicine */
    private JButton medicineButton;
    /** Button for putting pet to sleep */
    private JButton sleepButton;
    /** Button for giving gifts */
    private JButton giftButton;
    /** Button for accessing inventory */
    private JButton inventoryButton;
    /** Button for accessing shop */
    private JButton shopButton;

    // Timers
    /** Timer for updating UI elements */
    private Timer uiUpdateTimer;
    /** Timer for updating pet sprite animation */
    private Timer spriteUpdateTimer;
    /** Timer for vet visit cooldown */
    private Timer vetCooldownTimer;
    /** Timer for play action cooldown */
    private Timer playCooldownTimer;

    /** Manager for virtual pets data */
    private VitalStats stats;
    /** Reference to the parent MainMenu window */
    private MainMenu parentFrame;
    /** Current pet's vital statistics */
    private int hunger, happiness, health, sleep, overall;
    /** Current food count */
    private int foodCount;
    /** Current toy count */
    private int toyCount;
    /** Current gift count */
    private int giftCount;
    /** Manager for virtual pets */
    private VirtualPets virtualPets;
    /** ID of the current pet */
    private String petId;
    /** ID of the current player */
    private String playerId;
    /** Path to the pet's icon */
    String iconPath;
    /** ID of the last used food item */
    private String lastUsedFoodId;
    /** ID of the last used toy */
    private String lastUsedToyId;
    /** ID of the last used gift */
    private String lastUsedGiftId;
    /** Manager for player data */
    private Players players;
    /** Manager for inventory operations */
    private InventoryManager invManager;

    // Cooldown Times
    /** Cooldown time for vet visits in seconds */
    private int vetCooldownSeconds = 120;
    /** Cooldown time for play actions in seconds */
    private int playCooldownSeconds = 60;

    // Parental Controls
    /** Manager for game commands */
    private Commands commands;
    /** Timer for tracking playtime */
    private Timer playtimeTimer;
    /** Start time of the current session */
    private long sessionStartTime;
    /** Minutes played in current session */
    private int sessionMinutesPlayed = 0;
    /** Seconds played in current session */
    private int sessionSecondsPlayed = 0;
    /** Flag for playtime limit warning */
    private boolean playtimeLimitWarningShown = false;
    /** Label displaying playtime status */
    private JLabel playtimeStatusLabel;
    /** Global playtime tracker */
    private PlaytimeTracker playtimeTracker;

    /**
     * Creates a new PetInteractionWindow initialized with the specified pet.
     *
     * @param parent   the parent MainMenu window that contains this panel
     * @param petId    the unique identifier of the pet to interact with
     * @param playerId the unique identifier of the player (will be ignored as we use a fixed player ID)
     */
    public PetInteractionWindow(MainMenu parent, String petId, String playerId) {
        this.parentFrame = parent;
        this.petId = petId;
        this.playerId = "player"; // Fixed player ID for all pets

        System.out.println(
                "Creating PetInteractionWindow for pet: " + petId + ", using fixed player ID: " + this.playerId);

        // Instantiate backend (pass the appropriate CSV file path)
        virtualPets = new VirtualPets("Implementation/pets.csv");

        // Initialize Commands with required dependencies
        players = new Players("Implementation/player_data.csv");
        players.setScore(playerId, String.valueOf(players.getScore(playerId))); // Set the score to 0 initially
        Inventory inventory = new Inventory("Implementation/inventory.csv");
        invManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);

        this.lastUsedFoodId = "1";
        invManager.setLastUsedFoodId("1");
        this.lastUsedGiftId = "5";
        invManager.setLastUsedGiftId("5");
        this.lastUsedToyId = "0";
        invManager.setLastUsedToyId("0");

        foodCount = invManager.getItemCount(lastUsedFoodId);
        toyCount = invManager.getItemCount(lastUsedToyId);
        giftCount = invManager.getItemCount(lastUsedGiftId);

        Store store = new Store("Implementation/store.csv");
        GameProgress gameProgress = new GameProgress("Implementation/progress.csv");
        ParentalControls parentalControls = new ParentalControls();

        // Initialize Commands for parental controls
        commands = new Commands(players, virtualPets, inventory, store, gameProgress, parentalControls);

        // Get the global playtime tracker instance
        playtimeTracker = PlaytimeTracker.getInstance();

        // Log creation of commands object for debugging
        System.out.println("Commands object created with parental controls: " + (parentalControls != null));

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        initializeComponents();
        layoutComponents();
        addEventListeners();
        addKeyBindings();
        // Update inventory counts
        updateInventory();

        // Add component listeners for dynamic sizing and visibility changes
        addComponentListener(new ComponentAdapter() {
            /**
             * Handles component resize events to update UI element sizes.
             * 
             * @param e the ComponentEvent containing resize information
             * @return void
             */
            public void componentResized(ComponentEvent e) {
                updateComponentSizes();
            }

            /**
             * Handles component show events to initialize timers.
             * 
             * @param e the ComponentEvent containing show information
             * @return void
             */
            public void componentShown(ComponentEvent e) {
                initializeTimers();
            }

            /**
             * Handles component hide events to clean up resources.
             * 
             * @param e the ComponentEvent containing hide information
             * @return void
             */
            public void componentHidden(ComponentEvent e) {
                cleanupAndReturnToMain();
            }
        });

        // Load initial stats from backend
        stats = virtualPets.getVitalStats(petId);

        // Set pet name from CSV
        petNameLabel.setText(virtualPets.getPetName(petId));

        updateStats();
        updatePetDisplay();

        // Initialize and start all timers
        initializeTimers();
    }

    /**
     * Initializes and starts all timers used for UI updates, sprite animations,
     * and cooldown tracking. This includes:
     * <ul>
     *   <li>UI update timer (every 5 seconds)</li>
     *   <li>Sprite update timer (every 500ms)</li>
     *   <li>Vet cooldown timer</li>
     *   <li>Play cooldown timer</li>
     * </ul>
     */
    private void initializeTimers() {
        // Stop any existing timers to prevent duplicates
        if (uiUpdateTimer != null)
            uiUpdateTimer.stop();
        if (spriteUpdateTimer != null)
            spriteUpdateTimer.stop();
        if (vetCooldownTimer != null)
            vetCooldownTimer.stop();
        if (playCooldownTimer != null)
            playCooldownTimer.stop();

        // Initialize and start UI update timer (every 5 seconds)
        uiUpdateTimer = new Timer(5000, new ActionListener() {
            /**
             * Handles UI update timer events to refresh stats and score.
             * 
             * @param e the ActionEvent containing timer information
             * @return void
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                getStats().updateStats();
                updateStats();
                updateScore();
            }
        });
        uiUpdateTimer.start();

        // Initialize and start sprite update timer (every 500ms)
        spriteUpdateTimer = new Timer(500, new ActionListener() {
            /**
             * Handles sprite update timer events to refresh pet display.
             * 
             * @param e the ActionEvent containing timer information
             * @return void
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePetDisplay();
            }
        });
        spriteUpdateTimer.start();

        // Load saved cooldown times
        int savedVetCooldown = virtualPets.getVetCooldown(petId);
        int savedPlayCooldown = virtualPets.getPlayCooldown(petId);

        // --- FIX: Update member variables regardless of whether timers start ---
        this.vetCooldownSeconds = savedVetCooldown;
        this.playCooldownSeconds = savedPlayCooldown;
        // --- End FIX ---

        // Start cooldown timers if there are saved cooldown times > 0
        if (savedVetCooldown > 0) {
            startVetCooldown(savedVetCooldown);
        } else {
            updateVetButtonText(); // Ensure button text is correct when cooldown is 0
        }

        if (savedPlayCooldown > 0) {
            startPlayCooldown(savedPlayCooldown);
        } else {
            updatePlayButtonText(); // Ensure button text is correct when cooldown is 0
        }
        
        // Initialize session tracking with global tracker immediately when pet window is opened
        // This ensures the timer works as soon as they click into the pet
        startPlaytimeTracking();
    }

    /**
     * Starts a cooldown timer for vet visits.
     *
     * @param remainingSeconds the number of seconds remaining in the cooldown
     */
    private void startVetCooldown(int remainingSeconds) {
        vetButton.setEnabled(false);
        vetCooldownSeconds = remainingSeconds;

        if (vetCooldownTimer != null) {
            vetCooldownTimer.stop();
        }

        vetCooldownTimer = new Timer(1000, new ActionListener() {
            /**
             * Handles vet cooldown timer events to update button state.
             * 
             * @param e the ActionEvent containing timer information
             * @return void
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                vetCooldownSeconds--;
                updateVetButtonText();

                if (vetCooldownSeconds <= 0) {
                    vetCooldownTimer.stop();
                    vetButton.setEnabled(true);
                    updateVetButtonText();
                }
            }
        });
        vetCooldownTimer.start();
    }

    /**
     * Starts a cooldown timer for play actions.
     *
     * @param remainingSeconds the number of seconds remaining in the cooldown
     */
    private void startPlayCooldown(int remainingSeconds) {
        playButton.setEnabled(false);
        playCooldownSeconds = remainingSeconds;

        if (playCooldownTimer != null) {
            playCooldownTimer.stop();
        }

        playCooldownTimer = new Timer(1000, new ActionListener() {
            /**
             * Handles play cooldown timer events to update button state.
             * 
             * @param e the ActionEvent containing timer information
             * @return void
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                playCooldownSeconds--;
                updatePlayButtonText();

                if (playCooldownSeconds <= 0) {
                    playCooldownTimer.stop();
                    playButton.setEnabled(true);
                    updatePlayButtonText();
                }
            }
        });
        playCooldownTimer.start();

        // Playtime tracking is now started in initializeTimers
        // No need to call startPlaytimeTracking() here
    }

    /**
     * Updates the pet's display sprite based on its current state.
     * The sprite changes based on conditions such as:
     * <ul>
     *   <li>Pet's health status</li>
     *   <li>Sleeping state</li>
     *   <li>Hunger level</li>
     *   <li>Happiness level</li>
     * </ul>
     */
    private void updatePetDisplay() {
        // Get pet type from CSV
        String petType = virtualPets.getPetType(petId);
        String petState;

        // Determine pet state based on conditions
        if (getStats().isDead()) {
            petState = "dead";
        } else if (getStats().isSleeping()) {
            petState = "sleep";
        } else if (getStats().isCritical("hunger")) {
            petState = "hungry";
        } else if (getStats().isCritical("happiness")) {
            petState = "angry";
        } else {
            petState = "default";
        }

        // Load pet sprite
        if (iconPath == null) {
            iconPath = petType.toLowerCase() + "/" + petState + "_1.png";
        } else {
            if (iconPath.equals(petType.toLowerCase() + "/" + petState + "_1.png")) {
                iconPath = petType.toLowerCase() + "/" + petState + "_2.png";
            } else {
                iconPath = petType.toLowerCase() + "/" + petState + "_1.png";
            }
        }
        ImageIcon petIcon = loadPetIcon(iconPath);
        if (petIcon != null) {
            petSpriteLabel.setIcon(petIcon);
            petSpriteLabel.setText("");
        } else {
            createPlaceholderSprite();
        }
    }

    /**
     * Loads and resizes a pet icon from the icons directory.
     *
     * @param filename the name of the icon file to load
     * @return the loaded and resized ImageIcon, or null if loading fails
     */
    private ImageIcon loadPetIcon(String filename) {
        // Try to load from icons directory
        ImageIcon icon = new ImageIcon("Implementation/Icons/" + filename);
        if (icon.getIconWidth() <= 0) {
            return null; // Image not loaded properly
        }

        // Resize icon to fit well in the UI
        Image img = icon.getImage();
        String petType = virtualPets.getPetType(petId);
        if (petType.equals("dog")) {
            Image resizedImg = img.getScaledInstance(400, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImg);
        } else if (petType.equals("cat")) {
            Image resizedImg = img.getScaledInstance(450, 300, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImg);
        } else {
            Image resizedImg = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImg);
        }
    }

    /**
     * Creates a placeholder sprite when the pet icon cannot be loaded.
     */
    private void createPlaceholderSprite() {
        petSpriteLabel.setIcon(null);
        petSpriteLabel.setPreferredSize(new Dimension(200, 200));
        petSpriteLabel.setOpaque(true);
    }

    /**
     * Initializes all UI components including panels, labels, buttons,
     * and progress bars. Sets up the layout and appearance of each component.
     */
    private void initializeComponents() {
        // Initialize panels with responsive layout managers
        mainPanel = new JPanel(new BorderLayout());
        petDisplayPanel = new JPanel(new BorderLayout());
        statsPanel = new JPanel(new GridBagLayout()); // For better control
        actionsPanel = new JPanel(new GridLayout(2, 3, 20, 10));
        inventoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Set up pet display area
        petDisplayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Create pet name and sprite labels
        petNameLabel = new JLabel("Pet Name", SwingConstants.CENTER);
        petNameLabel.setFont(new Font("Arial", Font.BOLD, 24));

        petSpriteLabel = new JLabel();
        petSpriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        petSpriteLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Create speech bubble
        speechBubbleLabel = new JLabel("\"I'm so hungry!\" - changes based on mood", SwingConstants.CENTER);
        speechBubbleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        speechBubbleLabel.setBackground(Color.WHITE);
        speechBubbleLabel.setOpaque(true);

        // Create playtime status label
        playtimeStatusLabel = new JLabel("Playtime: 0 min", SwingConstants.CENTER);
        playtimeStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playtimeStatusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        playtimeStatusLabel.setBackground(new Color(220, 240, 255));
        playtimeStatusLabel.setOpaque(true);

        scoreLabel = new JLabel("Score: " + players.getScore(playerId), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(new Color(240, 240, 240));

        // Create stat bars and labels
        hungerLabel = new JLabel("Hunger:", SwingConstants.RIGHT);
        happinessLabel = new JLabel("Happiness:", SwingConstants.RIGHT);
        healthLabel = new JLabel("Health:", SwingConstants.RIGHT);
        sleepLabel = new JLabel("Sleep:", SwingConstants.RIGHT);
        overallLabel = new JLabel("Overall:", SwingConstants.RIGHT);

        hungerBar = createProgressBar(50);
        happinessBar = createProgressBar(50);
        healthBar = createProgressBar(50);
        sleepBar = createProgressBar(50);
        overallBar = createProgressBar(50);

        // Create action buttons exactly matching the mockup
        feedButton = createActionButton("Feed", "🍔");
        playButton = createActionButton("Play", "🎮");
        vetButton = createActionButton("Vet", "🏥");
        workoutButton = createActionButton("Workout", "💪");
        medicineButton = createActionButton("Medicine", "💊");
        sleepButton = createActionButton("Sleep", "💤");
        giftButton = createActionButton("Gift", "🎁");
        inventoryButton = createActionButton("Inventory", "🎒");
        shopButton = createActionButton("Shop", "🛒");


        // Create inventory counters with text icons
        //foodCountLabel = new JLabel("🍔 " + foodCount, SwingConstants.LEFT);
        //giftCountLabel = new JLabel("🎁 " + giftCount, SwingConstants.LEFT);

        // Create inventory panel
//        JPanel countersPanel = new JPanel(new GridLayout(3, 1, 0, 5));
//        countersPanel.add(foodCountLabel);
//        countersPanel.add(giftCountLabel);
//        countersPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//        inventoryPanel.add(countersPanel);

        // Add stat components to statsPanel using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        statsPanel.add(hungerLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        statsPanel.add(hungerBar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        statsPanel.add(happinessLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        statsPanel.add(happinessBar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        statsPanel.add(healthLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        statsPanel.add(healthBar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        statsPanel.add(sleepLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        statsPanel.add(sleepBar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        statsPanel.add(overallLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        statsPanel.add(overallBar, gbc);

        // Add action buttons to actionsPanel
        actionsPanel.add(feedButton);
        actionsPanel.add(playButton);
        actionsPanel.add(vetButton);
        actionsPanel.add(workoutButton);
        actionsPanel.add(medicineButton);
        actionsPanel.add(sleepButton);
        actionsPanel.add(giftButton);
        actionsPanel.add(inventoryButton);
        actionsPanel.add(shopButton);
    }

    /**
     * Creates a custom progress bar with rounded corners and text display.
     *
     * @param value the initial value of the progress bar
     * @return a customized JProgressBar instance
     */
    private JProgressBar createProgressBar(int value) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(String.format("%d%%", value));
        bar.setPreferredSize(new Dimension(300, STAT_BAR_HEIGHT));
        bar.setBackground(Color.LIGHT_GRAY);
        bar.setForeground(new Color(100, 180, 100));

        // Customize the progress bar appearance
        bar.setFont(new Font("Arial", Font.BOLD, 14));
        bar.setBorderPainted(false);
        bar.setOpaque(true);

        // Add a custom UI to improve the appearance
        bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Get the progress bar dimensions
                int w = bar.getWidth();
                int h = bar.getHeight();

                // Draw the background
                g2d.setColor(bar.getBackground());
                g2d.fillRoundRect(0, 0, w, h, 10, 10);

                // Draw the progress
                int progressWidth = (int) (w * bar.getPercentComplete());
                if (progressWidth > 0) {
                    g2d.setColor(bar.getForeground());
                    g2d.fillRoundRect(0, 0, progressWidth, h, 10, 10);
                }

                // Draw the text
                if (bar.isStringPainted()) {
                    FontMetrics fm = g2d.getFontMetrics(bar.getFont());
                    String text = bar.getString();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getHeight();

                    // Calculate text position to align with progress
                    int x;
                    if (progressWidth > textWidth + 10) {
                        // If there's enough space in the filled area, place text at the end of the fill
                        x = progressWidth - textWidth - 10;
                    } else {
                        // If not enough space, place text at the start of the fill
                        x = progressWidth + 10;
                    }
                    int y = (h + textHeight) / 2;

                    // Draw text shadow for better visibility
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.drawString(text, x + 1, y + 1);

                    // Draw the actual text
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(text, x, y);
                }

                g2d.dispose();
            }
        });

        return bar;
    }

    /**
     * Creates an action button with an icon and text.
     *
     * @param text the text to display on the button
     * @param iconText the emoji or icon text to display
     * @return a customized JButton instance
     */
    private JButton createActionButton(String text, String iconText) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(5, 0));
        button.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));

        button.setActionCommand(text);

        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        button.add(iconPanel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        return button;
    }

    /**
     * Lays out all components in the window using appropriate layout managers.
     * Organizes the pet display, stats panel, action buttons, and inventory display.
     */
    private void layoutComponents() {
        // Pet display area
        JPanel petContentPanel = new JPanel(new BorderLayout());
        petContentPanel.add(petSpriteLabel, BorderLayout.CENTER);
        petContentPanel.add(petNameLabel, BorderLayout.SOUTH);
        petDisplayPanel.add(petContentPanel, BorderLayout.CENTER);
        petDisplayPanel.add(speechBubbleLabel, BorderLayout.NORTH);

        String petType = virtualPets.getPetType(petId);
        if (petType.equals("dog")) {
            petContentPanel.setBackground(new Color(68, 0, 100)); // Purple for Dog
        } else if (petType.equals("cat")) {
            petContentPanel.setBackground(new Color(169, 169, 169)); // Grey for Cat
        } else {
            petContentPanel.setBackground(new Color(34, 139, 34)); // Green for Dragon
        }

        petNameLabel.setOpaque(true);
        petNameLabel.setBackground(Color.WHITE);

        // Stats panel with padding
        JPanel statsPaddedPanel = new JPanel(new BorderLayout());
        statsPaddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPaddedPanel.add(statsPanel, BorderLayout.CENTER);

        // Create centered content panel for pet display and stats
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel petContainer = new JPanel(new BorderLayout());
        petContainer.add(petDisplayPanel, BorderLayout.CENTER);
        contentPanel.add(petContainer, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        contentPanel.add(statsPaddedPanel, gbc);

        JPanel centeringPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centeringPanel.add(contentPanel, gbc);

        // Bottom panel for actions and inventory
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(inventoryPanel);
        bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);

        JPanel actionsContainer = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        actionsContainer.add(actionsPanel, gbc);
        bottomPanel.add(actionsContainer, BorderLayout.CENTER);

        // Add back to main menu button on the right
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> returnToMainMenu());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(backButton);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(centeringPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add playtime status label and score to the top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        scorePanel.add(scoreLabel);

        topPanel.add(playtimeStatusLabel, BorderLayout.CENTER);
        topPanel.add(scorePanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Updates component sizes based on the current window dimensions.
     * Ensures responsive layout when the window is resized.
     */
    private void updateComponentSizes() {
        int width = getWidth();
        int height = getHeight();

        int barWidth = Math.min((int) (width / 2.5), 1920);
        hungerBar.setPreferredSize(new Dimension(barWidth, STAT_BAR_HEIGHT));
        happinessBar.setPreferredSize(new Dimension(barWidth, STAT_BAR_HEIGHT));
        healthBar.setPreferredSize(new Dimension(barWidth, STAT_BAR_HEIGHT));
        sleepBar.setPreferredSize(new Dimension(barWidth, STAT_BAR_HEIGHT));
        overallBar.setPreferredSize(new Dimension(barWidth, STAT_BAR_HEIGHT));

        int petDisplayWidth = Math.min(width / 3, 400);
        int petDisplayHeight = (int) (petDisplayWidth * 0.75);
        petDisplayHeight = Math.min(petDisplayHeight, height / 2);
        petDisplayPanel.setPreferredSize(new Dimension(petDisplayWidth, petDisplayHeight));

        int baseFontSize = Math.max(12, width / 80);
        petNameLabel.setFont(new Font("Arial", Font.BOLD, baseFontSize + 4));

        revalidate();
        repaint();
    }

    /**
     * Adds event listeners to all action buttons.
     * Handles user interactions for feeding, playing, vet visits, etc.
     */
    private void addEventListeners() {
        // For each action button (Feed, Play, Vet, Workout, Sleep, Gift, Inventory,
        // Shop)
        for (JComponent comp : getActionButtons()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String command = e.getActionCommand();
                        if ("Feed".equals(command)) {
                            feed();
                        } else if ("Play".equals(command)) {
                            play();
                        } else if ("Vet".equals(command)) {
                            takeToVet();
                        } else if ("Workout".equals(command)) {
                            workout();
                        } else if ("Medicine".equals(command)) {
                            giveMedicine();
                        } else if ("Sleep".equals(command)) {
                            sleep();
                        } else if ("Gift".equals(command)) {
                            giveGift();
                        } else if ("Inventory".equals(command)) {
                            parentFrame.showInventory(playerId, petId);
                        } else if ("Shop".equals(command)) {
                            parentFrame.showStore(playerId, petId);
                        }
                    }
                });
            }
        }
    }
    /**
     * Adds key bindings to the pet interaction screen, support relevant functionalities tied to the core game.
     */
    private void addKeyBindings() {
        // Get the input and action maps for when this panel is in focus (or in focused window)
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        // Bind 'f' for feed
        inputMap.put(KeyStroke.getKeyStroke('f'), "feedAction");
        actionMap.put("feedAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed();
            }
        });

        // Bind 'v' for vet
        inputMap.put(KeyStroke.getKeyStroke('v'), "vetAction");
        actionMap.put("vetAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeToVet();
            }
        });

        // Bind 'w' for workout
        inputMap.put(KeyStroke.getKeyStroke('w'), "workoutAction");
        actionMap.put("workoutAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workout();
            }
        });

        // Bind 'z' for sleep
        inputMap.put(KeyStroke.getKeyStroke('z'), "sleepAction");
        actionMap.put("sleepAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sleep();
            }
        });

        // Bind 'm' for medicine
        inputMap.put(KeyStroke.getKeyStroke('m'), "medAction");
        actionMap.put("medAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                giveMedicine();
            }
        });

        // Bind 'p' for play
        inputMap.put(KeyStroke.getKeyStroke('p'), "playAction");
        actionMap.put("playAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });

        // Bind 'g' for gift
        inputMap.put(KeyStroke.getKeyStroke('g'), "giftAction");
        actionMap.put("giftAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                giveGift();
            }
        });

        // Bind Tab for inventory
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "inventoryAction");
        actionMap.put("inventoryAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showInventory(playerId, petId);
            }
        });

        // Bind 's' for shop
        inputMap.put(KeyStroke.getKeyStroke('s'), "storeAction");
        actionMap.put("storeAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showStore(playerId, petId);
            }
        });

    }
    /**
     * Returns a list of all action buttons in the window.
     *
     * @return a list of JComponent instances representing action buttons
     */
    private java.util.List<JComponent> getActionButtons() {
        java.util.List<JComponent> buttons = new ArrayList<>();
        if (actionsPanel != null) {
            for (Component comp : actionsPanel.getComponents()) {
                if (comp instanceof JComponent) {
                    buttons.add((JComponent) comp);
                }
            }
        }
        return buttons;
    }

    /**
     * Gets the current vital stats of the pet.
     *
     * @return the VitalStats instance containing the pet's current stats
     */
    private VitalStats getStats() {
        return this.stats;
    }

    /**
     * Updates the UI progress bars and labels using the backend stats.
     * Also updates the speech bubble and checks for critical conditions.
     */
    private void updateStats() {
        hunger = stats.getHunger();
        virtualPets.setHunger(petId, hunger);
        happiness = stats.getHappiness();
        virtualPets.setHappiness(petId, happiness);
        health = stats.getHealth();
        virtualPets.setHealth(petId, health);
        sleep = stats.getSleep();
        virtualPets.setSleep(petId, sleep);
        overall = (hunger + happiness + health + sleep) / 4;

        hungerBar.setValue(hunger);
        hungerBar.setString(hunger + "%");

        happinessBar.setValue(happiness);
        happinessBar.setString(happiness + "%");

        healthBar.setValue(health);
        healthBar.setString(health + "%");

        sleepBar.setValue(sleep);
        sleepBar.setString(sleep + "%");

        overallBar.setValue(overall);
        overallBar.setString(overall + "%");

        setCritical();
        updateSpeechBubble();
        if (getStats().isDead()) {
            cleanupAndReturnToMain();
        }
    }

    /**
     * Updates the color of progress bars based on critical conditions.
     * Red for critical values, green for normal values.
     */
    private void setCritical() {
        if (getStats().isCritical("hunger")) {
            hungerBar.setForeground(new Color(235, 115, 116));
        } else {
            hungerBar.setForeground(new Color(100, 180, 100));
        }
        if (getStats().isCritical("happiness")) {
            happinessBar.setForeground(new Color(235, 115, 116));
        } else {
            happinessBar.setForeground(new Color(100, 180, 100));
        }
        if (getStats().isCritical("sleep")) {
            sleepBar.setForeground(new Color(235, 115, 116));
        } else {
            sleepBar.setForeground(new Color(100, 180, 100));
        }
        if (getStats().isCritical("health")) {
            healthBar.setForeground(new Color(235, 115, 116));
        } else {
            healthBar.setForeground(new Color(100, 180, 100));
        }
        if (getStats().isCritical("overall")) {
            overallBar.setForeground(new Color(235, 115, 116));
        } else {
            overallBar.setForeground(new Color(100, 180, 100));
        }
    }

    /**
     * Updates the pet's speech bubble text based on its current state.
     */
    private void updateSpeechBubble() {
        if (health <= 0) {
            speechBubbleLabel.setText("I'm dead...");
        } else if (hunger < 30) {
            speechBubbleLabel.setText("I'm so hungry!");
        } else if (happiness < 30) {
            speechBubbleLabel.setText("I'm feeling sad...");
        } else if (health < 30) {
            speechBubbleLabel.setText("I don't feel so good...");
        } else if (sleep < 30) {
            speechBubbleLabel.setText("I'm tired...");
        } else {
            speechBubbleLabel.setText("I'm feeling great!");
        }
    }

    /**
     * Handles the feeding action when the feed button is clicked.
     * Shows a food selection dialog and applies the effects of the selected food.
     */
    private void feed() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;
        if (angryBlockAction())
            return;

        if (hunger >= 100) {
            JOptionPane.showMessageDialog(this, "Pet is not hungry.", "Feeding", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create an inventory manager to get the actual inventory items
        InventoryManager inventoryManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);

        // Show food selection dialog with items from inventory
        FoodSelectionDialog foodDialog = new FoodSelectionDialog(SwingUtilities.getWindowAncestor(this),
                inventoryManager);
        // Get selected inventory item position (row, col)
        int[] position = foodDialog.getSelectedItemPosition();

        if (position != null) {
            int row = position[0];
            int col = position[1];
            String[] selectedItem = inventoryManager.getItem(row, col);

            // Get food name
            String foodName = selectedItem[1];

            // Calculate effects based on food type
            int hungerValue = calculateHungerValue(foodName);
            int healthValue = calculateHealthValue(foodName);

            // Apply food effects to pet stats
            stats.setHunger(Math.min(100, hunger + hungerValue));
            virtualPets.setHunger(petId, stats.getHunger());

            // Some foods can affect health too
            if (healthValue > 0) {
                stats.setHealth(Math.min(100, health + healthValue));
                virtualPets.setHealth(petId, stats.getHealth());
            }

            // Decrease the item's quantity in inventory
            inventoryManager.editQuantity(row, col, -1);
            inventoryManager.saveInventory();

            // Update UI
            foodCount--;
            updateInventory();
            updateStats();
            players.setScore(playerId, Integer.toString(players.getScore(playerId) + 1));
            updateScore();
            // Show feeding message
            JOptionPane.showMessageDialog(this,
                    "You fed " + virtualPets.getPetName(petId) + " a " + foodName + "!",
                    "Feeding",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Calculates the hunger value increase for a given food item.
     *
     * @param foodName the name of the food item
     * @return the amount of hunger value increase
     */
    private int calculateHungerValue(String foodName) {
        // Assign hunger values based on food name
        switch (foodName.toLowerCase()) {
            case "feast":
                return 20;
            case "bandaid": // Not actually food but handle it anyway
                return 5;
            case "toys": // Not actually food but handle it anyway
                return 5;
            default:
                return 10; // Default value for unrecognized food
        }
    }

    /**
     * Calculates the health value increase for a given food item.
     *
     * @param foodName the name of the food item
     * @return the amount of health value increase
     */
    private int calculateHealthValue(String foodName) {
        // Assign health values based on food name
        switch (foodName.toLowerCase()) {
            case "feast":
                return 10;
            case "bandaid": // Has healing properties
                return 15;
            case "toys": // Toys aren't food but make pets happy
                return 5;
            default:
                return 0; // Default value for unrecognized food
        }
    }

    /**
     * Handles the play action when the play button is clicked.
     * Increases happiness and starts a cooldown timer.
     */
    private void play() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;

        if (happiness >= 100) {
            JOptionPane.showMessageDialog(this, "Pet is already very happy!", "Play", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        stats.setHappiness(Math.min(100, happiness + 10));
        virtualPets.setHappiness(petId, stats.getHappiness());

        toyCount--;
        updateInventory();
        updateStats();

        // Start cooldown timer
        startPlayCooldown(60);
        // Start play cooldown
        startPlayCooldown(60);
        players.setScore(playerId, Integer.toString(players.getScore(playerId) + 1));
        updateScore();
    }

    /**
     * Handles the workout action when the workout button is clicked.
     * Increases health but decreases hunger.
     */
    private void workout() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;
        if (angryBlockAction())
            return;

        if (health >= 100) {
            JOptionPane.showMessageDialog(this, "Pet is already at max health.", "Workout",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        stats.setHealth(Math.min(100, health + 10));
        virtualPets.setHealth(petId, stats.getHealth());

        // Workout makes pet hungrier
        stats.setHunger(Math.max(0, hunger - 5));
        virtualPets.setHunger(petId, stats.getHunger());

        updateInventory();
        updateStats();
        players.setScore(playerId, Integer.toString(players.getScore(playerId) + 1));
        updateScore();
    }

    /**
     * Handles the sleep action when the sleep button is clicked.
     * Gradually recovers the pet's sleep using a timer.
     */
    private void sleep() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;
        if (angryBlockAction())
            return;

        // Create a Timer to update sleep gradually every 1000ms (1 second)
        Timer sleepTimer = new Timer(1000, null);
        sleepTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stats.recoverSleep();
                stats.updateStats();
                updateStats(); // Update the UI progress bars

                // If pet's sleep has fully recovered, stop the timer and notify the user
                if (!stats.isSleeping() && !getStats().isDead()) {
                    sleepTimer.stop();
                    JOptionPane.showMessageDialog(PetInteractionWindow.this,
                            "Pet is fully rested!", "Sleep", JOptionPane.INFORMATION_MESSAGE);
                    // Optionally re-enable user actions here.
                }
            }
        });
        sleepTimer.start();
        players.setScore(playerId, Integer.toString(players.getScore(playerId) + 1));
        updateScore();
    }

    /**
     * Checks if the pet is sleeping and blocks actions if true.
     *
     * @return true if the pet is sleeping, false otherwise
     */
    private boolean sleepingBlockAction() {
        if (getStats().isSleeping()) {
            JOptionPane.showMessageDialog(this, "Pet is sleeping! Wait until they're fully recovered.",
                    "Uh-oh! Something went wrong", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Checks if the pet is dead and blocks actions if true.
     *
     * @return true if the pet is dead, false otherwise
     */
    private boolean deadBlockAction() {
        if (getStats().isDead()) {
            JOptionPane.showMessageDialog(this, "Your pet has passed away. Please return to the main menu.",
                    "Pet Deceased", JOptionPane.INFORMATION_MESSAGE);
            cleanupAndReturnToMain();
            return true;
        }
        return false;
    }

    /**
     * Checks if the pet is angry and blocks actions if true.
     *
     * @return true if the pet is angry, false otherwise
     */
    private boolean angryBlockAction() {
        if (getStats().isCritical("happiness")) {
            JOptionPane.showMessageDialog(this,
                    "Your pet is too angry to do that right now! Try playing with them first.", "Pet is Angry",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Updates the inventory display with current item counts.
     */
    private void updateInventory() {
        // Reload inventory to get updated counts
        invManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);
        
        foodCount = invManager.getItemCount(lastUsedFoodId);
        toyCount = invManager.getItemCount(lastUsedToyId);
        giftCount = invManager.getItemCount(lastUsedGiftId);
    }

    /**
     * Cleans up resources and returns to the main menu.
     * Stops all timers and saves the game state.
     */
    private void cleanupAndReturnToMain() {
        // Stop all timers
        if (uiUpdateTimer != null) {
            uiUpdateTimer.stop();
        }
        if (spriteUpdateTimer != null) {
            spriteUpdateTimer.stop();
        }
        if (vetCooldownTimer != null) {
            vetCooldownTimer.stop();
        }
        if (playCooldownTimer != null) {
            playCooldownTimer.stop();
        }
        if (playtimeTimer != null) {
            playtimeTimer.stop();
        }

        // End tracking session in the global tracker
        if (playtimeTracker != null) {
            playtimeTracker.endSession(playerId);
        }

        // Save the current game state and cooldown times
        saveGame();
        virtualPets.setVetCooldown(petId, vetCooldownSeconds);
        virtualPets.setPlayCooldown(petId, playCooldownSeconds);
    }

    /**
     * Returns to the main menu, saving the game state and ending the playtime session.
     */
    private void returnToMainMenu() {
        // Stop timers and save state
        cleanupAndReturnToMain();

        // Save game state
        saveGame();

        // Navigate back to main menu
        parentFrame.showMainContent();
    }

    /**
     * Handles the vet visit action when the vet button is clicked.
     * Increases health and starts a cooldown timer.
     */
    private void takeToVet() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;
        if (angryBlockAction())
            return;

        if (health >= 100) {
            JOptionPane.showMessageDialog(this, "Pet is already at max health.", "Vet Visit",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        stats.setHealth(Math.min(100, health + 15));
        virtualPets.setHealth(petId, stats.getHealth());

        // Start vet cooldown
        startVetCooldown(120);
        updateStats();
        players.setScore(playerId, Integer.toString(players.getScore(playerId) - 1));
        updateScore();
    }

    /**
     * Updates the vet button text to show remaining cooldown time.
     */
    private void updateVetButtonText() {
        if (vetCooldownSeconds > 0) {
            int minutes = vetCooldownSeconds / 60;
            int seconds = vetCooldownSeconds % 60;
            // Update the text label while preserving the icon
            for (Component comp : vetButton.getComponents()) {
                if (comp instanceof JLabel && !((JLabel) comp).getText().equals("🏥")) {
                    JLabel textLabel = (JLabel) comp;
                    textLabel.setText(String.format("Vet (%02d:%02d)", minutes, seconds));
                    textLabel.setForeground(Color.GRAY); // Grey out the text
                    break;
                }
            }
        } else {
            // Update the text label while preserving the icon
            for (Component comp : vetButton.getComponents()) {
                if (comp instanceof JLabel && !((JLabel) comp).getText().equals("🏥")) {
                    JLabel textLabel = (JLabel) comp;
                    textLabel.setText("Vet");
                    textLabel.setForeground(Color.BLACK); // Return to normal color
                    break;
                }
            }
        }
    }

    /**
     * Handles the gift action when the gift button is clicked.
     * Shows a gift selection dialog and applies the effects of the selected gift.
     */
    private void giveGift() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;

        System.out.println("Gift count: " + giftCount);

        // Create an inventory manager to get the actual inventory items
        InventoryManager inventoryManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);
        
        // Check if any gift items exist in inventory instead of just checking giftCount
        boolean hasAnyGifts = false;
        for (int row = 0; row < inventoryManager.getGridSize(); row++) {
            for (int col = 0; col < inventoryManager.getGridSize(); col++) {
                String[] item = inventoryManager.getItem(row, col);
                if (item != null && item.length >= 4) {
                    String quantity = item[2];
                    String category = item[3];
                    if (category != null && category.equalsIgnoreCase("gift") &&
                            quantity != null && !quantity.isEmpty() && Integer.parseInt(quantity) > 0) {
                        hasAnyGifts = true;
                        break;
                    }
                }
            }
            if (hasAnyGifts) break;
        }
        
        if (!hasAnyGifts) {
            JOptionPane.showMessageDialog(this, "You don't have any gifts left!", "Gift",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (happiness >= 100) {
            JOptionPane.showMessageDialog(this, "Pet is already very happy!", "Gift", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show gift selection dialog with items from inventory for gifting
        ToyGiftSelectionDialog giftDialog = new ToyGiftSelectionDialog(SwingUtilities.getWindowAncestor(this),
                inventoryManager);
        // Get selected inventory item position (row, col)
        int[] position = giftDialog.getSelectedItemPosition();

        if (position != null) {
            int row = position[0];
            int col = position[1];
            String[] selectedItem = inventoryManager.getItem(row, col);

            // Get gift name
            String giftName = selectedItem[1];

            // Calculate effects based on gift type
            int happinessValue = calculateGiftHappinessValue(giftName);

            // Apply gift effects to pet stats
            stats.setHappiness(Math.min(100, happiness + happinessValue));
            virtualPets.setHappiness(petId, stats.getHappiness());

            inventoryManager.editQuantity(row, col, -1);
            inventoryManager.saveInventory();

            String giftId = selectedItem[0];
            lastUsedGiftId = giftId;
            invManager.setLastUsedGiftId(giftId);

            updateInventory();
            updateStats();
            players.setScore(playerId, Integer.toString(players.getScore(playerId) + 1));
            updateScore();
            // Show gift message
            JOptionPane.showMessageDialog(this,
                    virtualPets.getPetName(petId) + " loved the " + giftName + " gift!",
                    "Gift",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Calculates the happiness value increase for a given gift item.
     *
     * @param giftName the name of the gift item
     * @return the amount of happiness value increase
     */
    private int calculateGiftHappinessValue(String giftName) {
        // Assign happiness values based on gift name
        switch (giftName.toLowerCase()) {
            case "toys":
                return 15;
            case "mouse":
                return 20;
            case "tennis":
                return 12;
            default:
                return 10;
        }
    }

    /**
     * Updates the play button text to show remaining cooldown time.
     */
    private void updatePlayButtonText() {
        if (playCooldownSeconds > 0) {
            int minutes = playCooldownSeconds / 60;
            int seconds = playCooldownSeconds % 60;
            // Update the text label while preserving the icon
            for (Component comp : playButton.getComponents()) {
                if (comp instanceof JLabel && !((JLabel) comp).getText().equals("🎮")) {
                    JLabel textLabel = (JLabel) comp;
                    textLabel.setText(String.format("Play (%02d:%02d)", minutes, seconds));
                    textLabel.setForeground(Color.GRAY); // Grey out the text
                    break;
                }
            }
        } else {
            // Update the text label while preserving the icon
            for (Component comp : playButton.getComponents()) {
                if (comp instanceof JLabel && !((JLabel) comp).getText().equals("🎮")) {
                    JLabel textLabel = (JLabel) comp;
                    textLabel.setText("Play");
                    textLabel.setForeground(Color.BLACK); // Return to normal color
                    break;
                }
            }
        }
    }

    /**
     * Updates the player's score display.
     */
    private void updateScore() {
        int score = players.getScore(playerId);
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Saves the current game state to the backend.
     */
    public void saveGame() {
        virtualPets.updateVitalStats(petId, stats);
        virtualPets.updateLastAccessed(petId);
    }

    /**
     * Updates the current pet and player IDs, reloading necessary data.
     *
     * @param newPetId the new pet ID to switch to
     * @param newPlayerId the new player ID to switch to
     */
    public void updatePetAndPlayer(String newPetId, String newPlayerId) {
        this.petId = newPetId;
        this.playerId = newPlayerId;

        // Update the virtual pets instance
        virtualPets = new VirtualPets("Implementation/pets.csv");

        // Load the current stats for the new pet
        stats = virtualPets.getVitalStats(petId);

        // Update the pet name
        petNameLabel.setText(virtualPets.getPetName(petId));

        // Update the display
        updateStats();
        updatePetDisplay();

        // Restart timers with current state
        initializeTimers();
    }

    /**
     * Starts tracking playtime for parental controls.
     * Initializes the playtime timer and updates the status display.
     */
    private void startPlaytimeTracking() {
        // Start tracking with the global tracker
        playtimeTracker.startSession(playerId, commands);

        // Start local timer for UI updates (every second instead of 5 seconds)
        playtimeTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the display first
                updatePlaytimeStatus();

                // Directly check for limits and enforce them immediately
                boolean canContinue = playtimeTracker.checkPlaytimeLimits(playerId, PetInteractionWindow.this);
                if (!canContinue) {
                    // Immediately stop the timer to prevent multiple enforcements
                    playtimeTimer.stop();
                    
                    // Use SwingUtilities.invokeLater to avoid timer thread issues
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("ENFORCING PLAYTIME LIMIT - EXITING GAME");
                        enforcePlaytimeLimit();
                    });
                }
            }
        });
        playtimeTimer.start();

        // Update the initial status display
        updatePlaytimeStatus();
    }

    /**
     * Updates the playtime status display with current session information.
     */
    private void updatePlaytimeStatus() {
        if (playtimeTracker != null) {
            String statusText = playtimeTracker.getPlaytimeStatusText(playerId);
            playtimeStatusLabel.setText(statusText);
            
            // The limit check is already done in the timer action listener,
            // so we don't need to check it again here to avoid duplicate enforcements
        } else {
            playtimeStatusLabel.setText("Playtime: Not tracking");
        }
    }

    /**
     * Enforces the playtime limit by saving the game and returning to main menu.
     */
    private void enforcePlaytimeLimit() {
        // Show limit reached message
        JOptionPane.showMessageDialog(this,
                "You've reached your daily playtime limit. The game will now save and exit.",
                "Playtime Limit Reached",
                JOptionPane.INFORMATION_MESSAGE);

        // Save the game and return to main menu
        cleanupAndReturnToMain();
        
        // Make sure to end the session and exit to main menu
        playtimeTracker.endSession(playerId);
        
        // Navigate back to main menu
        if (parentFrame != null) {
            parentFrame.showMainContent();
        }
    }

    /**
     * Handles the medicine action when the medicine button is clicked.
     * Shows a medicine selection dialog and applies the effects of the selected medicine.
     */
    private void giveMedicine() {
        if (deadBlockAction())
            return;
        if (sleepingBlockAction())
            return;
        if (angryBlockAction())
            return;

        if (health >= 100) {
            JOptionPane.showMessageDialog(this, "Pet is already at max health.", "Medicine",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create an inventory manager to get the actual inventory items
        InventoryManager inventoryManager = new InventoryManager(playerId, "Implementation/inventory.csv", petId);

        // Show medicine selection dialog with items from inventory
        MedicineSelectionDialog medicineDialog = new MedicineSelectionDialog(SwingUtilities.getWindowAncestor(this),
                inventoryManager);
        // Get selected inventory item position (row, col)
        int[] position = medicineDialog.getSelectedItemPosition();

        if (position != null) {
            int row = position[0];
            int col = position[1];
            String[] selectedItem = inventoryManager.getItem(row, col);

            // Get medicine name
            String medicineName = selectedItem[1];

            // Calculate effects based on medicine type
            int healthValue = calculateMedicineHealthValue(medicineName);

            // Apply medicine effects to pet stats
            stats.setHealth(Math.min(100, health + healthValue));
            virtualPets.setHealth(petId, stats.getHealth());

            // Decrease the item's quantity in inventory
            inventoryManager.editQuantity(row, col, -1);
            inventoryManager.saveInventory();

            // Update UI
            updateInventory();
            updateStats();

            players.setScore(playerId, Integer.toString(players.getScore(playerId) - 1));
            updateScore();
            // Show medicine message
            JOptionPane.showMessageDialog(this,
                    "You gave " + virtualPets.getPetName(petId) + " " + medicineName + "!",
                    "Medicine",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Calculates the health value increase for a given medicine item.
     *
     * @param medicineName the name of the medicine item
     * @return the amount of health value increase
     */
    private int calculateMedicineHealthValue(String medicineName) {
        // Assign health values based on medicine name
        switch (medicineName.toLowerCase()) {
            case "bandaid":
                return 15;
            case "needle":
                return 25;
            case "firstaidkit":
                return 40;
            default:
                return 10; // Default value for unrecognized medicine
        }
    }

}

/**
 * A dialog for selecting food items from the player's inventory.
 * Displays available food items with their effects and quantities.
 */
class FoodSelectionDialog extends JDialog {
    private int[] selectedItemPosition = null;
    private JPanel foodPanel;
    private InventoryManager inventoryManager;
    private Map<String, ImageIcon> itemImages;

    public FoodSelectionDialog(Window owner, InventoryManager inventoryManager) {
        super(owner, "Select Food", ModalityType.APPLICATION_MODAL);
        this.inventoryManager = inventoryManager;
        this.itemImages = new HashMap<>();
        loadItemImages();
        initComponents();
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Loads item images for food items - matching the inventory system
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
     * This matches the inventory icon system for consistency.
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
     * Initializes the components of the food selection dialog.
     * 
     * @return void
     */
    private void initComponents() {
        // Create main panel with a grid layout
        foodPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        foodPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add scroll pane for many food items
        JScrollPane scrollPane = new JScrollPane(foodPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Look for food items in the inventory
        boolean foodFound = false;
        for (int row = 0; row < inventoryManager.getGridSize(); row++) {
            for (int col = 0; col < inventoryManager.getGridSize(); col++) {
                String[] item = inventoryManager.getItem(row, col);
                if (item != null && item.length >= 4) {
                    String name = item[1];
                    String quantity = item[2];
                    String category = item[3];

                    // Only display food items with quantity > 0
                    if (name != null && !name.isEmpty() &&
                            category != null && category.equalsIgnoreCase("food") &&
                            quantity != null && !quantity.isEmpty() && Integer.parseInt(quantity) > 0) {

                        // Get icon using the same system as inventory
                        ImageIcon icon = getItemIcon(name);

                        addFoodItem(row, col, name, Integer.parseInt(quantity), icon);
                        foodFound = true;
                    }
                }
            }
        }

        // Show message if no food found
        if (!foodFound) {
            JLabel noFoodLabel = new JLabel("No food items in inventory! Visit the shop to buy some.", JLabel.CENTER);
            noFoodLabel.setFont(new Font("Arial", Font.BOLD, 14));
            foodPanel.add(noFoodLabel);
        }

        // Add cancel button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Add components to the dialog
        setLayout(new BorderLayout());
        add(new JLabel("Select food to feed your pet:", JLabel.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Gets the appropriate icon for the item, using the same system as the
     * inventory
     */
    private ImageIcon getItemIcon(String itemName) {
        String name = itemName.toLowerCase();
        if (itemImages.containsKey(name)) {
            return itemImages.get(name);
        } else {
            // Use the same fallback as inventory - gray icon with text
            return createColorIcon(Color.GRAY, name);
        }
    }

    private void addFoodItem(final int row, final int col, String name, int quantity, ImageIcon icon) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel nameLabel = new JLabel(name + " (x" + quantity + ")", JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Calculate effects based on food type
        int hungerValue = calculateHungerValue(name);
        int healthValue = calculateHealthValue(name);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        statsPanel.add(new JLabel("Hunger: +" + hungerValue, JLabel.CENTER));
        String healthText = healthValue >= 0 ? "Health: +" + healthValue : "Health: " + healthValue;
        statsPanel.add(new JLabel(healthText, JLabel.CENTER));

        itemPanel.add(iconLabel, BorderLayout.CENTER);
        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(statsPanel, BorderLayout.SOUTH);

        // Add click listener
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedItemPosition = new int[] { row, col };
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        });

        foodPanel.add(itemPanel);
    }

    /**
     * Calculate hunger value based on food name
     */
    private int calculateHungerValue(String foodName) {
        // Assign hunger values based on food name
        switch (foodName.toLowerCase()) {
            case "feast":
                return 20;
            case "bandaid": // Not actually food but handle it anyway
                return 5;
            case "toys": // Not actually food but handle it anyway
                return 5;
            default:
                return 10; // Default value for unrecognized food
        }
    }

    /**
     * Calculate health value based on food name
     */
    private int calculateHealthValue(String foodName) {
        // Assign health values based on food name
        switch (foodName.toLowerCase()) {
            case "feast":
                return 10;
            case "bandaid": // Has healing properties
                return 15;
            case "toys": // Toys aren't food but make pets happy
                return 5;
            default:
                return 0; // Default value for unrecognized food
        }
    }

    public int[] getSelectedItemPosition() {
        return selectedItemPosition;
    }
}

/**
 * A dialog for selecting gift items from the player's inventory.
 * Displays available gift items with their happiness effects and quantities.
 */
class ToyGiftSelectionDialog extends JDialog {
    private int[] selectedItemPosition = null;
    private JPanel giftPanel;
    private InventoryManager inventoryManager;
    private Map<String, ImageIcon> itemImages;

    public ToyGiftSelectionDialog(Window owner, InventoryManager inventoryManager) {
        super(owner, "Select Toy Gift", ModalityType.APPLICATION_MODAL);
        this.inventoryManager = inventoryManager;
        this.itemImages = new HashMap<>();
        loadItemImages();
        initComponents();
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Loads item images for toy items - matching the inventory system
     */
    private void loadItemImages() {
        // Load the same icons as used in the inventory
        ImageIcon mouse = new ImageIcon("Implementation/Icons/mouse.png");
        ImageIcon tennis = new ImageIcon("Implementation/Icons/tennis.png");
        ImageIcon toys = new ImageIcon("Implementation/Icons/toys.jpg");
        itemImages.put("mouse", mouse);
        itemImages.put("tennis", tennis);
        itemImages.put("toys", toys);
    }

    /**
     * Creates a simple icon with the specified color and label.
     * This matches the inventory icon system for consistency.
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

    private void initComponents() {
        // Create main panel with a grid layout
        giftPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        giftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add scroll pane for many gift items
        JScrollPane scrollPane = new JScrollPane(giftPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Look for gift items in the inventory
        boolean giftFound = false;
        for (int row = 0; row < inventoryManager.getGridSize(); row++) {
            for (int col = 0; col < inventoryManager.getGridSize(); col++) {
                String[] item = inventoryManager.getItem(row, col);
                if (item != null && item.length >= 4) {
                    String name = item[1];
                    String quantity = item[2];
                    String category = item[3];

                    // Only display gift items with quantity > 0
                    if (name != null && !name.isEmpty() &&
                            category != null && category.equalsIgnoreCase("gift") &&
                            quantity != null && !quantity.isEmpty() && Integer.parseInt(quantity) > 0) {

                        // Get icon using the same system as inventory
                        ImageIcon icon = getItemIcon(name);

                        addGiftItem(row, col, name, Integer.parseInt(quantity), icon);
                        giftFound = true;
                    }
                }
            }
        }

        // Show message if no gift found
        if (!giftFound) {
            JLabel noGiftLabel = new JLabel("No gift items in inventory! Visit the shop to buy some gifts.",
                    JLabel.CENTER);
            noGiftLabel.setFont(new Font("Arial", Font.BOLD, 14));
            giftPanel.add(noGiftLabel);
        }

        // Add cancel button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Add components to the dialog
        setLayout(new BorderLayout());
        add(new JLabel("Select a gift to give to your pet:", JLabel.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Gets the appropriate icon for the item, using the same system as the
     * inventory
     */
    private ImageIcon getItemIcon(String itemName) {
        String name = itemName.toLowerCase();
        if (itemImages.containsKey(name)) {
            return itemImages.get(name);
        } else {
            // Use the same fallback as inventory - gray icon with text
            return createColorIcon(Color.GRAY, name);
        }
    }

    private void addGiftItem(final int row, final int col, String name, int quantity, ImageIcon icon) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel nameLabel = new JLabel(name + " (x" + quantity + ")", JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Calculate happiness effect based on gift type
        int happinessValue = calculateGiftHappinessValue(name);

        JPanel statsPanel = new JPanel(new GridLayout(1, 1));
        statsPanel.add(new JLabel("Happiness: +" + happinessValue, JLabel.CENTER));

        itemPanel.add(iconLabel, BorderLayout.CENTER);
        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(statsPanel, BorderLayout.SOUTH);

        // Add click listener
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedItemPosition = new int[] { row, col };
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        });

        giftPanel.add(itemPanel);
    }

    /**
     * Calculate happiness value based on gift name
     */
    private int calculateGiftHappinessValue(String giftName) {
        // Assign happiness values based on gift name
        switch (giftName.toLowerCase()) {
            case "toys":
                return 15;
            case "mouse":
                return 20;
            case "tennis":
                return 12;
            default:
                return 10;
        }
    }

    public int[] getSelectedItemPosition() {
        return selectedItemPosition;
    }
}

/**
 * A dialog for selecting medicine items from the player's inventory.
 * Displays available medicine items with their health effects and quantities.
 */
class MedicineSelectionDialog extends JDialog {
    private int[] selectedItemPosition = null;
    private JPanel medicinePanel;
    private InventoryManager inventoryManager;
    private Map<String, ImageIcon> itemImages;

    public MedicineSelectionDialog(Window owner, InventoryManager inventoryManager) {
        super(owner, "Select Medicine", ModalityType.APPLICATION_MODAL);
        this.inventoryManager = inventoryManager;
        this.itemImages = new HashMap<>();
        loadItemImages();
        initComponents();
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Loads item images for medicine items - matching the inventory system
     */
    private void loadItemImages() {
        // Load medicine icons
        ImageIcon bandaid = new ImageIcon("Implementation/Icons/bandaid.jpg");
        ImageIcon needle = new ImageIcon("Implementation/Icons/needle.png");
        ImageIcon firstaidkit = new ImageIcon("Implementation/Icons/firstaidkit.jpg");
        itemImages.put("bandaid", bandaid);
        itemImages.put("needle", needle);
        itemImages.put("firstaidkit", firstaidkit);
    }

    /**
     * Creates a simple icon with the specified color and label.
     * This matches the inventory icon system for consistency.
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

    private void initComponents() {
        // Create main panel with a grid layout
        medicinePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        medicinePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add scroll pane for many medicine items
        JScrollPane scrollPane = new JScrollPane(medicinePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Look for medicine items in the inventory
        boolean medicineFound = false;
        for (int row = 0; row < inventoryManager.getGridSize(); row++) {
            for (int col = 0; col < inventoryManager.getGridSize(); col++) {
                String[] item = inventoryManager.getItem(row, col);
                if (item != null && item.length >= 4) {
                    String name = item[1];
                    String quantity = item[2];
                    String category = item[3];

                    // Only display health items with quantity > 0
                    if (name != null && !name.isEmpty() &&
                            category != null && category.equalsIgnoreCase("health") &&
                            quantity != null && !quantity.isEmpty() && Integer.parseInt(quantity) > 0) {

                        // Get icon using the same system as inventory
                        ImageIcon icon = getItemIcon(name);

                        addMedicineItem(row, col, name, Integer.parseInt(quantity), icon);
                        medicineFound = true;
                    }
                }
            }
        }

        // Show message if no medicine found
        if (!medicineFound) {
            JLabel noMedicineLabel = new JLabel("No medicine items in inventory! Visit the shop to buy some.", JLabel.CENTER);
            noMedicineLabel.setFont(new Font("Arial", Font.BOLD, 14));
            medicinePanel.add(noMedicineLabel);
        }

        // Add cancel button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Add components to the dialog
        setLayout(new BorderLayout());
        add(new JLabel("Select medicine to give your pet:", JLabel.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Gets the appropriate icon for the item, using the same system as the
     * inventory
     */
    private ImageIcon getItemIcon(String itemName) {
        String name = itemName.toLowerCase();
        if (itemImages.containsKey(name)) {
            return itemImages.get(name);
        } else {
            // Use the same fallback as inventory - gray icon with text
            return createColorIcon(Color.GRAY, name);
        }
    }

    private void addMedicineItem(final int row, final int col, String name, int quantity, ImageIcon icon) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel nameLabel = new JLabel(name + " (x" + quantity + ")", JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Calculate effects based on medicine type
        int healthValue = calculateMedicineHealthValue(name);

        JPanel statsPanel = new JPanel(new GridLayout(1, 1));
        statsPanel.add(new JLabel("Health: +" + healthValue, JLabel.CENTER));

        itemPanel.add(iconLabel, BorderLayout.CENTER);
        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(statsPanel, BorderLayout.SOUTH);

        // Add click listener
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedItemPosition = new int[] { row, col };
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        });

        medicinePanel.add(itemPanel);
    }

    /**
     * Calculate health value based on medicine name
     */
    private int calculateMedicineHealthValue(String medicineName) {
        // Assign health values based on medicine name
        switch (medicineName.toLowerCase()) {
            case "bandaid":
                return 15;
            case "needle":
                return 25;
            case "firstaidkit":
                return 40;
            default:
                return 10; // Default value for unrecognized medicine
        }
    }

    public int[] getSelectedItemPosition() {
        return selectedItemPosition;
    }
}
