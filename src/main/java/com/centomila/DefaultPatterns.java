package com.centomila;

public interface DefaultPatterns {

    final static Pattern[] patterns = {
        // --- Various ---
        new Pattern("Various: 8th Increasing Velocity", new int[] { 3, 0, 17, 0, 33, 0, 49, 0, 65, 0, 81, 0, 97, 0, 115, 0 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Various: 16th Increasing Velocity", new int[] { 3, 9, 17, 25, 33, 41, 49, 57, 65, 73, 81, 89, 97, 105, 115, 127 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Various: 8th Decreasing Velocity", new int[] { 115, 0, 97, 0, 81, 0, 65, 0, 49, 0, 33, 0, 17, 0, 3, 0 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Various: 16th Decreasing Velocity", new int[] { 127, 115, 105, 97, 89, 81, 73, 65, 57, 49, 41, 33, 25, 17, 9, 3 }, "C1", "1/16", "1/16", "Straight"),
        // --- KICK-ONLY PATTERNS ---
        new Pattern("Kick: Four on the Floor", new int[] { 100, 0, 0, 0, 100, 0, 0, 0, 100, 0, 0, 0, 100, 0, 0, 0 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Kick: 1 and 3", new int[] { 100, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Kick: Syncopated (1, & of 2, 3)", new int[] { 100, 0, 0, 0, 0, 0, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Kick: Off-Beats (2 and 4)", new int[] { 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0 }, "C1", "1/16", "1/16", "Straight"),
        new Pattern("Kick: Driving 8th Notes", new int[] { 100, 0, 100, 0, 100, 0, 100, 0, 100, 0, 100, 0, 100, 0, 100, 0 }, "C1", "1/16", "1/16", "Straight"),
        // --- SNARE-ONLY PATTERNS ---
        new Pattern("Snare: Backbeat 2 and 4", new int[] { 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0 }, "C#1", "1/16", "1/16", "Straight"),
        new Pattern("Snare: Only on Beat 2", new int[] { 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "C#1", "1/16", "1/16", "Straight"),
        new Pattern("Snare: Half-Time (3)", new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0 }, "C#1", "1/16", "1/16", "Straight"),
        new Pattern("Snare: 16th Ghost Notes", new int[] { 20, 0, 20, 0, 100, 0, 20, 0, 20, 0, 20, 0, 100, 0, 20, 0 }, "C#1", "1/16", "1/16", "Straight"),
        new Pattern("Snare: 16th Machine Gun", new int[] { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 }, "C#1", "1/16", "1/16", "Straight"),
        // --- HI-HAT-ONLY PATTERNS ---
        new Pattern("HiHat: Quarter Notes", new int[] { 80, 0, 0, 0, 80, 0, 0, 0, 80, 0, 0, 0, 80, 0, 0, 0 }, "D1", "1/16", "1/16", "Straight"),
        new Pattern("HiHat: 8th Notes", new int[] { 80, 0, 80, 0, 80, 0, 80, 0, 80, 0, 80, 0, 80, 0, 80, 0 }, "D1", "1/16", "1/16", "Straight"),
        new Pattern("HiHat: 16th Notes", new int[] { 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80 }, "D1", "1/16", "1/16", "Straight"),
        new Pattern("HiHat: Off-Beat 8ths", new int[] { 0, 80, 0, 80, 0, 80, 0, 80, 0, 80, 0, 80, 0, 80, 0, 80 }, "D1", "1/16", "1/16", "Straight"),
        new Pattern("HiHat: On 3rd Beat", new int[] { 0, 0, 80, 0, 0, 0, 80, 0, 0, 0, 80, 0, 0, 0, 80, 0 }, "D1", "1/16", "1/16", "Straight"),
        new Pattern("HiHat: Shuffle (Swing 16ths)", new int[] { 80, 0, 40, 0, 80, 0, 40, 0, 80, 0, 40, 0, 80, 0, 40, 0 }, "D1", "1/16", "1/16", "Straight")
    };

    /**
     * Retrieves a drum pattern by its name.
     *
     * @param name the name of the drum pattern to retrieve
     * @return the drum pattern as an array of integers
     * @throws IllegalArgumentException if no pattern is found with the specified name
     */
    public static int[] getPatternByName(String name) {
        for (Pattern pattern : patterns) {
            if (pattern.getName().equals(name)) {
                return pattern.getPattern();
            }
        }
        // Handle not found case appropriately
        throw new IllegalArgumentException("No pattern found with name: " + name);
    }

    void init();
}

class Pattern {
    private String name;
    private int[] pattern;
    private String defaultNote;
    private String defaultStepSize;
    private String defaultNoteLength;
    private String defaultSubdivisions;

    public Pattern(String name, int[] pattern, String defaultNote, String defaultStepSize, String defaultNoteLength, String defaultSubdivisions) {
        this.name = name;
        this.pattern = pattern;
        this.defaultNote = defaultNote;
        this.defaultStepSize = defaultStepSize;
        this.defaultNoteLength = defaultNoteLength;
        this.defaultSubdivisions = defaultSubdivisions;
    }

    public String getName() {
        return name;
    }

    public int[] getPattern() {
        return pattern;
    }

    public String getDefaultNote() {
        return defaultNote;
    }

    public String getDefaultStepSize() {
        return defaultStepSize;
    }

    public String getDefaultNoteLength() {
        return defaultNoteLength;
    }

    public String getDefaultSubdivisions() {
        return defaultSubdivisions;
    }
}
