package Implementation;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Global playtime tracker that manages play sessions across the entire application.
 * This singleton class ensures playtime is accurately tracked even when navigating
 * between different game screens.
 */
public class PlaytimeTracker {
    // Singleton instance
    private static PlaytimeTracker instance;
    
    // Track active sessions
    private Map<String, SessionData> activeSessions;
    
    // Global timer to track all sessions
    private Timer globalTimer;
    
    // Internal class to store session data
    private class SessionData {
        private String playerId;
        private long startTimeMillis;
        private int secondsPlayed;
        private boolean limitWarningShown;
        private ParentalControls controls;
        private Commands commands;
        
        public SessionData(String playerId, Commands commands) {
            this.playerId = playerId;
            this.startTimeMillis = System.currentTimeMillis();
            this.secondsPlayed = 0;
            this.limitWarningShown = false;
            this.controls = new ParentalControls();
            this.commands = commands;
        }
        
        public void incrementTime(int seconds) {
            this.secondsPlayed += seconds;
        }
        
        public int getSecondsPlayed() {
            return secondsPlayed;
        }
        
        public int getMinutesPlayed() {
            return secondsPlayed / 60;
        }
        
        public boolean isLimitWarningShown() {
            return limitWarningShown;
        }
        
        public void setLimitWarningShown(boolean shown) {
            this.limitWarningShown = shown;
        }
    }
    
