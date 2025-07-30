package Implementation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

/**
 * A GUI panel for selecting and customizing virtual pets.
 * This class provides an interactive interface for users to:
 * - Choose from different pet types (Dog, Cat, Dragon)
 * - View detailed lore about each pet type
 * - Name their selected pet
 * - Create a new pet with initial stats
 * 
 * The interface includes:
 * - Animated pet sprites with hover effects
 * - Detailed pet descriptions
 * - A naming interface
 * - Background color coding for different pet types
 */
public class PetSelectionGUI extends JPanel {

    /** Reference to the parent MainMenu window */
    private MainMenu parentFrame;
    /** Main panel for switching between selection and naming screens */
    private JPanel mainPanel;
    /** Text area for displaying pet lore */
    private JTextArea loreTextArea;
    /** Button for confirming pet selection */
    private JButton selectButton;
    /** Name of the currently selected pet */
    private String selectedPetName = "Pet Name";
    /** Index of the currently selected pet type */
    private int selectedPetIndex = -1;
    /** Manager for virtual pets data */
    private VirtualPets virtualPets;
    /** Manager for player data */
    private Players players;
    /** Timer for sprite animation */
    private Timer spriteUpdateTimer;
    /** Current path of the pet's sprite */
    private String currentSpritePath;
    /** Label for displaying the pet sprite in naming screen */
    private JLabel petSpriteLabel;
    /** Panel for displaying pet lore */
    private JPanel lorePanel;

    /** List of available pet types */
    private static final List<String> PET_TYPES = Arrays.asList("Dog", "Cat", "Dragon");
    /** Detailed descriptions for each pet type */
    private static final String[] PET_LORE = {
            "Dogs are loyal companions that have been by humanity's side for thousands of years. " +
                    "Known for their unwavering loyalty and playful nature, they form deep bonds with their owners. " +
                    "They come in countless breeds, each with unique traits and personalities.",

            "Cats are independent and mysterious creatures with a rich history alongside humans. " +
                    "Revered in ancient Egypt and known for their grace and hunting prowess, they make affectionate " +
                    "yet self-sufficient companions. Their purring has even been shown to have healing properties.",

            "Dragons are legendary creatures of myth and magic. Said to command the elements and possess " +
                    "ancient wisdom, they are rare and powerful companions for only the most worthy handlers. " +
                    "Having a dragon as a pet means forming a bond that transcends ordinary pet relationships."
    };

    /**
     * Creates a new PetSelectionGUI with the specified parent window.
     *
     * @param parent the parent MainMenu window
     */
    public PetSelectionGUI(MainMenu parent) {
        this.parentFrame = parent;
        this.virtualPets = new VirtualPets("Implementation/pets.csv");
        this.players = new Players("Implementation/player_data.csv");
        setLayout(new BorderLayout());

        // Initialize main panel that will hold either pet selection or naming screen
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Call the method to show the pet selection screen initially
        displayPetSelectionScreen();
    }

