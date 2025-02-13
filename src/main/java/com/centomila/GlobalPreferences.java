package com.centomila;

import java.io.File;
import java.io.IOException;

import java.nio.file.Paths;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GlobalPreferences {
    private String defaultPresetsPath;

    private final Preferences preferences;

    public final SettableStringValue presetsPath;
    private final Signal openPresetsFolder;
    private final Signal openPatreon;
    private final Signal browseFolderButton;
    private final Signal resetToDefaultButton;
    private final ControllerHost host;
    private boolean jfxInitialized = false;
    private final CustomPresetsHandler presetsHandler;

    public GlobalPreferences(ControllerHost host) {
        this.host = host;
        this.defaultPresetsPath = getDefaultExtensionsPath();
        preferences = host.getPreferences();
        presetsPath = preferences.getStringSetting(
                "Presets Path",
                "Preset Path",
                100,
                defaultPresetsPath);

        openPresetsFolder = preferences.getSignalSetting(
                "Opens the presets folder in system file explorer",
                "Preset Path",
                "Explore Preset Folder");

        openPresetsFolder.addSignalObserver(this::openPresetsFolderInExplorer);

        browseFolderButton = preferences.getSignalSetting(
                "Select presets folder location",
                "Preset Path",
                "Browse");
        browseFolderButton.addSignalObserver(this::browseForPresetsFolder);

        resetToDefaultButton = preferences.getSignalSetting(
                "Reset to Default Extensions/BeatBuddy",
                "Preset Path",
                "Reset to default location");
        resetToDefaultButton.addSignalObserver(this::resetToDefaultPath);

        // Patreon link
        openPatreon = preferences.getSignalSetting(
                "Support BeatBuddy on Patreon!",
                "Support",
                "Go to Patreon.com/Centomila");
        openPatreon.addSignalObserver(this::openPatreonPage);

        this.presetsHandler = new CustomPresetsHandler(host, this);
    }

    public String getPresetsPath() {
        return presetsPath.get();
    }

    public void setPresetsPath(String path) {
        presetsPath.set(path);
    }

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
                    File path = Paths.get(userHome, "OneDrive", docName, "Bitwig Studio", "Extensions", "BeatBuddy").toFile();
                    if (path.exists()) {
                        return path.toString();
                    }
                }
            }

            // Then check regular Documents folders
            for (String docName : possibleDocNames) {
                File path = Paths.get(userHome, docName, "Bitwig Studio", "Extensions", "BeatBuddy").toFile();
                if (path.exists()) {
                    return path.toString();
                }
            }
        } else if (host.platformIsMac()) {
            return Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions", "BeatBuddy").toString();
        }

        // Linux or fallback for Windows
        return Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions", "BeatBuddy").toString();
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

    private void initializeJavaFX() {
        if (!jfxInitialized) {
            try {
                Platform.startup(() -> {
                    // Initialize JavaFX
                });
                jfxInitialized = true;
            } catch (IllegalStateException e) {
                // JavaFX already initialized
                jfxInitialized = true;
            }
        }
    }

    private boolean isValidPresetsFolder(File folder) {
        return folder != null && folder.exists() && folder.isDirectory();
    }

    private File getValidInitialDirectory() {
        // Try current preset path first
        String currentPathStr = presetsPath.get();
        if (currentPathStr != null) {
            File currentPath = new File(currentPathStr);
            if (isValidPresetsFolder(currentPath)) {
                return currentPath;
            }
        }

        // Try Bitwig Extensions folder
        String userHome = System.getProperty("user.home");
        File extensionsFolder = Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions").toFile();
        if (isValidPresetsFolder(extensionsFolder)) {
            host.println("Using Bitwig Extensions folder: " + extensionsFolder);
            return extensionsFolder;
        }

        // Fallback to user home
        File homeFolder = new File(userHome);
        host.println("Using home folder: " + homeFolder);
        return homeFolder;
    }

    private void browseForPresetsFolder() {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<File> chosenFile = new AtomicReference<>();
        AtomicReference<String> errorMessage = new AtomicReference<>();

        try {
            initializeJavaFX();

            Platform.runLater(() -> {
                try {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Select BeatBuddy Presets Folder");
                    File initialDir = getValidInitialDirectory();
                    if (initialDir != null) {
                        directoryChooser.setInitialDirectory(initialDir);
                    }

                    Stage stage = new Stage();
                    File selectedDirectory = directoryChooser.showDialog(stage);
                    chosenFile.set(selectedDirectory);
                } catch (Exception e) {
                    errorMessage.set(e.getMessage());
                    host.errorln("Error in directory chooser: " + e.getMessage());
                } finally {
                    latch.countDown(); // Ensure latch is always counted down
                }
            });

            // Wait with timeout
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new TimeoutException("Folder selection dialog timed out");
            }

            // Check if there was an error
            if (errorMessage.get() != null) {
                throw new Exception("Directory chooser error: " + errorMessage.get());
            }

            File selectedDirectory = chosenFile.get();
            if (selectedDirectory != null) {
                if (isValidPresetsFolder(selectedDirectory)) {
                    setPresetsPath(selectedDirectory.getAbsolutePath());
                    host.showPopupNotification("Presets folder updated to: " + selectedDirectory.getAbsolutePath());
                } else {
                    host.showPopupNotification("Invalid presets folder selected: " + selectedDirectory.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            host.errorln("Failed to open folder browser: " + e.getMessage());
            host.showPopupNotification("Failed to open folder browser");
        }
    }

    private void resetToDefaultPath() {
        String defaultPath = getDefaultExtensionsPath();
        File defaultDir = new File(defaultPath);

        if (isValidPresetsFolder(defaultDir)) {
            setPresetsPath(defaultPath);
            host.showPopupNotification("Presets folder reset to default: " + defaultPath);
        } else {
            host.showPopupNotification("Default presets folder not found: " + defaultPath);
        }
    }

    public String getDefaultPresetsPath() {
        return defaultPresetsPath;
    }

    public void setDefaultPresetsPath(String defaultPresetsPath) {
        this.defaultPresetsPath = defaultPresetsPath;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public Signal getOpenPresetsFolder() {
        return openPresetsFolder;
    }

    public Signal getOpenPatreon() {
        return openPatreon;
    }

    public Signal getBrowseFolderButton() {
        return browseFolderButton;
    }

    public Signal getResetToDefaultButton() {
        return resetToDefaultButton;
    }

    public ControllerHost getHost() {
        return host;
    }

    public boolean isJfxInitialized() {
        return jfxInitialized;
    }

    public void setJfxInitialized(boolean jfxInitialized) {
        this.jfxInitialized = jfxInitialized;
    }
    
    public CustomPresetsHandler.CustomPreset[] getCustomPresets() {
        return presetsHandler.getCustomPresets();
    }
}
