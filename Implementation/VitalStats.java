package Implementation;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code VitalStats} class is used to contain the {@code VirtualPets} fetched statistics
 * It also contains methods to get/set stats, as well as update the state of the pet
 */
public class VitalStats {
    /** Public enumeration for the different states a pet can be in */
    public enum PetState{
        NORMAL,
        SLEEPING,
        HUNGRY,
        ANGRY,
        DEAD
    }
    /** The health stat of the pet */
    private int health;
    /** The sleep stat of the pet */
    private int sleep;
    /** The fullness stat of the pet */
    private int hunger;
    /** The happiness stat of the pet */
    private int happiness;
    /** The type of pet */
    private int type;

    //TODO: Have different maximums for different pets
    /** The maximum stat limit for every statistic */
    private final int MAX_STAT = 100;

    /** Sleep decline rate per tick */
    private final int[] sleepDeclineRate = {2, 1, 2};
    /** Sleep recovery rate per tick */
    private final int[] sleepRecoveryRate = {8, 6, 10};
    /** Hunger decline rate per tick */
    private final int[] fullnessDeclineRate = {2, 1, 1};
    /** Happiness decline rate per tick */
    private final int[] happinessDeclineRate = {1, 2, 1};
    /** Happiness decline rate while hungry per tick */
    private final int[] extraHappinessDeclineRate = {2, 2, 1};
    /** Flat hunger penalty when sleep is 0 */
    private final int[] sleepHealthPenalty = {10, 5, 5};
    /** Hunger health flat penalty when hunger is 0 */
    private final int[] hungerHealthPenalty = {5, 10, 5};
    /** Hunger health decline per tick */
    private final int[] hungerHealthDecline = {1, 1, 1};

    /** The current state of the pet */
    private Set<PetState> activeStates;

    /**
     * Constructor for the VitalStats class, which instantiates the instance variables
     * Updates the state of the pet
     * @param health The fetched health stat from csv
     * @param sleep The fetched sleep stat from csv
     * @param hunger The fetched fullness stat from csv
     * @param happiness The fetched happiness stat from csv
     */
    public VitalStats(int health, int sleep, int hunger, int happiness, int type){
        this.health = health;
        this.sleep = sleep;
        this.hunger = hunger;
        this.happiness = happiness;
        this.activeStates = new HashSet<>();
        this.type = type;
        updateStates();
    }

    /**
     * Gets the health of the pet
     * @return The health of the pet, an integer
     */
    public int getHealth(){
        return this.health;
    }

    /**
     * Sets the health according to a given health integer
     * @param health The new health of the pet, an integer
     */
    public void setHealth(int health){
        this.health = health;

    }
    /**
     * Gets the sleep of the pet
     * @return The sleep of the pet, an integer
     */
    public int getSleep(){
        return this.sleep;
    }
    /**
     * Sets the sleep according to a given sleep integer
     * @param sleep The new sleep of the pet, an integer
     */
    public void setSleep(int sleep){
        this.sleep = sleep;
    }
    /**
     * Gets the fullness of the pet
     * @return The fullness of the pet, an integer
     */
    public int getHunger(){
        return this.hunger;
    }
    /**
     * Sets the fullness according to a given fullness integer
     * @param hunger The new fullness of the pet, an integer
     */
    public void setHunger(int hunger){
        this.hunger = hunger;
    }
    /**
     * Gets the happiness of the pet
     * @return The happiness of the pet, an integer
     */
    public int getHappiness() {
        return this.happiness;
    }
    /**
     * Sets the happiness according to a given happiness integer
     * @param happiness The new happiness of the pet, an integer
     */
    public void setHappiness(int happiness){
        this.happiness = happiness;
    }
    /**
     * Gets the state of the pet
     * @return The states of the pet, a set of PetState enumerations
     */
    public Set<PetState> getActiveStates(){
        return this.activeStates;
    }

