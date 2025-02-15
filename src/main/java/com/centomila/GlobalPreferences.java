package com.centomila;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import com.bitwig.extension.controller.api.*;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import com.centomila.utils.PopupUtils;

/**
 * Handles global preferences and settings for the BeatBuddy extension.
 * Manages preset paths, UI interactions, and platform-specific operations.
 */
public class GlobalPreferences {
    private static final String PRESETS_SETTING_CATEGORY = "Preset Path";
    private static final String SUPPORT_CATEGORY = "Support";
    private static final int MAX_PATH_LENGTH = 10000;
    private static final String PATREON_URL = "https://www.patreon.com/Centomila";
    private static final String GITHUB_URL = "https://github.com/centomila/BeatBuddy-Bitwig-Extension-MIDI-Drum-Generator";
    private static final String CENTOMILA_URL = "https://centomila.com";
    private static final long DIALOG_TIMEOUT_SECONDS = 10;
    private static final String[] DOCUMENTS_LOCALIZED = {
        "Documents", "Documenti", "Documentos", "Dokumente",
        "文档", "文書", "문서", "Документы"
    };

    private enum PlatformCommand {
        WINDOWS("explorer.exe", "cmd", "/c", "start"),
        MAC("open", "open", "", ""),
        LINUX("xdg-open", "xdg-open", "", "");

        final String fileExplorer;
        final String browserCommand;
        final String browserParam1;
        final String browserParam2;

        PlatformCommand(String fileExplorer, String browserCommand, String browserParam1, String browserParam2) {
            this.fileExplorer = fileExplorer;
            this.browserCommand = browserCommand;
            this.browserParam1 = browserParam1;
            this.browserParam2 = browserParam2;
        }
    }

    private String defaultPresetsPath;
    private final Preferences preferences;
    private final SettableStringValue presetsPath;
    private final Signal openPresetsFolder;
    private final Signal browseFolderButton;
    private final Signal resetToDefaultButton;
    private final ControllerHost host;
    private boolean jfxInitialized = false;
    private final CustomPresetsHandler presetsHandler;
    @SuppressWarnings({"unused"})
    private final Signal openPatreon, openGitHub, openCentomila;

    /**
     * Initializes the global preferences with the specified controller host.
     * @param host The Bitwig controller host
     */
    public GlobalPreferences(ControllerHost host) {
        this.host = host;
        this.defaultPresetsPath = getDefaultExtensionsPath();
        this.preferences = host.getPreferences();
        
        // Initialize preference settings
        this.presetsPath = preferences.getStringSetting(
            "Presets Path",
            PRESETS_SETTING_CATEGORY,
            MAX_PATH_LENGTH,
            defaultPresetsPath
        );

        // Initialize signals
        this.openPresetsFolder = initializeOpenPresetsFolderSignal();
        this.browseFolderButton = initializeBrowseFolderSignal();
        this.resetToDefaultButton = initializeResetDefaultSignal();
        this.openPatreon = initializePatreonSignal();
        this.openGitHub = initializeGitHubSignal();     // Add this
        this.openCentomila = initializeCentomilaSignal(); // Add this
        
        this.presetsHandler = new CustomPresetsHandler(host, this);
    }

    private Signal initializeOpenPresetsFolderSignal() {
        Signal signal = preferences.getSignalSetting(
            "Opens the presets folder in system file explorer",
            PRESETS_SETTING_CATEGORY,
            "Explore Preset Folder"
        );
        signal.addSignalObserver(this::openPresetsFolderInExplorer);
        return signal;
    }

    private Signal initializeBrowseFolderSignal() {
        Signal signal = preferences.getSignalSetting(
            "Select presets folder location",
            PRESETS_SETTING_CATEGORY,
            "Browse"
        );
        signal.addSignalObserver(this::browseForPresetsFolder);
        return signal;
    }

    private Signal initializeResetDefaultSignal() {
        Signal signal = preferences.getSignalSetting(
            "Reset to Default Extensions/BeatBuddy",
            PRESETS_SETTING_CATEGORY,
            "Reset to default location"
        );
        signal.addSignalObserver(this::resetToDefaultPath);
        return signal;
    }

    private Signal initializePatreonSignal() {
        Signal signal = preferences.getSignalSetting(
            "Support BeatBuddy on Patreon!",
            SUPPORT_CATEGORY,
            "Go to Patreon.com/Centomila"
        );
        signal.addSignalObserver(this::openPatreonPage);
        return signal;
    }

