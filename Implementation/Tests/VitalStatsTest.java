package Implementation.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import Implementation.VitalStats;

import static org.junit.jupiter.api.Assertions.*;

class VitalStatsTest {
    private VitalStats vitalStats;
    private final int initialHealth = 100;
    private final int initialSleep = 100;
    private final int initialHunger = 100;
    private final int initialHappiness = 100;
    private final int petType = 0; // Using type 0 for most tests

    @BeforeEach
    void setUp() {
        vitalStats = new VitalStats(initialHealth, initialSleep, initialHunger, initialHappiness, petType);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(initialHealth, vitalStats.getHealth());
        assertEquals(initialSleep, vitalStats.getSleep());
        assertEquals(initialHunger, vitalStats.getHunger());
        assertEquals(initialHappiness, vitalStats.getHappiness());
        assertTrue(vitalStats.getActiveStates().isEmpty());
    }

    @Test
    void testSetters() {
        int newValue = 75;
        vitalStats.setHealth(newValue);
        vitalStats.setSleep(newValue);
        vitalStats.setHunger(newValue);
        vitalStats.setHappiness(newValue);

        assertEquals(newValue, vitalStats.getHealth());
        assertEquals(newValue, vitalStats.getSleep());
        assertEquals(newValue, vitalStats.getHunger());
        assertEquals(newValue, vitalStats.getHappiness());
    }

    @Test
    void testUpdateStatsNormalDecline() {
        vitalStats.updateStats();

        assertEquals(initialHealth, vitalStats.getHealth()); // Health shouldn't decline under normal conditions
        assertEquals(initialSleep - 2, vitalStats.getSleep()); // Type 0 has sleep decline rate of 2
        assertEquals(initialHunger - 2, vitalStats.getHunger()); // Type 0 has hunger decline rate of 2
        assertEquals(initialHappiness - 1, vitalStats.getHappiness()); // Type 0 has happiness decline of 1
        assertTrue(vitalStats.getActiveStates().isEmpty());
    }

