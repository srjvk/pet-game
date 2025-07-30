package Implementation;
import javax.swing.*;
import java.awt.*;

/**
 * A dialog window that displays game instructions and help information.
 * This class creates a modal dialog with formatted HTML content explaining
 * how to play the game, including sections on:
 * - Main menu navigation
 * - Pet selection and care
 * - Gameplay mechanics
 * - Pet states and warnings
 * - Saving and loading
 * - Parental controls
 */
public class InfoScreen extends JDialog {

    /**
     * Constructs a new InfoScreen dialog with the specified parent frame.
     * The dialog is modal, meaning it blocks interaction with the parent window
     * until it is closed.
     *
     * @param parent The parent frame that owns this dialog
     */
    public InfoScreen(JFrame parent) {
        super(parent, "How to Play the Game", true);
        setSize(550, 650);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Create a panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create the content in a scroll pane
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);

        // HTML content for formatting
        String htmlContent =
                "<html><body style='font-family:Arial; width: 480px'>" +
                        "<h1 style='text-align:center'>How to Play the Game</h1>" +
                        "<h2 style='text-align:center'>Welcome to Virtual Pet!</h2>" +
                        "<p>Take care of your pet by feeding, playing, and ensuring it stays healthy! Follow this guide to learn how to play.</p>" +

                        "<h3>Main Menu</h3>" +
                        "<ul>" +
                        "<li><b>Start a New Game</b> – Create and name your pet.</li>" +
                        "<li><b>Load Game</b> 💾 – Continue playing from a saved file.</li>" +
                        "<li><b>Instructions</b> ℹ️ – View this tutorial again.</li>" +
                        "<li><b>Parental Controls</b> 🔐 – Set playtime limits (if enabled).</li>" +
                        "<li><b>Exit</b> 🚪 – Quit the game.</li>" +
                        "</ul>" +

                        "<h3>Choosing Your Pet 🐶🐱🐲</h3>" +
                        "<ul>" +
                        "<li>Select one of <b>three pet types</b>, each with different needs and personalities.</li>" +
                        "<li>Name your pet before starting the game.</li>" +
                        "<li>Each pet has <b>hunger, happiness, and health stats</b> that you must manage.</li>" +
                        "</ul>" +

                        "<h3>Gameplay</h3>" +
                        "<ul>" +
                        "<li><b>Feed</b> 🍖 – Choose food from your inventory to keep your pet full.</li>" +
                        "<li><b>Play</b> 🎾 – Keep your pet happy by playing with it.</li>" +
                        "<li><b>Heal</b> 💊 – Heal your pet to improve happiness and health.</li>" +
                        "<li><b>Sleep</b> 💤 – Put your pet to bed to restore energy.</li>" +
                        "<li><b>Go to Vet</b> 🏥 – Heal your pet when its health is low.</li>" +
                        "<li><b>Buy Items</b> 🛒 – Visit the <b>Shop</b> to purchase food, toys, and accessories.</li>" +
                        "<li><b>Monitor Stats</b> 📊 – Keep an eye on Hunger, Happiness, and Health bars!</li>" +
                        "</ul>" +

                        "<h3>Pet States & Warnings 🚨</h3>" +
                        "<ul>" +
                        "<li><b>Hungry</b> 😢 – Feed your pet before health decreases!</li>" +
                        "<li><b>Tired</b> 😴 – Let your pet sleep before it loses energy.</li>" +
                        "<li><b>Angry</b> 😡 – Play with or gift your pet to make it happy.</li>" +
                        "<li><b>Sick</b> 🤒 – Take your pet to the vet before health reaches zero!</li>" +
                        "</ul>" +

                        "<h3>Saving & Loading 🗂️</h3>" +
                        "<ul>" +
                        "<li>Save your game anytime to continue later.</li>" +
                        "<li>Load a previous save to pick up where you left off.</li>" +
                        "</ul>" +

                        "<h3>Parental Controls 🔒</h3>" +
                        "<ul>" +
                        "<li>Parents can set playtime limits and monitor stats.</li>" +
                        "<li>A password is required to access settings.</li>" +
                        "</ul>" +
                        "</body></html>";

        textPane.setText(htmlContent);

        // Ensure the scroll pane starts at the top
        textPane.setCaretPosition(0);

        // Add the text pane to a scroll pane
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create Close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}