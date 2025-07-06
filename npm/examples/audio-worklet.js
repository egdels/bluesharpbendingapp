/**
 * Audio Worklet Example
 * 
 * This example demonstrates how to use the PitchProcessor AudioWorklet
 * for real-time pitch detection in a browser environment.
 * 
 * Note: This example only works in a browser environment that supports
 * the AudioWorklet API (Chrome 66+, Firefox 76+, Safari 14.1+).
 */

// Import the PitchProcessor from the package
import { PitchProcessor } from 'https://esm.sh/bluesharp-pitch-detection@latest/audio-worklet';
import {NoteUtils} from 'https://esm.sh/bluesharp-pitch-detection@latest';

/**
 * Initialize the audio worklet for pitch detection
 */
async function initAudioWorklet() {
  // Create UI elements for displaying results
  const pitchDisplay = document.getElementById('pitch-display') || createUIElements();
  const noteDisplay = document.getElementById('note-display');
  const confidenceDisplay = document.getElementById('confidence-display');
  const statusDisplay = document.getElementById('status-display');

  // Update status
  statusDisplay.textContent = 'Initializing audio context...';

  try {
    // Create audio context
    const audioContext = new AudioContext();

    // Update status
    statusDisplay.textContent = 'Loading audio worklet module...';

    // Register the processor module
    // Note: In a real application, you would use the path to the module in your node_modules
    await audioContext.audioWorklet.addModule('/npm/src/audio-worklet.js');

    // Update status
    statusDisplay.textContent = 'Audio worklet loaded. Waiting for microphone access...';

    // Request microphone access
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    // Create media stream source
    const microphoneSource = audioContext.createMediaStreamSource(stream);

    // Create the AudioWorkletNode with the PitchProcessor
    const pitchNode = new AudioWorkletNode(audioContext, 'pitch-processor', {
      processorOptions: {
        minFrequency: 80,    // Minimum frequency to detect (Hz)
        maxFrequency: 1000,  // Maximum frequency to detect (Hz)
        algorithm: 'hybrid'  // Algorithm to use: 'yin', 'mpm', 'fft', or 'hybrid'
      }
    });

    // Listen for messages from the processor
    pitchNode.port.onmessage = (event) => {
      const { pitch, confidence } = event.data;

      // Update the UI with the detected pitch
      if (pitch > 0) {
        const noteName = NoteUtils.frequencyToNoteName(pitch);
        const cents = NoteUtils.frequencyToCents(pitch);
        const centsText = cents !== 0 ? ` ${cents > 0 ? '+' : ''}${cents.toFixed(0)} cents` : '';

        pitchDisplay.textContent = `${pitch.toFixed(1)} Hz`;
        noteDisplay.textContent = `${noteName}${centsText}`;
        confidenceDisplay.textContent = `${(confidence * 100).toFixed(0)}%`;

        // Update color based on confidence
        if (confidence > 0.8) {
          noteDisplay.style.color = '#4CAF50'; // Green for high confidence
        } else if (confidence > 0.5) {
          noteDisplay.style.color = '#FFC107'; // Yellow for medium confidence
        } else {
          noteDisplay.style.color = '#F44336'; // Red for low confidence
        }
      } else {
        pitchDisplay.textContent = 'No pitch detected';
        noteDisplay.textContent = '-';
        confidenceDisplay.textContent = '-';
        noteDisplay.style.color = '#757575'; // Gray for no detection
      }
    };

    // Connect the microphone to the pitch node
    microphoneSource.connect(pitchNode);

    // Update status
    statusDisplay.textContent = 'Pitch detection active. Make some noise!';

    // Add a stop button
    const stopButton = document.createElement('button');
    stopButton.textContent = 'Stop';
    stopButton.addEventListener('click', () => {
      // Disconnect and close
      microphoneSource.disconnect();
      stream.getTracks().forEach(track => track.stop());
      audioContext.close();

      // Update status
      statusDisplay.textContent = 'Pitch detection stopped.';
      pitchDisplay.textContent = '-';
      noteDisplay.textContent = '-';
      confidenceDisplay.textContent = '-';

      // Remove stop button and add start button
      stopButton.remove();
      document.body.appendChild(createStartButton());
    });

    // Add the stop button to the UI
    document.getElementById('button-container').innerHTML = '';
    document.getElementById('button-container').appendChild(stopButton);

  } catch (error) {
    console.error('Error initializing audio worklet:', error);
    statusDisplay.textContent = `Error: ${error.message}`;
  }
}

/**
 * Create UI elements for displaying pitch detection results
 */
