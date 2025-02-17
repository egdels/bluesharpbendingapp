package de.schliweb.bluesharpbendingapp.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.utils.PitchDetectionUtil;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(AudioWebSocketHandler.class);

    /**
     * Handles incoming binary WebSocket messages.
     */
    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        logger.info("Received new binary message from Session ID: {}", session.getId());

        // Extract the binary data from the message payload
        ByteBuffer byteBuffer = message.getPayload();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // Ensure compatibility with the expected byte order

        // Convert ByteBuffer to a Float32 array
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        float[] floatArray = new float[floatBuffer.remaining()];
        floatBuffer.get(floatArray);

        logger.debug("Received Float32 data (first 10 values): {}", Arrays.toString(Arrays.copyOf(floatArray, 10)));

        // Process the audio data
        String jsonResponse = processAudioBuffer(floatArray);

        // Send the processed result back to the client as a JSON response
        try {
            session.sendMessage(new org.springframework.web.socket.TextMessage(jsonResponse));
        } catch (Exception e) {
            logger.error("Error sending the message to the client.", e);
        }
    }

    /**
     * Processes the received audio buffer to detect pitch and generate JSON output.
     */
    private String processAudioBuffer(float[] floatArray) {
        // Step 1: Normalize the float array (if required)
        // floatArray = normalizeFloatArray(floatArray);

        // Step 2: Convert Float32 to Double array (if required)
        double[] doubleArray = convertFloatToDoubleArray(floatArray);

        // Step 3: Perform pitch detection and create JSON response
        try {
            int sampleRate = 44100; // Expected sample rate for the pitch detection utility
            PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(doubleArray, sampleRate);

            if (result.getPitch() > 0) {
                // Map the detected pitch to a musical note
                String noteName = NoteLookup.getNoteName(result.getPitch());
                double noteFrequency = NoteLookup.getNoteFrequency(noteName);
                double cents = NoteUtils.getCents(noteFrequency, result.getPitch());

                logger.info("Detected pitch: {} Hz with confidence: {} and note name: {}", result.getPitch(), result.getConfidence(), noteName);

                // Create JSON response using ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(
                        new PitchResult(noteName, Math.round(cents), result.getConfidence()) // Round cents to the nearest integer
                );
            } else {
                logger.warn("No pitch detected.");
                return "{\"error\": \"No pitch detected\"}";
            }
        } catch (Exception e) {
            logger.error("Error processing the audio buffer.", e);
            return "{\"error\": \"Error processing audio buffer\"}";
        }
    }

    /**
     * Helper class for formatting pitch detection results into JSON.
     */
    @Getter
    private static class PitchResult {
        private final String noteName;
        private final long cents;
        private final double confidence;

        public PitchResult(String noteName, long cents, double confidence) {
            this.noteName = noteName;
            this.cents = cents;
            this.confidence = confidence;
        }
    }

    /**
     * Converts a Float32 array to a Double array.
     */
    private double[] convertFloatToDoubleArray(float[] floatArray) {
        double[] doubleArray = new double[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) {
            doubleArray[i] = floatArray[i];
        }
        return doubleArray;
    }

    /**
     * Indicates whether partial WebSocket messages are supported.
     */
    @Override
    public boolean supportsPartialMessages() {
        return true; // Enable support for fragmented WebSocket messages
    }
}

