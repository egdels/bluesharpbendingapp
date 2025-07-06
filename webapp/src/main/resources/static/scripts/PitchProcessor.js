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
import YINPitchDetector from './YINPitchDetector.js';
import MPMPitchDetector from './MPMPitchDetector.js';
import HybridPitchDetector from './HybridPitchDetector.js';

/**
 * The PitchProcessor class extends AudioWorkletProcessor and is used for real-time
 * audio pitch detection. It processes audio data in chunks and uses a pitch
 * detection algorithm to analyze and identify the pitch in the input audio signal.
 * Detected pitch results are sent back to the main thread using the message port.
 */
class PitchProcessor extends AudioWorkletProcessor {
    /**
     * Constructs a new instance of the class using the provided options.
     *
     * @param {Object} options - Configuration options for the constructor.
     * @param {Object} options.processorOptions - Processor-specific options.
     * @param {number} options.processorOptions.sampleRate - The sample rate to initialize.
     * @return {Object} A new instance of the class.
     */
    constructor(options) {
        super();
        this.bufferSize = 8192;
        this.sampleRate = options.processorOptions.sampleRate;
        this.algorithm = options.processorOptions.algorithm;
        this.buffer = new Float32Array(this.bufferSize);
        this.bufferIndex = 0;
    }

    /**
     * Processes audio input data, performs pitch detection, and sends the results back through a communication port.
     *
     * @param {Array} inputs - An array of input buffers containing audio data. The first element is assumed to represent the main input channel.
     * @param {Array} outputs - An array of output buffers. Not used in this method.
     * @return {boolean} Returns true indicating the process has completed successfully.
     */
    process(inputs, outputs) {
        const input = inputs[0];
        const channel = input[0];

        if (!channel) return true;

        // Fill the buffer with new audio data
        for (let i = 0; i < channel.length; i++) {
            this.buffer[this.bufferIndex] = channel[i];
            this.bufferIndex++;

            if (this.bufferIndex >= this.bufferSize) {
                // Buffer is full, perform pitch detection
                let result = {};
                if (this.algorithm === 'YIN') {
                    // console.log("YIN");
                    result = YINPitchDetector.detectPitch(this.buffer, this.sampleRate);
                }
                else if (this.algorithm === 'MPM') {
                    // console.log("MPM");
                    result = MPMPitchDetector.detectPitch(this.buffer, this.sampleRate);
                }
                else if (this.algorithm === 'HYBRID') {
                    // console.log("HYBRID");
                    result = HybridPitchDetector.detectPitch(this.buffer, this.sampleRate);
                }
                // console.log(result);
                // Send the result back to the main thread
                this.port.postMessage(result);

                // Reset the buffer
                this.bufferIndex = 0;
            }
        }

        return true;
    }

}

registerProcessor('pitch-processor', PitchProcessor);

export default PitchProcessor;