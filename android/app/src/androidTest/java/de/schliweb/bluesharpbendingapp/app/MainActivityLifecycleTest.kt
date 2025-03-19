package de.schliweb.bluesharpbendingapp.app

import android.Manifest
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import de.schliweb.bluesharpbendingapp.model.mircophone.android.MicrophoneAndroid
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


/**
 * Unit tests for verifying the lifecycle behavior of the MainActivity.
 * This test suite checks if the MainActivity correctly transitions through all lifecycle states
 * without any exceptions or undesired behavior.
 *
 * The test ensures that the activity under test is isolated and runs in a controlled environment.
 * It validates lifecycle methods like onPause, onResume, and activity recreation scenarios.
 *
 * This class uses test rules such as `ActivityScenarioRule` for managing the activity lifecycle
 * and `GrantPermissionRule` for automatically granting audio recording permissions.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityLifecycleTest {
    @Rule
    @JvmField
    var grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)
// This rule automatically grants the RECORD_AUDIO permission for the test execution
// Ensures that the test environment has the necessary permissions to simulate and interact with components requiring audio recording

    @Rule
    @JvmField
    var activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        MainActivity::class.java
    )
// Provides a managed ActivityScenario for the MainActivity
// Allows lifecycle management and testing of MainActivity in an isolated, controlled environment

    private lateinit var mockMicrophone: MicrophoneAndroid
// A mock instance of the MicrophoneAndroid class, used for unit testing to avoid interacting with the real implementation

    @Before
    fun setup() {
        // Initializes the mock object for the MicrophoneAndroid class before each test
        mockMicrophone = Mockito.mock(MicrophoneAndroid::class.java)
    }


    /**
     * Tests the complete lifecycle of an activity, transitioning through various {@link Lifecycle.State} states
     * without encountering any exceptions.
     *
     * Simulates the activity's lifecycle from creation to destruction, including intermediate states such as
     * CREATED, STARTED, RESUMED, and a recreation simulating configuration changes (e.g., screen rotation).
     *
     * Ensures that no exceptions are thrown during the transitions, verifying the stability and consistency
     * of the lifecycle handling mechanisms implemented within the activity.
     *
     * Fails the test if an exception occurs during any of the lifecycle transitions or recreations.
     */
    @Test
    fun testLifecycle_NoExceptionThroughCompleteLifecycle() {
        try {
            // Simulate the lifecycle transitions of the activity, starting from the CREATED state
            activityScenarioRule.scenario.moveToState(Lifecycle.State.CREATED)

            // Transition to the STARTED state
            activityScenarioRule.scenario.moveToState(Lifecycle.State.STARTED)

            // Transition to the RESUMED state
            activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)

            // Simulate a recreation of the activity (e.g., configuration changes like screen rotation)
            activityScenarioRule.scenario.recreate() // recreate() triggers initialization of the activity again

            // Transition back to the RESUMED state
            activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)

            // Move to the DESTROYED state to simulate a complete lifecycle tear-down
            activityScenarioRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        } catch (e: Exception) {
            // If any exception is thrown during the lifecycle transitions, the test should fail
            throw AssertionError("An exception was thrown during the lifecycle transitions", e)
        }
    }


    /**
     * Tests the lifecycle of the MainActivity, verifying its behavior during repeated pause and resume events.
     *
     * Simulates multiple onPause and onResume lifecycle callbacks to evaluate the stability and proper handling of
     * transitions within the activity. Ensures the activity behaves as expected without any runtime exceptions
     * or anomalies during these lifecycle changes.
     *
     * This test employs ActivityScenario to launch the MainActivity and transitions through specific lifecycle states
     * programmatically. ActivityScenario ensures the proper handling and cleanup of the activity during and after the test.
     */
    @Test
    fun testLifecycle() {
        // Act and Assert: Test the lifecycle events of MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // The `use` method ensures the scenario is properly closed after the execution of this block
            scenario.onActivity { activity: MainActivity ->
                // Simulate the MainActivity being paused and resumed repeatedly
                activity.onPause()  // Trigger the onPause callback
                activity.onResume() // Trigger the onResume callback
                activity.onPause()
                activity.onResume()
                activity.onPause()
                activity.onResume()
                activity.onPause()
                activity.onResume()
            }
        }
    }

}

