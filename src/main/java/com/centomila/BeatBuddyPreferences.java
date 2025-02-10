package com.centomila;

import java.io.File;
import java.io.IOException;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;

public class BeatBuddyPreferences {
    private static final String PRESETS_PATH_KEY = "presetsPath";
    // private static final String DEFAULT_PRESETS_PATH = new File(BeatBuddyPreferences.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
private String getDefaultPresetsPath() {
    String basePath = System.getProperty("user.home") + File.separator;
    String documentsFolder;
    
    if (host.platformIsWindows()) {
        // Try OneDrive Documents first
        String oneDrivePath = basePath + "OneDrive";
        File oneDriveDir = new File(oneDrivePath);
        
        // Try possible Documents folder names
        String[] possibleDocNames = {"Documents", "Documenti", "Documentos", "Dokumente", "Documents", "文档", "文書", "문서", "Документы"};
        documentsFolder = "Documents"; // default fallback
        
        for (String docName : possibleDocNames) {
            // Check in OneDrive first
            if (oneDriveDir.exists()) {
                File oneDriveDocPath = new File(oneDrivePath + File.separator + docName + File.separator + "Bitwig Studio");
                if (oneDriveDocPath.exists()) {
                    documentsFolder = "OneDrive" + File.separator + docName;
                    break;
                }
            }
            
            // Then check in regular Documents
            File regularDocPath = new File(basePath + docName + File.separator + "Bitwig Studio");
            if (regularDocPath.exists()) {
                documentsFolder = docName;
                break;
            }
        }
    } else if (host.platformIsMac()) {
        documentsFolder = "Documents";
    } else { // Linux
        documentsFolder = "Documents";
    }
    
    return new File(basePath + documentsFolder + 
        File.separator + "Bitwig Studio" + 
        File.separator + "Extensions").getAbsolutePath();
}

private String defaultPresetsPath;
    
    private final Preferences preferences;
    private final SettableStringValue presetsPath;
    private final Signal openPresetsFolder;
    private final ControllerHost host;

    public BeatBuddyPreferences(ControllerHost host) {
        this.host = host;
        this.defaultPresetsPath = getDefaultPresetsPath();
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

        public String getPresetsPath() {
        return presetsPath.get();
    }

    public void setPresetsPath(String path) {
        presetsPath.set(path);
    }
}
