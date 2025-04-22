package de.schliweb.bluesharpbendingapp.model.microphone.desktop;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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