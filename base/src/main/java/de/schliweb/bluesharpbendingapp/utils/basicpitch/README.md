# Basic Pitch Integration (Phase 0)

Dieses Package ist der Beginn der Java-Integration von **Basic Pitch** in Let's Bend,
gemäß `docs/Integrationskonzept_BasicPitch_LetsBend.md`, **Phase 0 – Java-basierter
A/B-Baseline-Vergleich**.

Leitprinzip: **Basic Pitch liefert nur Multi-Pitch-Kandidaten** („welche Töne klingen“);
die mundharmonikaspezifische Interpretation (Kanäle, Bends, Oktaven, Splits) bleibt
**deterministisch** in Let's Bend.

## Komponenten

| Klasse | Verantwortung | Python-Pendant |
|---|---|---|
| `MultiPitchDetector` | gemeinsame Schnittstelle (neben `ChordDetector`) für die Multi-Pitch-Stufe | – |
| `NoteEvent` | Multi-Pitch-Ereignis (`start`, `end`, `midi`, `amplitude`) | `note_events` |
| `BasicPitchModelRunner` | plattformspezifische Modell-Inferenz (ONNX/CoreML/TF.js) | Backend-Wahl |
| `BasicPitchOutput` | rohe Modell-Tensoren `note`/`onset`/`contour` | `model_output` |
| `BasicPitchProvider` | Adapter Tensoren → `NoteEvent`s (als `MultiPitchDetector`) | `evaluate_basic_pitch.predict()` |
| `HarpTone` / `HarpPitchMapper` / `MappingConfig` / `ActiveChannel` / `MappingResult` | Mapping Noten → Harp-Kanäle (key-aware), Plausibilität | `harp_mapping.py` |
| `MultiPitchPostprocessor` / `MultiPitchPostprocessConfig` | Artefaktfilter + zeitliche Glättung | `postprocess.py` |
| `AudioResampler` | Vorverarbeitung auf 22050 Hz Mono (lineare Interpolation, Stereo→Mono) für jeden `BasicPitchModelRunner` | Resampling in `evaluate_basic_pitch.py` |
| `OnnxBasicPitchModelRunner` | Desktop/JVM-`BasicPitchModelRunner` via ONNX Runtime (`nmp.onnx`); reproduziert Fensterung/Unwrapping der Referenz-Inferenz | `basic_pitch.inference.run_inference` |
| `ChordDetectorMultiPitchAdapter` | exponiert den bestehenden `ChordDetector` als `MultiPitchDetector` | – |
| `BaselineComparison` | engine-neutraler A/B-Mess-/Scoring-Harness (Tonklassen & Harp-Kanäle: TP/FP/FN, Precision/Recall/F1, Inferenzzeit) | Precision/Recall der pytest-Suiten |

## Status

- ✅ Deterministischer Kern (Mapping, Postprocessing, Note-Event-Extraktion) portiert und
  durch JUnit-Tests abgedeckt (`HarpPitchMapperTest`, `MultiPitchPostprocessorTest`,
  `BasicPitchProviderTest`).
- ✅ A/B-Baseline-Harness (`BaselineComparison`) + `ChordDetectorMultiPitchAdapter`: beide
  Engines laufen über dieselbe Schnittstelle und werden auf Tonklassen- und Harp-Kanal-Ebene
  mit Precision/Recall/F1 bewertet (`BaselineComparisonTest`).
- ✅ `AudioResampler`: deterministisches Resampling auf 22050 Hz Mono (das von jedem
  `BasicPitchModelRunner` benötigte Eingabeformat) inkl. Stereo→Mono-Downmix, durch
  `AudioResamplerTest` abgesichert (Länge, Frequenzerhalt, Edge-Cases).
- ✅ `OnnxBasicPitchModelRunner`: konkreter Desktop/JVM-Runner über **ONNX Runtime**
  (`com.microsoft.onnxruntime:onnxruntime:1.22.0`; in `base` `compileOnly`+`testImplementation`,
  im `desktop`-Modul `implementation`). Input `serving_default_input_2:0` (`[n,43844,1]` float),
  Outputs note=`StatefulPartitionedCall:1`, onset=`:2`, contour=`:0`; die Fensterung (Front-Pad
  3840, Fenster 43844 / Hop 36164) und das Unwrapping (15 Overlap-Frames/Seite verwerfen, Trim auf
  `floor(len*86/22050)`) bilden `basic_pitch.inference.run_inference` exakt nach.
  `OnnxBasicPitchModelRunnerTest` prüft Fenster-Konstanten und (sofern `nmp.onnx` lokal vorhanden,
  z. B. im `venv`) einen echten C-Dur-Lauf End-to-End über `BasicPitchProvider`.
