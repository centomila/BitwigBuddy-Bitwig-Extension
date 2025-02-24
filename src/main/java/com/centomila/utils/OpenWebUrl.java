package com.centomila.utils;

import java.io.IOException;
import com.bitwig.extension.controller.api.ControllerHost;
import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Utility class for opening URLs in the system's default web browser.
 * Provides platform-specific command handling for Windows, macOS, and Linux.
 */
public class OpenWebUrl {
    /**
     * Enum defining platform-specific commands for opening URLs and files.
     * Contains command configurations for Windows, macOS, and Linux systems.
     */
    public enum PlatformCommand {
        /** Windows-specific commands using explorer.exe and cmd */
        WINDOWS("explorer.exe", "cmd", "/c", "start"),
        /** macOS-specific commands using the 'open' command */
        MAC("open", "open", "", ""),
        /** Linux-specific commands using xdg-open */
        LINUX("xdg-open", "xdg-open", "", "");

        final String fileExplorer;
        final String browserCommand;
        final String browserParam1;
        final String browserParam2;

        /**
         * Constructs a PlatformCommand with specific command parameters.
         *
         * @param fileExplorer   Command to open file explorer
         * @param browserCommand Command to open web browser
         * @param browserParam1  First parameter for browser command
         * @param browserParam2  Second parameter for browser command
         */
        PlatformCommand(String fileExplorer, String browserCommand, String browserParam1, String browserParam2) {
            this.fileExplorer = fileExplorer;
            this.browserCommand = browserCommand;
            this.browserParam1 = browserParam1;
            this.browserParam2 = browserParam2;
        }
    }

    /**
     * Opens a URL in the system's default web browser.
     *
     * @param host     The Bitwig ControllerHost instance for platform detection
     * @param url      The URL to open
     * @param pageName The name of the page (used for error reporting)
     */
    public static void openUrl(ControllerHost host, String url, String pageName) {
        try {
            PlatformCommand cmd = getPlatformCommand(host);
            String[] command = cmd.browserParam1.isEmpty()
                    ? new String[] { cmd.browserCommand, url }
                    : new String[] { cmd.browserCommand, cmd.browserParam1, cmd.browserParam2, url };

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            host.errorln("Failed to open " + pageName + " page: " + e.getMessage());
            showPopup("Please visit " + url + " in your web browser.");
        }
    }

    /**
     * Determines the appropriate platform command configuration based on the host
     * system.
     *
     * @param host The Bitwig ControllerHost instance used for platform detection
     * @return The PlatformCommand enum corresponding to the current operating
     *         system
     */
    private static PlatformCommand getPlatformCommand(ControllerHost host) {
        if (host.platformIsWindows())
            return PlatformCommand.WINDOWS;
        if (host.platformIsMac())
            return PlatformCommand.MAC;
        if (host.platformIsLinux())
            return PlatformCommand.LINUX;
        return null;
    }
}
