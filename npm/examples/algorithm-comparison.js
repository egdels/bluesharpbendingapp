/**
 * Algorithm Comparison Example
 * 
 * This example demonstrates how to use different pitch detection algorithms
 * and compare their results for the same audio input.
 */

// Import all pitch detectors from the package
import { 
  YINPitchDetector, 
  MPMPitchDetector, 
  FFTDetector, 
  HybridPitchDetector,
  NoteUtils,
  NoteLookup
} from 'https://esm.sh/bluesharp-pitch-detection@latest';

/**
 * Compares the results of different pitch detection algorithms
 * @param {Float32Array} audioData - Audio data to analyze
 * @param {number} sampleRate - Sample rate of the audio data
 */
function compareAlgorithms(audioData, sampleRate) {
  // Configure frequency range for all detectors
  const minFrequency = 80;
  const maxFrequency = 1000;

  YINPitchDetector.setMinFrequency(minFrequency);
  YINPitchDetector.setMaxFrequency(maxFrequency);

  MPMPitchDetector.setMinFrequency(minFrequency);
  MPMPitchDetector.setMaxFrequency(maxFrequency);

  // Detect pitch using different algorithms
  const yinResult = YINPitchDetector.detectPitch(audioData, sampleRate);
  const mpmResult = MPMPitchDetector.detectPitch(audioData, sampleRate);
  const fftResult = FFTDetector.detectPitch(audioData, sampleRate);
  const hybridResult = HybridPitchDetector.detectPitch(audioData, sampleRate);

  // Display results
  console.log('=== Pitch Detection Algorithm Comparison ===');
  console.log(`Audio buffer length: ${audioData.length} samples`);
  console.log(`Sample rate: ${sampleRate} Hz`);
  console.log('-------------------------------------------');

  displayResult('YIN Algorithm', yinResult, sampleRate);
  displayResult('MPM Algorithm', mpmResult, sampleRate);
  displayResult('FFT Algorithm', fftResult, sampleRate);
  displayResult('Hybrid Algorithm', hybridResult, sampleRate);

  console.log('===========================================');
}

/**
 * Helper function to display pitch detection results
 * @param {string} algorithmName - Name of the algorithm
 * @param {Object} result - Result object from the pitch detector
 * @param {number} sampleRate - Sample rate of the audio data
 */
function displayResult(algorithmName, result, sampleRate) {
  console.log(`${algorithmName}:`);

  if (result.pitch > 0) {
    const noteName = NoteLookup.getNoteName(result.pitch);
    const frequency = NoteLookup.getFrequency(noteName);
    const cents = NoteUtils.getCents(result.pitch, frequency);

    console.log(`  Pitch: ${result.pitch.toFixed(2)} Hz (${noteName})`);
    console.log(`  Cents deviation: ${cents.toFixed(2)}`);
    console.log(`  Confidence: ${(result.confidence * 100).toFixed(2)}%`);
  } else {
    console.log('  No pitch detected');
  }
  console.log('');
}

/**
 * Generate test signals for algorithm comparison
 */
function generateTestSignals() {
  const sampleRate = 44100;
  const bufferSize = 2048;

  console.log('Generating test signals for comparison...');

  // Test with a pure sine wave (440 Hz - A4)
  const sineWave = new Float32Array(bufferSize);
  const frequency = 440;
  for (let i = 0; i < bufferSize; i++) {
    sineWave[i] = 0.5 * Math.sin(2 * Math.PI * frequency * i / sampleRate);
  }

  console.log('\nTest 1: Pure sine wave (440 Hz - A4)');
  compareAlgorithms(sineWave, sampleRate);

  // Test with a complex tone (fundamental + harmonics)
  const complexTone = new Float32Array(bufferSize);
  const fundamental = 261.63; // C4
  for (let i = 0; i < bufferSize; i++) {
    // Fundamental + harmonics with decreasing amplitude
    complexTone[i] = 0.5 * Math.sin(2 * Math.PI * fundamental * i / sampleRate) + // Fundamental
                     0.25 * Math.sin(2 * Math.PI * fundamental * 2 * i / sampleRate) + // 2nd harmonic
                     0.125 * Math.sin(2 * Math.PI * fundamental * 3 * i / sampleRate) + // 3rd harmonic
                     0.0625 * Math.sin(2 * Math.PI * fundamental * 4 * i / sampleRate); // 4th harmonic
  }

  console.log('\nTest 2: Complex tone with harmonics (261.63 Hz - C4)');
  compareAlgorithms(complexTone, sampleRate);

  // Test with a noisy signal
  const noisySignal = new Float32Array(bufferSize);
  const signalFrequency = 329.63; // E4
  for (let i = 0; i < bufferSize; i++) {
    // Signal + noise
    noisySignal[i] = 0.5 * Math.sin(2 * Math.PI * signalFrequency * i / sampleRate) + // Signal
                     0.2 * (Math.random() * 2 - 1); // Noise
  }

  console.log('\nTest 3: Noisy signal (329.63 Hz - E4 with noise)');
  compareAlgorithms(noisySignal, sampleRate);
}

// Run the example
generateTestSignals();

// For browser environments, you can create a UI to run the tests
if (typeof window !== 'undefined') {
  document.addEventListener('DOMContentLoaded', () => {
    const runButton = document.createElement('button');
    runButton.textContent = 'Run Algorithm Comparisons';
    runButton.addEventListener('click', generateTestSignals);

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
