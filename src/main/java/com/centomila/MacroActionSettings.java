package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.centomila.utils.ExecuteBBMacros;
import com.centomila.utils.LoopProcessor;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Manages macro action settings and execution for the BitwigBuddy extension.
 * This class handles the initialization, configuration, and execution of
 * user-defined macros that can automate sequences of Bitwig Studio actions.
 */
public class MacroActionSettings {
    // -------------------- Constants --------------------
    private static final String MACRO_PREFIX = "Macro:";
    private static final long DEBOUNCE_MS = 500;
    private static final int MAX_NESTING_LEVEL = Integer.MAX_VALUE;

    // View constants
    public static final String VIEW_ALL = "All";
    public static final String VIEW_SLOT1 = "1";
    public static final String VIEW_SLOT2 = "2";
    public static final String VIEW_SLOT3 = "3";
    public static final String VIEW_SLOT4 = "4";
    public static final String VIEW_SLOT1_2 = "1+2";  // New constant
    public static final String VIEW_SLOT3_4 = "3+4";  // New constant
    public static final String VIEW_IM = "Instant Macro";
    public static final String VIEW_ALL_IM = "All + Instant Macro";
    public static final String VIEW_COMPACT = "Compact";

    // -------------------- Static Fields --------------------
    // Core components
    private static ControllerHost host;
    private static GlobalPreferences preferences;

    // Execution state
    private static volatile boolean isExecuting = false;
    private static volatile boolean stopExecution = false;
    private static volatile String currentExecutionSource = null;
    private static volatile long executionStartTime = 0;
    private static long lastExecutionTime = 0;
    private static boolean isExecutingMacro = false;
    private static int nestingLevel = 0;

    // Synchronization objects
    private static final Object EXECUTION_LOCK = new Object();
    private static final Object executionLock = new Object();

    // -------------------- UI Settings --------------------
    // Macro slot settings
    public static Setting[] macroLaunchBtnSignalSettings = new Setting[4];
    public static Setting[] macroSelectorSettings = new Setting[4];
    public static Setting[] macroDescriptionSettings = new Setting[4];
    public static Setting[] macroAuthorSettings = new Setting[4];
    public static Setting[] macroOpenSignals = new Setting[4];
    public static Setting[] macroSpacerSettings = new Setting[4];

    // Global macro settings
    public static Setting macroSpacerSetting;
    public static Setting macroViewSelectorSetting;
    public static Setting macroHeaderSetting;
    public static Setting macroStopBtnSignalSetting;
    public static Setting[] allSettings;

    // Instant macro settings
    public static Setting instantMacroSpacer;
    public static Setting[] instantMacroLines = new Setting[8];
    public static Setting executeInstantMacroSignal;
    public static Setting clearAllInstantMacroLines;

    /**
     * Initializes the macro action settings for the extension.
     * Sets up the necessary settings UI elements and observers.
     *
     * @param extension The BitwigBuddy extension instance
     */
    public static void init(BitwigBuddyExtension extension) {
        host = extension.getHost();
        preferences = extension.preferences;

        // Initialize settings first
        initMacroActionSettings();

        // Then set up observers
        initMacroActionObservers(extension);
    }

