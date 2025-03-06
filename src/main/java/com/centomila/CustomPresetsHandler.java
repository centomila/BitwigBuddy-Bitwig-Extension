package com.centomila;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.StringValue;

/**
 * Handles the loading and parsing of custom presets for the BitwigBuddy
 * extension.
 * Each preset is stored in a text file with a specific format containing name,
 * default MIDI note,
 * and pattern information.
 */
public class CustomPresetsHandler {
    private static final String NAME_PREFIX = "Name:";
    private static final String DEFAULT_NOTE_PREFIX = "DefaultNote:";
    private static final String PATTERN_PREFIX = "Pattern:";

    private final ControllerHost host;
    private final GlobalPreferences preferences;

    /**
     * Creates a new CustomPresetsHandler instance.
     * 
     * @param host        The Bitwig controller host for logging
     * @param preferences Global preferences containing the presets path
     * @throws NullPointerException if host or preferences is null
     */
    public CustomPresetsHandler(ControllerHost host, GlobalPreferences preferences) {
        this.host = Objects.requireNonNull(host, "Host cannot be null");
        this.preferences = Objects.requireNonNull(preferences, "Preferences cannot be null");
    }

    /**
     * Reads all preset files from the configured presets directory.
     * Files are sorted naturally by name before processing.
     * 
     * @return Array of CustomPreset objects, empty array if no presets are found
     */
    public CustomPreset[] getCustomPresets() {
        File presetsDir = new File(preferences.getPresetsPath());
        String subdir = "Custom Presets";
        presetsDir = new File(presetsDir, subdir);
        List<CustomPreset> presetList = new ArrayList<>();

        // Early return with default preset if directory doesn't exist or isn't
        // accessible
        if (!presetsDir.exists() || !presetsDir.isDirectory()) {
            host.errorln("Using default preset - directory does not exist or is not accessible: " + presetsDir);
            return new CustomPreset[] { createDefaultPreset() };
        }

        File[] files = presetsDir.listFiles();
        if (files == null) {
            host.errorln("Using default preset - failed to list files in directory: " + presetsDir);
            return new CustomPreset[] { createDefaultPreset() };
        }

        // Sort files by name
        Arrays.sort(files);

        // Check if directory is empty
        if (files.length == 0) {
            host.errorln("Using default preset - directory is empty");
            return new CustomPreset[] { createDefaultPreset() };
        }

        // Process each file
        for (File file : files) {
            if (file.isFile()) {
                try {
                    CustomPreset preset = readPresetFile(file);
                    if (preset != null) {
                        presetList.add(preset);
                    }
                } catch (IOException e) {
                    host.errorln("Failed to read preset file " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        // Return default preset if no valid presets were found
        if (presetList.isEmpty()) {
            host.errorln("Using default preset - no valid preset files found");
            return new CustomPreset[] { createDefaultPreset() };
        }

        return presetList.toArray(new CustomPreset[0]);
    }

    private CustomPreset createDefaultPreset() {
        // Simple default pattern: quarter notes
        int[] defaultPattern = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        return new CustomPreset(
                "default.txt",
                "NO CUSTOM PATTERN",
                "C1",
                defaultPattern);
    }

    /**
     * Retrieves the pattern array for a preset with the given name.
     * 
     * @param patternName The name of the preset to search for
     * @return The pattern array if found, null if no preset matches the name
     */
    public int[] getCustomPatternByName(String patternName) {
        if (patternName == null) {
            return null;
        }

        CustomPreset[] presets = getCustomPresets();
        for (CustomPreset preset : presets) {
            if (patternName.equals(preset.getName())) {
                return preset.getPattern();
            }
        }

        host.errorln("No preset found with name: " + patternName);
        return null;
    }

    /**
     * Reads and parses a single preset file.
     * 
     * @param file The preset file to read
     * @return CustomPreset object if successful, null if parsing failed
     * @throws IOException if file reading fails
     */
    private CustomPreset readPresetFile(File file) throws IOException {
        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        String name = "";
        String defaultNote = "";
        int[] pattern = new int[0];

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            try {
                if (line.startsWith(NAME_PREFIX)) {
                    name = extractQuotedValue(line);
                } else if (line.startsWith(DEFAULT_NOTE_PREFIX)) {
                    defaultNote = extractQuotedValue(line);
                } else if (line.startsWith(PATTERN_PREFIX)) {
                    pattern = parsePatternArray(line);
                }
            } catch (IllegalArgumentException e) {
                host.errorln("Error parsing line in " + file.getName() + ": " + e.getMessage());
                return null;
            }
        }

        if (name.isEmpty() || defaultNote.isEmpty() || pattern.length == 0) {
            host.errorln("Invalid preset file " + file.getName() + ": missing required fields");
            return null;
        }

        return new CustomPreset(file.getName(), name, defaultNote, pattern);
    }

    /**
     * Extracts a value enclosed in quotes from a line.
     * 
     * @throws IllegalArgumentException if the format is invalid
     */
    private String extractQuotedValue(String line) {
        int firstQuote = line.indexOf('"');
        int lastQuote = line.lastIndexOf('"');
        if (firstQuote < 0 || lastQuote <= firstQuote) {
            throw new IllegalArgumentException("Invalid format - expected quoted value");
        }
        return line.substring(firstQuote + 1, lastQuote);
    }

    /**
     * Parses a pattern array from a line containing comma-separated integers.
     * 
     * @throws IllegalArgumentException if the format is invalid
     */
    private int[] parsePatternArray(String line) {
        int start = line.indexOf('[');
        int end = line.indexOf(']');
        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("Invalid pattern format - expected [n1,n2,...]");
        }

        String[] parts = line.substring(start + 1, end).split(",");
        return Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .mapToInt(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid number format: " + s);
                    }
                })
                .toArray();
    }

