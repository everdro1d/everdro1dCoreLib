package com.everdro1d.libs.core;

import com.everdro1d.libs.commands.*;

import javax.swing.*;
import java.net.*;
import java.util.prefs.Preferences;

public final class ApplicationCore {
    private ApplicationCore() {}

    /**
     * Implement CLI arguments through CommandInterface
     * @param args passed from main
     */
    public static void checkCLIArgs(String[] args, CommandManager commandManager) {
        for (String arg : args) {
            commandManager.executeCommand(arg);
        }
    }

    /**
     * Detects the OS as String.
     * @param debug whether to print debug information
     * @return the detected OS as a String
     *      - "Windows"
     *      - "macOS"
     *      - "Unix"
     *      - "Unknown"
     */
    public static String detectOS(boolean debug) {
        String os = System.getProperty("os.name").toLowerCase();
        String detectedOS = os.contains("win") ? "Windows" : os.contains("mac") ? "macOS" : os.contains("nix") || os.contains("nux") ? "Unix" : "Unknown";
        if (debug) System.out.println(detectedOS + " detected.");
        return detectedOS;
    }

    /**
     * Get the latest version of the application from the GitHub latest releases page using redirect.
     * <p>Uses version tags in the format "v1.2.1"
     * @param githubURL the URL of the GitHub releases page
     * @param debug whether to print debug information
     * @return the latest version # as a String
     * <p>Example Output: - "1.2.1"
     * @see com.everdro1d.libs.swing.SwingGUI#updateCheckerDialog(String, JFrame, boolean, String, String, Preferences)
     */
    public static String getLatestVersion(String githubURL, boolean debug) {
        try {
            URL url = new URI(githubURL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            String location = connection.getHeaderField("Location");
            connection.disconnect();
            if (location != null) {
                String latestVersion = location.split("/v")[1];
                if (debug) System.out.println("Latest version: " + latestVersion);
                return latestVersion;
            }
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
        return null;
    }
}