    /**
     * Initializes the macro action UI settings.
     * Creates and configures the following settings:
     * <ul>
     * <li>Macro spacer - Visual separator</li>
     * <li>Macro selector - Dropdown to choose a macro</li>
     * <li>Macro description - Displays the selected macro's description</li>
     * <li>Execute button - Triggers the selected macro</li>
     * </ul>
     */
    private static void initMacroActionSettings() {
        // Create header first
        macroHeaderSetting = (Setting) createStringSetting(titleWithLine("MACRO ---------------------------------------"),
                "Macro", 0, "---------------------------------------------------");
        disableSetting(macroHeaderSetting);

        // Create view selector
        String[] viewOptions = new String[] {
                VIEW_IM,
                VIEW_SLOT1,
                VIEW_SLOT2,
                VIEW_SLOT3,
                VIEW_SLOT4,
                VIEW_SLOT1_2,  // New view option
                VIEW_SLOT3_4,  // New view option
                VIEW_ALL,
                VIEW_ALL_IM,
                VIEW_COMPACT // Add new option
        };
        macroViewSelectorSetting = (Setting) createEnumSetting("Show Slots", "Macro", viewOptions, VIEW_SLOT1);

        // Get macro titles for the selector
        String[] macroTitles = getMacroTitles();
        if (macroTitles.length == 0) {
            macroTitles = new String[] { "No Macros Found" };
        }

        // Create settings for each macro slot
        for (int i = 0; i < 4; i++) {
            String slotNum = String.valueOf(i + 1);
            String category = "Macro " + slotNum;

            // 0. Spacer Header
            macroSpacerSettings[i] = (Setting) createStringSetting(titleWithLine("MACRO " + slotNum),
                    category, 0, "---------------------------------------------------");
            disableSetting(macroSpacerSettings[i]);

            // 1. Preset List
            macroSelectorSettings[i] = (Setting) createEnumSetting("Select Macro " + slotNum,
                    category, macroTitles, macroTitles[0]);

            // 2. Open Button
            macroOpenSignals[i] = (Setting) createSignalSetting("Open Macro " + slotNum + " File",
                    category, "Open the selected macro file in default editor");

            // 3. Description
            macroDescriptionSettings[i] = (Setting) createStringSetting("Macro " + slotNum + " Description",
                    category, 0, "Select a macro to execute");

            // 4. Author
            macroAuthorSettings[i] = (Setting) createStringSetting("Macro " + slotNum + " Author",
                    category, 0, "Unknown");

            // 5. Run Button
            macroLaunchBtnSignalSettings[i] = (Setting) createSignalSetting("Execute Macro " + slotNum,
                    category, "Execute Macro " + slotNum);
        }

        // Update allSettings array with the new order
        List<Setting> settingsList = new ArrayList<>();

        // Add header and view selector at the top
        settingsList.add(macroHeaderSetting);
        settingsList.add(macroViewSelectorSetting);

        // Add macro groups with headers followed by their controls
        for (int i = 0; i < 4; i++) {
            settingsList.add(macroSpacerSettings[i]); // Add header separator first
            settingsList.add(macroSelectorSettings[i]); // Preset List
            settingsList.add(macroOpenSignals[i]); // Open Button
            settingsList.add(macroDescriptionSettings[i]); // Description
            settingsList.add(macroAuthorSettings[i]); // Author
            settingsList.add(macroLaunchBtnSignalSettings[i]); // Run Button
        }

        // Instant Macro section

        // Add instant macro section
        instantMacroSpacer = (Setting) createStringSetting(titleWithLine("INSTANT MACRO"),
                "Instant Macro", 0, "---------------------------------------------------");
        instantMacroSpacer.disable();

        // Initialize instant macro line settings
        for (int i = 0; i < 8; i++) {
            instantMacroLines[i] = (Setting) createStringSetting("Instant Line " + (i + 1),
                    "Instant Macro", 256, "");
            settingsList.add(instantMacroLines[i]);
        }

        // Add instant macro control buttons
        executeInstantMacroSignal = (Setting) createSignalSetting("Execute Instant Macro",
                "Instant Macro", "Execute this commands sequence");
        settingsList.add(executeInstantMacroSignal);

        clearAllInstantMacroLines = (Setting) createSignalSetting("Clear All Lines",
                "Instant Macro", "Clear all instant macro lines");
        settingsList.add(clearAllInstantMacroLines);

        // STOP Section
        // Add spacer title STOP
        macroSpacerSetting = (Setting) createStringSetting(titleWithLine("STOP MACRO -------------------------------"),
                "Macro Control", 0, "---------------------------------------------------");
        disableSetting(macroSpacerSetting);
        settingsList.add(macroSpacerSetting);

        // Add stop button as the very last setting
        macroStopBtnSignalSetting = (Setting) createSignalSetting("Stop All Macros",
                "Macro Control", "Stop all currently executing macros");
        settingsList.add(macroStopBtnSignalSetting); // Add this line

        allSettings = settingsList.toArray(new Setting[0]);

    }

