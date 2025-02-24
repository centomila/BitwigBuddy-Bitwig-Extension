package com.centomila.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.bitwig.extension.controller.api.ControllerHost;

public class ExtensionPath {
    private static ControllerHost host;

    public static void init(ControllerHost controllerHost) {
        host = controllerHost;
    }

    private static final String[] WINDOWS_DOCUMENTS_LOCALIZED = {
        "Documents", "Documenti", "Documentos", "Dokumente",
        "文档", "文書", "문서", "Документы"
    };
    
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
    
    public static String getExstensionsSubfolderPath(String subfolder) {
        return Paths.get(getDefaultExtensionsPath(), subfolder).toString();
    }

}
