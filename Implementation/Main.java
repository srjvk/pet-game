package Implementation;

/**
 * The main entry point for the Virtual Pet Game application.
 * This class initializes the game by creating and displaying the main menu.
 */
public class Main {
    /** The main menu window of the application */
    MainMenu mainMenu;

    /**
     * Main entry point for the application.
     * Creates a new instance of the game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        new Main();
    }

    /**
     * Constructs a new Main instance and initializes the game.
     * Creates and displays the main menu window.
     */
    public Main() {
        mainMenu = new MainMenu();
    }
}
