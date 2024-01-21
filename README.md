# bluesharpbendingapp

This is the repository of the software project bluesharpbendingapp. It is designed to help beginners bend notes on a
harmonica by visualizing the notes played.

The original idea behind the development was to create an application that is available on all common operating systems
and whose application window scales well. It is not uncommon to have other applications open while practicing
or want to use the program on different devices. Last but not least, all keys and the most common
special tunings of harmonicas should be supported.

As a result, there are now two versions of the app. A desktop version for the macOS, Debian and Windows operating
systems,
and an Android version for mobile devices.

The software project consists of several subprojects, which are listed below:

## Structure of the project

* [base](base): <br>Maven project that is used for both the desktop versions and the Android version. It is developed
  using MVC patterns and contains all program components that are not device-specific. The extensive
  library [TarsosDSP](https://github.com/JorenSix/TarsosDSP) is used to determine the frequency of the sounds.
* [desktop](desktop): <br>Maven project for the desktop version for the macOS, Debian and Windows operating systems. The
  standard classes under javax.sound.sampled.\* are used to read from the microphone. The data stream is not persisted,
  but the frequency, volume and accuracy of the frequency determination are calculated at runtime. The interfaces are
  created using [intellij gui designer](https://www.jetbrains.com/help/idea/creating-and-opening-forms.html).
* [android](android): <br>Gradle project for the Android version. The standard
  library [MediaRecorder](https://developer.android.com/reference/android/media/MediaRecorder) is used to read the
  microphone. Here too, the data stream is not persisted, but only evaluated at runtime for frequency determination.
* [webapp](webapp): <br>SpringBootApplication. Responsive website for the app, which is linked in the Amazon App Store 
  and Google Play Store. In particular, the necessary privacy policy for the Android version is provided here. There 
  is a language switch based on AcceptHeaderLocaleResolver
  that displays the content of the website in either English or German depending on the language setting of the browser.
* [debian-build](debian-build): <br>Maven project for creating the Debian package
  using [jpackage](https://openjdk.org/jeps/392). There is no support for cross-compilers, so a corresponding Debian
  device is required for successful package creation.
* [macos-build](macos-build): <br>Maven project for creating the macOS package
  using [jpackage](https://openjdk.org/jeps/392). There is no support for cross-compilers, so a corresponding macOS
  device is required for successful package creation. Without signing the package with a trusted developer certificate,
  gatekeeper will prevent installation. For workaround see [here](https://www.letsbend.de/download/readme_macos.txt)
* [win-build](win-build): <br>Maven project for creating the Windows package
  using [jpackage](https://openjdk.org/jeps/392). There is no support for cross-compilers, so a corresponding Windows
  device is required for successful package creation. If the package is not signed by a trusted developer certificate, a
  warning is issued during installation.

## Build

In order to build the respective desktop variants, a corresponding operating system with JDK including jpackage
is required. The path to jpackage is configured in the Maven parameter env.JPACKAGE_HOME.

### Build under Debian

    cd all
    mvn clean install -Pdebian -DprofileIdEnabled=true

The created package can then be found under debian-build/target/jpackage

### Build under Windows

In addition to jpackage, wixtoolset version 3 is required to build the MSI package. This can be
downloaded [here](https://github.com/wixtoolset/wix3/releases).

    cd all
    mvn clean install -Pwin -DprofileIdEnabled=true

The MSI package created can then be found under win-build/target/jpackage

### Build under macOS

Under macOS, package creation using OpenJDK results in a missing dependency to harfbuzz in the package. Compare
[https://github.com/JetBrains/compose-multiplatform/issues/3107](https://github.com/JetBrains/compose-multiplatform/issues/3107)
As a work-around, an alternative JDK can be used in the Maven parameter env.JPACKAGE_HOME.

    cd all
    mvn clean install -Pmacos -DprofileIdEnabled=true

The created package can then be found under win-build/target/jpackage
