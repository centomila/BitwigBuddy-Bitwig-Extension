package com.centomila.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.bitwig.extension.controller.api.ControllerHost;

/**
 * Utility class for handling Bitwig Studio extension paths across different operating systems.
 * This class helps locate the correct extensions directory based on the user's OS and language settings.
 */
public class ExtensionPath {
    private static ControllerHost host;

    /**
     * Initializes the ExtensionPath utility with a ControllerHost instance.
     * This method must be called before using any other methods in this class.
     *
     * @param controllerHost The Bitwig Studio ControllerHost instance
     */
    public static void init(ControllerHost controllerHost) {
        host = controllerHost;
    }

    /**
     * Array of localized "Documents" folder names for Windows systems in different languages.
     */
    private static final String[] WINDOWS_DOCUMENTS_LOCALIZED = {
        "Documents", "Documenti", "Documentos", "Dokumente",
        "文档", "文書", "문서", "Документы"
    };
    
    /**
     * Gets the default path to the Bitwig Studio extensions folder.
     * Handles different locations based on operating system:
     * - Windows: Checks both OneDrive and regular Documents folders with localization support
     * - MacOS: Uses ~/Documents/Bitwig Studio/Extensions
     * - Linux: Uses ~/Bitwig Studio/Extensions
     *
     * @return String representation of the extensions directory path
     */
    public static String getDefaultExtensionsPath() {
        String userHome = System.getProperty("user.home");

        // Windows
        if (host.platformIsWindows()) {
            // Check OneDrive paths first
            Path oneDriveBase = Paths.get(userHome, "OneDrive");
            if (Files.exists(oneDriveBase)) {
                for (String docName : WINDOWS_DOCUMENTS_LOCALIZED) {
                    Path path = Paths.get(userHome, "OneDrive", docName, "Bitwig Studio", "Extensions");
                    if (Files.exists(path)) {
                        return path.toString();
                    }
                }
            }
            // Then check regular Documents folders
            for (String docName : WINDOWS_DOCUMENTS_LOCALIZED) {
                Path path = Paths.get(userHome, docName, "Bitwig Studio", "Extensions");
                if (Files.exists(path)) {
                    return path.toString();
                }
            }
            
            // MacOS
        } else if (host.platformIsMac()) {
            return Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions").toString();

            // Linux
        } else if (host.platformIsLinux()) {
            return Paths.get(userHome, "Bitwig Studio", "Extensions").toString();
        }
        // Fallback
        return Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions").toString();
    }
    
    /**
     * Gets the path to a specific subfolder within the Bitwig Studio extensions directory.
     *
     * @param subfolder Name of the subfolder to append to the extensions path
     * @return String representation of the complete path including the subfolder
     */
    public static String getExstensionsSubfolderPath(String subfolder) {
        return Paths.get(getDefaultExtensionsPath(), subfolder).toString();
    }

}