    @Test
    void testSleepDepletion() {
        // Set sleep to 0 by calling updateStats multiple times
        while (vitalStats.getSleep() > 0) {
            vitalStats.updateStats();
        }

        // At this point, sleep should be 0 and health should have penalty applied
        assertEquals(0, vitalStats.getSleep());
        assertEquals(initialHealth - 15, vitalStats.getHealth()); // 10 is the sleep health penalty for type 0
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.SLEEPING));
        assertTrue(vitalStats.isSleeping());
    }

    @Test
    void testSleepRecovery() {
        // Force pet to sleep state
        vitalStats.setSleep(0);
        vitalStats.updateStats();
        assertTrue(vitalStats.isSleeping());

        // Sleep recovery
        int initialSleepAfterDepletion = vitalStats.getSleep();
        vitalStats.updateStats();
        assertEquals(initialSleepAfterDepletion + 8, vitalStats.getSleep()); // 8 is the sleep recovery rate for type 0

        // Sleep until max
        vitalStats.setSleep(95);
        vitalStats.updateStats();
        assertEquals(100, vitalStats.getSleep());
        assertFalse(vitalStats.isSleeping());
    }

    @Test
    void testHungerDepletion() {
        // Deplete hunger
        while (vitalStats.getHunger() > 0) {
            vitalStats.updateStats();
        }

        assertEquals(0, vitalStats.getHunger());
        assertEquals(initialHealth - 10 - 5, vitalStats.getHealth()); // -10 from sleep penalty, -5 from hunger penalty
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.HUNGRY));

        // Additional health decline when hungry
        vitalStats.updateStats();
        assertEquals(initialHealth - 10 - 5 - 1, vitalStats.getHealth()); // Additional -1 per tick
    }

    @Test
    void testHappinessDepletion() {
        // Set hunger to 0 to increase happiness decline
        vitalStats.setHunger(0);
        vitalStats.updateStats();

        // Calculate expected happiness decline
        int expectedHappinessDecline = 1 + 2; // normal + extra decline when hungry
        assertEquals(initialHappiness - expectedHappinessDecline, vitalStats.getHappiness());

        // Deplete happiness
        vitalStats.setHappiness(0);
        vitalStats.updateStats();
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.ANGRY));
    }

    @Test
    void testDeath() {
        vitalStats.setHealth(0);
        vitalStats.updateStats();

        assertTrue(vitalStats.isDead());
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.DEAD));
        assertEquals(1, vitalStats.getActiveStates().size()); // Only DEAD state should be active
    }

    @Test
    void testNoStatsUpdateWhenDead() {
        // Kill the pet
        vitalStats.setHealth(0);
        vitalStats.updateStats();

        // Record stats
        int sleepBeforeUpdate = vitalStats.getSleep();
        int hungerBeforeUpdate = vitalStats.getHunger();
        int happinessBeforeUpdate = vitalStats.getHappiness();

        // Call update
        vitalStats.updateStats();

        // Stats should not change when dead
        assertEquals(sleepBeforeUpdate, vitalStats.getSleep());
        assertEquals(hungerBeforeUpdate, vitalStats.getHunger());
        assertEquals(happinessBeforeUpdate, vitalStats.getHappiness());
    }

    @Test
    void testFeedMethod() {
        // Deplete hunger first
        vitalStats.setHunger(0);
        vitalStats.updateStats();
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.HUNGRY));

        // Feed the pet
        vitalStats.feed(50);
        assertEquals(50, vitalStats.getHunger());
        assertFalse(vitalStats.getActiveStates().contains(VitalStats.PetState.HUNGRY));

        // Feed over max
        vitalStats.feed(100);
        assertEquals(100, vitalStats.getHunger()); // Should cap at 100
    }

    @Test
    void testPlayMethod() {
        // Make pet angry first
        vitalStats.setHappiness(0);
        vitalStats.updateStats();
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.ANGRY));

        // Play with pet, but not enough to remove angry state
        vitalStats.play(20);
        assertEquals(20, vitalStats.getHappiness());
        assertTrue(vitalStats.getActiveStates().contains(VitalStats.PetState.ANGRY));

        // Play enough to remove angry state (>= 50)
        vitalStats.play(30);
        assertEquals(50, vitalStats.getHappiness());
        assertFalse(vitalStats.getActiveStates().contains(VitalStats.PetState.ANGRY));

        // Play over max
        vitalStats.play(100);
        assertEquals(100, vitalStats.getHappiness()); // Should cap at 100
    }

    @Test
    void testRecoverSleep() {
        vitalStats.setSleep(50);
        vitalStats.recoverSleep();
        assertTrue(vitalStats.isSleeping());

        vitalStats.setSleep(100);
        vitalStats.recoverSleep();
        assertFalse(vitalStats.isSleeping());
    }

    @Test
    void testIsCritical() {
        // All stats are initially at max, so nothing should be critical
        assertFalse(vitalStats.isCritical("sleep"));
        assertFalse(vitalStats.isCritical("hunger"));
        assertFalse(vitalStats.isCritical("happiness"));
        assertFalse(vitalStats.isCritical("health"));
        assertFalse(vitalStats.isCritical("overall"));

        // Set stats to critical levels (<25%)
        vitalStats.setHealth(24);
        vitalStats.setSleep(24);
        vitalStats.setHunger(24);
        vitalStats.setHappiness(24);

        assertTrue(vitalStats.isCritical("sleep"));
        assertTrue(vitalStats.isCritical("hunger"));
        assertTrue(vitalStats.isCritical("happiness"));
        assertTrue(vitalStats.isCritical("heatlh")); // Note: there's a typo in the method
        assertTrue(vitalStats.isCritical("overall"));

        // Invalid stat should return false
        assertFalse(vitalStats.isCritical("invalid"));
    }

    @Test
    void testDifferentPetTypes() {
        // Create different pet types
        VitalStats type1Pet = new VitalStats(100, 100, 100, 100, 1);
        VitalStats type2Pet = new VitalStats(100, 100, 100, 100, 2);

        // Update stats for different types
        type1Pet.updateStats();
        type2Pet.updateStats();

        // Type 1 has sleep decline rate of 1
        assertEquals(99, type1Pet.getSleep());
        // Type 2 has sleep decline rate of 2
        assertEquals(98, type2Pet.getSleep());

        // Type 1 has hunger decline rate of 1
        assertEquals(99, type1Pet.getHunger());
        // Type 2 has hunger decline rate of 1 as well
        assertEquals(99, type2Pet.getHunger());

        // Type 1 has happiness decline rate of 2
        assertEquals(98, type1Pet.getHappiness());
        // Type 2 has happiness decline rate of 1
        assertEquals(99, type2Pet.getHappiness());
    }
}