    /**
     * Immutable class representing a custom preset for the BitwigBuddy extension.
     */
    public static final class CustomPreset {
        private final String fileName;
        private final String name;
        private final String defaultNote;
        private final int[] pattern;

        /**
         * Creates a new CustomPreset instance.
         * 
         * @throws NullPointerException if any parameter is null
         */
        public CustomPreset(String fileName, String name, String defaultNote, int[] pattern) {
            this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
            this.name = Objects.requireNonNull(name, "name cannot be null");
            this.defaultNote = Objects.requireNonNull(defaultNote, "defaultNote cannot be null");
            this.pattern = Arrays.copyOf(Objects.requireNonNull(pattern, "pattern cannot be null"), pattern.length);
        }

        public String getFileName() {
            return fileName;
        }

        public String getName() {
            return name;
        }

        public String getDefaultNote() {
            return defaultNote;
        }

        public int[] getPattern() {
            return Arrays.copyOf(pattern, pattern.length);
        }
    }

    /**
     * Saves a custom preset to file, ensuring it has a .txt extension.
     * 
     * @param preset      The preset to save
     * @param preferences Global preferences containing the presets path
     * @param host        The Bitwig controller host for logging
     */
    public static void saveCustomPreset(CustomPreset preset, GlobalPreferences preferences, ControllerHost host) {
        File presetsDir = new File(preferences.getPresetsPath());
        String subdir = "Custom Presets";
        presetsDir = new File(presetsDir, subdir);
        
        // Ensure filename has .txt extension
        String presetName =  ((StringValue) PatternSettings.customPresetSaveNamSetting).get();
        String fileName = presetName;
        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName = fileName + ".txt";
        }

        String defaultNote = NoteDestinationSettings.currentNoteAsString + NoteDestinationSettings.currentOctaveAsInt;
        
        File presetFile = new File(presetsDir, fileName);

        try {
            if (!presetsDir.exists() && !presetsDir.mkdirs()) {
                host.errorln("Failed to create directory: " + presetsDir);
                return;
            }

            List<String> lines = new ArrayList<>();
            lines.add(NAME_PREFIX + " \"" + presetName + "\"");
            lines.add(DEFAULT_NOTE_PREFIX + " \"" + defaultNote + "\"");
            lines.add(PATTERN_PREFIX + " [" + Arrays.toString(preset.getPattern()).replaceAll("[\\[\\]]", "") + "]");

            java.nio.file.Files.write(presetFile.toPath(), lines);
        } catch (IOException e) {
            host.errorln("Failed to save preset file " + fileName + ": " + e.getMessage());
        }
    }
}
