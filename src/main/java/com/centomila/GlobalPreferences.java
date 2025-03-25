package com.centomila;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;

import com.centomila.utils.ExecuteBBMacros;
import com.centomila.utils.ExtensionPath;
import com.centomila.utils.OpenWebUrl;

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

    private enum PlatformCommand {
        WINDOWS("explorer.exe", "cmd", "/c", "start"),
        MAC("open", "open", "", ""),
        LINUX("xdg-open", "xdg-open", "", "");

        final String fileExplorer;

        PlatformCommand(String fileExplorer, String browserCommand, String browserParam1, String browserParam2) {
            this.fileExplorer = fileExplorer;
        }
    }
    private static final String PRESETS_SETTING_CATEGORY = "Preset Path";
    private static final String SUPPORT_CATEGORY = "Support";
    private static final String UTILITIES_CATEGORY = "Utilities";
    private static final int MAX_PATH_LENGTH = 10000;
    private static final String PATREON_URL = "https://www.patreon.com/Centomila";
    private static final String GITHUB_URL = "https://github.com/centomila/BitwigBuddy-Bitwig-Extension";

    private static final String CENTOMILA_URL = "https://centomila.com";

    private String defaultPresetsPath;
    private Preferences preferences;
    private static SettableStringValue presetsPath;
    private Signal openPresetsFolder;
    private Signal browseFolderButton;
    private Signal resetToDefaultButton;
    public static BooleanValue showChannelDestinationPref;
    private ControllerHost host;
    private BitwigBuddyExtension extension;
    
    private CustomPresetsHandler presetsHandler;

    private Signal openPatreon, openGitHub, openCentomila;
    private Signal openBitwigConsoleButton, openBitwigAdvancedGPUSettings;
    

    /**
     * Initializes the global preferences with the specified controller host.
     * 
     * @param host The Bitwig controller host
     */
    public GlobalPreferences(ControllerHost host, BitwigBuddyExtension extension) {
        this.host = host;
        this.defaultPresetsPath = ExtensionPath.getExstensionsSubfolderPath("BitwigBuddy");
        this.preferences = host.getPreferences();
        this.extension = extension;

        // Initialize all settings first
        initPreferencesSettings();

        // Add observers after all settings are initialized
        initPreferencesObservers();

        this.presetsHandler = new CustomPresetsHandler(host, this);
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

    public CustomPresetsHandler.CustomPreset[] getCustomPresets() {
        return presetsHandler.getCustomPresets();
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
        showChannelDestinationPref = preferences.getBooleanSetting(
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

        // Utilities settings
        this.openBitwigConsoleButton = preferences.getSignalSetting(
                "Opens the Bitwig Studio Console window",
                UTILITIES_CATEGORY,
                "Open Bitwig Console");

        this.openBitwigAdvancedGPUSettings = preferences.getSignalSetting(
                "Opens the Bitwig Studio Advanced GPU Settings",
                UTILITIES_CATEGORY,
                "Open GPU Settings");
    }

    private void initPreferencesObservers() {
        // Add observers for signals
        this.openPresetsFolder.addSignalObserver(this::openPresetsFolderInExplorer);
        this.browseFolderButton.addSignalObserver(this::browseForPresetsFolder);
        this.resetToDefaultButton.addSignalObserver(this::resetToDefaultPath);

        // Add observer for channel destination
        showChannelDestinationPref.addValueObserver(value -> {
            console("Show Channel Destination: " + value);
            if (value) {
                showSetting(NoteDestinationSettings.noteChannelSetting);
                showPopup("Channel Destination enabled");
            } else {
                ((SettableRangedValue) NoteDestinationSettings.noteChannelSetting).set(0); // Set to Channel 1
                hideSetting(NoteDestinationSettings.noteChannelSetting);
                showPopup("Channel Destination disabled. All notes will be sent to Channel 1.");
            }
        });

        // Add observers for support buttons
        this.openPatreon.addSignalObserver(this::openPatreonPage);
        this.openGitHub.addSignalObserver(this::openGitHubPage);
        this.openCentomila.addSignalObserver(this::openCentomilaPage);

        // Add observers for utility buttons
        this.openBitwigConsoleButton.addSignalObserver(this::openBitwigConsole);
        this.openBitwigAdvancedGPUSettings.addSignalObserver(this::openBitwigGPUSettings);
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
            console("Using Bitwig Extensions folder: " + extensionsFolder);
            return extensionsFolder;
        }

        // Fallback to user home
        Path homeFolder = Paths.get(userHome);
        console("Using home folder: " + homeFolder);
        return homeFolder;
    }

    private void browseForPresetsFolder() {
        try {
            // Get the initial directory to start browsing from
            Path initialDirectory = getValidInitialDirectory();
            String result = null;

            if (host.platformIsWindows()) {
                // Windows: Use PowerShell approach
                String script = String.format(
                    "Add-Type -AssemblyName System.Windows.Forms; " +
                    "$form = New-Object System.Windows.Forms.Form; " +
                    "$form.Text = 'Select Presets Folder'; " +
                    "$form.Size = New-Object System.Drawing.Size(600, 150); " +
                    "$form.StartPosition = [System.Windows.Forms.FormStartPosition]::CenterScreen; " +
                    
                    "$textBox = New-Object System.Windows.Forms.TextBox; " +
                    "$textBox.Location = New-Object System.Drawing.Point(10, 25); " +
                    "$textBox.Size = New-Object System.Drawing.Size(460, 20); " +
                    "$textBox.Text = '%s'; " +
                    "$form.Controls.Add($textBox); " +
                    
                    "$browseButton = New-Object System.Windows.Forms.Button; " +
                    "$browseButton.Location = New-Object System.Drawing.Point(480, 24); " +
                    "$browseButton.Size = New-Object System.Drawing.Size(90, 23); " +
                    "$browseButton.Text = 'Browse...'; " +
                    "$browseButton.Add_Click({ " +
                    "    $folderBrowser = New-Object System.Windows.Forms.FolderBrowserDialog; " +
                    "    $folderBrowser.Description = 'Select Presets Folder'; " +
                    "    $startPath = $textBox.Text; " +
                    "    if (Test-Path $startPath) { " +
                    "        $folderBrowser.SelectedPath = $startPath; " +
                    "    } " +
                    "    $folderBrowser.ShowNewFolderButton = $true; " +
                    "    if ($folderBrowser.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) { " +
                    "        $textBox.Text = $folderBrowser.SelectedPath; " +
                    "    } " +
                    "}); " +
                    "$form.Controls.Add($browseButton); " +
                    
                    "$label = New-Object System.Windows.Forms.Label; " +
                    "$label.Location = New-Object System.Drawing.Point(10, 5); " +
                    "$label.Size = New-Object System.Drawing.Size(500, 20); " +
                    "$label.Text = 'Enter or select presets folder path:'; " +
                    "$form.Controls.Add($label); " +
                    
                    "$okButton = New-Object System.Windows.Forms.Button; " +
                    "$okButton.Location = New-Object System.Drawing.Point(400, 70); " +
                    "$okButton.Size = New-Object System.Drawing.Size(75, 23); " +
                    "$okButton.Text = 'OK'; " +
                    "$okButton.DialogResult = [System.Windows.Forms.DialogResult]::OK; " +
                    "$form.AcceptButton = $okButton; " +
                    "$form.Controls.Add($okButton); " +
                    
                    "$cancelButton = New-Object System.Windows.Forms.Button; " +
                    "$cancelButton.Location = New-Object System.Drawing.Point(490, 70); " +
                    "$cancelButton.Size = New-Object System.Drawing.Size(75, 23); " +
                    "$cancelButton.Text = 'Cancel'; " +
                    "$cancelButton.DialogResult = [System.Windows.Forms.DialogResult]::Cancel; " +
                    "$form.CancelButton = $cancelButton; " +
                    "$form.Controls.Add($cancelButton); " +
                    
                    "if ($form.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) { " +
                    "    Write-Output $textBox.Text; " +
                    "} else { " +
                    "    Write-Output 'CANCELED'; " +
                    "}",
                    initialDirectory.toAbsolutePath().toString()
                );
                
                Process process = Runtime.getRuntime().exec(new String[] {
                    "powershell.exe", "-Command", script
                });
                
                result = readProcessOutput(process);
                
            } else if (host.platformIsMac()) {
                // macOS: Use osascript (AppleScript)
                String script = String.format(
                    "osascript -e 'tell application \"System Events\"' " +
                    "-e 'set folderPath to POSIX path of (choose folder with prompt \"Select Presets Folder\" " +
                    "default location POSIX file \"%s\")' " +
                    "-e 'return folderPath' " +
                    "-e 'end tell'",
                    initialDirectory.toAbsolutePath().toString()
                );
                
                Process process = Runtime.getRuntime().exec(new String[] { "bash", "-c", script });
                result = readProcessOutput(process);
                
            } else {
                // Linux: Use zenity if available
                Process checkZenity = Runtime.getRuntime().exec(new String[] {"which", "zenity"});
                if (checkZenity.waitFor() == 0) {
                    String command = String.format(
                        "zenity --file-selection --directory --title=\"Select Presets Folder\" " +
                        "--filename=\"%s/\"",
                        initialDirectory.toAbsolutePath().toString()
                    );
                    Process process = Runtime.getRuntime().exec(new String[] { "bash", "-c", command });
                    result = readProcessOutput(process);
                } else {
                    // Fallback for Linux without zenity
                    showPopup("Directory selection requires zenity to be installed");
                    console("Directory selection requires zenity on Linux");
                    return;
                }
            }
            
            // Process the result
            if (result != null && !result.isEmpty() && !result.equals("CANCELED")) {
                result = result.trim();
                Path selectedPath = Paths.get(result);
                
                if (isValidPresetsFolder(selectedPath)) {
                    setPresetsPath(selectedPath.toString());
                    showPopup("Presets folder set to: " + selectedPath);
                    console("Presets folder set to: " + selectedPath);
                } else {
                    showPopup("Selected folder is not valid: " + selectedPath);
                    console("Selected folder is not valid: " + selectedPath);
                }
            } else {
                console("Folder selection was canceled or returned empty result");
            }
            
        } catch (Exception e) {
            host.errorln("Error in folder selection: " + e.getMessage());
            showPopup("Failed to open folder browser: " + e.getMessage());
        }
    }

    private String readProcessOutput(Process process) throws IOException, InterruptedException {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                console("Process exited with code " + exitCode);
            }
            
            return output.toString();
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

    private void openBitwigConsole() {
        
        extension.getApplication().getAction("show_controller_script_console").invoke();
        
    }
    
    private void openBitwigGPUSettings() {
        extension.getApplication().getAction("show_advanced_settings").invoke();
        
        
    }

    public static String getCurrentPresetsPath() {
        return presetsPath.get();
    }
}