    /**
     * Sets up observers for macro-related UI interactions.
     * Handles macro execution requests and updates the description when a new macro
     * is selected.
     * Includes debouncing logic to prevent rapid repeated executions.
     *
     * @param extension The BitwigBuddy extension instance
     */
    private static void initMacroActionObservers(BitwigBuddyExtension extension) {
        // Create observers for each macro slot
        for (int i = 0; i < 4; i++) {
            final int slotIndex = i;

            // Launch button observer
            ((Signal) macroLaunchBtnSignalSettings[i]).addSignalObserver(() -> {
                host.println("Signal triggered for Macro " + (slotIndex + 1) + " at " +
                        new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastExecutionTime > DEBOUNCE_MS) {
                    lastExecutionTime = currentTime;

                    MacroBB macro = getSelectedMacro(slotIndex);
                    if (macro != null) {
                        executeMacro(macro, extension);
                    }
                } else {
                    host.println("Ignoring rapid signal trigger, wait " + DEBOUNCE_MS + "ms between triggers");
                }
            });

            // Macro selection observer
            ((SettableEnumValue) macroSelectorSettings[i]).addValueObserver(newValue -> {
                MacroBB macro = getSelectedMacro(slotIndex);
                if (macro != null) {
                    ((SettableStringValue) macroDescriptionSettings[slotIndex]).set(macro.getDescription());
                    ((SettableStringValue) macroAuthorSettings[slotIndex]).set(macro.getAuthor());
                } else {
                    ((SettableStringValue) macroDescriptionSettings[slotIndex]).set("No description");
                    ((SettableStringValue) macroAuthorSettings[slotIndex]).set("Unknown");
                }
            });

            // Open macro file observer
            ((Signal) macroOpenSignals[i]).addSignalObserver(() -> {
                MacroBB macro = getSelectedMacro(slotIndex);
                openMacroFile(macro, extension);
            });
        }

        ((Signal) executeInstantMacroSignal).addSignalObserver(() -> {
            synchronized (executionLock) {
                if (isExecuting) {
                    host.println("Cannot start instant macro - execution in progress from: " + currentExecutionSource);
                    host.showPopupNotification("Cannot start - execution in progress");
                    return;
                }

                List<String> commands = new ArrayList<>();

                // Collect non-empty commands from instant macro lines
                for (Setting lineSetting : instantMacroLines) {
                    String command = ((SettableStringValue) lineSetting).get().trim();
                    if (!command.isEmpty()) {
                        commands.add(command);
                    }
                }

                if (!commands.isEmpty()) {
                    // Create temporary macro
                    MacroBB instantMacro = new MacroBB(
                            "instant_macro",
                            "Instant Macro",
                            commands.toArray(new String[0]),
                            "Instant macro execution",
                            "Unknown",
                            ""); // Add relative path

                    // Execute the macro
                    executeMacro(instantMacro, extension);
                }
            }
        });

        // Add this in initMacroActionObservers after other signal observers
        ((Signal) macroStopBtnSignalSetting).addSignalObserver(() -> {
            stopExecution = true;
            host.println("Macro execution stop requested");
            host.showPopupNotification("Stopping macro execution...");
        });

        // Add this with other signal observers
        ((Signal) clearAllInstantMacroLines).addSignalObserver(() -> {
            // Clear all instant macro lines
            for (Setting lineSetting : instantMacroLines) {
                ((SettableStringValue) lineSetting).set("");
            }
            host.showPopupNotification("Cleared all instant macro lines");
        });

        // Add observer in initMacroActionObservers()
        ((SettableEnumValue) macroViewSelectorSetting).addValueObserver(newValue -> {
            // if bb is in generate or edit mode, hide all, else go to the switch case
            if (ModeSelectSettings.getCurrentMode().equals(ModeSelectSettings.MODE_MACRO)) {
                switch (newValue) {
                    case VIEW_ALL:
                        showMacroSlots(true, true, true, true);
                        hideInstantMacro();
                        break;
                    case VIEW_SLOT1:
                        showMacroSlots(true, false, false, false);
                        hideInstantMacro();
                        break;
                    case VIEW_SLOT2:
                        showMacroSlots(false, true, false, false);
                        hideInstantMacro();
                        break;
                    case VIEW_SLOT3:
                        showMacroSlots(false, false, true, false);
                        hideInstantMacro();
                        break;
                    case VIEW_SLOT4:
                        showMacroSlots(false, false, false, true);
                        hideInstantMacro();
                        break;
                    case VIEW_SLOT1_2:  // New case for showing slots 1 and 2
                        showMacroSlots(true, true, false, false);
                        hideInstantMacro();
                        break;
                    case VIEW_SLOT3_4:  // New case for showing slots 3 and 4
                        showMacroSlots(false, false, true, true);
                        hideInstantMacro();
                        break;
                    case VIEW_IM:
                        showMacroSlots(false, false, false, false);
                        showInstantMacro();
                        break;
                    case VIEW_ALL_IM:
                        showMacroSlots(true, true, true, true);
                        showInstantMacro();
                        break;
                    case VIEW_COMPACT:
                        showCompactView();
                        break;
                }
            } else {
                hideAllSettings();
                hideInstantMacro();
            }
        });
    }

