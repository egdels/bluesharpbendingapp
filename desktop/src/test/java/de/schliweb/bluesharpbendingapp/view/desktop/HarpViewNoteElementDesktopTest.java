package de.schliweb.bluesharpbendingapp.view.desktop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import static org.mockito.Mockito.*;

class HarpViewNoteElementDesktopTest {

    @Test
    void testUpdate_withValidCents_updatesNotePaneAndRevalidatesPanel() {
        // Arrange
        JPanel mockPanel = Mockito.mock(JPanel.class);
        HarpViewNoteElementDesktop instance = HarpViewNoteElementDesktop.getInstance(mockPanel);
        instance.setColor(Color.red);
        instance.setNoteName("C");
        double cents = 50.0;

        verify(mockPanel).setLayout(any(BorderLayout.class));
        verify(mockPanel).add(any(Component.class), eq(BorderLayout.CENTER));

        reset(mockPanel); // Reset interactions with mockPanel

        // Act
        instance.update(cents);

        // Assert
        verify(mockPanel).remove(any(Component.class));
        verify(mockPanel).setLayout(any(BorderLayout.class));
        verify(mockPanel).add(any(Component.class), eq(BorderLayout.CENTER));
        verify(mockPanel).revalidate();
    }

    @Test
    void testUpdate_withNegativeCents_updatesNotePaneAndRevalidatesPanel() {
        // Arrange
        JPanel mockPanel = Mockito.mock(JPanel.class);
        HarpViewNoteElementDesktop instance = HarpViewNoteElementDesktop.getInstance(mockPanel);
        instance.setColor(Color.blue);
        instance.setNoteName("D");
        double cents = -25.0;

        verify(mockPanel).setLayout(any(BorderLayout.class));
        verify(mockPanel).add(any(Component.class), eq(BorderLayout.CENTER));

        reset(mockPanel); // Reset interactions with mockPanel

        // Act
        instance.update(cents);

        // Assert
        verify(mockPanel).remove(any(Component.class));
        verify(mockPanel).setLayout(any(BorderLayout.class));
        verify(mockPanel).add(any(Component.class), eq(BorderLayout.CENTER));
        verify(mockPanel).revalidate();
    }

    @Test
    void testUpdate_withZeroCents_updatesNotePaneAndRevalidatesPanel() {
        // Arrange
        JPanel mockPanel = Mockito.mock(JPanel.class);
        HarpViewNoteElementDesktop instance = HarpViewNoteElementDesktop.getInstance(mockPanel);
        instance.setColor(Color.green);
        instance.setNoteName("E");
        double cents = 0.0;

        verify(mockPanel).setLayout(any(BorderLayout.class));
        verify(mockPanel).add(any(Component.class), eq(BorderLayout.CENTER));

        reset(mockPanel); // Reset interactions with mockPanel

        // Act
        instance.update(cents);

        // Assert
        verify(mockPanel).remove(any(Component.class));
        verify(mockPanel).setLayout(any(BorderLayout.class));
        verify(mockPanel).add(any(Component.class), eq(BorderLayout.CENTER));
        verify(mockPanel).revalidate();
    }
}