package de.schliweb.bluesharpbendingapp.service;

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

import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VersionService is a utility class responsible for managing and fetching the version information
 * from a predefined remote server URL. The class facilitates retrieval of version details provided
 * by the server.
 *
 * <p>The class cannot be instantiated since it has a private constructor. It provides static
 * methods for accessing version information.
 */
public class VersionService {

  /**
   * A constant that defines the URL used to retrieve the version information of the application
   * from the GitHub API. The URL points to the latest release endpoint of the GitHub repository.
   *
   * <p>This variable is used internally by the class methods to establish a connection to the
   * GitHub API and fetch the version data. It is immutable and statically defined to ensure
   * uniformity in all network requests related to version retrieval.
   */
  private static final String VERSION_URL =
      "https://api.github.com/repos/egdels/bluesharpbendingapp/releases/latest";

  /**
   * Represents the version information retrieved from a predefined remote server URL. This variable
   * is statically maintained within the class and serves as a cached value for the remote version
   * data to avoid redundant network calls.
   *
   * <p>The value is populated by the checkVersionFromHost() method, which fetches the version from
   * the server and trims unnecessary whitespace. If the variable is null, the getVersionFromHost()
   * method triggers the population process.
   */
  private static String versionFromHost = null;

  private VersionService() {
    LoggingContext.setComponent("VersionService");
  }

  /**
   * Fetches the version information from a predefined remote server URL and updates the statically
   * maintained version cache within the class. The method establishes an HTTP connection to the URL
   * defined by VERSION_URL and reads the version information if the response code received is
   * HTTP_OK (200).
   *
   * <p>The fetched version string is trimmed to remove unnecessary whitespaces before being cached
   * in the versionFromHost variable. If an error occurs during the process, such as an invalid URL,
   * connection failure, or I/O issue, the error is logged using the logger without re-throwing the
   * exception.
   *
   * <p>This method is called internally to ensure the versionFromHost variable is populated, either
   * directly or via other methods like getVersionFromHost().
   *
   * <p>Key behaviors: - Connects to the remote server URL specified by VERSION_URL. - Fetches the
   * version details if the server responds with an HTTP_OK status. - Logs errors in cases of
   * connection or processing failures.
   */
  private static void checkVersionFromHost() {
    LoggingContext.setOperation("versionFromHost");
    LoggingUtils.logDebug("Checking version from host");
    URL url;
    HttpURLConnection huc;
    try {
      // Use the constant for the URL
      url = new URI(VERSION_URL).toURL();
      huc = (HttpURLConnection) url.openConnection();
      huc.setRequestProperty("Accept", "application/vnd.github+json");
      int responseCode = huc.getResponseCode();

      if (HttpURLConnection.HTTP_OK == responseCode) {
        LoggingUtils.logDebug("ok");
        StringBuilder response = new StringBuilder();
        try (InputStream is = (InputStream) huc.getContent();
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
          while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
          }
        }

        String json = response.toString();
        Pattern pattern = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
          versionFromHost = matcher.group(1).trim();
        }
        LoggingUtils.logDebug("Version from host: " + versionFromHost);
      }
    } catch (IOException | URISyntaxException e) {
      LoggingUtils.logError("Failed to check version from host", e);
    }
  }

  /**
   * Retrieves the version information from the host. If the version has already been fetched and
   * cached, it returns the cached version. Otherwise, it triggers a check to obtain the version
   * from the host and caches the result before returning it.
   *
   * @return the version information from the host as a String
   */
  public static String getVersionFromHost() {
    checkVersionFromHost();
    return versionFromHost;
  }
}