    /**
     * Executes a macro by running its commands in sequence.
     * Logs the execution start time and details of each command.
     *
     * @param macro     The macro to execute
     * @param extension The BitwigBuddy extension instance
     */
    private static void executeMacro(MacroBB macro, BitwigBuddyExtension extension) {
        synchronized (executionLock) {
            if (isExecuting) {
                host.println("Another execution is in progress from: " + currentExecutionSource);
                host.showPopupNotification("Cannot start macro - execution in progress");
                return;
            }

            try {
                isExecuting = true;
                currentExecutionSource = macro.getTitle();
                executionStartTime = System.currentTimeMillis();
                stopExecution = false;

                host.println("=== MACRO EXECUTION START: "
                        + new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()) + " ===");
                host.println("Processing macro: " + macro.getTitle());

                // Flatten macro commands
                List<String> flattenedCommands = flattenMacroCommands(macro, extension, new HashSet<>());

                host.println("Commands sequence (total " + flattenedCommands.size() + "):");
                for (int i = 0; i < flattenedCommands.size(); i++) {
                    host.println((i + 1) + ": " + flattenedCommands.get(i));
                }

                scheduleCommands(flattenedCommands.toArray(new String[0]), 0, extension);

            } finally {
                // Note: Don't reset isExecuting here - it should be reset when all commands
                // complete
            }
        }
    }

    /**
     * Executes a macro when called from another macro's action.
     * This is a public static method to allow execution from ExecuteBitwigAction.
     *
     * @param macro     The macro to execute
     * @param extension The BitwigBuddy extension instance
     */
    public static void executeMacroFromAction(MacroBB macro, BitwigBuddyExtension extension) {
        synchronized (EXECUTION_LOCK) {
            // Check for recursive execution
            if (isExecutingMacro) {
                extension.getHost().println("Already executing a macro - nesting level: " + nestingLevel);
                if (nestingLevel >= MAX_NESTING_LEVEL) {
                    extension.getHost().errorln(
                            "Maximum macro nesting level reached (" + MAX_NESTING_LEVEL + "). Stopping execution.");
                    return;
                }
                nestingLevel++;
            } else {
                isExecutingMacro = true;
                nestingLevel = 1;
            }

            try {
                // Reset execution state before starting nested macro
                resetExecutionState();
                executeMacro(macro, extension);
            } finally {
                nestingLevel--;
                if (nestingLevel == 0) {
                    isExecutingMacro = false;
                }
            }
        }
    }

    /**
     * Recursively schedules commands to be executed one after another with delays.
     * 
     * @param commands  The array of commands to execute
     * @param index     The current index in the commands array
     * @param extension The Bitwig extension
     */
    public static void scheduleCommands(String[] commands, int index, BitwigBuddyExtension extension) {
        if (index >= commands.length || stopExecution) {
            synchronized (executionLock) {
                if (stopExecution) {
                    host.println("=== MACRO EXECUTION STOPPED BY USER ===");
                    host.showPopupNotification("Macro execution stopped");
                    stopExecution = false;
                } else {
                    host.println("=== MACRO EXECUTION END: "
                            + new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()) + " ===");
                }

                // Reset execution state
                isExecuting = false;
                currentExecutionSource = null;
                long duration = System.currentTimeMillis() - executionStartTime;
                host.println("Total execution time: " + duration + "ms");
            }
            return;
        }

        String command = commands[index];
        final long startTime = System.currentTimeMillis();
        host.println("Executing command " + (index + 1) + "/" + commands.length + ": " + command);

        try {
            // Handle the "Wait" command explicitly
            if (command.startsWith("Wait")) {
                // Match the "Wait" command with a parameter in parentheses
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Wait\\s*\\(\\s*(\\d+)\\s*\\)");
                java.util.regex.Matcher matcher = pattern.matcher(command);

                if (matcher.matches()) {
                    int waitTime = Integer.parseInt(matcher.group(1));
                    host.println("Waiting for " + waitTime + "ms...");
                    extension.getHost().scheduleTask(() -> {
                        scheduleCommands(commands, index + 1, extension);
                    }, waitTime);
                    return; // Exit early to respect the wait time
                } else {
                    host.errorln("Invalid Wait command format. Use: Wait (milliseconds)");
                    return; // Stop execution for invalid format
                }
            }

            // Try executing via ExecuteBitwigAction first
            boolean handled = ExecuteBBMacros.executeBitwigAction(command, extension);

            // If not handled, try default Bitwig action
            if (!handled) {
                Action action = extension.getApplication().getAction(command);
                if (action != null) {
                    action.invoke();
                } else {
                    host.errorln("Action not found: " + command);
                }
            }
        } catch (Exception e) {
            host.errorln("Error executing command '" + command + "': " + e.getMessage());
        }

        host.println("Completed command " + (index + 1) + "/" + commands.length + ": " + command +
                " after " + (System.currentTimeMillis() - startTime) + "ms");

        // Schedule next command with a default delay
        int defaultDelay = 60; // Default delay in milliseconds
        extension.getHost().scheduleTask(() -> {
            scheduleCommands(commands, index + 1, extension);
        }, defaultDelay);
    }

    /**
     * Returns an array of all available macros.
     * Reads macro files from the configured macros directory and sorts them by
     * name.
     *
     * @return Array of Macro objects, empty array if no macros are found
     */
    public static MacroBB[] getMacros() {
        File macrosDir = new File(preferences.getPresetsPath(), "Macros");
        List<MacroBB> macroList = new ArrayList<>();

        // Early return with empty array if directory doesn't exist or isn't accessible
        if (!macrosDir.exists() || !macrosDir.isDirectory()) {
            host.errorln("Macro directory does not exist or is not accessible: " + macrosDir);
            return new MacroBB[0];
        }

        // Recursively scan directory
        scanDirectory(macrosDir, macrosDir, macroList);

        // Sort macros by their display name (relative path + title)
        macroList.sort((m1, m2) -> {
            String display1 = m1.getRelativePath() + m1.getTitle();
            String display2 = m2.getRelativePath() + m2.getTitle();
            return display1.compareToIgnoreCase(display2);
        });

        return macroList.toArray(new MacroBB[0]);
    }

    private static void scanDirectory(File baseDir, File currentDir, List<MacroBB> macroList) {
        File[] files = currentDir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                try {
                    // Calculate relative path from base directory
                    String relativePath = "";
                    if (!currentDir.equals(baseDir)) {
                        String basePath = baseDir.getCanonicalPath();
                        String currentPath = currentDir.getCanonicalPath();
                        relativePath = currentPath.substring(basePath.length() + 1)
                                .replace(File.separatorChar, '/') + "/";
                    }

                    MacroBB macro = readMacroFile(file, relativePath);
                    if (macro != null) {
                        macroList.add(macro);
                    }
                } catch (IOException e) {
                    host.errorln("Failed to read macro file " + file.getName() + ": " + e.getMessage());
                }
            } else if (file.isDirectory()) {
                scanDirectory(baseDir, file, macroList);
            }
        }
    }

    /**
     * Returns the currently selected macro based on the selector setting for a
     * specific slot.
     *
     * @param slotIndex The index of the macro slot
     * @return The selected Macro or null if none is selected or available
     */
    public static MacroBB getSelectedMacro(int slotIndex) {
        String selectedTitle = ((SettableEnumValue) macroSelectorSettings[slotIndex]).get();
        MacroBB[] macros = getMacros();

        for (MacroBB macro : macros) {
            if (macro.getTitle().equals(selectedTitle)) {
                return macro;
            }
        }

        return null;
    }

    /**
     * Gets only the titles of available macros for the selector.
     */
    private static String[] getMacroTitles() {
        MacroBB[] macros = getMacros();
        String[] titles = new String[macros.length];
        for (int i = 0; i < macros.length; i++) {
            titles[i] = macros[i].getTitle();
        }
        return titles;
    }

    /**
     * Reads and parses a single macro file.
     * 
     * @param file The macro file to read
     * @return Macro object if successful, null if parsing failed
     * @throws IOException if file reading fails
     */
    private static MacroBB readMacroFile(File file, String relativePath) throws IOException {
        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        String title = "";
        String description = "No description";
        String author = "Unknown";
        List<String> commands = new ArrayList<>();

        if (lines.isEmpty()) {
            host.errorln("Empty macro file: " + file.getName());
            return null;
        }

        String firstLine = lines.get(0).trim();
        if (firstLine.startsWith(MACRO_PREFIX)) {
            title = extractQuotedValue(firstLine);
        } else {
            host.errorln("Invalid macro file format, first line must be Macro: \"Title\": " + file.getName());
            return null;
        }

        // Process remaining lines
        int commandStartIndex = 1;

        // Check for description and author in the second and third lines
        if (lines.size() > 1) {
            String secondLine = lines.get(1).trim();
            if (secondLine.startsWith("Description:") || secondLine.startsWith("Descritpion:")) {
                try {
                    description = extractQuotedValue(secondLine);
                    commandStartIndex = 2;

                    // Check for author line
                    if (lines.size() > 2) {
                        String thirdLine = lines.get(2).trim();
                        if (thirdLine.startsWith("Author:")) {
                            try {
                                author = extractQuotedValue(thirdLine);
                                commandStartIndex = 3;
                            } catch (IllegalArgumentException e) {
                                host.errorln("Invalid author format in file " + file.getName());
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    host.errorln("Invalid description format in file " + file.getName());
                }
            }
        }

        // Rest of the lines are commands
        for (int i = commandStartIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isEmpty()) {
                commands.add(line);
            }
        }

        if (title.isEmpty()) {
            host.errorln("Invalid macro file " + file.getName() + ": missing title");
            return null;
        }

        // Display title with relative path for selector
        String displayTitle = relativePath.isEmpty() ? title : relativePath + title;

        return new MacroBB(file.getName(), displayTitle, commands.toArray(new String[0]),
                description, author, relativePath);
    }

    @SuppressWarnings("unused")
    private static void printAllAvailableActions(BitwigBuddyExtension extension) {
        host.println("Collecting available actions...");

        // Prepare the content
        StringBuilder content = new StringBuilder();
        content.append("Action ID | Action Name | Action Category\n");
        content.append("----------------------------------------\n");

        ActionCategory[] categories = extension.getApplication().getActionCategories();
        for (ActionCategory category : categories) {
            for (Action action : category.getActions()) {
                content.append(String.format("%s | %s | %s\n",
                        action.getId(),
                        action.getName(),
                        category.getName()));

                // Also print to console
                host.println(action.getId() + " | " + action.getName());
            }
        }

        // Save to file
        File presetsDir = new File(preferences.getPresetsPath());
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        File outputFile = new File(presetsDir, "actions_list_" + timestamp + ".txt");

        try {
            java.nio.file.Files.write(outputFile.toPath(), content.toString().getBytes());
            host.println("Actions list saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            host.errorln("Failed to save actions list: " + e.getMessage());
        }
    }

    /**
     * Extracts a value enclosed in quotes from a line.
     * 
     * @throws IllegalArgumentException if the format is invalid
     */
    private static String extractQuotedValue(String line) {
        int firstQuote = line.indexOf('"');
        int lastQuote = line.lastIndexOf('"');
        if (firstQuote < 0 || lastQuote <= firstQuote) {
            throw new IllegalArgumentException("Invalid format - expected quoted value");
        }
        return line.substring(firstQuote + 1, lastQuote);
    }

    /**
     * Opens a macro file in the default text editor.
     * 
     * @param macro     The macro whose file should be opened
     * @param extension The BitwigBuddy extension instance
     */
    private static void openMacroFile(MacroBB macro, BitwigBuddyExtension extension) {
        if (macro != null) {
            try {
                File macrosDir = new File(preferences.getPresetsPath(), "Macros");
                File macroFile;

                if (!macro.getRelativePath().isEmpty()) {
                    // Convert relative path slashes to system-specific separator
                    String systemPath = macro.getRelativePath().replace('/', File.separatorChar);
                    File subDir = new File(macrosDir, systemPath);
                    macroFile = new File(subDir, macro.getFileName());
                } else {
                    macroFile = new File(macrosDir, macro.getFileName());
                }

                if (macroFile.exists()) {
                    boolean opened = false;

                    // Try Desktop API first
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                        if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                            try {
                                desktop.open(macroFile);
                                opened = true;
                                host.println("Opening macro file: " + macroFile.getAbsolutePath());
                            } catch (Exception e) {
                                host.errorln("Desktop API failed: " + e.getMessage());
                            }
                        }
                    }

                    // Fallback to system commands if Desktop API failed
                    if (!opened) {
                        ProcessBuilder pb;

                        if (host.platformIsWindows()) {
                            // Use Windows default file association
                            pb = new ProcessBuilder("cmd", "/c", "start", "", macroFile.getAbsolutePath());
                        } else if (host.platformIsMac()) {
                            pb = new ProcessBuilder("open", "-t", macroFile.getAbsolutePath());
                        } else if (host.platformIsLinux()) {
                            // Linux - try common text editors in order
                            String[] editors = { "gedit", "kate", "nano", "vim" };
                            String editor = null;

                            for (String e : editors) {
                                try {
                                    Process p = new ProcessBuilder("which", e).start();
                                    if (p.waitFor() == 0) {
                                        editor = e;
                                        break;
                                    }
                                } catch (Exception ex) {
                                    // Continue to next editor
                                }
                            }

                            if (editor != null) {
                                pb = new ProcessBuilder(editor, macroFile.getAbsolutePath());
                            } else {
                                throw new IOException("No suitable text editor found");
                            }
                        } else {
                            throw new IOException("Unsupported platform");
                        }

                        pb.start();
                        host.println("Opening macro file using system command: " + macroFile.getAbsolutePath());
                    }
                } else {
                    host.errorln("Macro file not found: " + macroFile.getAbsolutePath());
                    host.showPopupNotification("Macro file not found");
                }
            } catch (Exception e) {
                host.errorln("Error opening macro file: " + e.getMessage());
                host.showPopupNotification("Error opening macro file");
                e.printStackTrace();
            }
        } else {
            host.showPopupNotification("No macro selected");
        }
    }

    /**
     * Hides all macro-related settings in the UI.
     */
    public static void hideAllSettings() {
         hideSetting(allSettings);
        // Be sure to hide everything related to macro settings
        hideInstantMacro();

    }

    /**
     * Shows all macro-related settings in the UI.
     */
    public static void showMacroSettings() {
        for (Setting setting : allSettings) {
            setting.show();
        }
    }

    // Change these methods from private to public
    public static void showMacroSlots(boolean slot1, boolean slot2, boolean slot3, boolean slot4) {
        // Slot 1-4 visibility
        setSettingsVisibility(0, slot1, macroSpacerSettings, macroSelectorSettings, macroOpenSignals,
                macroDescriptionSettings, macroAuthorSettings, macroLaunchBtnSignalSettings);

        setSettingsVisibility(1, slot2, macroSpacerSettings, macroSelectorSettings, macroOpenSignals,
                macroDescriptionSettings, macroAuthorSettings, macroLaunchBtnSignalSettings);

        setSettingsVisibility(2, slot3, macroSpacerSettings, macroSelectorSettings, macroOpenSignals,
                macroDescriptionSettings, macroAuthorSettings, macroLaunchBtnSignalSettings);

        setSettingsVisibility(3, slot4, macroSpacerSettings, macroSelectorSettings, macroOpenSignals,
                macroDescriptionSettings, macroAuthorSettings, macroLaunchBtnSignalSettings);

        // Always show stop controls when any macro slot is visible
        if (slot1 || slot2 || slot3 || slot4) {
            macroSpacerSetting.show();
            macroStopBtnSignalSetting.show();
        } else {
            macroSpacerSetting.hide();
            macroStopBtnSignalSetting.hide();
        }
    }

    public static void showInstantMacro() {
        instantMacroSpacer.show();
        for (Setting line : instantMacroLines) {
            line.show();
        }
        executeInstantMacroSignal.show();
        clearAllInstantMacroLines.show();
        
        // Always show stop controls when Instant Macro is visible
        macroSpacerSetting.show();
        macroStopBtnSignalSetting.show();
    }

    public static void hideInstantMacro() {
        instantMacroSpacer.hide();
        for (Setting line : instantMacroLines) {
            line.hide();
        }
        executeInstantMacroSignal.hide();
        clearAllInstantMacroLines.hide();
    }

    private static void setSettingsVisibility(int index, boolean show, Setting[]... settingsArrays) {
        for (Setting[] settings : settingsArrays) {
            if (settings[index] != null) {
                if (show) {
                    settings[index].show();
                } else {
                    settings[index].hide();
                }
            }
        }
    }

    public static void showCompactView() {
        // Show only selectors, execute buttons, and separators for all slots
        for (int i = 0; i < 4; i++) {
            // Show separator and essential controls
            macroSpacerSettings[i].show(); // Keep separator visible
            macroSelectorSettings[i].show(); // Show selector
            macroLaunchBtnSignalSettings[i].show(); // Show execute button

            // Hide other controls
            macroOpenSignals[i].hide();
            macroDescriptionSettings[i].hide();
            macroAuthorSettings[i].hide();
        }
        // Hide instant macro section
        hideInstantMacro();

        // Show stop button
        macroSpacerSetting.show();
        macroStopBtnSignalSetting.show();

    }

    /**
     * Immutable class representing a macro for the BitwigBuddy extension.
     */
    public static final class MacroBB {
        private final String fileName;
        private final String title;
        private final String[] commands;
        private final String description;
        private final String author;
        private final String relativePath; // New field for relative path

        public MacroBB(String fileName, String title, String[] commands, String description, String author,
                String relativePath) {
            this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
            this.title = Objects.requireNonNull(title, "title cannot be null");
            this.commands = Arrays.copyOf(Objects.requireNonNull(commands, "commands cannot be null"), commands.length);
            this.description = description != null ? description : "No description";
            this.author = author != null ? author : "Unknown";
            this.relativePath = relativePath != null ? relativePath : "";
        }

        // Add new getter
        public String getRelativePath() {
            return relativePath;
        }

        // Existing getters...
        public String getAuthor() {
            return author;
        }

        public String getFileName() {
            return fileName;
        }

        public String getTitle() {
            return title;
        }

        public String[] getCommands() {
            return Arrays.copyOf(commands, commands.length);
        }

        public String getDescription() {
            return description;
        }
    }

    private static List<String> flattenMacroCommands(MacroBB macro, BitwigBuddyExtension extension,
            Set<String> visitedMacros) {
        List<String> flattenedCommands = new ArrayList<>();

        // Prevent infinite recursion
        if (visitedMacros.contains(macro.getTitle())) {
            extension.getHost().errorln("Circular macro reference detected: " + macro.getTitle());
            return flattenedCommands;
        }

        visitedMacros.add(macro.getTitle());

        // Process commands through LoopProcessor first
        LoopProcessor loopProcessor = new LoopProcessor();
        List<String> processedCommands = loopProcessor.processLoop(Arrays.asList(macro.getCommands()));

        for (String command : processedCommands) {
            // Check if this command is a macro reference
            if (command.trim().matches("(?i)Macro\\s*\\(.*")) {
                // Extract macro name using regex to handle whitespace variations
                java.util.regex.Pattern pattern = java.util.regex.Pattern
                        .compile("(?i)Macro\\s*\\(\\s*\"([^\"]+)\"\\s*\\)");
                java.util.regex.Matcher matcher = pattern.matcher(command);

                if (matcher.find()) {
                    String macroName = matcher.group(1);

                    // Find referenced macro
                    MacroBB[] macros = getMacros();
                    boolean macroFound = false;
                    for (MacroBB referencedMacro : macros) {
                        if (referencedMacro.getTitle().equals(macroName)) {
                            // Recursively flatten nested macro
                            flattenedCommands.addAll(flattenMacroCommands(referencedMacro, extension, visitedMacros));
                            macroFound = true;
                            break;
                        }
                    }

                    if (!macroFound) {
                        extension.getHost().errorln("Referenced macro not found: " + macroName);
                    }
                } else {
                    extension.getHost().errorln("Invalid macro reference format: " + command);
                }
            } else {
                flattenedCommands.add(command);
            }
        }

        return flattenedCommands;
    }

    // -------------------- Getter Methods --------------------

    public static Object getExecutionLock() {
        return EXECUTION_LOCK;
    }

    public static void resetExecutionState() {
        synchronized (EXECUTION_LOCK) {
            isExecuting = false;
            currentExecutionSource = null;
            stopExecution = false;
        }
    }
}
