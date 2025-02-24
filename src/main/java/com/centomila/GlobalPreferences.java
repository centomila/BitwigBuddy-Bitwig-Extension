package com.centomila;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;

import static com.centomila.utils.PopupUtils.*;

import com.centomila.utils.ExtensionPath;
import com.centomila.utils.OpenWebUrl;

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
    @SuppressWarnings({ "unused" })
    private final Signal openPatreon, openGitHub, openCentomila;
    private static final Object jfxInitLock = new Object();

    /**
     * Initializes the global preferences with the specified controller host.
     * 
     * @param host The Bitwig controller host
     */
    public GlobalPreferences(ControllerHost host) {
        this.host = host;
        this.defaultPresetsPath = ExtensionPath.getExstensionsSubfolderPath("BitwigBuddy");
        this.preferences = host.getPreferences();

        // Initialize preference settings
        this.presetsPath = preferences.getStringSetting(
                "Presets Path",
                PRESETS_SETTING_CATEGORY,
                MAX_PATH_LENGTH,
                defaultPresetsPath);

        // Initialize signals
        this.openPresetsFolder = initializeOpenPresetsFolderSignal();
        this.browseFolderButton = initializeBrowseFolderSignal();
        this.resetToDefaultButton = initializeResetDefaultSignal();
        this.openPatreon = initializePatreonSignal();
        this.openGitHub = initializeGitHubSignal();
        this.openCentomila = initializeCentomilaSignal();

        this.presetsHandler = new CustomPresetsHandler(host, this);
    }

    private Signal initializeOpenPresetsFolderSignal() {
        Signal signal = preferences.getSignalSetting(
                "Opens the presets folder in system file explorer",
                PRESETS_SETTING_CATEGORY,
                "Explore Preset Folder");
        signal.addSignalObserver(this::openPresetsFolderInExplorer);
        return signal;
    }

    private Signal initializeBrowseFolderSignal() {
        Signal signal = preferences.getSignalSetting(
                "Select presets folder location",
                PRESETS_SETTING_CATEGORY,
                "Browse");
        signal.addSignalObserver(this::browseForPresetsFolder);
        return signal;
    }

    private Signal initializeResetDefaultSignal() {
        Signal signal = preferences.getSignalSetting(
                "Reset to Default Extensions/BitwigBuddy",
                PRESETS_SETTING_CATEGORY,
                "Reset to default location");
        signal.addSignalObserver(this::resetToDefaultPath);
        return signal;
    }

    private Signal initializePatreonSignal() {
        Signal signal = preferences.getSignalSetting(
                "Support BitwigBuddy on Patreon!",
                SUPPORT_CATEGORY,
                "Go to Patreon.com/Centomila");
        signal.addSignalObserver(this::openPatreonPage);
        return signal;
    }

    private Signal initializeGitHubSignal() {
        Signal signal = preferences.getSignalSetting(
                "Visit BitwigBuddy on GitHub",
                SUPPORT_CATEGORY,
                "Go to GitHub Repository");
        signal.addSignalObserver(this::openGitHubPage);
        return signal;
    }

    private Signal initializeCentomilaSignal() {
        Signal signal = preferences.getSignalSetting(
                "Visit Centomila Website",
                SUPPORT_CATEGORY,
                "Go to Centomila.com");
        signal.addSignalObserver(this::openCentomilaPage);
        return signal;
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

    private void initializeJavaFX() {
        if (jfxInitialized) {
            host.println("JavaFX already initialized, skipping initialization");
            return;
        }

        synchronized (jfxInitLock) {
            if (!jfxInitialized) {
                host.println("Starting JavaFX initialization...");
                try {
                    if (host.platformIsMac()) {
                        host.println("Setting Mac-specific JavaFX properties");
                        System.setProperty("javafx.toolkit", "com.sun.javafx.tk.quantum.QuantumToolkit");
                        System.setProperty("glass.platform", "mac");
                    }

                    if (!Platform.isFxApplicationThread()) {
                        host.println("Not on FX thread, starting platform...");
                        final CountDownLatch initLatch = new CountDownLatch(1);

                        Platform.startup(() -> {
                            host.println("In Platform.startup callback");
                            try {
                                host.println("Attempting to create test Stage");
                                new Stage();
                                host.println("JavaFX initialized successfully");
                                jfxInitialized = true;
                            } catch (Exception e) {
                                host.errorln("JavaFX Stage creation failed: " + e.getMessage());
                                e.printStackTrace();
                            } finally {
                                host.println("Countdown latch released");
                                initLatch.countDown();
                            }
                        });

                        host.println("Waiting for initialization completion...");
                        if (!initLatch.await(5, TimeUnit.SECONDS)) {
                            host.errorln("JavaFX initialization timed out after 5 seconds");
                            return;
                        }
                        host.println("Initialization wait completed");

                    } else {
                        host.println("Already on FX thread, marking as initialized");
                        jfxInitialized = true;
                    }
                } catch (IllegalStateException e) {
                    host.println("JavaFX toolkit already running: " + e.getMessage());
                    jfxInitialized = true;
                } catch (Exception e) {
                    host.errorln("JavaFX initialization failed with: " + e.getMessage());
                    e.printStackTrace();
                }
                host.println("JavaFX initialization process complete. Success: " + jfxInitialized);
            }
        }
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
            // Initialize JavaFX first
            initializeJavaFX();

            if (!jfxInitialized) {
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
        return jfxInitialized;
    }

    public void setJfxInitialized(boolean jfxInitialized) {
        this.jfxInitialized = jfxInitialized;
    }

    public CustomPresetsHandler.CustomPreset[] getCustomPresets() {
        return presetsHandler.getCustomPresets();
    }
}
