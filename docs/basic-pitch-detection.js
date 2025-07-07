/**
 * Basic Pitch Detection Example
 * 
 * This example demonstrates how to use the YIN pitch detection algorithm
 * to analyze audio data and detect the fundamental frequency (pitch).
 */

// Import the YINPitchDetector from the package
import { YINPitchDetector } from 'https://esm.sh/bluesharp-pitch-detection@latest';

// Sample audio processing function
function processMicrophoneData(audioData, sampleRate) {
  // Configure the detector (optional)
  YINPitchDetector.setMinFrequency(80);  // Set minimum frequency in Hz
  YINPitchDetector.setMaxFrequency(1000); // Set maximum frequency in Hz

  // Detect pitch from audio data
  const result = YINPitchDetector.detectPitch(audioData, sampleRate);
  const resultsDiv = document.getElementById('results');

  // Check if a pitch was detected
  if (result.pitch !== YINPitchDetector.NO_DETECTED_PITCH) {
    console.log(`Detected pitch: ${result.pitch.toFixed(2)} Hz Confidence: ${(result.confidence * 100).toFixed(2)}%`);
    resultsDiv.innerHTML = `<div>${result.pitch.toFixed(2)} Hz Confidence: ${(result.confidence * 100).toFixed(2)}%</div>`
  } else {
    resultsDiv.innerHTML = `<div>No pitch detected</div>`
    console.log('No pitch detected');
  }
}

// Example usage with Web Audio API
async function startMicrophoneAnalysis() {
  try {
    // Request microphone access
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    // Create audio context
    const audioContext = new AudioContext();
    const source = audioContext.createMediaStreamSource(stream);

    // Create analyzer node
    const analyserNode = audioContext.createAnalyser();
    analyserNode.fftSize = 2048;
    source.connect(analyserNode);

    // Create buffer for audio data
    const bufferLength = analyserNode.frequencyBinCount;
    const audioData = new Float32Array(bufferLength);

    // Process audio data at regular intervals
    function analyzeAudio() {
      // Get audio data
      analyserNode.getFloatTimeDomainData(audioData);

      // Process the data
      processMicrophoneData(audioData, audioContext.sampleRate);

      // Schedule next analysis
      requestAnimationFrame(analyzeAudio);
    }

    // Start analysis
    analyzeAudio();

    console.log('Microphone analysis started');
  } catch (error) {
    console.error('Error accessing microphone:', error);
  }
}

// Start the example when the page loads
document.addEventListener('DOMContentLoaded', () => {
  const startButton = document.createElement('button');
  startButton.textContent = 'Start Pitch Detection';
  startButton.addEventListener('click', startMicrophoneAnalysis);
  document.body.appendChild(startButton);

  console.log('Click the button to start pitch detection');
});

// For Node.js environments (without browser APIs), you can use this simplified example:
function nodejsExample() {
  // In a real application, you would get audio data from a file or other source
  const sampleRate = 44100;
  const audioData = new Float32Array(2048).fill(0);

  // Generate a simple sine wave at 440 Hz (A4)
  const frequency = 440;
  for (let i = 0; i < audioData.length; i++) {
    audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
  }

  // Process the data
  processMicrophoneData(audioData, sampleRate);
}

// Uncomment to run the Node.js example
// nodejsExample();
