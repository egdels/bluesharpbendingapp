package de.schliweb.bluesharpbendingapp.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AndroidModelTest {

    /**
     * Test case to verify that the `createFromString` method correctly sets the `storedLockScreenIndex`
     * when the input string contains a valid key-value pair for "setStoredLockScreenIndex".
     */
    @Test
    public void testCreateFromStringValidSingleEntry() {
        String input = "[setStoredLockScreenIndex:5]";
        AndroidModel model = AndroidModel.createFromString(input);

        assertNotNull("The model should not be null", model);
        assertEquals("The storedLockScreenIndex should be set to 5", 5, model.getStoredLockScreenIndex());
    }

    /**
     * Test case to verify that the `createFromString` method handles empty input gracefully
     * and creates a default `AndroidModel` object without setting any properties.
     */
    @Test
    public void testCreateFromStringEmptyInput() {
        String input = "[]";
        AndroidModel model = AndroidModel.createFromString(input);

        assertNotNull("The model should not be null", model);
        assertEquals("The storedLockScreenIndex should remain at its default value", 0, model.getStoredLockScreenIndex());
    }

    /**
     * Test case to verify that the `createFromString` method ignores invalid method names
     * and does not throw an exception.
     */
    @Test
    public void testCreateFromStringInvalidMethodName() {
        String input = "[setInvalidMethod:10]";
        AndroidModel model = AndroidModel.createFromString(input);

        assertNotNull("The model should not be null", model);
        assertEquals("The storedLockScreenIndex should remain at its default value", 0, model.getStoredLockScreenIndex());
    }

    /**
     * Test case to verify that the `createFromString` method handles multiple valid entries
     * correctly and sets the `storedLockScreenIndex` to the latest value provided.
     */
    @Test
    public void testCreateFromStringMultipleEntries() {
        String input = "[setStoredLockScreenIndex:5, setStoredLockScreenIndex:10]";
        AndroidModel model = AndroidModel.createFromString(input);

        assertNotNull("The model should not be null", model);
        assertEquals("The storedLockScreenIndex should be set to the latest provided value, 10", 10, model.getStoredLockScreenIndex());
    }

    /**
     * Test case to verify that the `createFromString` method handles input with invalid format
     * gracefully without throwing an exception.
     */
    @Test
    public void testCreateFromStringInvalidFormat() {
        String input = "invalid_format_string";
        AndroidModel model = AndroidModel.createFromString(input);

        assertNotNull("The model should not be null", model);
        assertEquals("The storedLockScreenIndex should remain at its default value", 0, model.getStoredLockScreenIndex());
    }
}