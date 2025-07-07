/**
 * Chord Detection Example
 * 
 * This example demonstrates how to use the chord detection functionality
 * to identify chords from a set of frequencies or audio input.
 */

// Import chord detection classes
import { ChordDetector, ChordDetectionResult, NoteLookup} from 'https://esm.sh/bluesharp-pitch-detection@latest';

/**
 * Display chord detection result
 * @param {ChordDetectionResult} result - The chord detection result
 */
function displayChordResult(result) {
  const notes = [];
  if (result && result.hasPitches()) {
    for (let i = 0; i < result.pitches.length; i++) {
      const pitch = result.pitches[i];
      const noteName = NoteLookup.getNoteName(pitch);
      notes.push(noteName);
    }
    console.log('Notes in chord:', notes.join(', '));
    console.log('Confidence:', `${(result.confidence * 100).toFixed(2)}%`);
  } else {
    console.log('No chord detected');
  }
}

/**
 * Detect chords from audio data by first detecting pitches
 * @param {Float32Array} audioData - Audio data to analyze
 * @param {number} sampleRate - Sample rate of the audio data
 */
function detectChordsFromAudio(audioData, sampleRate) {
  console.log('Analyzing audio data for chord detection...');
  console.log(`Buffer size: ${audioData.length}, Sample rate: ${sampleRate} Hz`);
  displayChordResult(ChordDetector.detectChord(audioData, sampleRate));
}



/**
 * Run examples of chord detection
 */
function runChordExamples() {
  console.log('=== Chord Detection Example ===\n');

  // Example: Generate audio data and detect chord
  console.log('Example: Chord from Audio Data');
  const sampleRate = 44100;
  const bufferSize = 4096;
  const audioData = new Float32Array(bufferSize);

  // Generate a simple C major chord waveform
  const cFreq = 261.63; // C4
  const eFreq = 329.63; // E4
  const gFreq = 392.00; // G4

  for (let i = 0; i < bufferSize; i++) {
    // Sum of three sine waves with different amplitudes
    audioData[i] = 0.3 * Math.sin(2 * Math.PI * cFreq * i / sampleRate) +
                   0.3 * Math.sin(2 * Math.PI * eFreq * i / sampleRate) +
                   0.3 * Math.sin(2 * Math.PI * gFreq * i / sampleRate);
  }

  detectChordsFromAudio(audioData, sampleRate);
}

// Run the examples
runChordExamples();

// For browser environments, you can create a UI to run the examples
if (typeof window !== 'undefined') {
  document.addEventListener('DOMContentLoaded', () => {
    const runButton = document.createElement('button');
    runButton.textContent = 'Run Chord Detection Examples';
    runButton.addEventListener('click', runChordExamples);

    const resultDiv = document.createElement('div');
    resultDiv.id = 'results';

    document.body.appendChild(runButton);
    document.body.appendChild(resultDiv);

    // Override console.log to display in the browser
    const originalLog = console.log;
    console.log = function(...args) {
      originalLog.apply(console, args);

      const logLine = document.createElement('div');
      logLine.textContent = args.join(' ');
      resultDiv.appendChild(logLine);
    };
  });
}
