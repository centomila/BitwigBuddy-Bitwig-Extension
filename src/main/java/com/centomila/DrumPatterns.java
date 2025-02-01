package com.centomila;

public interface DrumPatterns {

    void init();

    final Object[][] patterns = {
            // --- KICK-ONLY PATTERNS ---
            {
                    "Kick: Four on the Floor",
                    new int[] {
                            100, 0, 0, 0,
                            100, 0, 0, 0,
                            100, 0, 0, 0,
                            100, 0, 0, 0
                    }
            },
            {
                    "Kick: 1 and 3",
                    new int[] {
                            100, 0, 0, 0,
                            0, 0, 0, 0,
                            100, 0, 0, 0,
                            0, 0, 0, 0
                    }
            },
            {
                    "Kick: Syncopated (1, & of 2, 3)",
                    new int[] {
                            100, 0, 0, 0,
                            0, 0, 100, 0,
                            100, 0, 0, 0,
                            0, 0, 0, 0
                    }
            },
            {
                    "Kick: Off-Beats (2 and 4)",
                    new int[] {
                            0, 0, 0, 0,
                            100, 0, 0, 0,
                            0, 0, 0, 0,
                            100, 0, 0, 0
                    }
            },
            {
                    "Kick: Driving 8th Notes",
                    new int[] {
                            100, 0, 100, 0,
                            100, 0, 100, 0,
                            100, 0, 100, 0,
                            100, 0, 100, 0
                    }
            },

            // --- SNARE-ONLY PATTERNS ---
            {
                    "Snare: Backbeat 2 and 4",
                    new int[] {
                            0, 0, 0, 0,
                            100, 0, 0, 0,
                            0, 0, 0, 0,
                            100, 0, 0, 0
                    }
            },
            {
                    "Snare: Only on Beat 2",
                    new int[] {
                            0, 0, 0, 0,
                            100, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    }
            },
            {
                    "Snare: Half-Time (3)",
                    new int[] {
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            100, 0, 0, 0,
                            0, 0, 0, 0
                    }
            },
            {
                    "Snare: 16th Ghost Notes",
                    new int[] {
                            20, 0, 20, 0,
                            100, 0, 20, 0,
                            20, 0, 20, 0,
                            100, 0, 20, 0
                    }
            },
            {
                    "Snare: 16th Machine Gun",
                    new int[] {
                            100, 100, 100, 100,
                            100, 100, 100, 100,
                            100, 100, 100, 100,
                            100, 100, 100, 100
                    }
            },

            // --- HI-HAT-ONLY PATTERNS ---
            {
                    "HiHat: Quarter Notes",
                    new int[] {
                            80, 0, 0, 0,
                            80, 0, 0, 0,
                            80, 0, 0, 0,
                            80, 0, 0, 0
                    }
            },
            {
                    "HiHat: 8th Notes",
                    new int[] {
                            80, 0, 80, 0,
                            80, 0, 80, 0,
                            80, 0, 80, 0,
                            80, 0, 80, 0
                    }
            },
            {
                    "HiHat: 16th Notes",
                    new int[] {
                            80, 80, 80, 80,
                            80, 80, 80, 80,
                            80, 80, 80, 80,
                            80, 80, 80, 80
                    }
            },
            {
                    "HiHat: Off-Beat 8ths",
                    new int[] {
                            0, 80, 0, 80,
                            0, 80, 0, 80,
                            0, 80, 0, 80,
                            0, 80, 0, 80
                    }
            },
            {
                    "HiHat: Shuffle (Swing 16ths)",
                    new int[] {
                            80, 0, 40, 0,
                            80, 0, 40, 0,
                            80, 0, 40, 0,
                            80, 0, 40, 0
                    }
            }
    };

    static int[] getPatternByName(String name) {
        for (Object[] pattern : patterns) {
            if (pattern[0].equals(name)) {
                // pattern[1] is our int[] data
                return (int[]) pattern[1];
            }
        }
        // Handle not found case appropriately
        throw new IllegalArgumentException("No pattern found with name: " + name);
    }
}
