package com.centomila;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.bitwig.extension.controller.api.ControllerHost;

public class CustomPresetsHandler {
    private final ControllerHost host;
    private final GlobalPreferences preferences;

    public CustomPresetsHandler(ControllerHost host, GlobalPreferences preferences) {
        this.host = host;
        this.preferences = preferences;
    }

    /**
     * Reads all files in the current presets folder, parses their content and returns an array of CustomPreset.
     * Each file is expected to contain lines like:
     *   Name: "Kick Four On The Floor"
     *   DefaultNote: "C1"
     *   Pattern: [100, 0, 0, 0, 100, 0, 0, 0, 100, 0, 0, 0, 100, 0, 0, 0]
     * @return an array of CustomPreset objects, or an empty array if none are found.
     */
    public CustomPreset[] getCustomPresets() {
        File presetsDir = new File(preferences.getPresetsPath());
        if (presetsDir.exists() && presetsDir.isDirectory()) {
            File[] files = presetsDir.listFiles();
            if (files != null) {
                Arrays.sort(files, (f1, f2) -> Utils.naturalCompare(f1.getName(), f2.getName()));
                List<CustomPreset> presetList = new ArrayList<>();
                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                            String name = "";
                            String defaultNote = "";
                            int[] pattern = new int[0];
                            for (String line : lines) {
                                line = line.trim();
                                if (line.startsWith("Name:")) {
                                    name = extractValue(line);
                                } else if (line.startsWith("DefaultNote:")) {
                                    defaultNote = extractValue(line);
                                } else if (line.startsWith("Pattern:")) {
                                    pattern = extractIntArray(line);
                                }
                            }
                            presetList.add(new CustomPreset(file.getName(), name, defaultNote, pattern));
                        } catch (IOException e) {
                            host.errorln("Failed to read file " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
                return presetList.toArray(new CustomPreset[0]);
            }
        }
        return new CustomPreset[0];
    }

    // Helper method to extract the value between quotes
    private String extractValue(String line) {
        int firstQuote = line.indexOf('"');
        int lastQuote = line.lastIndexOf('"');
        if (firstQuote >= 0 && lastQuote > firstQuote) {
            return line.substring(firstQuote + 1, lastQuote);
        }
        return "";
    }

    // Helper method to extract an integer array from a line such as: Pattern: [100, 0, 0, 0, ...]
    private int[] extractIntArray(String line) {
        int start = line.indexOf('[');
        int end = line.indexOf(']');
        if (start < 0 || end < 0 || end <= start) {
            return new int[0];
        }
        String numbers = line.substring(start + 1, end);
        String[] parts = numbers.split(",");
        List<Integer> ints = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                try {
                    ints.add(Integer.parseInt(part));
                } catch (NumberFormatException e) {
                    host.errorln("Failed to parse number from part: " + part);
                }
            }
        }
        int[] intArray = new int[ints.size()];
        for (int i = 0; i < ints.size(); i++) {
            intArray[i] = ints.get(i);
        }
        return intArray;
    }

    /**
     * Represents a custom preset for the BeatBuddy extension.
     * Each preset contains a file name, display name, default MIDI note, and a pattern sequence.
     */
    public final class CustomPreset {
        private final String fileName;
        private final String name;
        private final String defaultNote;
        private final int[] pattern;

        /**
         * Creates a new CustomPreset instance.
         */
        public CustomPreset(String fileName, String name, String defaultNote, int[] pattern) {
            this.fileName = fileName;
            this.name = name;
            this.defaultNote = defaultNote;
            this.pattern = pattern;
        }

        public String getDefaultNote() {
            return defaultNote;
        }

        public String getFileName() {
            return fileName;
        }

        public String getName() {
            return name;
        }

        public int[] getPattern() {
            return pattern;
        }
    }
}