    private Signal initializeGitHubSignal() {
        Signal signal = preferences.getSignalSetting(
            "Visit BeatBuddy on GitHub",
            SUPPORT_CATEGORY,
            "Go to GitHub Repository"
        );
        signal.addSignalObserver(this::openGitHubPage);
        return signal;
    }

    private Signal initializeCentomilaSignal() {
        Signal signal = preferences.getSignalSetting(
            "Visit Centomila Website",
            SUPPORT_CATEGORY,
            "Go to Centomila.com"
        );
        signal.addSignalObserver(this::openCentomilaPage);
        return signal;
    }

    /**
     * Gets the current platform-specific command configuration.
     */
    private PlatformCommand getPlatformCommand() {
        if (host.platformIsWindows()) return PlatformCommand.WINDOWS;
        if (host.platformIsMac()) return PlatformCommand.MAC;
        return PlatformCommand.LINUX;
    }

    /**
     * Opens the current presets folder in the system's file explorer.
     */
    private void openPresetsFolderInExplorer() {
        File directory = new File(presetsPath.get());
        if (!isValidPresetsFolder(directory)) {
            PopupUtils.showPopup("Presets folder does not exist: " + directory.getAbsolutePath());
            return;
        }

        try {
            PlatformCommand cmd = getPlatformCommand();
            Runtime.getRuntime().exec(new String[]{cmd.fileExplorer, directory.getAbsolutePath()});
        } catch (IOException e) {
            host.errorln("Failed to open presets folder: " + e.getMessage());
        }
    }

    private String getDefaultExtensionsPath() {
        String userHome = System.getProperty("user.home");

        if (host.platformIsWindows()) {
            // First check OneDrive paths
            File oneDriveBase = new File(userHome, "OneDrive");
            if (oneDriveBase.exists()) {
                for (String docName : DOCUMENTS_LOCALIZED) {
                    File path = Paths.get(userHome, "OneDrive", docName, "Bitwig Studio", "Extensions", "BeatBuddy").toFile();
                    if (path.exists()) {
                        return path.toString();
                    }
                }
            }

            // Then check regular Documents folders
            for (String docName : DOCUMENTS_LOCALIZED) {
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

    /**
     * Opens the Patreon page in the default system browser.
     */
    private void openWebUrl(String url, String pageName) {
        try {
            PlatformCommand cmd = getPlatformCommand();
            String[] command = cmd.browserParam1.isEmpty() 
                ? new String[]{cmd.browserCommand, url}
                : new String[]{cmd.browserCommand, cmd.browserParam1, cmd.browserParam2, url};
            
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            host.errorln("Failed to open " + pageName + " page: " + e.getMessage());
            PopupUtils.showPopup("Please visit " + url + " in your web browser.");
        }
    }

    private void openPatreonPage() {
        openWebUrl(PATREON_URL, "Patreon");
    }

    private void openGitHubPage() {
        openWebUrl(GITHUB_URL, "GitHub");
    }

    private void openCentomilaPage() {
        openWebUrl(CENTOMILA_URL, "Centomila");
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
            if (!latch.await(DIALOG_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
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
                    PopupUtils.showPopup("Presets folder updated to: " + selectedDirectory.getAbsolutePath());
                } else {
                    PopupUtils.showPopup("Invalid presets folder selected: " + selectedDirectory.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            host.errorln("Failed to open folder browser: " + e.getMessage());
            PopupUtils.showPopup("Failed to open folder browser");
        }
    }

    private void resetToDefaultPath() {
        String defaultPath = getDefaultExtensionsPath();
        File defaultDir = new File(defaultPath);

        if (isValidPresetsFolder(defaultDir)) {
            setPresetsPath(defaultPath);
            PopupUtils.showPopup("Presets folder reset to default: " + defaultPath);
        } else {
            PopupUtils.showPopup("Default presets folder not found: " + defaultPath);
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
        return jfxInitialized;
    }

    public void setJfxInitialized(boolean jfxInitialized) {
        this.jfxInitialized = jfxInitialized;
    }
    
    public CustomPresetsHandler.CustomPreset[] getCustomPresets() {
        return presetsHandler.getCustomPresets();
    }
}
