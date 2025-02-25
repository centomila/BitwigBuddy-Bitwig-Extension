package com.centomila;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;

import com.centomila.utils.ExtensionPath;
import com.centomila.utils.OpenWebUrl;
import com.centomila.utils.JavaFXInitializer;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;
import com.bitwig.extension.controller.api.BooleanValue;

/**
 * Handles global preferences and settings for the BitwigBuddy extension.
 * Manages preset paths, UI interactions, and platform-specific operations.
 */
public class GlobalPreferences {

    private static final String PRESETS_SETTING_CATEGORY = "Preset Path";
    private static final String SUPPORT_CATEGORY = "Support";
    private static final int MAX_PATH_LENGTH = 10000;
    private static final String PATREON_URL = "https://www.patreon.com/Centomila";
    private static final String GITHUB_URL = "https://github.com/centomila/BitwigBuddy-Bitwig-Extension";
    private static final String CENTOMILA_URL = "https://centomila.com";

    private enum PlatformCommand {
        WINDOWS("explorer.exe", "cmd", "/c", "start"),
        MAC("open", "open", "", ""),
        LINUX("xdg-open", "xdg-open", "", "");

        final String fileExplorer;

        PlatformCommand(String fileExplorer, String browserCommand, String browserParam1, String browserParam2) {
            this.fileExplorer = fileExplorer;
        }
    }

    private String defaultPresetsPath;
    private Preferences preferences;
    private SettableStringValue presetsPath;
    private Signal openPresetsFolder;
    private Signal browseFolderButton;
    private Signal resetToDefaultButton;
    private BooleanValue showChannelDestination;
    private ControllerHost host;
    private BitwigBuddyExtension extension;
    
    private CustomPresetsHandler presetsHandler;

    private Signal openPatreon, openGitHub, openCentomila;
    

    /**
     * Initializes the global preferences with the specified controller host.
     * 
     * @param host The Bitwig controller host
     */
    public GlobalPreferences(ControllerHost host, BitwigBuddyExtension extension) {
        this.host = host;
        this.extension = extension;
        this.defaultPresetsPath = ExtensionPath.getExstensionsSubfolderPath("BitwigBuddy");
        this.preferences = host.getPreferences();

        // Initialize all settings first
        initPreferencesSettings();

        // Add observers after all settings are initialized
        initPreferencesObservers();

        this.presetsHandler = new CustomPresetsHandler(host, this);
    }

    private void initPreferencesSettings() {
        // Presets path settings
        this.presetsPath = preferences.getStringSetting(
                "Presets Path",
                PRESETS_SETTING_CATEGORY,
                MAX_PATH_LENGTH,
                defaultPresetsPath);

        // Signal settings
        this.openPresetsFolder = preferences.getSignalSetting(
                "Opens the presets folder in system file explorer",
                PRESETS_SETTING_CATEGORY,
                "Explore Preset Folder");

        this.browseFolderButton = preferences.getSignalSetting(
                "Select presets folder location",
                PRESETS_SETTING_CATEGORY,
                "Browse");

        this.resetToDefaultButton = preferences.getSignalSetting(
                "Reset to Default Extensions/BitwigBuddy",
                PRESETS_SETTING_CATEGORY,
                "Reset to default location");

        // Channel destination setting
        this.showChannelDestination = preferences.getBooleanSetting(
                "Show Channel Destination Selector",
                "Note Destination Settings",
                true);

        // Support settings
        this.openPatreon = preferences.getSignalSetting(
                "Support BitwigBuddy on Patreon!",
                SUPPORT_CATEGORY,
                "Go to Patreon.com/Centomila");

        this.openGitHub = preferences.getSignalSetting(
                "Visit BitwigBuddy on GitHub",
                SUPPORT_CATEGORY,
                "Go to GitHub Repository");

        this.openCentomila = preferences.getSignalSetting(
                "Visit Centomila Website",
                SUPPORT_CATEGORY,
                "Go to Centomila.com");
    }

    private void initPreferencesObservers() {
        // Add observers for signals
        this.openPresetsFolder.addSignalObserver(this::openPresetsFolderInExplorer);
        this.browseFolderButton.addSignalObserver(this::browseForPresetsFolder);
        this.resetToDefaultButton.addSignalObserver(this::resetToDefaultPath);

        // Add observer for channel destination
        this.showChannelDestination.addValueObserver(value -> {
            host.println("Show Channel Destination: " + value);
            if (value) {
                showSetting(extension.noteChannelSetting);
                showPopup("Channel Destination enabled");
            } else {
                ((SettableRangedValue) extension.noteChannelSetting).set(0); // Set to Channel 1
                hideSetting(extension.noteChannelSetting);
                showPopup("Channel Destination disabled. All notes will be sent to Channel 1.");
            }
        });

        // Add observers for support buttons
        this.openPatreon.addSignalObserver(this::openPatreonPage);
        this.openGitHub.addSignalObserver(this::openGitHubPage);
        this.openCentomila.addSignalObserver(this::openCentomilaPage);
    }

