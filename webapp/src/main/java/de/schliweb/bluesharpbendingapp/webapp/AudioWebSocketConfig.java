package de.schliweb.bluesharpbendingapp.webapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


/**
 * Configuration class for the WebSocket setup.
 * Enables WebSocket support and configures a WebSocket endpoint for handling audio streaming.
 */
@Configuration
@EnableWebSocket
public class AudioWebSocketConfig implements WebSocketConfigurer {

    /**
     * A WebSocket handler that facilitates real-time audio streaming communication.
     * This handler processes binary WebSocket messages containing audio data from clients,
     * interprets pitch information, and sends analysis results back to the client as text messages.
     * It is responsible for interpreting incoming audio data, normalizing it, detecting pitch,
     * and formatting the result for response transmission.
     * <p>
     * This instance is defined as a Spring-managed bean and is registered as
     * the handler for a specific WebSocket endpoint in the application configuration.
     */
    private final AudioWebSocketHandler audioWebSocketHandler;

    @Value("${websocket.allowed.origins}")
    private String[] allowedOrigins;


    /**
     * Constructs an instance of AudioWebSocketConfig with the specified AudioWebSocketHandler.
     *
     * @param audioWebSocketHandler the WebSocket handler responsible for processing binary audio messages
     *                              and sending processed results back to the client
     */
    public AudioWebSocketConfig(AudioWebSocketHandler audioWebSocketHandler) {
        this.audioWebSocketHandler = audioWebSocketHandler;
    }

    /**
     * Registers WebSocket handlers to enable WebSocket communication within the application.
     * This method defines the WebSocket endpoint and configures it to use a specific handler
     * for processing messages.
     *
     * @param registry the WebSocketHandlerRegistry used to register WebSocket handlers
     *                 and configure their associated endpoints.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(audioWebSocketHandler, "/audio-stream-socket")
                .setAllowedOrigins(allowedOrigins);
    }
}
