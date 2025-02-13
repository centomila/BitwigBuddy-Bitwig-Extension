package com.centomila;

public interface DefaultPatterns {

        final static Object[][] patterns = {
                        // --- Various ---
                        {
                                        "Various: 8th Increasing Velocity",
                                        new int[] {
                                                        3, 0, 17, 0, 33, 0, 49, 0, 65, 0, 81, 0, 97, 0, 115, 0
                                        }
                        },
                        {
                                        "Various: 16th Increasing Velocity",
                                        new int[] {
                                                        3, 9, 17, 25, 33, 41, 49, 57, 65, 73, 81, 89, 97, 105, 115, 127
                                        }
                        },
                        {
                                        "Various: 8th Decreasing Velocity",
                                        new int[] {
                                                        115, 0, 97, 0, 81, 0, 65, 0, 49, 0, 33, 0, 17, 0, 3, 0
                                        }
                        },
                        {
                                        "Various: 16th Decreasing Velocity",
                                        new int[] {
                                                        127, 115, 105, 97, 89, 81, 73, 65, 57, 49, 41, 33, 25, 17, 9, 3
                                        }
                        },
                        // --- KICK-ONLY PATTERNS ---
                        {
                                        "Kick: Four on the Floor",
                                        new int[] {
                                                        100, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        100, 0, 0, 0
                                        },
                                        "C1"
                        },
                        {
                                        "Kick: 1 and 3",
                                        new int[] {
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0
                                        },
                                        "C1"
                        },
                        {
                                        "Kick: Syncopated (1, & of 2, 3)",
                                        new int[] {
                                                        100, 0, 0, 0,
                                                        0, 0, 100, 0,
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0
                                        },
                                        "C1"
                        },
                        {
                                        "Kick: Off-Beats (2 and 4)",
                                        new int[] {
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0
                                        },
                                        "C1"
                        },
                        {
                                        "Kick: Driving 8th Notes",
                                        new int[] {
                                                        100, 0, 100, 0,
                                                        100, 0, 100, 0,
                                                        100, 0, 100, 0,
                                                        100, 0, 100, 0
                                        },
                                        "C1"
                        },

                        // --- SNARE-ONLY PATTERNS ---
                        {
                                        "Snare: Backbeat 2 and 4",
                                        new int[] {
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0
                                        },
                                        "C#1"
                        },
                        {
                                        "Snare: Only on Beat 2",
                                        new int[] {
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0,
                                                        0, 0, 0, 0
                                        },
                                        "C#1"
                        },
                        {
                                        "Snare: Half-Time (3)",
                                        new int[] {
                                                        0, 0, 0, 0,
                                                        0, 0, 0, 0,
                                                        100, 0, 0, 0,
                                                        0, 0, 0, 0
                                        },
                                        "C#1"
                        },
                        {
                                        "Snare: 16th Ghost Notes",
                                        new int[] {
                                                        20, 0, 20, 0,
                                                        100, 0, 20, 0,
                                                        20, 0, 20, 0,
                                                        100, 0, 20, 0
                                        },
                                        "C#1"
                        },
                        {
                                        "Snare: 16th Machine Gun",
                                        new int[] {
                                                        100, 100, 100, 100,
                                                        100, 100, 100, 100,
                                                        100, 100, 100, 100,
                                                        100, 100, 100, 100
                                        },
                                        "C#1"
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
                                        "HiHat: On 3rd Beat",
                                        new int[] {
                                                        0, 0, 80, 0,
                                                        0, 0, 80, 0,
                                                        0, 0, 80, 0,
                                                        0, 0, 80, 0
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

        /**
         * Retrieves a drum pattern by its name.
         *
         * @param name the name of the drum pattern to retrieve
         * @return the drum pattern as an array of integers
         * @throws IllegalArgumentException if no pattern is found with the specified name
         */
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

        void init();
}
