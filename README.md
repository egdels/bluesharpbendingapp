# Bluesharp Bending App

**Bluesharp Bending App** is a software tool designed to help beginners learn to bend notes on a harmonica by
visualizing the notes being played.

---

## Project Goal

The main goals of the Bluesharp Bending App are:

- **Learn Note Bending**: Create a user-friendly application that helps beginners practice and master bending notes on
  the harmonica.
- **Scale Training**: Provide an effective tool for scale training to improve musical knowledge and harmonica technique.
- **Multi-Platform Support**: Make the app available on all common operating systems (macOS, Debian Linux, Windows, and
  Android).
- **Scalable UI**: Ensure a scalable application window for flexible use, allowing users to practice comfortably even
  while multitasking or using devices with different screen sizes.
- **Support for Different Tunings and Keys**: Include support for all harmonica keys and the most common special
  tunings.

---

## Versions Overview

The Bluesharp Bending App includes two main versions:

1. **Desktop Version**:

- Supported operating systems: *macOS*, *Debian Linux*, *Windows*.

2. **Android Version**:

- Developed specifically for mobile devices.

---

## Project Structure

The project is divided into several subprojects to keep development modular and maintainable. Below is an overview:

### 1. **Base** ([base](base))

- A shared Maven project for both the desktop and Android versions.
- Developed using the **Model-View-Controller (MVC)** pattern.
- Contains all platform-independent components of the application.

### 2. **Desktop** ([desktop](desktop))

- Maven project for the desktop version (macOS, Debian, Windows).
- Uses the standard classes from `javax.sound.sampled.*` to access the microphone.
- Microphone data is not persisted; instead, the following is computed in real-time:
    - Frequency,
    - Volume
- User interfaces are created using
  the [IntelliJ GUI Designer](https://www.jetbrains.com/help/idea/creating-and-opening-forms.html).

### 3. **Android** ([android](android))

- Gradle project developed for Android.
- Leverages the [MediaRecorder](https://developer.android.com/reference/android/media/MediaRecorder) standard library
  for microphone access.
- Similar to the desktop version, the audio stream is analyzed in real-time for frequency determination without
  persisting the data.

### 4. **WebApp** ([webapp](webapp))

- A **Spring Boot**-based responsive website accompanying the app.
- Key functionalities:
    - Hosting the required privacy policy for the Android app.
    - Language switching based on the `AcceptHeaderLocaleResolver`.
    - Automatically displays content in *English* or *German* based on browser settings.
- The app is linked to this website via the *Amazon App Store* and *Google Play Store*.

### 5. **Debian-Build** ([debian-build](debian-build))

- Maven project for creating Debian packages using [jpackage](https://openjdk.org/jeps/392).
- Note: A Debian device is required for building (cross-compilers are not supported).

### 6. **macOS-Build** ([macos-build](macos-build))

- Maven project for building macOS packages using [jpackage](https://openjdk.org/jeps/392).
- Note: Installation requires signing the package with a trusted developer certificate; otherwise, *Gatekeeper* will
  block the installation. See the [MacOS-Readme workaround](https://www.letsbend.de/download/readme_macos.txt).

### 7. **Windows-Build** ([win-build](win-build))

- Maven project for building Windows packages using [jpackage](https://openjdk.org/jeps/392).
- Note: The **WiX Toolset Version 3** is required to create MSI packages. It can
  be [downloaded here](https://github.com/wixtoolset/wix3/releases).
- If the package is not signed with a trusted developer certificate, users will receive a warning during installation.

---

## Build Instructions

To build the desktop variants, the respective operating system and a JDK installation (including `jpackage`) are
required. The path to `jpackage` is configured via the Maven parameter `env.JPACKAGE_HOME`.

### Build on Debian

```bash
mvn clean install -Pdebian -DprofileIdEnabled=true
```

The created package can be found in the following directory:
`debian-build/target/jpackage`

---

### Build on Windows

In addition to `jpackage`, the **WiX Toolset Version 3** is required. Download it
here: [WiX Toolset](https://github.com/wixtoolset/wix3/releases).

```bash
mvn clean install -Pwin -DprofileIdEnabled=true
```

The created MSI package can be found in the following directory:
`win-build/target/jpackage`

---

### Build on macOS

Note: Building macOS packages with OpenJDK may result in missing **harfbuzz dependencies**. For details, review
the [GitHub issue](https://github.com/JetBrains/compose-multiplatform/issues/3107).

As a workaround, use an alternative JDK specified as the Maven parameter `env.JPACKAGE_HOME`.

```bash
mvn clean install -Pmacos -DprofileIdEnabled=true
```

The created package can be found in the following directory:
`macos-build/target/jpackage`

---

## Contribution Guidelines

Contributions to this project are welcome! Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch for your changes.
3. Submit a **Pull Request** with a clear description of your changes.

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Contact

For questions or feedback:

- **Website**: [letsbend.de](https://www.letsbend.de)
- **Support Email**: *christian.kierdorf@letsbend.de*