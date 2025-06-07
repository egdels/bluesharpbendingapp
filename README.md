# Bluesharp Bending App

**Bluesharp Bending App** is a software tool designed to help beginners learn to bend notes on a harmonica by visualizing the notes being played.

---

## Project Goal

The main goals of the Bluesharp Bending App are:

- **Learn Note Bending**: Create a user-friendly application that helps beginners practice and master bending notes on the harmonica.
- **Scale Training**: Provide an effective tool for scale training to improve musical knowledge and harmonica technique.
- **Multi-Platform Support**: Make the app available on all common operating systems (*macOS*, *Debian Linux*, *Windows*, and *Android*).
- **Scalable UI**: Ensure a scalable application window for flexible use, allowing users to practice comfortably even while multitasking or using devices with different screen sizes.
- **Support for Different Tunings and Keys**: Include support for all harmonica keys and the most common special tunings.

---

## Versions Overview

The Bluesharp Bending App includes two main versions:

**Desktop Version**:

  - Supported operating systems: *macOS*, *Debian Linux*, *Windows*.

**Android Version**:

  - Developed specifically for mobile devices.

---

## Project Structure

The project is divided into several subprojects to keep development modular and maintainable. Below is an overview:

### 1. **Base** ([base](base))

- A shared **Gradle** project for both the desktop and Android versions.
- Developed using the **Model-View-Controller (MVC)** pattern.
- Contains all platform-independent components of the application.

### 2. **Desktop** ([desktop](desktop))

- **Gradle** project for the desktop version (macOS, Debian, Windows).
- Uses the standard classes from `javax.sound.sampled.*` to access the microphone.
- Microphone data is not persisted; instead, the following is computed in real-time:
  - Frequency,
  - Volume.
- User interfaces are implemented using JavaFX, providing a modern, platform-independent GUI framework.

### 3. **Android** ([android](android))

- **Gradle** project developed for Android.
- Leverages the [MediaRecorder](https://developer.android.com/reference/android/media/MediaRecorder) standard library for microphone access.
- Similar to the desktop version, the audio stream is analyzed in real-time for frequency determination without persisting the data.

### 4. **WebApp** ([webapp](webapp))

- A **Spring Boot**-based responsive website accompanying the app.
- Key functionalities:
    - Hosting the required privacy policy for the Android app.
    - Language switching based on the `AcceptHeaderLocaleResolver`.
    - Automatically displays content in *English* or *German* based on browser settings.
    - **WebVersion**: Includes a web-based version of the application, allowing users to practice bending notes on harmonicas directly in their browser without requiring any installation. This version features:
        - Real-time microphone access for frequency detection.
        - Temporary analysis of audio data without storing it, ensuring privacy.
        - Support for understanding and improving note bending techniques.

---

## Build Instructions

The build process for the desktop variants no longer requires platform-specific Maven projects or `jpackage` configurations. Instead, Gradle is now used to provide a unified and simplified build configuration for all platforms.

### General Build Instructions

1. Install JDK 17 or higher on your system.
2. Clone the repository and navigate to the project directory.

   ```bash
   git clone <repository-url>
   cd <repository-name>
   ```

3. Build the project using Gradle:

   ```bash
   gradle clean build
   ```

The build artifacts will be found in the `build` directories of the respective subprojects.

---

### Building Platform-Specific Installation Files

The creation of operating system-specific installation files (e.g., `deb`, `.dmg`, or `.msi`) for the desktop variant is now handled using **Gradle** and integrated build tools. Below are instructions for each platform:

#### **Debian Linux**

To create a `.deb` package for Debian-based systems:

1. Ensure that a JDK (version 17 or higher) and Gradle are installed on your system.
2. Install `debhelper` and other required tools (if not already installed):

   ```bash
   sudo apt-get install -y debhelper dpkg
   ```

3. Run the following Gradle task to generate the `.deb` package:

   ```bash
   gradle jpackage
   ```

4. The resulting `.deb` file will be located in the `/desktop/build/jpackage/` directory.

---

#### **Windows**

To create a `.msi` package for Windows:

1. Install the **WiX Toolset Version 3**. You can download it here:  
   [WiX Toolset Download Page](https://github.com/wixtoolset/wix3/releases).

2. Ensure the WiX binaries (e.g., `candle.exe` and `light.exe`) are added to your system's `PATH`.

3. Run the following Gradle task to generate the `.msi` package:

   ```bash
   gradle jpackage
   ```

4. The resulting `.msi` file will be located in the `/desktop/build/jpackage/` directory.

---

#### **macOS**

To create a `.dmg` package for macOS:

1. Ensure you are using a machine with macOS and the necessary developer tools installed.
2. Add a valid Developer ID Certificate for signing the package. If the app is unsigned, macOS **Gatekeeper** may block the installation.
3. Run the following Gradle task to generate the `.dmg` package:

   ```bash
   gradle jpackage
   ```

4. The resulting `.dmg` file will be located in the `/desktop/build/jpackage/` directory.

---

### Notes:

- **Cross-Platform Builds**: Platform-specific builds must be executed on the respective operating system (e.g., `.dmg` on macOS, `.msi` on Windows). Cross-compilation for different platforms is not supported.
- All Gradle tasks apply the same project structure and configurations, ensuring consistency across platforms.

---

## Contribution Guidelines

Contributions to this project are welcome! Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch for your changes.
3. Follow the [Development Guidelines](docs/development-guidelines.md) for code style, documentation, and testing requirements.
4. Submit a **Pull Request** with a clear description of your changes.

---

## ⭐ Support the Project

If you find this app helpful, please consider giving it a ⭐ on GitHub – it helps others discover it too!

[![GitHub stars](https://img.shields.io/github/stars/egdels/bluesharpbendingapp?style=social)](https://github.com/egdels/bluesharpbendingapp)

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Contact

For questions or feedback:

- **Website**: [letsbend.de](https://www.letsbend.de)
- **Support Email**: *christian.kierdorf@letsbend.de*

---
