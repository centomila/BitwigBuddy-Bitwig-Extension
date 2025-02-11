package com.centomila;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.awt.Desktop;
import java.nio.file.Paths;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;

public class BeatBuddyPreferences {
    private static final String PRESETS_PATH_KEY = "presetsPath";

    private String getDefaultExtensionsPath() {
        String userHome = System.getProperty("user.home");
        
        if (host.platformIsWindows()) {
            String[] possibleDocNames = { 
                "Documents", "Documenti", "Documentos", "Dokumente", 
                "文档", "文書", "문서", "Документы" 
            };
            
            // First check OneDrive paths
            File oneDriveBase = new File(userHome, "OneDrive");
            if (oneDriveBase.exists()) {
                for (String docName : possibleDocNames) {
                    File path = Paths.get(userHome, "OneDrive", docName, "Bitwig Studio", "Extensions").toFile();
                    if (path.exists()) {
                        return path.toString();
                    }
                }
            }
            
            // Then check regular Documents folders
            for (String docName : possibleDocNames) {
                File path = Paths.get(userHome, docName, "Bitwig Studio", "Extensions").toFile();
                if (path.exists()) {
                    return path.toString();
                }
            }
        } else if (host.platformIsMac()) {
            return Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions").toString();
        }
        
        // Linux or fallback for Windows
        return Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions").toString();
    }

    private String defaultPresetsPath;

    private final Preferences preferences;
    private final SettableStringValue presetsPath;
    private final Signal openPresetsFolder;
    private final Signal openPatreon;
    private final ControllerHost host;

    public BeatBuddyPreferences(ControllerHost host) {
        this.host = host;
        this.defaultPresetsPath = getDefaultExtensionsPath();
        preferences = host.getPreferences();
        presetsPath = preferences.getStringSetting(
                "Presets Path",
                "BeatBuddy",
                100,
                defaultPresetsPath);

        openPresetsFolder = preferences.getSignalSetting(
                "Open Presets Folder",
                "BeatBuddy",
                "Opens the presets folder in system file explorer");

        openPresetsFolder.addSignalObserver(this::openPresetsFolderInExplorer);

        openPatreon = preferences.getSignalSetting(
                "Support BeatBuddy on Patreon!",
                "BeatBuddy",
                "Go to Patreon.com/Centomila");

        openPatreon.addSignalObserver(this::openPatreonPage);
    }

    private void openPresetsFolderInExplorer() {
        try {
            File directory = new File(presetsPath.get());
            if (!directory.exists()) {
                host.showPopupNotification("Presets folder does not exist: " + directory.getAbsolutePath());
                return;
            }

            String[] command;
            if (host.platformIsWindows()) {
                command = new String[] { "explorer.exe", directory.getAbsolutePath() };
            } else if (host.platformIsMac()) {
                command = new String[] { "open", directory.getAbsolutePath() };
            } else { // Linux
                command = new String[] { "xdg-open", directory.getAbsolutePath() };
            }

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            host.errorln("Failed to open presets folder: " + e.getMessage());
        }
    }

    private void openPatreonPage() {
        String patreonUrl = "https://www.patreon.com/Centomila";
        try {
            String[] command;
            if (host.platformIsWindows()) {
                command = new String[] { "cmd", "/c", "start", patreonUrl };
            } else if (host.platformIsMac()) {
                command = new String[] { "open", patreonUrl };
            } else { // Linux
                command = new String[] { "xdg-open", patreonUrl };
            }
            
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            host.errorln("Failed to open Patreon page: " + e.getMessage());
            // Fallback: Show the URL to the user
            host.showPopupNotification("Please visit " + patreonUrl + " in your web browser.");
        }
    }

    public String getPresetsPath() {
        return presetsPath.get();
    }

    public void setPresetsPath(String path) {
        presetsPath.set(path);
    }
}
