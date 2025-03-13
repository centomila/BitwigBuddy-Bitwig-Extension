package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.centomila.utils.ExecuteBitwigAction;
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
 * user-defined macros
 * that can automate sequences of Bitwig Studio actions.
 * 
 * <p>
 * A macro consists of a title, description, and a sequence of commands that can
 * be
 * executed in order with configurable delays between each command.
 * </p>
 * 
 * <p>
 * Macros are stored as text files in the Macros subdirectory of the extension's
 * presets directory.
 * </p>
 */
public class MacroActionSettings {

    public static Setting macroLaunchBtnSignalSetting;
    public static Setting macroSelectorSetting;
    public static Setting macroDescriptionSetting;
    public static Setting macroAuthorSetting; // Add this line
    // public static Setting macroPrintAllActionsBtnSignalSetting;
    public static Setting macroSpacerSetting;
    public static Setting[] allSettings;
    private static ControllerHost host;
    private static GlobalPreferences preferences;
    private static final String MACRO_PREFIX = "Macro:";
    private static long lastExecutionTime = 0;
    private static final long DEBOUNCE_MS = 500; // Adjust as needed
    // TODO: 8 FIELDS FOR INSTA MACRO + SELECTOR: MACO FOLDER/INSTANT

    public static Setting instantMacroSpacer;
    public static Setting[] instantMacroLines = new Setting[8];
    public static Setting executeInstantMacroSignal;
    public static Setting clearAllInstantMacroLines; // Add this new field

    // Add this near the other static fields at the top of the class
    public static Setting macroStopBtnSignalSetting;
    public static volatile boolean stopExecution = false;
    
    private static final int MAX_NESTING_LEVEL = Integer.MAX_VALUE;
    private static boolean isExecutingMacro = false;
    private static int nestingLevel = 0;

    // Add these near other static fields at top of class
    private static volatile boolean isExecuting = false;
    private static final Object executionLock = new Object();
    private static volatile String currentExecutionSource = null;
    private static volatile long executionStartTime = 0;

    // Add near the top with other static fields
    private static final Object EXECUTION_LOCK = new Object();

    // Add these public static methods
    public static Object getExecutionLock() {
        return EXECUTION_LOCK;
    }

    public static void resetExecutionState() {
        synchronized(EXECUTION_LOCK) {
            isExecuting = false;
            currentExecutionSource = null;
            stopExecution = false;
        }
    }

