// Import statements would go here, but they depend on the specific JavaScript environment and libraries being used

/**
	* The NoteContainer class.
	*/
class NoteContainer {
	/**
		* Instantiates a new NoteContainer.
		*
		* @param {number} channel - the channel
		* @param {number} note - the note
		* @param {string} noteName - the note name
		* @param {Harmonica} harmonica - the harmonica (optional)
		* @param {HarpView} harpView - the harp view (optional)
		* @param {boolean} hasInverseCentsHandling - the has inverse cents handling (optional)
		*/
	constructor(channel, note, noteName, harmonica, harpView, hasInverseCentsHandling = false) {
		this.channel = channel;
		this.note = note;
		this.noteName = noteName;
		this.exec = new ScheduledThreadPoolExecutor(1); // This might need to be replaced with a JavaScript equivalent
		this.frequencyToHandle = 0;
		this.harmonica = harmonica;
		this.harpViewElement = harpView ? harpView.getHarpViewElement(channel, note) : null;
		this.hasInverseCentsHandling = hasInverseCentsHandling;
		this.maxFrequency = harmonica ? harmonica.getNoteFrequencyMaximum(channel, note) : 0;
		this.minFrequency = harmonica ? harmonica.getNoteFrequencyMinimum(channel, note) : 0;
		this.toBeCleared = false;
	}

	/**
		* Gets channel.
		*
		* @return {number} the channel
		*/
	getChannel() {
		return this.channel;
	}

	/**
		* Gets note.
		*
		* @return {number} the note
		*/
	getNote() {
		return this.note;
	}

	/**
		* Gets note name.
		*
		* @return {string} the note name
		*/
	getNoteName() {
		return this.noteName;
	}

	/**
		* Run method.
		*/
	run() {
		if (this.frequencyToHandle <= this.maxFrequency && this.minFrequency <= this.frequencyToHandle) {
			const cents = this.harmonica.getCentsNote(this.channel, this.note, this.frequencyToHandle);
			if (!this.hasInverseCentsHandling) {
				this.harpViewElement.update(cents);
			} else {
				this.harpViewElement.update(-cents);
			}
			this.toBeCleared = true;
		} else {
			if (this.toBeCleared) {
				setTimeout(() => {
					this.harpViewElement.clear();
					this.toBeCleared = false;
				}, 100);
			}
		}
	}

	/**
		* Sets frequency to handle.
		*
		* @param {number} frequencyToHandle - the frequency to handle
		*/
	setFrequencyToHandle(frequencyToHandle) {
		this.frequencyToHandle = frequencyToHandle;
	}

	/**
		* Is overblow boolean.
		*
		* @return {boolean} the boolean
		*/
	isOverblow() {
		return this.harmonica.isOverblow(this.channel, this.note);
	}

	/**
		* Is overdraw boolean.
		*
		* @return {boolean} the boolean
		*/
	isOverdraw() {
		return this.harmonica.isOverdraw(this.channel, this.note);
	}
}

// Export the class if using modules
// export default NoteContainer;

import {
	MainModel
} from './model/MainModel';
import {
	AbstractHarmonica
} from './model/harmonica/AbstractHarmonica';
import {
	Harmonica
} from './model/harmonica/Harmonica';
import {
	NoteLookup
} from './model/harmonica/NoteLookup';
import {
	Microphone
} from './model/microphone/Microphone';
import {
	MicrophoneHandler
} from './model/microphone/MicrophoneHandler';
import {
	MainWindow
} from './view/MainWindow';
import {
	MicrophoneSettingsView
} from './view/MicrophoneSettingsView';
import {
	HarpSettingsView
} from './view/HarpSettingsView';
import {
	HarpView
} from './view/HarpView';
import {
	NoteSettingsView
} from './view/NoteSettingsView';
import {
	NoteContainer
} from './model/NoteContainer';

/**
	* The type Main controller.
	*/
class MainController {
	/**
		* The constant CHANNEL_MAX.
		*/
	static CHANNEL_MAX = 10;

	/**
		* The constant CHANNEL_MIN.
		*/
	static CHANNEL_MIN = 1;

	/**
		* The Model.
		*/
	model;

	/**
		* The Window.
		*/
	window;

