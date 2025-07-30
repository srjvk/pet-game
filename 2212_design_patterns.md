**Design Patterns Documentation**

## **1\. Input Feedback**

### **Overview**

Input feedback is a crucial aspect of user interaction design that ensures users receive immediate and clear responses from the system when they perform actions. This can enhance usability by making users feel informed and confident in their interactions.

### **Why it is Appropriate for the Project**

In a virtual pet game, feedback is essential for guiding both players and parents in their actions. Without feedback, users might feel lost or unsure whether their actions were registered.

### **Application in the Project**

* **Parental Controls**: Provide visual or auditory feedback when settings are changed, such as displaying confirmation messages when enabling/disabling restrictions.  
* **Player Commands**: When the player issues a command to the pet (e.g., feeding, playing), feedback should be provided through:  
  * Animations of the pet reacting.  
  * Sound effects confirming the action.  
  * Changing button states (e.g., disabling buttons for commands that are on cooldown).  
* **Inline Alerts and Notifications**: Error messages for invalid actions (e.g., trying to feed the pet when inventory is empty) should be clear and informative.

---

## **2\. Settings**

### **Overview**

The settings pattern provides a structured way to manage user preferences by ensuring they are accessible, well-organized, and easy to navigate.

### **Why it is Appropriate for the Project**

A well-structured settings menu enhances usability and ensures that users can efficiently manage their preferences without frustration.

### **Application in the Project**

* **Clustering Preferences**:  
  * A small number of settings (≤7) should be displayed as a simple list.  
  * Larger sets (8–16) should be grouped under labeled sections.  
  * If settings exceed 16, submenus should be introduced for better organization.  
    * These are just guidelines. We can slightly bend the rules if appropriate  
* **Parental Controls Menu**: Options for setting playtime restrictions, viewing statistics, and reviving pets should be categorized separately.  
* **Default Settings**: Sensible defaults should be chosen to minimize the need for user adjustments while preventing unintended risks.

---

## **3\. Variable Rewards**

### **Overview**

The variable rewards pattern leverages randomness to create engagement and motivation. Users are more likely to stay engaged when rewards are unpredictable but fair.

### **Why it is Appropriate for the Project**

A sense of unpredictability keeps players engaged and encourages them to interact regularly with their pet.

### **Application in the Project**

* **Pet Reactions**: The pet should occasionally exhibit different responses to the same command, maintaining novelty.  
* **Item Acquisition**: When obtaining items (e.g., food, gifts), a randomized system could determine the specific item received, ensuring variety.  
* **Avoiding Extinction**: Players should always receive some form of reward or acknowledgment, even if minimal, to prevent disengagement.  
* **Negative Rewards**: If the pet is neglected, it should exhibit displeasure, but not to the extent that discourages the player entirely.

---

## **4\. Praise**

### **Overview**

Encouraging positive behavior through praise can reinforce desired actions, making them more likely to be repeated.

### **Why it is Appropriate for the Project**

A virtual pet game should reward players for taking care of their pet, reinforcing the habit of regular interaction.

### **Application in the Project**

* **Visual and Audio Feedback**: When the pet is in a good state, it should display a happy animation and play a cheerful sound effect.  
* **Score Rewards**: Taking good care of the pet should increase the player’s score.  
* **Notifications**: Text-based praise messages (e.g., "Your pet loves playing with you\!") can further reinforce positive actions.

---

## **5\. State Pattern**

### **Overview**

The state pattern is used to manage an object’s behavior based on its current state. It involves creating separate classes for different states, with a context class handling transitions.

### **Why it is Appropriate for the Project**

The pet in this game has clearly defined states (e.g., happy, hungry, angry, asleep, dead). Managing these states using the state pattern ensures better modularity and easier maintenance.

### **Application in the Project**

* **Separate State Classes**: Each pet state (HappyState, HungryState, AngryState, SleepingState, DeadState) should be implemented as a class.  
* **Context Class**: The pet class should delegate behavior to the current state object, allowing dynamic transitions.  
* **State Transitions**: Defined transitions between states ensure consistency in gameplay logic.

---

## **6\. Command Pattern**

### **Overview**

The command pattern encapsulates actions as objects, allowing requests to be parameterized, queued, and executed dynamically.

### **Why it is Appropriate for the Project**

In a virtual pet game, player actions (e.g., feeding, playing, exercising) can be encapsulated as commands, making it easier to manage undo/redo functionality and implement cooldowns.

### **Application in the Project**

* **Encapsulation of Player Actions**:  
  * `FeedCommand`: Increases fullness and decreases item count.  
  * `PlayCommand`: Increases happiness and triggers a cooldown.  
  * `ExerciseCommand`: Affects multiple stats (hunger, health, sleep).  
* **Invoker Component**: The UI should send commands to an invoker that manages their execution.  
* **Undo Feature (Optional)**: Parents might have an option to undo recent actions in case of mistakes.

---

## **Conclusion**

These design patterns provide a structured approach to implementing key gameplay and UI elements. Input feedback ensures clarity, settings organization enhances usability, and variable rewards improve engagement. Praise reinforces positive behavior, while the state and command patterns manage game logic efficiently. By applying these patterns, the virtual pet game will be both intuitive and scalable.

## Sources:

[https://ui-patterns.com/patterns/InputFeedback](https://ui-patterns.com/patterns/InputFeedback)  
[https://ui-patterns.com/patterns/settings](https://ui-patterns.com/patterns/settings)  
[https://ui-patterns.com/patterns/Variable-rewards](https://ui-patterns.com/patterns/Variable-rewards)  
[https://ui-patterns.com/patterns/Praise](https://ui-patterns.com/patterns/Praise)  
[https://refactoring.guru/design-patterns/state](https://refactoring.guru/design-patterns/state)  
[https://www.oodesign.com/command-pattern](https://www.oodesign.com/command-pattern) 