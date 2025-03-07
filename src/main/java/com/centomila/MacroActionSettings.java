package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.centomila.BitwigBuddyExtension;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MacroActionSettings {

    public static Setting macroLaunchBtnSignalSetting;
    public static Setting macroSelectorSetting;
    public static Setting macroPrintAllActionsBtnSignalSetting;
    public static Setting[] AllSettings;
    private static ControllerHost host;
    private static GlobalPreferences preferences;
    private static final String MACRO_PREFIX = "Macro:";

    public static void init(BitwigBuddyExtension extension) {

        host = extension.getHost();
        preferences = extension.preferences;

        initMacroActionSettings();
        initMacroActionObservers(extension);
    }

    private static void initMacroActionSettings() {
        macroLaunchBtnSignalSetting = (Setting) createSignalSetting("macroLaunchBtnSignal",
                "Macro Launch Button Signal", "Signal to launch the macro action");

        // Get macro titles for the selector
        String[] macroTitles = getMacroTitles();
        if (macroTitles.length == 0) {
            macroTitles = new String[] { "No Macros Found" };
        }

        macroSelectorSetting = (Setting) createEnumSetting("macroSelector", "Macro Selector", macroTitles,
                macroTitles[0]);

        macroPrintAllActionsBtnSignalSetting = (Setting) createSignalSetting("macroPrintAllActionsBtnSignal",
                "Print All Actions Button Signal", "Signal to print all available actions");

        AllSettings = new Setting[] { macroLaunchBtnSignalSetting, macroSelectorSetting,
                macroPrintAllActionsBtnSignalSetting };
    }

    private static void initMacroActionObservers(BitwigBuddyExtension extension) {
        ((Signal) macroLaunchBtnSignalSetting).addSignalObserver(() -> {

            Macro macro = getSelectedMacro();
            if (macro != null) {
                executeMacro(macro, extension);
            }

        });

        ((Signal) macroPrintAllActionsBtnSignalSetting).addSignalObserver(() -> {
            printAllAvailableActions(extension);
        });
    }

    private static void executeMacro(Macro macro, BitwigBuddyExtension extension) {
        for (String command : macro.getCommands()) {
            host.println("Executing command: " + command);
            host.scheduleTask(() -> {
                host.println("command" + command);
                extension.getApplication().getAction(command).invoke();
            }, 100);
        }
    }

    /**
     * Reads all macro files from the configured macros directory.
     * Files are sorted naturally by name before processing.
     * 
     * @return Array of Macro objects, empty array if no macros are found
     */
    public static Macro[] getMacros() {
        File macrosDir = new File(preferences.getPresetsPath());
        String subdir = "macros";
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
        List<String> commands = new ArrayList<>();

        // First line should be the macro title
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

        // Rest of the lines are commands
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                commands.add(line);
            }
        }

        if (title.isEmpty()) {
            host.errorln("Invalid macro file " + file.getName() + ": missing title");
            return null;
        }

        return new Macro(file.getName(), title, commands.toArray(new String[0]));
    }

    private static void printAllAvailableActions(BitwigBuddyExtension extension) {
        host.println("Available actions:");
        for (Action action : extension.getApplication().getActions()) {
            host.println(action.getName());
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
     * Get the currently selected macro based on the selector setting.
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
     * Immutable class representing a macro for the BitwigBuddy extension.
     */
    public static final class Macro {
        private final String fileName;
        private final String title;
        private final String[] commands;

        /**
         * Creates a new Macro instance.
         * 
         * @throws NullPointerException if any parameter is null
         */
        public Macro(String fileName, String title, String[] commands) {
            this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
            this.title = Objects.requireNonNull(title, "title cannot be null");
            this.commands = Arrays.copyOf(Objects.requireNonNull(commands, "commands cannot be null"), commands.length);
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
    }
}
