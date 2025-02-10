package com.centomila;

import java.io.File;
import java.io.IOException;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;

public class BeatBuddyPreferences {
    private static final String PRESETS_PATH_KEY = "presetsPath";
    private static final String DEFAULT_PRESETS_PATH = new File(BeatBuddyPreferences.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    
    private final Preferences preferences;
    private final SettableStringValue presetsPath;
    private final Signal openPresetsFolder;
    private final ControllerHost host;

    public BeatBuddyPreferences(ControllerHost host) {
        this.host = host;
        preferences = host.getPreferences();
        presetsPath = preferences.getStringSetting(
            "Presets Path", 
            "BeatBuddy", 
            255, 
            DEFAULT_PRESETS_PATH);
            
        openPresetsFolder = preferences.getSignalSetting(
            "Open Presets Folder",
            "BeatBuddy",
            "Opens the presets folder in system file explorer");
            
        openPresetsFolder.addSignalObserver(this::openPresetsFolderInExplorer);
    }

    private void openPresetsFolderInExplorer() {
        try {
            File directory = new File(presetsPath.get());
            if (directory.exists()) {
                String[] command = new String[] { "explorer.exe", directory.getAbsolutePath() };
                Runtime.getRuntime().exec(command);
            } else {
                host.showPopupNotification("Presets folder does not exist: " + directory.getAbsolutePath());
            }
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
