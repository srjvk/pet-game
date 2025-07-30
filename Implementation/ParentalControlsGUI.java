package Implementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * A GUI for the ParentalControls functionality.
 * This class provides an interface for managing parental control settings,
 * including playtime limits, viewing statistics, and managing game restrictions.
 */
public class ParentalControlsGUI extends JPanel {
    
    private ParentalControls parentalControls;
    private String currentPlayerId;
    private MainMenu mainMenu; // Reference to main menu
    
    /**
     * Constructs the ParentalControls GUI.
     * 
     * @param parentalControls The ParentalControls instance to use for data operations
     * @param playerId The ID of the current player
     * @param mainMenu The main menu reference
     */
    public ParentalControlsGUI(ParentalControls parentalControls, String playerId, MainMenu mainMenu) {
        this.parentalControls = parentalControls;
        this.currentPlayerId = playerId;
        this.mainMenu = mainMenu;
        
        initializeUI();
    }
    
    /**
     * Initialize the user interface components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Parental Controls");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titlePanel.add(titleLabel);
        
        // Create the buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        
        // Add some padding
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Create buttons
        JButton setPlaytimeLimitsBtn = createButton("Set Playtime Limits");
        JButton viewPlayStatisticsBtn = createButton("View Play Statistics");
        JButton resetStatisticsBtn = createButton("Reset Statistics");
        JButton revivePetBtn = createButton("Revive Pet");
        JButton changePasswordBtn = createButton("Change Password");
        JButton backToMainMenuBtn = createButton("Back to Main Menu");
        
        // Add action listeners
        setPlaytimeLimitsBtn.addActionListener(e -> setPlaytimeLimits());
        viewPlayStatisticsBtn.addActionListener(e -> viewPlayStatistics());
        resetStatisticsBtn.addActionListener(e -> resetStatistics());
        revivePetBtn.addActionListener(e -> revivePet());
        changePasswordBtn.addActionListener(e -> changePassword());
        backToMainMenuBtn.addActionListener(e -> backToMainMenu());
        
        // Add buttons to panel with spacing
        buttonsPanel.add(Box.createVerticalGlue());
        addComponentWithSpacing(buttonsPanel, setPlaytimeLimitsBtn);
        addComponentWithSpacing(buttonsPanel, viewPlayStatisticsBtn);
        addComponentWithSpacing(buttonsPanel, resetStatisticsBtn);
        addComponentWithSpacing(buttonsPanel, revivePetBtn);
        addComponentWithSpacing(buttonsPanel, changePasswordBtn);
        addComponentWithSpacing(buttonsPanel, backToMainMenuBtn);
        buttonsPanel.add(Box.createVerticalGlue());
        
        // Add help/info button at top-right
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton infoButton = new JButton("ⓘ");
        infoButton.setFont(new Font("Arial", Font.PLAIN, 20));
        infoButton.setContentAreaFilled(false);
        infoButton.setBorderPainted(false);
        infoButton.setFocusPainted(false);
        infoButton.addActionListener(e -> showHelp());
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.add(infoButton);
        
        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.EAST);
        
        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(new JPanel(), BorderLayout.SOUTH); // Empty footer panel
    }
    
    /**
     * Creates a styled button for the GUI with consistent formatting.
     *
     * @param text The text to display on the button
     * @return A styled JButton with the specified text
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        
        // Set preferred size to match the mockup
        button.setPreferredSize(new Dimension(400, 60));
        button.setMaximumSize(new Dimension(400, 60));
        
        return button;
    }
    
    /**
     * Adds a component to a container with consistent vertical spacing.
     *
     * @param container The container to add the component to
     * @param component The component to add
     */
    private void addComponentWithSpacing(Container container, Component component) {
        container.add(Box.createVerticalStrut(15));
        container.add(component);
        container.add(Box.createVerticalStrut(15));
    }
    
    // Button action methods
    
