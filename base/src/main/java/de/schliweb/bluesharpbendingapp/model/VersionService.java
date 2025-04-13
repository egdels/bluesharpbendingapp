package de.schliweb.bluesharpbendingapp.model;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * VersionService is a utility class responsible for managing and fetching the version information
 * from a predefined remote server URL. The class facilitates retrieval of version details provided
 * by the server.
 * <p>
 * The class cannot be instantiated since it has a private constructor. It provides static methods
 * for accessing version information.
 */
@Slf4j
public class VersionService {

    /**
     * A constant that defines the URL used to retrieve the version information of the application
     * from a remote server. The URL points to a plain text file containing the version details.
     * <p>
     * This variable is used internally by the class methods to establish a connection to the
     * remote server and fetch the version data. It is immutable and statically defined to ensure
     * uniformity in all network requests related to version retrieval.
     */
    private static final String VERSION_URL = "https://letsbend.de/download/version.txt";
    /**
     * Represents the version information retrieved from a predefined remote server URL.
     * This variable is statically maintained within the class and serves as a cached
     * value for the remote version data to avoid redundant network calls.
     * <p>
     * The value is populated by the checkVersionFromHost() method, which fetches the
     * version from the server and trims unnecessary whitespace. If the variable is null,
     * the getVersionFromHost() method triggers the population process.
     */
    private static String versionFromHost = null;

    private VersionService() {
    }

    /**
     * Fetches the version information from a predefined remote server URL and updates the
     * statically maintained version cache within the class. The method establishes an HTTP
     * connection to the URL defined by VERSION_URL and reads the version information if
     * the response code received is HTTP_OK (200).
     * <p>
     * The fetched version string is trimmed to remove unnecessary whitespaces before being
     * cached in the versionFromHost variable. If an error occurs during the process, such
     * as an invalid URL, connection failure, or I/O issue, the error is logged using the
     * logger without re-throwing the exception.
     * <p>
     * This method is called internally to ensure the versionFromHost variable is populated,
     * either directly or via other methods like getVersionFromHost().
     * <p>
     * Key behaviors:
     * - Connects to the remote server URL specified by VERSION_URL.
     * - Fetches the version details if the server responds with an HTTP_OK status.
     * - Logs errors in cases of connection or processing failures.
     */
    private static void checkVersionFromHost() {
        URL url;
        HttpURLConnection huc;
        try {
            // Use the constant for the URL
            url = new URI(VERSION_URL).toURL();
            huc = (HttpURLConnection) url.openConnection();
            int responseCode = huc.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                log.info("ok");
                try (Scanner scanner = new Scanner((InputStream) huc.getContent())) {
                    versionFromHost = scanner.nextLine();
                }

                if (versionFromHost != null) {
                    versionFromHost = versionFromHost.trim();
                }
                log.info(versionFromHost);
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Retrieves the version information from the host. If the version has already
     * been fetched and cached, it returns the cached version. Otherwise, it triggers
     * a check to obtain the version from the host and caches the result before returning it.
     *
     * @return the version information from the host as a String
     */
    public static String getVersionFromHost() {
        if (versionFromHost != null) return versionFromHost;
        checkVersionFromHost();
        return versionFromHost;
    }

}
