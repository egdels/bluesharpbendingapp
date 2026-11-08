/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.schliweb.bluesharpbendingapp.app;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the application. The Launcher class is used to initialize and configure the
 * application environment before delegating control to the main application logic. This includes
 * setting up logging configurations and ensuring necessary directories exist.
 */
public class Launcher {

  /**
   * The main entry point of the application. Initializes logging configurations based on the
   * provided command-line arguments and ensures necessary directories exist before delegating
   * control to the main application logic.
   *
   * @param args command-line arguments passed to the application. If an argument in the form
   *     "--logging.profile=<profile>" is provided, it specifies the logging profile to be used.
   *     Defaults to "DEV" if no such argument is provided.
   */
  public static void main(String[] args) {

    String loggingProfile = "DEV";

    for (String arg : args) {
      if (arg.startsWith("--logging.profile=")) {
        loggingProfile = arg.substring("--logging.profile=".length()).toUpperCase(Locale.ROOT);
        break;
      }
    }

    ensureLogsDirectory();

    configureLogging(loggingProfile);

    MainDesktop.main(args);
  }

  /**
   * Configures the logging framework (Logback) based on the specified profile. This method
   * dynamically loads the logback configuration file corresponding to the given profile.
   *
   * @param profile the name of the profile for which the logback configuration file should be
   *     loaded. This is used to determine the specific logback configuration file, e.g., "dev" or
   *     "prod". The logback configuration file is expected to be named in the format
   *     "logback-{profile}.xml" and located in the "config" directory.
   */
  private static void configureLogging(String profile) {
    try {
      LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      context.reset();

      // Construct the logback configuration file path based on the given profile
      String configFile = "config/logback-" + profile.toLowerCase(Locale.ROOT) + ".xml";

      // Load the configuration file from the classpath
      ClassLoader classLoader = Launcher.class.getClassLoader();
      if (classLoader.getResource(configFile) != null) {
        // Configure Logback directly from the InputStream of the classpath resource
        configurator.doConfigure(classLoader.getResourceAsStream(configFile));
        LoggingContext.setComponent("Launcher");
        LoggingUtils.logConfigChange("Logging", "configuration", configFile);
      } else {
        // Print an error message if the configuration file is not found in the classpath
        LoggingContext.setComponent("Launcher");
        LoggingUtils.logError("Logback configuration file not found", configFile);
      }
    } catch (Exception e) {
      // Handle any exceptions during the Logback configuration process
      LoggingContext.setComponent("Launcher");
      LoggingUtils.logError("Error loading Logback configuration", e);
    }
  }

  /**
   * System property with the log directory, read by {@code config/logback-prod.xml}. Set from
   * {@link AppDirectories#logDir()} unless given on the command line.
   */
  static final String LOG_DIR_PROPERTY = "bluesharpbendingapp.log.dir";

  /**
   * Determines the platform log directory (see {@link AppDirectories}), publishes it as system
   * property {@value #LOG_DIR_PROPERTY} for the logging configuration and creates it.
   */
  private static void ensureLogsDirectory() {
    String configured = System.getProperty(LOG_DIR_PROPERTY);
    Path logsDir =
        configured == null || configured.isBlank()
            ? AppDirectories.forCurrentUser().logDir()
            : Paths.get(configured);
    System.setProperty(LOG_DIR_PROPERTY, logsDir.toString());
    try {
      Files.createDirectories(logsDir);
    } catch (IOException e) {
      // Logging is not configured yet; the file appender reports its own errors later.
      System.err.println("Failed to create logs directory " + logsDir + ": " + e.getMessage());
    }
  }
}