	/**
		* The Notes.
		*/
	noteContainers;

	/**
		* Instantiates a new Main controller.
		*
		* @param window the window
		* @param model  the model
		*/
	constructor(window, model) {
		this.window = window;
		this.model = model;
		// Set stored concert pitch before creating harmonica
		NoteLookup.setConcertPitchByIndex(model.getStoredConcertPitchIndex());
		const harmonica = AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex());
		this.model.setHarmonica(harmonica);
		const microphone = model.getMicrophone();
		microphone.setAlgorithm(model.getStoredAlgorithmIndex());
		microphone.setName(model.getStoredMicrophoneIndex());
		this.model.setMicrophone(microphone);
		this.window.setMicrophoneSettingsViewHandler(this);
		this.window.setHarpSettingsViewHandler(this);
		this.window.setHarpViewHandler(this);
		this.window.setNoteSettingsViewHandler(this);
		this.model.getMicrophone().setMicrophoneHandler(this);
	}

	handleAlgorithmSelection(algorithmIndex) {
		this.model.setStoredAlgorithmIndex(algorithmIndex);
		const microphone = this.model.getMicrophone();
		microphone.close();
		microphone.setAlgorithm(algorithmIndex);
		microphone.open();
	}

	handleKeySelection(keyIndex) {
		this.model.setStoredKeyIndex(keyIndex);
		this.model.setHarmonica(AbstractHarmonica.create(keyIndex, this.model.getStoredTuneIndex()));
	}

	handleMicrophoneSelection(microphoneIndex) {
		this.model.setStoredMicrophoneIndex(microphoneIndex);
		const microphone = this.model.getMicrophone();
		microphone.close();
		microphone.setName(microphoneIndex);
		microphone.open();
	}

	handleTuneSelection(tuneIndex) {
		this.model.setStoredTuneIndex(tuneIndex);
		this.model.setHarmonica(AbstractHarmonica.create(this.model.getStoredKeyIndex(), tuneIndex));
	}

	handle(frequency, volume, probability) {
		this.updateMicrophoneSettingsViewVolume(volume);
		this.updateMicrophoneSettingsViewFrequency(frequency);
		this.updateMicrophoneSettingsViewProbability(probability);
		this.updateHarpView(frequency);
	}

	/**
		* Update microphone settings view probability.
		*
		* @param probability the probability
		*/
	updateMicrophoneSettingsViewProbability(probability) {
		if (this.window.isMicrophoneSettingsViewActive()) {
			const microphoneSettingsView = this.window.getMicrophoneSettingsView();
			microphoneSettingsView.setProbability(probability);
		}
	}

	initAlgorithmList() {
		if (this.window.isMicrophoneSettingsViewActive()) {
			const microphoneSettingsView = this.window.getMicrophoneSettingsView();
			microphoneSettingsView.setAlgorithms(this.model.getAlgorithms());
			microphoneSettingsView.setSelectedAlgorithm(this.model.getSelectedAlgorithmIndex());
		}
	}

	initKeyList() {
		if (this.window.isHarpSettingsViewActive()) {
			const harpSettingsView = this.window.getHarpSettingsView();
			harpSettingsView.setKeys(this.model.getKeys());
			harpSettingsView.setSelectedKey(this.model.getSelectedKeyIndex());
		}
	}

	initMicrophoneList() {
		if (this.window.isMicrophoneSettingsViewActive()) {
			const microphoneSettingsView = this.window.getMicrophoneSettingsView();
			microphoneSettingsView.setMicrophones(this.model.getMicrophones());
			microphoneSettingsView.setSelectedMicrophone(this.model.getSelectedMicrophoneIndex());
		}
	}

	initNotes() {
		if (this.window.isHarpViewActive()) {
			const notesList = [];
			const harmonica = this.model.getHarmonica();
			const harpView = this.window.getHarpView();

			for (let channel = MainController.CHANNEL_MIN; channel <= MainController.CHANNEL_MAX; channel++) {
				// Blow notes
				const frequency0 = harmonica.getNoteFrequency(channel, 0);
				const hasInverseCentsHandling = harmonica.hasInverseCentsHandling(channel);
				const note0 = NoteLookup.getNote(frequency0);
				if (note0) {
					notesList.push(new NoteContainer(channel, 0, note0.key, harmonica, harpView, hasInverseCentsHandling));
				}

				// Draw notes
				const frequency1 = harmonica.getNoteFrequency(channel, 1);
				const note1 = NoteLookup.getNote(frequency1);
				if (note1) {
					notesList.push(new NoteContainer(channel, 1, note1.key, harmonica, harpView, hasInverseCentsHandling));
				}

				// Bending notes
				const blowBendingCount = harmonica.getBlowBendingTonesCount(channel);
				const drawBendingCount = harmonica.getDrawBendingTonesCount(channel);
				for (let note = 2; note < 2 + drawBendingCount; note++) {
					const noteEntry = NoteLookup.getNote(harmonica.getNoteFrequency(channel, note));
					if (noteEntry) {
						notesList.push(new NoteContainer(channel, note, noteEntry.key, harmonica, harpView));
					}
				}
				for (let note = -blowBendingCount; note < 0; note++) {
					const noteEntry = NoteLookup.getNote(harmonica.getNoteFrequency(channel, note));
					if (noteEntry) {
						notesList.push(new NoteContainer(channel, note, noteEntry.key, harmonica, harpView, true));
					}
				}

				// Overblows
				if (!hasInverseCentsHandling) {
					const noteEntry = NoteLookup.getNote(harmonica.getNoteFrequency(channel, -1));
					if (noteEntry) {
						notesList.push(new NoteContainer(channel, -1, noteEntry.key, harmonica, harpView, false));
					}
				}

				// Overdraws
				if (hasInverseCentsHandling) {
					const noteEntry = NoteLookup.getNote(harmonica.getNoteFrequency(channel, 2));
					if (noteEntry) {
						notesList.push(new NoteContainer(channel, 2, noteEntry.key, harmonica, harpView, true));
					}
				}
			}
			this.noteContainers = notesList;
			harpView.initNotes(notesList);
		}
	}

	initTuneList() {
		if (this.window.isHarpSettingsViewActive()) {
			const harpSettingsView = this.window.getHarpSettingsView();
			harpSettingsView.setTunes(this.model.getTunes());
			harpSettingsView.setSelectedTune(this.model.getSelectedTuneIndex());
		}
	}

	/**
		* Start.
		*/
	start() {
		this.model.getMicrophone().open();
		this.window.open();
	}

	/**
		* Stop.
		*/
	stop() {
		this.model.getMicrophone().close();
	}

	/**
		* Update harp view.
		*
		* @param frequency the frequency
		*/
	updateHarpView(frequency) {
		if (this.window.isHarpViewActive() && this.noteContainers) {
			this.noteContainers.forEach(noteContainer => {
				noteContainer.setFrequencyToHandle(frequency);
				new Thread(noteContainer).start();
			});
		}
	}

	/**
		* Update microphone settings view frequency.
		*
		* @param frequency the frequency
		*/
	updateMicrophoneSettingsViewFrequency(frequency) {
		if (this.window.isMicrophoneSettingsViewActive()) {
			const microphoneSettingsView = this.window.getMicrophoneSettingsView();
			microphoneSettingsView.setFrequency(frequency);
		}
	}

	/**
		* Update microphone settings view volume.
		*
		* @param volume the volume
		*/
	updateMicrophoneSettingsViewVolume(volume) {
		if (this.window.isMicrophoneSettingsViewActive()) {
			const microphoneSettingsView = this.window.getMicrophoneSettingsView();
			microphoneSettingsView.setVolume(volume);
		}
	}

	handleConcertPitchSelection(pitchIndex) {
		this.model.setStoredConcertPitchIndex(pitchIndex);
		NoteLookup.setConcertPitchByIndex(pitchIndex);
		this.model.setHarmonica(AbstractHarmonica.create(this.model.getStoredKeyIndex(), this.model.getStoredTuneIndex()));
	}

	initConcertPitchList() {
		if (this.window.isNoteSettingsViewActive()) {
			const noteSettingsView = this.window.getNoteSettingsView();
			noteSettingsView.setConcertPitches(this.model.getConcertPitches());
			noteSettingsView.setSelectedConcertPitch(this.model.getSelectedConcertPitchIndex());
		}
	}
}
