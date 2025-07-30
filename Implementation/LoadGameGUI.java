package Implementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * A GUI panel for loading saved games.
 * This class provides an interface for users to view and select their saved games,
 * displaying up to 4 most recent saves with pet names and last accessed dates.
 */
public class LoadGameGUI extends JPanel {
    /** Reference to the parent MainMenu window */
    private MainMenu parentFrame;
    /** Manager for virtual pets data */
    private VirtualPets virtualPets;
    /** Main panel containing save slots */
    private JPanel mainPanel;
    /** Maximum number of save slots to display */
    private static final int MAX_SAVES = 4;

    /**
     * Constructs a new LoadGameGUI with the specified parent window.
     * Initializes the interface and loads existing save slots.
     *
     * @param parent The parent MainMenu window
     */
    public LoadGameGUI(MainMenu parent) {
        this.parentFrame = parent;
        this.virtualPets = new VirtualPets("Implementation/pets.csv");
        setLayout(new BorderLayout());
        
        // Create title
        JLabel titleLabel = new JLabel("Load Existing Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create main panel for save slots
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add info button to top right
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // Use ResourceLoader to load the icon
        ImageIcon infoIcon = ResourceLoader.loadImage("Implementation/Icons/icon.png");
        if (infoIcon != null) {
            Image infoImage = infoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            JButton infoButton = new JButton(new ImageIcon(infoImage));
            infoButton.setBorderPainted(false);
            infoButton.setContentAreaFilled(false);
            infoButton.setFocusPainted(false);
            infoButton.setOpaque(false);
            
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            infoPanel.setOpaque(false);
            infoPanel.add(infoButton);
            topPanel.add(infoPanel, BorderLayout.EAST);
            topPanel.add(titleLabel, BorderLayout.CENTER);
        }
        
        // Load and display save slots
        loadSaveSlots();
        
        // Create "Back to Main Menu" button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> parentFrame.showMainContent());
        
        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads and displays the available save slots.
     * Shows up to MAX_SAVES most recent saves, each with the pet's name
     * and last accessed date.
     */
    private void loadSaveSlots() {
        List<String[]> saves = virtualPets.readCSV();
        int displayedSaves = 0;
        
        // Display up to MAX_SAVES most recent saves
        for (int i = saves.size() - 1; i >= 0 && displayedSaves < MAX_SAVES; i--) {
            String[] save = saves.get(i);
            // Add safety checks for array indices - check up to index 11 now
            if (save.length < 12) {  // Check if array has enough elements for LastAccessedDate
                System.out.println("Warning: Save data at index " + i + " is incomplete (missing LastAccessedDate)");
                continue;  // Skip this save
            }
            
            String petName = save[2];
            String lastAccessedDateString = save[11]; // Read from index 11
            
            if (petName == null || lastAccessedDateString == null) {
                System.out.println("Warning: Invalid save data (name or date) at index " + i);
                continue;  // Skip this save
            }
            
            // Create save slot button
            JButton saveSlot = createSaveSlotButton(petName, lastAccessedDateString);
            mainPanel.add(saveSlot);
            mainPanel.add(Box.createVerticalStrut(10));
            displayedSaves++;
        }
        
        // Add message if no saves are found
        if (displayedSaves == 0) {
            JLabel noSavesLabel = new JLabel("No saved games found", SwingConstants.CENTER);
            noSavesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noSavesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noSavesLabel);
        }
    }
    
    /**
     * Creates a button representing a save slot with the pet's name and last accessed date.
     *
     * @param petName The name of the pet in the save
     * @param dateString The date string when the save was last accessed
     * @return A JButton configured to display the save slot information
     */
    private JButton createSaveSlotButton(String petName, String dateString) { // Renamed parameter
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(400, 60));
        button.setMaximumSize(new Dimension(400, 60));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Format the timestamp
        String formattedTime;
        try {
            // Use our custom Date class with the correct date string
            Date date = new Date(dateString);
            formattedTime = date.toString();
        } catch (Exception e) {
            System.err.println("Error parsing date string: " + dateString + " for pet: " + petName + " Error: " + e.getMessage());
            formattedTime = "Unknown Date"; // Better default if parsing fails
        }
        
        // Add text to button
        JLabel nameLabel = new JLabel(petName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel timeLabel = new JLabel(formattedTime, SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        button.add(nameLabel, BorderLayout.CENTER);
        button.add(timeLabel, BorderLayout.SOUTH);
        
        // Add action listener with safety checks
        button.addActionListener(e -> {
            List<String[]> saves = virtualPets.readCSV();
            for (String[] save : saves) {
                // Check array length and use correct indices (2 for name, 11 for date)
                if (save.length >= 12 &&
                    save[2] != null && save[2].equals(petName) &&
                    save[11] != null && save[11].equals(dateString) && // Check index 11
                    save[0] != null && save[1] != null) { // Ensure petId and playerId exist
                    String petId = save[0];
                    String playerId = save[1];
                    parentFrame.openPetInteraction(petId);
                    break;
                }
            }
        });
        
        return button;
    }
}
