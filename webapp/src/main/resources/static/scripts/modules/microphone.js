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
// https://github.com/cwilso/PitchDetect/blob/main/js/pitchdetect.js
// https://alexanderell.is/posts/tuner/
export class BrowserMicrophone {

	window.AudioContext = window.AudioContext || window.webkitAudioContext;

	var audioContext = null;
	var handler = null;
	var sourceNode = null;
	var analyser = null;
	var mediaStreamSource = null;

	constructor() {}

	close() {
		this.sourceNode.stop(0);
		this.sourceNode = null;
		this.analyser = null;
	}

	getAlgorithm() {}

	setAlgorithm(storedAlgorithmIndex) {}

	getName() {}

	setName(storedMicrophoneIndex) {}

	getSupportedAlgorithms() {}

	getSupportedMicrophones() {}

	open() {
		// grab an audio context
		this.audioContext = new AudioContext();

		// Attempt to get audio input
		navigator.mediaDevices.getUserMedia({
			"audio": {
				"mandatory": {
					"googEchoCancellation": "false",
					"googAutoGainControl": "false",
					"googNoiseSuppression": "false",
					"googHighpassFilter": "false"
				},
				"optional": []
			},
		}).then((stream) => {
			// Create an AudioNode from the stream.
			this.mediaStreamSource = audioContext.createMediaStreamSource(stream);

			// Connect it to the destination.
			this.analyser = audioContext.createAnalyser();
			this.analyser.fftSize = 2048;
			this.mediaStreamSource.connect(analyser);
			this.updatePitch();
		}).catch((err) => {
			// always check for errors at the end.
			console.error(`${err.name}: ${err.message}`);
			alert('Stream generation failed.');
		});
	}

	setMicrophoneHandler(handler) {
		this.handler = handler;
	}


	autoCorrelate(buf, sampleRate) {
		// Implements the ACF2+ algorithm
		var SIZE = buf.length;
		var rms = 0;

		for (var i = 0; i < SIZE; i++) {
			var val = buf[i];
			rms += val * val;
		}
		rms = Math.sqrt(rms / SIZE);
		if (rms < 0.01) // not enough signal
			return -1;

		var r1 = 0,
			r2 = SIZE - 1,
			thres = 0.2;
		for (var i = 0; i < SIZE / 2; i++)
			if (Math.abs(buf[i]) < thres) {
				r1 = i;
				break;
			}
		for (var i = 1; i < SIZE / 2; i++)
			if (Math.abs(buf[SIZE - i]) < thres) {
				r2 = SIZE - i;
				break;
			}

		buf = buf.slice(r1, r2);
		SIZE = buf.length;

		var c = new Array(SIZE).fill(0);
		for (var i = 0; i < SIZE; i++)
			for (var j = 0; j < SIZE - i; j++)
				c[i] = c[i] + buf[j] * buf[j + i];

		var d = 0;
		while (c[d] > c[d + 1]) d++;
		var maxval = -1,
			maxpos = -1;
		for (var i = d; i < SIZE; i++) {
			if (c[i] > maxval) {
				maxval = c[i];
				maxpos = i;
			}
		}
		var T0 = maxpos;

		var x1 = c[T0 - 1],
			x2 = c[T0],
			x3 = c[T0 + 1];
		a = (x1 + x3 - 2 * x2) / 2;
		b = (x3 - x1) / 2;
		if (a) T0 = T0 - b / (2 * a);

		return sampleRate / T0;
	}
	
	var buflen=2048;
	var buf = new Float32Array (buflen);
	updatePitch() {
		if (this.handler != null) {
			this.analyser.getFloatTimeDomainData(buf);
			var pitch = autoCorrelate(buf, audioContext.sampleRate);
			this.handler.handle(pitch);
		}
	}

}