    /**
     * Private constructor for singleton pattern
     */
    private PlaytimeTracker() {
        activeSessions = new HashMap<>();
        
        // Create global timer that ticks every 1 second (was 10 seconds)
        globalTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAllSessions();
            }
        });
        
        globalTimer.start();
    }
    
    /**
     * Get the singleton instance
     */
    public static PlaytimeTracker getInstance() {
        if (instance == null) {
            instance = new PlaytimeTracker();
        }
        return instance;
    }
    
    /**
     * Start tracking a new session for a player
     */
    public void startSession(String playerId, Commands commands) {
        if (activeSessions.containsKey(playerId)) {
            return;
        }
        
        // Create and store new session
        activeSessions.put(playerId, new SessionData(playerId, commands));
        
        // Ensure the player exists in the database
        ensurePlayerExists(playerId);
    
        System.out.println("Added new session for player: " + playerId);
    }
    
    /**
     * End tracking for a player session and update statistics
     */
    public void endSession(String playerId) {
        SessionData session = activeSessions.get(playerId);
        if (session == null) {
            System.out.println("No active session found for player: " + playerId);
            return;
        }
        
        try {
            // Update playtime statistics
            int minutesPlayed = session.getMinutesPlayed();            
            if (session.commands != null) {
                session.commands.updatePlayTimeStatistics(playerId, minutesPlayed);
            } else {
                System.err.println("Commands object is null for player: " + playerId);
            }
        } catch (Exception e) {
            System.err.println("Error ending session: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Remove session
        activeSessions.remove(playerId);
    }
    
    /**
     * Get the current session time in seconds
     */
    public int getSessionSeconds(String playerId) {
        SessionData session = activeSessions.get(playerId);
        return session != null ? session.getSecondsPlayed() : 0;
    }
    
    /**
     * Get the current session time in minutes
     */
    public int getSessionMinutes(String playerId) {
        return getSessionSeconds(playerId) / 60;
    }
    
    /**
     * Check if player is approaching or has exceeded playtime limits
     * @return true if player should be allowed to continue, false if limit exceeded
     */
    public boolean checkPlaytimeLimits(String playerId, JPanel parentComponent) {
        SessionData session = activeSessions.get(playerId);
        if (session == null) {
            System.out.println("No active session found for player: " + playerId);
            return true; // Allow play if no session is tracked
        }
        
        try {
            // Get parental control settings
            ParentalControls controls = new ParentalControls();
            boolean isLimitEnabled = controls.getPlaytimeLimitEnabled(playerId);

            if (isLimitEnabled) {
                // Get current and max allowed playtime
                int currentPlaytime = controls.getPlaytimeMinutes(playerId);
                int maxAllowedPlaytime = controls.getMaxAllowedPlaytimeMinutes(playerId);
                
                // Calculate total and remaining time
                int sessionMinutes = session.getMinutesPlayed();
                int totalPlaytime = currentPlaytime + sessionMinutes;
                int remainingMinutes = maxAllowedPlaytime - totalPlaytime;
                
                
                // Show warning when approaching limit (5 minutes remaining)
                if (remainingMinutes <= 5 && remainingMinutes > 0 && !session.isLimitWarningShown()) {
                    session.setLimitWarningShown(true);
                    
                    if (parentComponent != null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(parentComponent,
                                "You have " + remainingMinutes + " minutes of playtime remaining today.",
                                "Playtime Limit Warning",
                                JOptionPane.WARNING_MESSAGE);
                        });
                    }
                }
                
                // Check if limit is exceeded
                if (totalPlaytime >= maxAllowedPlaytime) {
                    System.out.println("Playtime limit reached for player: " + playerId);
                    return false; // Limit exceeded, should not continue
                }
            }
            
            return true; // No limit or limit not exceeded
            
        } catch (Exception e) {
            System.err.println("Error checking playtime limits: " + e.getMessage());
            e.printStackTrace();
            return true; // Allow play in case of error
        }
    }
    
    /**
     * Update all active sessions (called by timer)
     */
    private void updateAllSessions() {
        // Add 1 second to each active session (was 10 seconds)
        for (SessionData session : activeSessions.values()) {
            session.incrementTime(1);
        }  
    }
    
    /**
     * Force update playtime statistics for all players
     */
    public void forceUpdateAllStatistics() {
        Map<String, SessionData> sessionsToRestore = new HashMap<>(activeSessions);
        
        for (String playerId : sessionsToRestore.keySet()) {
            SessionData oldSession = sessionsToRestore.get(playerId);
            Commands sessionCommands = oldSession != null ? oldSession.commands : null;
            
            // End the session and update statistics
            endSession(playerId);
            
            // Restart session if we have the commands object
            if (sessionCommands != null) {
                startSession(playerId, sessionCommands);
            }
        }
    }
    
    /**
     * Shutdown the tracker and update all statistics
     */
    public void shutdown() {
        globalTimer.stop();
        
        // End all sessions
        for (String playerId : new HashMap<>(activeSessions).keySet()) {
            endSession(playerId);
        }
        
        System.out.println("PlaytimeTracker shutdown complete");
    }
    
    /**
     * Get play status information for display
     */
    public String getPlaytimeStatusText(String playerId) {
        try {
            SessionData session = activeSessions.get(playerId);
            if (session == null) {
                return "Playtime: Not tracking";
            }
            
            ParentalControls controls = new ParentalControls();
            int savedPlaytime = 0;
            int maxAllowed = 0;
            boolean limitEnabled = false;
            
            try {
                savedPlaytime = controls.getPlaytimeMinutes(playerId);
                maxAllowed = controls.getMaxAllowedPlaytimeMinutes(playerId);
                limitEnabled = controls.getPlaytimeLimitEnabled(playerId);
            } catch (Exception e) {
                System.err.println("Error getting playtime data: " + e.getMessage());
            }
            
            // Calculate total playtime in minutes (including current session)
            int sessionMinutes = session.getSecondsPlayed() / 60;
            int sessionSeconds = session.getSecondsPlayed() % 60;
            int totalPlaytime = savedPlaytime + sessionMinutes;
            
            return String.format(
                "Playtime: %d min (Session: %d:%02d) | Limit: %s (%d/%d min)", 
                totalPlaytime, sessionMinutes, sessionSeconds, 
                limitEnabled ? "ON" : "OFF", 
                totalPlaytime, maxAllowed
            );
            
        } catch (Exception e) {
            System.err.println("Error generating status text: " + e.getMessage());
            return "Playtime status unavailable";
        }
    }
    
    /**
     * Ensures a player exists in the ParentalControls database
     */
    private void ensurePlayerExists(String playerId) {
        try {
            ParentalControls controls = new ParentalControls();
            int row = controls.findRow(playerId);
            
            if (row == -1) {
                // Player doesn't exist, create a new entry
                System.out.println("Creating new player in ParentalControls database: " + playerId);
                java.util.List<String[]> data = controls.readCSV();
                
                // Create a new row for the player with default values
                String[] newRow = new String[6]; // 6 columns in ParentalControls CSV
                newRow[0] = playerId;          // Player ID
                newRow[1] = "true";            // Playtime limit enabled (on by default)
                newRow[2] = "0";               // Total playtime minutes
                newRow[3] = "0";               // Average playtime minutes
                newRow[4] = "60";               // Max allowed playtime minutes (default: 3 minutes for testing)
                newRow[5] = "0";               // Session count
                
                // Add the row and write back
                data.add(newRow);
                controls.writeCSV(data);
                System.out.println("Created new player entry with 3 minute limit: " + playerId);
            } else {
                // Player exists, make sure the data is valid for playtime tracking
                System.out.println("Found existing player at row " + row + ": " + playerId);
                
                try {
                    // Test if we can read playtime values from this player
                    boolean limitEnabled = controls.getPlaytimeLimitEnabled(playerId);
                    int maxAllowed = controls.getMaxAllowedPlaytimeMinutes(playerId);
                    
                    System.out.println("Playtime limit: " + (limitEnabled ? "ON" : "OFF") + 
                                      ", Max allowed: " + maxAllowed + " minutes");
                } catch (Exception e) {
                    // If there's an error reading values, it might be due to incompatible data format
                    System.out.println("Error reading playtime values, updating player record: " + e.getMessage());
                    
                    // Update the player data to be compatible with our system
                    java.util.List<String[]> data = controls.readCSV();
                    String[] newRow = new String[6];
                    newRow[0] = playerId;      // Keep the same ID
                    newRow[1] = "true";        // Enable limits
                    newRow[2] = "0";           // Reset playtime
                    newRow[3] = "0";           // Reset average
                    newRow[4] = "3";           // 3 minute limit for testing
                    newRow[5] = "0";           // Reset session count
                    
                    data.add(newRow);          // Add as a new row (will be found by findRow)
                    controls.writeCSV(data);
                    System.out.println("Added compatible player record for: " + playerId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error ensuring player exists: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 