    /**
     * Initializes the macro action settings for the extension.
     * Sets up the necessary settings UI elements and observers.
     *
     * @param extension The BitwigBuddy extension instance
     */
    public static void init(BitwigBuddyExtension extension) {

        host = extension.getHost();
        preferences = extension.preferences;

        initMacroActionSettings();
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

        macroSpacerSetting = (Setting) createStringSetting(titleWithLine("MACRO"), "Macro", 0,
                "---------------------------------------------------");
        disableSetting(macroSpacerSetting);

        // Get macro titles for the selector
        String[] macroTitles = getMacroTitles();
        if (macroTitles.length == 0) {
            macroTitles = new String[] { "No Macros Found" };
        }

        macroSelectorSetting = (Setting) createEnumSetting("Select a Macro", "Macro", macroTitles,
                macroTitles[0]);

        macroDescriptionSetting = (Setting) createStringSetting("Macro Description", "Macro", 0,
                "Select a macro to execute");

        macroAuthorSetting = (Setting) createStringSetting("Macro Author", "Macro", 0,
                "Unknown");

        macroLaunchBtnSignalSetting = (Setting) createSignalSetting("Execute Macro",
                "Macro", "Execute the selected macro");

        // Add this after the macroLaunchBtnSignalSetting initialization
        macroStopBtnSignalSetting = (Setting) createSignalSetting("Stop Execution",
                "Macro", "Stop the current macro execution");

        // macroPrintAllActionsBtnSignalSetting = (Setting) createSignalSetting("Print
        // All Actions in Console",
        // "Macro", "Signal to print all available actions");

        // INSTANT MACRO SETTINGS
        instantMacroSpacer = (Setting) createStringSetting(titleWithLine("INSTANT MACRO"), "Macro", 0,
                "---------------------------------------------------");
        disableSetting(instantMacroSpacer);
        // Initialize instant macro line settings
        for (int i = 0; i < 8; i++) {
            instantMacroLines[i] = (Setting) createStringSetting("Macro Line " + (i + 1), "Instant Macro",
                    256, "");
        }

        executeInstantMacroSignal = (Setting) createSignalSetting("Execute Instant Macro",
                "Instant Macro", "Execute this commands sequence");

        clearAllInstantMacroLines = (Setting) createSignalSetting("Clear All Lines",
                "Instant Macro", "Clear all instant macro lines");

        // Update allSettings array to include new settings
        allSettings = new Setting[] {
                macroLaunchBtnSignalSetting,
                macroStopBtnSignalSetting, // Add this line
                macroSelectorSetting,
                macroDescriptionSetting,
                macroAuthorSetting, // Add this line
                macroSpacerSetting,
                instantMacroLines[0],
                instantMacroLines[1],
                instantMacroLines[2],
                instantMacroLines[3],
                instantMacroLines[4],
                instantMacroLines[5],
                instantMacroLines[6],
                instantMacroLines[7],
                executeInstantMacroSignal,
                clearAllInstantMacroLines  // Add this new setting
        };
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
        ((Signal) macroLaunchBtnSignalSetting).addSignalObserver(() -> {
            host.println("Signal triggered at " + 
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastExecutionTime > DEBOUNCE_MS) {
                lastExecutionTime = currentTime;

                Macro macro = getSelectedMacro();
                if (macro != null) {
                    executeMacro(macro, extension);
                }
            } else {
                host.println("Ignoring rapid signal trigger, wait " + DEBOUNCE_MS + "ms between triggers");
            }
        });

        // Add observer for macro selection changes
        ((SettableEnumValue) macroSelectorSetting).addValueObserver(newValue -> {
            Macro macro = getSelectedMacro();
            if (macro != null) {
                ((SettableStringValue) macroDescriptionSetting).set(macro.getDescription());
                ((SettableStringValue) macroAuthorSetting).set(macro.getAuthor()); // Add this line
            } else {
                ((SettableStringValue) macroDescriptionSetting).set("No description");
                ((SettableStringValue) macroAuthorSetting).set("Unknown"); // Add this line
            }
        });

        // ((Signal) macroPrintAllActionsBtnSignalSetting).addSignalObserver(() -> {
        // printAllAvailableActions(extension);
        // });

        ((Signal) executeInstantMacroSignal).addSignalObserver(() -> {
            synchronized(executionLock) {
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
                    Macro instantMacro = new Macro(
                            "instant_macro",
                            "Instant Macro",
                            commands.toArray(new String[0]),
                            "Instant macro execution",
                            "Unknown"
                    );

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
    }

    /**
     * Executes a macro by running its commands in sequence.
     * Logs the execution start time and details of each command.
     *
     * @param macro     The macro to execute
     * @param extension The BitwigBuddy extension instance
     */
    private static void executeMacro(Macro macro, BitwigBuddyExtension extension) {
        synchronized(executionLock) {
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
                // Note: Don't reset isExecuting here - it should be reset when all commands complete
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
    public static void executeMacroFromAction(Macro macro, BitwigBuddyExtension extension) {
        synchronized(EXECUTION_LOCK) {
            // Check for recursive execution
            if (isExecutingMacro) {
                extension.getHost().println("Already executing a macro - nesting level: " + nestingLevel);
                if (nestingLevel >= MAX_NESTING_LEVEL) {
                    extension.getHost().errorln("Maximum macro nesting level reached (" + MAX_NESTING_LEVEL + "). Stopping execution.");
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
    private static void scheduleCommands(String[] commands, int index, BitwigBuddyExtension extension) {
        if (index >= commands.length || stopExecution) {
            synchronized(executionLock) {
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
            // Try executing via ExecuteBitwigAction first
            boolean handled = ExecuteBitwigAction.executeBitwigAction(command, extension);
            
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

        // Schedule next command with delay
        extension.getHost().scheduleTask(() -> {
            scheduleCommands(commands, index + 1, extension);
        }, 50); // Increased delay to 50ms for better reliability
    }

    /**
     * Hides all macro-related settings in the UI.
     */
    public static void hideMacroSettings() {
        for (Setting setting : allSettings) {
            setting.hide();
        }
    }

    /**
     * Shows all macro-related settings in the UI.
     */
    public static void showMacroSettings() {
        for (Setting setting : allSettings) {
            setting.show();
        }
    }

    /**
     * Returns an array of all available macros.
     * Reads macro files from the configured macros directory and sorts them by
     * name.
     *
     * @return Array of Macro objects, empty array if no macros are found
     */
    public static Macro[] getMacros() {
        File macrosDir = new File(preferences.getPresetsPath());
        String subdir = "Macros";
        macrosDir = new File(macrosDir, subdir);
        List<Macro> macroList = new ArrayList<>();

        // Early return with empty array if directory doesn't exist or isn't accessible
        if (!macrosDir.exists() || !macrosDir.isDirectory()) {
            host.errorln("Macro directory does not exist or is not accessible: " + macrosDir);
            return new Macro[0];
        }

        File[] files = macrosDir.listFiles();
        if (files == null) {
            host.errorln("Failed to list files in macro directory: " + macrosDir);
            return new Macro[0];
        }

        // Sort files by name
        Arrays.sort(files);

        // Check if directory is empty
        if (files.length == 0) {
            host.errorln("Macro directory is empty");
            return new Macro[0];
        }

        // Process each file
        for (File file : files) {
            if (file.isFile()) {
                try {
                    Macro macro = readMacroFile(file);
                    if (macro != null) {
                        macroList.add(macro);
                    }
                } catch (IOException e) {
                    host.errorln("Failed to read macro file " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        return macroList.toArray(new Macro[0]);
    }

    /**
     * Returns the currently selected macro based on the selector setting.
     *
     * @return The selected Macro or null if none is selected or available
     */
    public static Macro getSelectedMacro() {

        String selectedTitle = ((SettableEnumValue) macroSelectorSetting).get();
        Macro[] macros = getMacros();

        for (Macro macro : macros) {
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
        Macro[] macros = getMacros();
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
    private static Macro readMacroFile(File file) throws IOException {
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

        return new Macro(file.getName(), title, commands.toArray(new String[0]), description, author);
    }

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
     * Immutable class representing a macro for the BitwigBuddy extension.
     */
    public static final class Macro {
        private final String fileName;
        private final String title;
        private final String[] commands;
        private final String description;
        private final String author;

        public Macro(String fileName, String title, String[] commands, String description, String author) {
            this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
            this.title = Objects.requireNonNull(title, "title cannot be null");
            this.commands = Arrays.copyOf(Objects.requireNonNull(commands, "commands cannot be null"), commands.length);
            this.description = description != null ? description : "No description";
            this.author = author != null ? author : "Unknown";
        }

        // Add getter for author
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

    private static List<String> flattenMacroCommands(Macro macro, BitwigBuddyExtension extension, Set<String> visitedMacros) {
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
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)Macro\\s*\\(\\s*\"([^\"]+)\"\\s*\\)");
                java.util.regex.Matcher matcher = pattern.matcher(command);
                
                if (matcher.find()) {
                    String macroName = matcher.group(1);
                    
                    // Find referenced macro
                    Macro[] macros = getMacros();
                    boolean macroFound = false;
                    for (Macro referencedMacro : macros) {
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
}
