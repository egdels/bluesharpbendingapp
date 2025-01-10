package de.schliweb.bluesharpbendingapp.view.desktop;

import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ItemEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SettingsViewDesktopTest {

    @Test
    void testAddHarpSettingsViewHandlerForKeySelection() {
        // Arrange
        HarpSettingsViewHandler mockHandler = Mockito.mock(HarpSettingsViewHandler.class);
        SettingsViewDesktop settingsViewDesktop = new SettingsViewDesktop(false);
        String[] keys = {"Key1", "Key2", "Key3"};
        settingsViewDesktop.setKeys(keys);
        settingsViewDesktop.addHarpSettingsViewHandler(mockHandler);

        // Act
        settingsViewDesktop.comboKeys.setSelectedIndex(1);
        settingsViewDesktop.comboKeys.dispatchEvent(new ItemEvent(settingsViewDesktop.comboKeys, ItemEvent.ITEM_STATE_CHANGED, keys[1], ItemEvent.SELECTED));

        // Assert
        verify(mockHandler, times(1)).handleKeySelection(1);
    }


    @Test
    void testAddHarpSettingsViewHandlerForTuneSelection() {
        // Arrange
        HarpSettingsViewHandler mockHandler = Mockito.mock(HarpSettingsViewHandler.class);
        SettingsViewDesktop settingsViewDesktop = new SettingsViewDesktop(false);
        String[] tunes = {"Tune1", "Tune2", "Tune3"};
        settingsViewDesktop.setTunes(tunes);
        settingsViewDesktop.addHarpSettingsViewHandler(mockHandler);

        // Act
        settingsViewDesktop.comboTunes.setSelectedIndex(2);
        settingsViewDesktop.comboTunes.dispatchEvent(new ItemEvent(settingsViewDesktop.comboTunes, ItemEvent.ITEM_STATE_CHANGED, tunes[2], ItemEvent.SELECTED));

        // Assert
        verify(mockHandler, times(1)).handleTuneSelection(2);
    }

    @Test
    void testSetKeys() {
        // Arrange
        SettingsViewDesktop settingsViewDesktop = new SettingsViewDesktop(false);
        String[] keys = {"KeyA", "KeyB", "KeyC"};

        // Act
        settingsViewDesktop.setKeys(keys);
        ComboBoxModel<String> model = settingsViewDesktop.comboKeys.getModel();

        // Assert
        assertEquals(3, model.getSize());
        assertEquals("KeyA", model.getElementAt(0));
        assertEquals("KeyB", model.getElementAt(1));
        assertEquals("KeyC", model.getElementAt(2));
    }

    @Test
    void testDefaultSelectionForKeys() {
        // Arrange
        SettingsViewDesktop settingsViewDesktop = new SettingsViewDesktop(false);
        String[] keys = {"KeyX", "KeyY", "KeyZ"};

        // Act
        settingsViewDesktop.setKeys(keys);
        String selectedKey = (String) settingsViewDesktop.comboKeys.getSelectedItem();

        // Assert
        assertEquals("KeyX", selectedKey, "Default selected key should be the first one.");
    }

}