    /**
     * Displays the initial pet selection screen with animated sprites and descriptions.
     */
    private void displayPetSelectionScreen() {
        // Create the pet panel with a grid layout
        JPanel petPanel = new JPanel();
        petPanel.setLayout(new GridLayout(3, 1, 10, 10)); // 3 rows for 3 pet types
        petPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create and add pet images
        for (int i = 0; i < PET_TYPES.size(); i++) {
            final int petIndex = i; // Create a final copy of i
            final String petType = PET_TYPES.get(i).toLowerCase();

            JPanel petContainer = new JPanel();
            petContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            petContainer.setLayout(new BorderLayout());
            
            // Create a panel for the pet icon
            JPanel iconPanel = new JPanel(new BorderLayout());
            
            // Set background color based on pet type
            if (petType.equals("dog")) {
                iconPanel.setBackground(new Color(68,0,100));
            } else if (petType.equals("cat")) {
                iconPanel.setBackground(new Color(169, 169, 169));
            } else {
                iconPanel.setBackground(new Color(34, 139, 34));
            }
            iconPanel.setOpaque(true);

            // Try to load both regular and hover images
            final ImageIcon regularIcon = loadPetIcon(petType + "/default_1.png", true);
            final ImageIcon hoverIcon = loadPetIcon(petType + "/default_2.png", true);

            // Create the pet label
            final JLabel petLabel;

            if (regularIcon != null) {
                petLabel = new JLabel(regularIcon);
            } else {
                petLabel = createPlaceholderLabel(PET_TYPES.get(i));
            }

            iconPanel.add(petLabel, BorderLayout.CENTER);

            // Add label with pet type below the icon
            JLabel typeLabel = new JLabel(PET_TYPES.get(i), SwingConstants.CENTER);
            typeLabel.setFont(new Font("Arial", Font.BOLD, 14));

            petContainer.add(iconPanel, BorderLayout.CENTER);
            petContainer.add(typeLabel, BorderLayout.SOUTH);

            // Add action listener for mouse events
            petContainer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    selectedPetIndex = petIndex;
                    displayPetLore(petIndex);

                    // Reset borders for all pet containers
                    for (Component comp : petPanel.getComponents()) {
                        if (comp instanceof JPanel) {
                            ((JPanel) comp).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                        }
                    }

                    // Highlight the selected pet
                    petContainer.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent evt) {
                    // Change to hover icon when mouse enters
                    if (hoverIcon != null && petLabel.getIcon() != hoverIcon) {
                        petLabel.setIcon(hoverIcon);
                    }

                    // Change border color unless it's already selected
                    if (selectedPetIndex != petIndex) {
                        petContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    }
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent evt) {
                    // Change back to regular icon when mouse exits
                    if (regularIcon != null && petLabel.getIcon() != regularIcon) {
                        petLabel.setIcon(regularIcon);
                    }

                    // Reset border color unless it's already selected
                    if (selectedPetIndex != petIndex) {
                        petContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    }
                    repaint();
                }
            });

            petPanel.add(petContainer);
        }

        // Create the lore panel
        lorePanel = new JPanel();
        lorePanel.setLayout(new BorderLayout());
        lorePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title "Pet Type" at the top of lore panel
        JLabel titleLabel = new JLabel("Select Your Pet");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        lorePanel.add(titleLabel, BorderLayout.NORTH);

        // Create the lore text area with appropriate styling
        loreTextArea = new JTextArea();
        loreTextArea.setEditable(false);
        loreTextArea.setFont(new Font("Arial", Font.ITALIC, 14));
        loreTextArea.setText("Click on a pet to learn more about it!");
        loreTextArea.setLineWrap(true);
        loreTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(loreTextArea);
        lorePanel.add(scrollPane, BorderLayout.CENTER);

        // Create select pet button
        selectButton = new JButton("Select Pet");
        selectButton.setPreferredSize(new Dimension(150, 40));
        selectButton.setEnabled(false); // Initially disabled until a pet is selected
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(selectButton);
        lorePanel.add(buttonPanel, BorderLayout.SOUTH);

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedPetIndex >= 0) {
                    showNamingScreen(); // Transition to the naming screen when clicked
                }
            }
        });

        // Add panels to the main panel
        mainPanel.removeAll(); // Remove existing components if any
        mainPanel.setLayout(new BorderLayout());
        
        // Add back to main menu button in top right
        JButton backToMainButton = new JButton("Back to Main Menu");
        backToMainButton.setPreferredSize(new Dimension(150, 30));
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backToMainButton);
        backButtonPanel.add(Box.createHorizontalStrut(20));
        mainPanel.add(backButtonPanel, BorderLayout.NORTH);
        
        // Add pet panel to west (left side, 1/3 of width)
        petPanel.setPreferredSize(new Dimension(300, 0)); // Set preferred width to 300 pixels
        petPanel.setMinimumSize(new Dimension(250, 0));   // Set minimum width to 250 pixels
        mainPanel.add(petPanel, BorderLayout.WEST);
        
        // Add lore panel to center (takes up remaining space)
        mainPanel.add(lorePanel, BorderLayout.CENTER);
        
        // Add action listener for back to main menu button
        backToMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showMainContent(); // Return to main menu
            }
        });
        
        // Revalidate and repaint to update the window content
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Loads and resizes a pet icon from the file system.
     *
     * @param filename the path to the icon file
     * @param isNamingScreen whether the icon is being loaded for the naming screen
     * @return the loaded and resized ImageIcon, or null if loading fails
     */
    private ImageIcon loadPetIcon(String filename, boolean isNamingScreen) {
        try {
            // Try to load from icons directory
            ImageIcon icon = new ImageIcon("Implementation/Icons/" + filename);
            if (icon.getIconWidth() <= 0) {
                return null; // Image not loaded properly
            }

            // Resize icon to fit well in the UI
            Image img = icon.getImage();
            Image resizedImg;
            if (filename.startsWith("dog/")) {
                resizedImg = img.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            } else if (filename.startsWith("cat/")) {
                resizedImg = img.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            } else {
                resizedImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            }
            return new ImageIcon(resizedImg);
        } catch (Exception e) {
            System.out.println("Failed to load icon: " + filename);
            return null; // Return null if the icon cannot be loaded
        }
    }

    /**
     * Loads a pet icon from the file system (overloaded method for backward compatibility).
     *
     * @param filename the path to the icon file
     * @return the loaded and resized ImageIcon, or null if loading fails
     */
    private ImageIcon loadPetIcon(String filename) {
        return loadPetIcon(filename, false);
    }

    /**
     * Displays the lore for the selected pet type.
     *
     * @param petIndex the index of the selected pet type
     */
    private void displayPetLore(int petIndex) {
        if (petIndex >= 0 && petIndex < PET_TYPES.size()) {
            String petType = PET_TYPES.get(petIndex);
            loreTextArea.setText(PET_LORE[petIndex]);

            // Update title label above lore
            Component[] components = lorePanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel && comp != null) {
                    JLabel label = (JLabel) comp;
                    label.setText(petType);
                    break;
                }
            }

            // Enable the select button
            selectButton.setEnabled(true);
        }
    }

    /**
     * Shows the naming screen for the selected pet.
     */
    private void showNamingScreen() {
        // Remove the current content
        mainPanel.removeAll();

        // Create a panel for the naming screen
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add go back button in top right
        JButton goBackButton = new JButton("Go Back");
        goBackButton.setPreferredSize(new Dimension(100, 30));
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(goBackButton);
        backButtonPanel.add(Box.createHorizontalStrut(20));
        namePanel.add(backButtonPanel, BorderLayout.EAST);

        // Create center panel with pet image and naming components
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2, 20, 0)); // 1 row, 2 columns with 20px gap

        // Add the selected pet's portrait on the left
        JPanel petImagePanel = new JPanel(new BorderLayout());
        String petType = PET_TYPES.get(selectedPetIndex).toLowerCase();
        
        // Create pet sprite label
        petSpriteLabel = new JLabel();
        petSpriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        petSpriteLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        if (petType.equals("dog")) {
            petSpriteLabel.setBackground(new Color(68,0,100));
        } else if (petType.equals("cat")) {
            petSpriteLabel.setBackground(new Color(169, 169, 169));
        } else {
            petSpriteLabel.setBackground(new Color(34, 139, 34));
        }

        petSpriteLabel.setOpaque(true);
        
        // Set initial sprite
        currentSpritePath = petType + "/default_1.png";
        ImageIcon petIcon = loadPetIcon(currentSpritePath, true);
        if (petIcon != null) {
            petSpriteLabel.setIcon(petIcon);
            petSpriteLabel.setText(""); // Clear placeholder text
        } else {
            petImagePanel = createPlaceholderPanel(PET_TYPES.get(selectedPetIndex));
        }
        
        petImagePanel.add(petSpriteLabel, BorderLayout.CENTER);
        petImagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        centerPanel.add(petImagePanel);

        // Start sprite animation timer
        if (spriteUpdateTimer != null) {
            spriteUpdateTimer.stop();
        }
        spriteUpdateTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentSpritePath.equals(petType + "/default_1.png")) {
                    currentSpritePath = petType + "/default_2.png";
                } else {
                    currentSpritePath = petType + "/default_1.png";
                }
                ImageIcon newIcon = loadPetIcon(currentSpritePath, true);
                if (newIcon != null) {
                    petSpriteLabel.setIcon(newIcon);
                    petSpriteLabel.setText(""); // Clear placeholder text
                }
            }
        });
        spriteUpdateTimer.start();

        // Create right panel for naming interface
        JPanel namingPanel = new JPanel();
        namingPanel.setLayout(new BoxLayout(namingPanel, BoxLayout.Y_AXIS));
        namingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add congratulations text
        JLabel congratsLabel = new JLabel("Congratulations! You have selected:");
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        congratsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add selected pet type
        JLabel selectedPetLabel = new JLabel(PET_TYPES.get(selectedPetIndex));
        selectedPetLabel.setFont(new Font("Arial", Font.BOLD, 16));
        selectedPetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add naming prompt
        JLabel namePromptLabel = new JLabel("What is their name?");
        namePromptLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        namePromptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        namePromptLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Add name input field with placeholder text
        JTextField nameField = new JTextField("Enter name here");
        nameField.setPreferredSize(new Dimension(200, 30));
        nameField.setMaximumSize(new Dimension(300, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add focus listener to clear placeholder text when clicked
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (nameField.getText().equals("Enter name here")) {
                    nameField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("Enter name here");
                }
            }
        });

        // Add confirm button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmButton.setMaximumSize(new Dimension(100, 30));
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        confirmButtonPanel.add(confirmButton);

        // Add components to naming panel
        namingPanel.add(congratsLabel);
        namingPanel.add(Box.createVerticalStrut(10));
        namingPanel.add(selectedPetLabel);
        namingPanel.add(Box.createVerticalStrut(30));
        namingPanel.add(namePromptLabel);
        namingPanel.add(nameField);
        namingPanel.add(Box.createVerticalStrut(10));
        namingPanel.add(confirmButtonPanel);

        centerPanel.add(namingPanel, BorderLayout.CENTER);
        namePanel.add(centerPanel, BorderLayout.CENTER);

        // Add action listeners
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String petName = nameField.getText();
                if (!petName.isEmpty() && !petName.equals("Enter name here")) {
                    selectedPetName = petName; // Save the pet's name
                    
                    // Stop the sprite animation timer
                    if (spriteUpdateTimer != null) {
                        spriteUpdateTimer.stop();
                    }
                    
                    // Generate a unique pet ID
                    String petId = UUID.randomUUID().toString();
                    
                    // Get the current player ID (assuming first player for now)
                    String playerId = players.getPlayerId(0);
                    
                    // Create initial vital stats for the new pet
                    VitalStats initialStats = new VitalStats(100, 100, 100, 100, selectedPetIndex);
                    
                    // Add the new pet to the CSV file
                    List<String[]> data = virtualPets.readCSV();
                    String[] newPet = new String[13];  // Increased size to 13
                    newPet[0] = petId;                    // Pet ID
                    newPet[1] = "player";                 // Player ID
                    newPet[2] = selectedPetName;          // Pet Name
                    newPet[3] = "0";                      // Age
                    newPet[4] = "50";                     // Hunger
                    newPet[5] = "50";                     // Happiness
                    newPet[6] = "50";                     // Health
                    newPet[7] = "50";                     // Sleep
                    newPet[8] = "0";                      // Last Play Timestamp - String '0' might be placeholder?
                    newPet[9] = "0";                      // Vet Cooldown
                    newPet[10] = "0";                     // Play Cooldown
                    newPet[11] = Implementation.Date.now().toString(); // Last Accessed Date (String)
                    newPet[12] = PET_TYPES.get(selectedPetIndex).toLowerCase(); // Pet Type String

                    data.add(newPet);
                    virtualPets.writeCSV(data);
                    
                    // Switch to pet interaction window
                    parentFrame.openPetInteraction(petId);
                } else {
                    JOptionPane.showMessageDialog(
                            parentFrame,
                            "Please enter a name for your " + PET_TYPES.get(selectedPetIndex) + ".",
                            "Name Required",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPetSelectionScreen(); // Go back to pet selection
            }
        });

        // Add the panel to the main panel
        mainPanel.add(namePanel, BorderLayout.CENTER);

        // Revalidate and repaint to update the window content
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Creates a placeholder panel for when a pet sprite cannot be loaded.
     *
     * @param petType the type of pet to create a placeholder for
     * @return a JPanel containing a placeholder for the pet sprite
     */
    private JPanel createPlaceholderPanel(String petType) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        // Set background color based on pet type
        if (petType.toLowerCase().equals("dog")) {
            panel.setBackground(new Color(68,0,100));
        } else if (petType.toLowerCase().equals("cat")) {
            panel.setBackground(new Color(169, 169, 169));
        } else {
            panel.setBackground(new Color(34, 139, 34));
        }
        panel.setOpaque(true);
        
        JLabel placeholderLabel = new JLabel(petType, SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 24));
        placeholderLabel.setForeground(Color.WHITE);
        panel.add(placeholderLabel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates a placeholder label for when a pet sprite cannot be loaded.
     *
     * @param petType the type of pet to create a placeholder for
     * @return a JLabel containing a placeholder for the pet sprite
     */
    private JLabel createPlaceholderLabel(String petType) {
        JLabel label = new JLabel(petType, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Set text color based on pet type
        if (petType.toLowerCase().equals("dog")) {
            label.setForeground(new Color(68,0,100));
        } else if (petType.toLowerCase().equals("cat")) {
            label.setForeground(new Color(169, 169, 169));
        } else {
            label.setForeground(new Color(34, 139, 34));
        }
        
        return label;
    }
}