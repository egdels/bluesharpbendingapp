package de.schliweb.bluesharpbendingapp.model

import org.junit.Assert
import org.junit.Test

class AndroidModelTest {
    /**
     * Test case to verify that the `createFromString` method correctly sets the `storedLockScreenIndex`
     * when the input string contains a valid key-value pair for "setStoredLockScreenIndex".
     */
    @Test
    fun testCreateFromStringValidSingleEntry() {
        val input = "[setStoredLockScreenIndex:5]"
        val model = AndroidModel.createFromString(input)

        Assert.assertNotNull("The model should not be null", model)
        Assert.assertEquals(
            "The storedLockScreenIndex should be set to 5",
            5,
            model.storedLockScreenIndex.toLong()
        )
    }

    /**
     * Test case to verify that the `createFromString` method handles empty input gracefully
     * and creates a default `AndroidModel` object without setting any properties.
     */
    @Test
    fun testCreateFromStringEmptyInput() {
        val input = "[]"
        val model = AndroidModel.createFromString(input)

        Assert.assertNotNull("The model should not be null", model)
        Assert.assertEquals(
            "The storedLockScreenIndex should remain at its default value",
            0,
            model.storedLockScreenIndex.toLong()
        )
    }

    /**
     * Test case to verify that the `createFromString` method ignores invalid method names
     * and does not throw an exception.
     */
    @Test
    fun testCreateFromStringInvalidMethodName() {
        val input = "[setInvalidMethod:10]"
        val model = AndroidModel.createFromString(input)

        Assert.assertNotNull("The model should not be null", model)
        Assert.assertEquals(
            "The storedLockScreenIndex should remain at its default value",
            0,
            model.storedLockScreenIndex.toLong()
        )
        Assert.assertEquals(
            "The storedConfidenceIndex should remain at its default value",
            0,
            model.storedConfidenceIndex.toLong()
        )
    }

    /**
     * Test case to verify that the `createFromString` method handles multiple valid entries
     * correctly and sets the `storedLockScreenIndex` to the latest value provided.
     */
    @Test
    fun testCreateFromStringMultipleEntries() {
        val input = "[setStoredLockScreenIndex:5, setStoredLockScreenIndex:10]"
        val model = AndroidModel.createFromString(input)

        Assert.assertNotNull("The model should not be null", model)
        Assert.assertEquals(
            "The storedLockScreenIndex should be set to the latest provided value, 10",
            10,
            model.storedLockScreenIndex.toLong()
        )
    }

    /**
     * Test case to verify that the `createFromString` method handles input with invalid format
     * gracefully without throwing an exception.
     */
    @Test
    fun testCreateFromStringInvalidFormat() {
        val input = "invalid_format_string"
        val model = AndroidModel.createFromString(input)

        Assert.assertNotNull("The model should not be null", model)
        Assert.assertEquals(
            "The storedLockScreenIndex should remain at its default value",
            0,
            model.storedLockScreenIndex.toLong()
        )
    }
}