    /**
     * Updates the stats of the pet per normal stat decline, applying penalties and updating states
     * Gets called per tick, updating states accordingly
     */
    public void updateStats(){
        // If pet is dead, no further updates occur
        if(activeStates.contains(PetState.DEAD)) {
            return;
        }

        // Process the sleep state
        if(activeStates.contains(PetState.SLEEPING)) {
            // While sleeping, the sleep stat recovers gradually
            sleep = Math.min(MAX_STAT, sleep + sleepRecoveryRate[type]);

            // Once sleep hits max, then remove the state as the pet is fully rested
            if(sleep >= MAX_STAT) {
                activeStates.remove(PetState.SLEEPING);
            }
        }
        else {
            // If not sleeping, sleep declines
            sleep = Math.max(0, sleep - sleepDeclineRate[type]);
            if(sleep == 0) {
                // When sleep depletes, apply a flat health penalty and enter SLEEPING state
                health = Math.max(0, health - sleepHealthPenalty[type]);
                activeStates.add(PetState.SLEEPING);
            }
        }

        // Process hunger state
        hunger = Math.max(0, hunger - fullnessDeclineRate[type]);
        if(hunger == 0 && !activeStates.contains(PetState.HUNGRY)) {
            activeStates.add(PetState.HUNGRY);
            // Apply a flat health penalty first time when hungry
            health = Math.max(0, health - hungerHealthPenalty[type]);
        }
        else if(hunger == 0 && activeStates.contains(PetState.HUNGRY)){
            // Apply a tick-based health decline otherwise
            health = Math.max(0, health - hungerHealthDecline[type]);
        }
        else {
            // When hunger is no longer 0, remove the hungry state
            activeStates.remove(PetState.HUNGRY);
        }

        // Process the happiness state
        int currentHappinessDecline = happinessDeclineRate[type];
        if(activeStates.contains(PetState.HUNGRY)) {
            // Increase happiness decline rate when hungry.
            currentHappinessDecline += extraHappinessDeclineRate[type];
        }
        happiness = Math.max(0, happiness - currentHappinessDecline);

        // If happiness is 0, then set the angry state
        if(happiness == 0) {
            activeStates.add(PetState.ANGRY);
        }
        // If angry and happiness is over 50%, remove the angry state
        else if(activeStates.contains(PetState.ANGRY) && happiness >= MAX_STAT / 2) {
            activeStates.remove(PetState.ANGRY);
        }

        // Check for death state at the end
        if(health == 0) {
            activeStates.clear();
            activeStates.add(PetState.DEAD);
        }
    }

    /**
     * Returns true if a stat is below 25%
     * @param stat The stat to be checked
     * @return True or false, if the pet is in critical condition
     */
    public boolean isCritical(String stat) {
        switch (stat.toLowerCase()) {
            case "sleep":
                return this.sleep < MAX_STAT * 0.25;
            case "hunger":
                return this.hunger < MAX_STAT * 0.25;
            case "happiness":
                return this.happiness < MAX_STAT * 0.25;
            case "heatlh":
                return this.health < MAX_STAT * 0.25;
            case "overall":
                return ((this.sleep + this.hunger + this.happiness + this.health) / 4) < MAX_STAT * 0.25;
            default:
                return false;
        }
    }

    /**
     * Increases fullness when the pet is fed.
     * Exits the HUNGRY state if fullness becomes positive.
     * @param amount the amount to feed the pet
     */
    public void feed(int amount) {
        hunger = Math.min(MAX_STAT, hunger + amount);
        if(hunger > 0) {
            activeStates.remove(PetState.HUNGRY);
        }
        updateStates();
    }

    /**
     * Increases happiness when the pet is played with.
     * Exits the ANGRY state if happiness reaches at least half the maximum.
     * @param amount the amount to feed the pet
     */
    public void play(int amount) {
        happiness = Math.min(MAX_STAT, happiness + amount);
        if(happiness >= MAX_STAT / 2) {
            activeStates.remove(PetState.ANGRY);
        }
        updateStates();
    }

    /**
     * Directly recovers sleep. This method can be called if, for instance, an external command
     * is used to wake the pet up.
     */
    public void recoverSleep() {
        activeStates.add(PetState.SLEEPING);
        if(sleep >= MAX_STAT) {
            activeStates.remove(PetState.SLEEPING);
        }
        updateStates();
    }

    /**
     * Checks if pet is sleeping
     * @return true or false
     */
    public boolean isSleeping(){
        return getActiveStates().contains(PetState.SLEEPING);
    }

    /**
     * Checks if pet is dead
     * @return true or false
     */
    public boolean isDead(){
        return getActiveStates().contains(PetState.DEAD);
    }

    /**
     * Determines the pet's state based on its current vital stats.
     */
    private void updateStates() {
        if(health == 0) {
            activeStates.clear();
            activeStates.add(PetState.DEAD);
            return;
        }
        if(sleep == 0) {
            activeStates.add(PetState.SLEEPING);
        }
        else if(activeStates.contains(PetState.SLEEPING) && sleep >= MAX_STAT) {
            activeStates.remove(PetState.SLEEPING);
        }
        if(hunger == 0) {
            activeStates.add(PetState.HUNGRY);
        }
        else {
            activeStates.remove(PetState.HUNGRY);
        }
        if(happiness == 0) {
            activeStates.add(PetState.ANGRY);
        }
        else if(activeStates.contains(PetState.ANGRY) && happiness >= MAX_STAT / 2) {
            activeStates.remove(PetState.ANGRY);
        }
    }
}