- ✅ **Plattform-Hinweis (gelöst):** Bewusst auf `onnxruntime:1.22.0` festgelegt, weil dieses
  Artefakt Natives für **Linux, Windows und macOS** bündelt (`linux-aarch64`, `linux-x64`,
  `win-x64`, `osx-aarch64`, `osx-x64`). Die Version `1.24.1` enthielt nur Linux/Windows-Natives
  und scheiterte auf macOS mit `UnsatisfiedLinkError`; mit `1.22.0` läuft der echte Inferenztest
  auch auf Apple Silicon real durch. Der defensive Skip im Test bleibt nur als Fallback für
  nicht abgedeckte Plattformen/Architekturen erhalten.
- ✅ **A/B-Lauf mit dem echten Modell:** `BaselineComparisonTest.abComparisonWithRealOnnxModel`
  treibt denselben `BaselineComparison`-Harness mit dem **realen** `OnnxBasicPitchModelRunner`
  (statt Test-Double) gegen den `ChordDetector` auf identischem C-Dur-Audio; das echte Modell
  erkennt die Tonklassen `{C, E, G}` (F1 ≥ 0.9). Der Test läuft real, sofern `nmp.onnx` lokal
  vorhanden ist (z. B. im `venv`), sonst wird er übersprungen (Modell nicht im Repo committet).
- ✅ **Modell-Bundling:** Das Modell `nmp.onnx` (~225 KB) wird als Classpath-Ressource im
  `base`-Modul unter `base/src/main/resources/basic_pitch/nmp.onnx` mitgeliefert (Konstante
  `OnnxBasicPitchModelRunner.BUNDLED_MODEL_RESOURCE = "/basic_pitch/nmp.onnx"`). Geladen wird es
  über `OnnxBasicPitchModelRunner.fromBundledModel()` (Verfügbarkeit via `isBundledModelAvailable()`);
  das Desktop-Modul erhält die Ressource transitiv über `base`. `OnnxBasicPitchModelRunnerTest`
  prüft Vorhandensein (`bundledModelResourceIsAvailable`) und einen echten C-Dur-Lauf aus der
  gebündelten Ressource (`detectsCMajorChordFromBundledModel`).
- ✅ **Größerer A/B-Messlauf:** `BaselineReportTest` vergleicht beide Engines auf einem
  erweiterten Ground-Truth-Set (5 synthetische Fälle + reale `unbenannt2/3/4.wav`) unter
  identischer Fensterung (2 s/1 s) und identischem Filter (MIDI 48..96) und gibt eine
  Markdown-Tabelle (P/R/F1, Inferenzzeit, ~RAM) aus. Befund: synthetisch beide F1 = 1.00; auf
  der groben Tonklassen-Union höhere Precision beim `ChordDetector` (mittlerer F1 0.94 vs. 0.85),
  beide echtzeitfähig. Vollständige Tabelle + Interpretation: `docs/Phase0_AB_Baseline_Ergebnisse.md`.
  **Go-Entscheidung: Hybrid/optional**, Default `ChordDetector`. Damit ist **Phase 0 abgeschlossen.**
- ⬜ Offen: Android-Runner (`onnxruntime-android`) folgt später.

### Phase-0-Befund (synthetischer C-Dur-Akkord)

- Beide Engines erkennen die Tonklassen `{C, E, G}` perfekt (F1 = 1.00).
- Auf Kanal-Ebene war die ursprünglich greedy/eingabereihenfolge-sensitive Konfliktauflösung
  problematisch: G4 ist mehrdeutig (`3B` ≡ `2D`); je nach Reihenfolge konnte G4 auf `2D` landen
  und dadurch E4=`2B` (gleicher Kanal Blow/Draw-Konflikt) verdrängen.
- **Gelöst:** Die Zuweisung erfolgt nun **global** (`HarpPitchMapper.assignChannels`): unter allen
  konfliktfreien Belegungen wird diejenige mit den **meisten erkannten Tönen** gewählt
  (Tiebreaks: geringste Cents-Abweichung, dann Kandidatenpräferenz Blow/ungebogen). Dadurch wird
  der C-Dur-**Blow**-Akkord vollständig als `1B,2B,3B` und der **Draw**-Akkord als `1D,2D,3D`
  erkannt — beide ohne dass das mehrdeutige G4 einen Partnerton verdrängt
  (`BaselineComparisonTest.chordDetectorMapsCMajorToBlowTriad`, F1 = 1.00).

Die `HALF_TONES_*`-Tabellen sind 1:1 aus
`model/harmonica/RichterHarmonica.java` übernommen (Basis C4 = MIDI 60). Der `baseMidi`
des `HarpPitchMapper` ist konfigurierbar, sodass die Harp-Tonart später eingebunden
werden kann.
