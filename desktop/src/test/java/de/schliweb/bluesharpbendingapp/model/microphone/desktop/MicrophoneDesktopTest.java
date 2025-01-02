package de.schliweb.bluesharpbendingapp.model.microphone.desktop;

import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.TargetDataLine;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MicrophoneDesktopTest {

    /**
     * Test open method: Ensure that it correctly initializes the executor services and audio queue.
     */
    @Test
    void testOpenInitializesExecutorServicesAndQueue() {
        MicrophoneDesktop microphoneDesktop = new MicrophoneDesktop();
        microphoneDesktop.setName(0);
        microphoneDesktop.open();

        assertNotNull(microphoneDesktop.audioDataQueue);
        assertNotNull(microphoneDesktop.executorService);
        assertNotNull(microphoneDesktop.processingExecutor);

        microphoneDesktop.close();
    }

    /**
     * Test open method: Check that audio data is added to the queue when read.
     */
    @Test
    void testOpenAudioDataAddedToQueue() throws InterruptedException {
        MicrophoneDesktop microphoneDesktop = new MicrophoneDesktop();
        microphoneDesktop.setName(0);

        TargetDataLine mockLine = mock(TargetDataLine.class);
        when(mockLine.read(any(), eq(0), eq(MicrophoneDesktop.BUFFER_SIZE))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            for (int i = 0; i < MicrophoneDesktop.BUFFER_SIZE; i++) {
                buffer[i] = (byte) i;
            }
            return MicrophoneDesktop.BUFFER_SIZE;
        });


        microphoneDesktop.open();

        BlockingQueue<byte[]> queue = microphoneDesktop.audioDataQueue;

        byte[] data = queue.poll(2, TimeUnit.SECONDS);

        assertNotNull(data);
        assertEquals(MicrophoneDesktop.BUFFER_SIZE, data.length);

        microphoneDesktop.close();
    }

    /**
     * Test open method: Verify audio processing thread handles interruptions.
     */
    @Test
    void testOpenProcessingThreadHandlesInterruptions() {
        MicrophoneDesktop microphoneDesktop = new MicrophoneDesktop();
        microphoneDesktop.setName(0);
        microphoneDesktop.open();

        microphoneDesktop.executorService.shutdownNow();
        microphoneDesktop.processingExecutor.shutdownNow();

        assertTrue(microphoneDesktop.executorService.isShutdown());
        assertTrue(microphoneDesktop.processingExecutor.isShutdown());
    }

    /**
     * Test open method: Verify processAudioData processes incoming audio correctly.
     */
    @Test
    void testOpenProcessesAudioDataCorrectly() {
        MicrophoneDesktop microphoneDesktop = spy(new MicrophoneDesktop());
        MicrophoneHandler mockHandler = mock(MicrophoneHandler.class);
        microphoneDesktop.setMicrophoneHandler(mockHandler);
        microphoneDesktop.setName(0);

        byte[] testAudioData = new byte[MicrophoneDesktop.BUFFER_SIZE];
        for (int i = 0; i < testAudioData.length; i++) {
            testAudioData[i] = (byte) i;
        }

        doNothing().when(mockHandler).handle(anyDouble(), anyDouble());
        microphoneDesktop.processAudioData(testAudioData, testAudioData.length);

        verify(mockHandler, atLeastOnce()).handle(anyDouble(), anyDouble());
    }
}