    /**
     * Gets the current platform-specific command configuration.
     */
    private PlatformCommand getPlatformCommand() {
        if (host.platformIsWindows())
            return PlatformCommand.WINDOWS;
        if (host.platformIsMac())
            return PlatformCommand.MAC;
        return PlatformCommand.LINUX;
    }

    /**
     * Opens the current presets folder in the system's file explorer.
     */
    private void openPresetsFolderInExplorer() {
        Path directory = Paths.get(presetsPath.get());
        if (!isValidPresetsFolder(directory)) {
            showPopup("Presets folder does not exist: " + directory.toAbsolutePath());
            return;
        }

        try {
            PlatformCommand cmd = getPlatformCommand();
            Runtime.getRuntime().exec(new String[] { cmd.fileExplorer, directory.toAbsolutePath().toString() });
        } catch (IOException e) {
            host.errorln("Failed to open presets folder: " + e.getMessage());
        }
    }

    private void openPatreonPage() {
        OpenWebUrl.openUrl(host, PATREON_URL, "Patreon");
    }

    private void openGitHubPage() {
        OpenWebUrl.openUrl(host, GITHUB_URL, "GitHub");
    }

    private void openCentomilaPage() {
        OpenWebUrl.openUrl(host, CENTOMILA_URL, "Centomila");
    }


    private boolean isValidPresetsFolder(Path folder) {
        return folder != null && Files.exists(folder) && Files.isDirectory(folder);
    }

    private Path getValidInitialDirectory() {
        // Try current preset path first
        String currentPathStr = presetsPath.get();
        if (currentPathStr != null) {
            Path currentPath = Paths.get(currentPathStr);
            if (isValidPresetsFolder(currentPath)) {
                return currentPath;
            }
        }

        // Try Bitwig Extensions folder
        String userHome = System.getProperty("user.home");
        Path extensionsFolder = Paths.get(userHome, "Documents", "Bitwig Studio", "Extensions");
        if (isValidPresetsFolder(extensionsFolder)) {
            host.println("Using Bitwig Extensions folder: " + extensionsFolder);
            return extensionsFolder;
        }

        // Fallback to user home
        Path homeFolder = Paths.get(userHome);
        host.println("Using home folder: " + homeFolder);
        return homeFolder;
    }

    private void browseForPresetsFolder() {
        try {
            // Initialize JavaFX first using the utility class
            if (!JavaFXInitializer.initialize(host)) {
                showPopup("Failed to initialize JavaFX. Please try again or use manual path input.");
                return;
            }

            // Ensure we're on the FX thread before proceeding
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(this::showDirectoryChooser);
            } else {
                showDirectoryChooser();
            }
        } catch (Exception e) {
            host.errorln("Browse folder operation failed: " + e.getMessage());
            showPopup("Failed to open folder browser. Please try again.");
        }
    }

    private void showDirectoryChooser() {
        try {
            host.println("Creating directory chooser...");
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select BitwigBuddy Presets Folder");

            Path initialDir = getValidInitialDirectory();
            if (initialDir != null) {
                File dir = initialDir.toFile();
                if (dir.exists() && dir.isDirectory()) {
                    chooser.setInitialDirectory(dir);
                }
            }

            Stage stage = new Stage();
            File selectedDirectory = chooser.showDialog(stage);

            if (selectedDirectory != null) {
                Path selectedPath = selectedDirectory.toPath();
                if (isValidPresetsFolder(selectedPath)) {
                    setPresetsPath(selectedPath.toAbsolutePath().toString());
                    showPopup("Presets folder updated to: " + selectedPath);
                } else {
                    showPopup("Invalid presets folder selected: " + selectedPath);
                }
            }

            stage.close();
        } catch (Exception e) {
            host.errorln("Directory chooser error: " + e.getMessage());
            showPopup("Failed to open folder browser: " + e.getMessage());
        }
    }

    private void resetToDefaultPath() {
        String defaultPath = ExtensionPath.getExstensionsSubfolderPath("BitwigBuddy");
        Path defaultDir = Paths.get(defaultPath);

        if (isValidPresetsFolder(defaultDir)) {
            setPresetsPath(defaultPath);
            showPopup("Presets folder reset to default: " + defaultPath);
        } else {
            showPopup("Default presets folder not found: " + defaultPath);
        }
    }

    public String getPresetsPath() {
        return presetsPath.get();
    }

    public void setPresetsPath(String path) {
        presetsPath.set(path);
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
        return JavaFXInitializer.isInitialized();
    }

    public CustomPresetsHandler.CustomPreset[] getCustomPresets() {
        return presetsHandler.getCustomPresets();
    }
}