function createUIElements() {
  // Create container
  const container = document.createElement('div');
  container.style.fontFamily = 'Arial, sans-serif';
  container.style.maxWidth = '600px';
  container.style.margin = '0 auto';
  container.style.padding = '20px';
  container.style.textAlign = 'center';

  // Create title
  const title = document.createElement('h1');
  title.textContent = 'Real-time Pitch Detection';
  container.appendChild(title);

  // Create status display
  const statusDisplay = document.createElement('div');
  statusDisplay.id = 'status-display';
  statusDisplay.style.margin = '20px 0';
  statusDisplay.style.padding = '10px';
  statusDisplay.style.backgroundColor = '#f0f0f0';
  statusDisplay.style.borderRadius = '5px';
  statusDisplay.textContent = 'Waiting to start...';
  container.appendChild(statusDisplay);

  // Create results container
  const resultsContainer = document.createElement('div');
  resultsContainer.style.display = 'flex';
  resultsContainer.style.flexDirection = 'column';
  resultsContainer.style.alignItems = 'center';
  resultsContainer.style.margin = '20px 0';
  resultsContainer.style.padding = '20px';
  resultsContainer.style.backgroundColor = '#f9f9f9';
  resultsContainer.style.borderRadius = '10px';
  resultsContainer.style.boxShadow = '0 2px 5px rgba(0,0,0,0.1)';

  // Create note display (large)
  const noteDisplay = document.createElement('div');
  noteDisplay.id = 'note-display';
  noteDisplay.style.fontSize = '72px';
  noteDisplay.style.fontWeight = 'bold';
  noteDisplay.style.margin = '10px 0';
  noteDisplay.textContent = '-';
  resultsContainer.appendChild(noteDisplay);

  // Create pitch display
  const pitchDisplay = document.createElement('div');
  pitchDisplay.id = 'pitch-display';
  pitchDisplay.style.fontSize = '24px';
  pitchDisplay.style.margin = '10px 0';
  pitchDisplay.textContent = '-';
  resultsContainer.appendChild(pitchDisplay);

  // Create confidence display
  const confidenceDisplay = document.createElement('div');
  confidenceDisplay.id = 'confidence-display';
  confidenceDisplay.style.fontSize = '18px';
  confidenceDisplay.style.margin = '10px 0';
  confidenceDisplay.textContent = '-';
  resultsContainer.appendChild(confidenceDisplay);

  container.appendChild(resultsContainer);

  // Create button container
  const buttonContainer = document.createElement('div');
  buttonContainer.id = 'button-container';
  buttonContainer.style.margin = '20px 0';
  buttonContainer.appendChild(createStartButton());
  container.appendChild(buttonContainer);

  // Add container to the document
  document.body.appendChild(container);

  return pitchDisplay;
}

/**
 * Create a start button for the UI
 */
function createStartButton() {
  const startButton = document.createElement('button');
  startButton.textContent = 'Start Pitch Detection';
  startButton.style.padding = '10px 20px';
  startButton.style.fontSize = '16px';
  startButton.style.backgroundColor = '#4CAF50';
  startButton.style.color = 'white';
  startButton.style.border = 'none';
  startButton.style.borderRadius = '5px';
  startButton.style.cursor = 'pointer';
  startButton.addEventListener('click', initAudioWorklet);
  return startButton;
}

/**
 * Initialize the example when the page loads
 */
if (typeof window !== 'undefined') {
  document.addEventListener('DOMContentLoaded', () => {
    // Check if AudioWorklet is supported
    if (window.AudioContext && 'audioWorklet' in AudioContext.prototype) {
      createUIElements();
    } else {
      // Display error if AudioWorklet is not supported
      const errorContainer = document.createElement('div');
      errorContainer.style.fontFamily = 'Arial, sans-serif';
      errorContainer.style.maxWidth = '600px';
      errorContainer.style.margin = '0 auto';
      errorContainer.style.padding = '20px';
      errorContainer.style.textAlign = 'center';

      const errorTitle = document.createElement('h1');
      errorTitle.textContent = 'AudioWorklet Not Supported';
      errorContainer.appendChild(errorTitle);

      const errorMessage = document.createElement('p');
      errorMessage.textContent = 'Your browser does not support the AudioWorklet API. Please use a modern browser like Chrome 66+, Firefox 76+, or Safari 14.1+.';
      errorContainer.appendChild(errorMessage);

      document.body.appendChild(errorContainer);
    }
  });
}

// For Node.js environments, display a message that this example requires a browser
if (typeof window === 'undefined') {
  console.log('This example requires a browser environment with AudioWorklet support.');
  console.log('Please run this example in a web browser that supports AudioWorklet (Chrome 66+, Firefox 76+, Safari 14.1+).');
}
