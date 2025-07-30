# Virtual Pet Game

A Java-based virtual pet simulation game where players can care for and interact with their virtual pets. The game features three different pet types, various activities like feeding and playing, and includes parental controls for a safe gaming experience.

## Requirements

- Java Development Kit (JDK) 17 or later
- Maven 3.6 or later
- Operating System: Windows, macOS, or Linux

## Dependencies

- JUnit Jupiter 5.10.1 (for testing)
- Java Swing (included in JDK)

## Building from Source

1. Clone or download the repository
2. Open a terminal/command prompt
3. Navigate to the project root directory
4. Run the following command to build the project:
   ```bash
   mvn clean package
   ```
5. The compiled JAR file will be generated in the `target` directory as `virtual-pet-game.jar`

## Running the Game

### Using the JAR file (Recommended)
1. Navigate to the `target` directory
2. Run using: `java -jar target/game-project-1.0-SNAPSHOT.jar`
or
2. Build the project using Maven: `mvn clean package`
3. Run using: `java -jar target/game-project-1.0-SNAPSHOT.jar`

### Running from Source
Since the project uses Maven for dependency management, you must use Maven to build and run the project. Direct compilation with `javac` will not work due to missing dependencies.

To run the game after building:
```bash
mvn exec:java -Dexec.mainClass="Implementation.Main"
```

## User Guide

### Starting the Game
1. Launch the game using one of the methods above
2. The main menu will appear with options to:
   - Start a new game
   - Load a saved game
   - Access settings
   - Exit

### Game Features
- Choose from three different pet types
- Feed your pet to maintain its health
- Play with your pet to increase happiness
- Take your pet to the vet when needed
- Save and load your game progress

### Parental Controls
The game includes parental controls to ensure a safe gaming experience:
- Default password: `parent1234`
- Access parental controls through the settings menu
- Parents can set time limits and restrict certain features

## Additional Information

- The game saves progress automatically
- All game data is stored locally on your computer
- The game supports both mouse and keyboard controls
- For technical support or bug reports, please contact the development team

## Troubleshooting

If you encounter any issues:
1. Ensure you have the correct Java version installed
2. Verify that Maven is properly installed and configured
3. Check that you have sufficient disk space for saving game data
4. Make sure you have the necessary permissions to run the application

## For TAs

- The project uses Maven for dependency management and building
- All source code is located in the `Implementation` directory
- Test files are included in the `Implementation` directory
- The main entry point is `Implementation.Main`
- The game uses Java Swing for the GUI