    private void setPlaytimeLimits() {
        // Check if player exists and create if necessary
        ensurePlayerExists();
        
        try {
            // Get current playtime limit
            int currentLimit = 0;
            try {
                currentLimit = parentalControls.getMaxAllowedPlaytimeMinutes(currentPlayerId);
            } catch (Exception e) {
                // If there's an error, use default value
                currentLimit = 60; // Default to 60 minutes
            }
            
            // Create slider for minutes selection
            JSlider minutesSlider = new JSlider(JSlider.HORIZONTAL, 0, 240, currentLimit);
            minutesSlider.setMajorTickSpacing(60);
            minutesSlider.setMinorTickSpacing(15);
            minutesSlider.setPaintTicks(true);
            minutesSlider.setPaintLabels(true);
            
            JLabel valueLabel = new JLabel(currentLimit + " minutes", JLabel.CENTER);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Update label when slider changes
            minutesSlider.addChangeListener(e -> {
                valueLabel.setText(minutesSlider.getValue() + " minutes");
            });
            
            // Create checkbox for enabling/disabling limits
            JCheckBox enableLimits = new JCheckBox("Enable playtime limits");
            enableLimits.setSelected(parentalControls.getPlaytimeLimitEnabled(currentPlayerId));
            
            // Create dialog layout
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel instructionLabel = new JLabel("Set maximum daily playtime:");
            instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            minutesSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            enableLimits.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            panel.add(instructionLabel);
            panel.add(Box.createVerticalStrut(10));
            panel.add(minutesSlider);
            panel.add(Box.createVerticalStrut(5));
            panel.add(valueLabel);
            panel.add(Box.createVerticalStrut(15));
            panel.add(enableLimits);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                panel, 
                "Set Playtime Limits", 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                // Save settings
                parentalControls.setMaxAllowedPlaytimeMinutes(currentPlayerId, minutesSlider.getValue());
                parentalControls.setPlaytimeLimitEnabled(currentPlayerId, enableLimits.isSelected());
                
                JOptionPane.showMessageDialog(this, 
                    "Playtime limits updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating playtime limits: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewPlayStatistics() {
        // Check if player exists and create if necessary
        ensurePlayerExists();
        
        try {
            int totalPlaytime = parentalControls.getPlaytimeMinutes(currentPlayerId);
            int avgPlaytime = parentalControls.getAveragePlaytimeMinutes(currentPlayerId);
            int sessionCount = parentalControls.getSessionCount(currentPlayerId);
            
            // Format playtime for better readability
            String formattedTotal = formatPlaytime(totalPlaytime);
            String formattedAvg = formatPlaytime(avgPlaytime);
            
            JOptionPane.showMessageDialog(
                this,
                "Play Statistics:\n\n" +
                "Total Playtime: " + formattedTotal + "\n" +
                "Average Session: " + formattedAvg + "\n" +
                "Session Count: " + sessionCount,
                "Play Statistics",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not retrieve play statistics: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Formats playtime in minutes to a human-readable format.
     * Converts minutes to hours and minutes format.
     *
     * @param minutes The number of minutes to format
     * @return A formatted string representing the playtime
     */
    private String formatPlaytime(int minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            if (mins == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "");
            } else {
                return hours + " hour" + (hours > 1 ? "s" : "") + " " + mins + " min" + (mins > 1 ? "s" : "");
            }
        }
    }
    
    private void resetStatistics() {
        // Check if player exists and create if necessary
        ensurePlayerExists();
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset all play statistics?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                parentalControls.setPlaytimeMinutes(currentPlayerId, 0);
                parentalControls.setAveragePlaytimeMinutes(currentPlayerId, 0);
                parentalControls.setSessionCount(currentPlayerId, 0);
                
                // Reset player score to 0
                Players players = new Players("Implementation/player_data.csv");
                players.setScore(currentPlayerId, "0");
                
                JOptionPane.showMessageDialog(this, "Statistics have been reset.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error resetting statistics: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void revivePet() {
        // Check if player exists and create if necessary
        ensurePlayerExists();
        
        try {
            // Get list of pets for the player
            Players players = new Players("Implementation/player_data.csv");
            VirtualPets virtualPets = new VirtualPets("Implementation/pets.csv");
            // print virtual pets csv properly
            List<String[]> petsData = virtualPets.readCSV();
            System.out.println("Virtual Pets CSV contents:");
            for (String[] row : petsData) {
                System.out.println(String.join(",", row));
            }
            Inventory inventory = new Inventory("Implementation/inventory.csv");
            Store store = new Store("Implementation/store.csv");
            GameProgress gameProgress = new GameProgress("Implementation/progress.csv");
            Commands commands = new Commands(players, virtualPets, inventory, store, gameProgress, parentalControls);
            
            // Manually get pets for the current player
            List<String[]> petData = virtualPets.readCSV();
            java.util.List<String> playerPets = new java.util.ArrayList<>();
            
            // Filter pets belonging to this player
            for (String[] row : petData) {
                if (row.length > 1 && row[1].equals(currentPlayerId)) {
                    playerPets.add(row[0]); // Add petId
                }
            }
            
            if (playerPets.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "No pets found for this player.",
                    "Pet Revival",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Create a list of pet names with status
            String[] petDisplayNames = new String[playerPets.size()];
            for (int i = 0; i < playerPets.size(); i++) {
                String petId = playerPets.get(i);
                // Check if pet is dead (health = 0)
                int health = virtualPets.getHealth(petId);
                boolean isDead = (health == 0);
                String status = isDead ? " (Dead)" : " (Alive)";
                String petName = virtualPets.getPetName(petId);
                petDisplayNames[i] = petName + " - " + petId + status;
            }
            
            // Show pet selection dialog
            String selectedPet = (String) JOptionPane.showInputDialog(
                this,
                "Select a pet to revive:",
                "Pet Revival",
                JOptionPane.QUESTION_MESSAGE,
                null,
                petDisplayNames,
                petDisplayNames[0]
            );
            
            if (selectedPet == null) {
                // User cancelled
                return;
            }
            
            // Extract pet ID from selection
            String petId = selectedPet.split(" - ")[1].split(" ")[0];
            
            // Revive the pet
            String result = commands.revivePet(petId);
            
            JOptionPane.showMessageDialog(
                this,
                result,
                "Pet Revival",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error reviving pet: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void backToMainMenu() {
        // Tell the main menu to show its main content
        mainMenu.showMainContent();
    }
    
    private void showHelp() {
        // Display help information
        JOptionPane.showMessageDialog(
            this,
            "Parental Controls Help:\n\n" +
            "• Set Playtime Limits: Configure maximum daily playtime in minutes\n" +
            "• View Play Statistics: See play history and usage patterns\n" +
            "• Reset Statistics: Clear all play history data\n" +
            "• Revive Pet: Restore a virtual pet if inactive\n" +
            "• Change Password: Update the parental controls password\n" +
            "• Back: Return to the main menu",
            "Help",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Ensures the current player exists in the data file.
     * Creates a default entry for the player if they don't exist.
     */
    private void ensurePlayerExists() {
        try {
            // Check if player exists directly
            if (parentalControls.findRow(currentPlayerId) == -1) {
                createNewPlayer(currentPlayerId);
            }
        } catch (Exception e) {
            // If we get an exception, create the player
            createNewPlayer(currentPlayerId);
        }
    }
    
    /**
     * Initializes default data for a new player.
     * Sets up default values for playtime limits and statistics.
     */
    private void initializePlayerData() {
        try {
            // Set default values for player
            parentalControls.setMaxAllowedPlaytimeMinutes(currentPlayerId, 60); // Default 60 minutes
            parentalControls.setPlaytimeLimitEnabled(currentPlayerId, false);
            parentalControls.setPlaytimeMinutes(currentPlayerId, 0);
            parentalControls.setAveragePlaytimeMinutes(currentPlayerId, 0);
            parentalControls.setSessionCount(currentPlayerId, 0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error initializing player data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Creates a new player entry in the CSV file with default values.
     *
     * @param playerId The ID of the player to create
     */
    private void createNewPlayer(String playerId) {
        try {
            // Get current data
            List<String[]> data = parentalControls.readCSV();
            
            // Create a new row for the player with default values
            String[] newRow = new String[6]; // Assuming 6 columns in ParentalControls CSV
            newRow[0] = playerId;                  // Player ID
            newRow[1] = "false";                   // Playtime limit enabled (off by default)
            newRow[2] = "0";                       // Total playtime minutes
            newRow[3] = "0";                       // Average playtime minutes
            newRow[4] = "60";                      // Max allowed playtime minutes
            newRow[5] = "0";                       // Session count
            
            // Add the row
            data.add(newRow);
            
            // Write back to CSV
            parentalControls.writeCSV(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error creating new player: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Allows changing the parental controls password
     */
    private void changePassword() {
        // Create a panel with password fields
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add current password field
        JLabel currentLabel = new JLabel("Current Password:");
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField currentField = new JPasswordField(20);
        currentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentField.setMaximumSize(new Dimension(300, 30));
        
        // Add new password field
        JLabel newLabel = new JLabel("New Password:");
        newLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField newField = new JPasswordField(20);
        newField.setAlignmentX(Component.LEFT_ALIGNMENT);
        newField.setMaximumSize(new Dimension(300, 30));
        
        // Add confirm password field
        JLabel confirmLabel = new JLabel("Confirm New Password:");
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField confirmField = new JPasswordField(20);
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setMaximumSize(new Dimension(300, 30));
        
        // Add components to panel
        panel.add(currentLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(currentField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(newLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(newField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(confirmLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(confirmField);
        
        // Show dialog
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Change Parental Controls Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            // Get entered values
            String currentPassword = new String(currentField.getPassword());
            String newPassword = new String(newField.getPassword());
            String confirmPassword = new String(confirmField.getPassword());
            
            // Initialize Commands to use authenticateParentalControls
            Players players = new Players("Implementation/player_data.csv");
            VirtualPets virtualPets = new VirtualPets("Implementation/pets.csv");
            Inventory inventory = new Inventory("Implementation/inventory.csv");
            Store store = new Store("Implementation/store.csv");
            GameProgress gameProgress = new GameProgress("Implementation/progress.csv");
            Commands commands = new Commands(players, virtualPets, inventory, store, gameProgress, parentalControls);
            
            // Validate inputs
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "All fields are required.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Check if current password is correct
            if (!commands.authenticateParentalControls(currentPassword)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Current password is incorrect.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Check if new passwords match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(
                    this,
                    "New passwords do not match.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Update the password by updating the Commands class
            try {
                // Create a new file to store the password
                // WARNING: This is just a demonstration. In a real application,
                // passwords should be securely hashed and stored
                java.io.File passwordFile = new java.io.File("Implementation/parentalPassword.txt");
                java.io.PrintWriter writer = new java.io.PrintWriter(passwordFile);
                writer.println(newPassword);
                writer.close();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Password updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error updating password: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Main method to inform users to run MainMenu instead.
     * This class is no longer a standalone application.
     */
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null,
            "ParentalControlsGUI is not a standalone application.\n" +
            "Please run the MainMenu class instead.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
            
        // Launch MainMenu for convenience
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}