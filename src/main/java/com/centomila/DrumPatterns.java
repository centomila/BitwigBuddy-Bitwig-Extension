package com.centomila;

public interface DrumPatterns {

    void init();

    // Create an array of patterns with pattern name and list of steps
    // { 127, 0, 0, 0, 100, 0, 0, 0, 127, 0, 0, 0, 100, 0, 0, 0 };

    final Object[][] patterns = {
            {"Four on the Floor", new int[] {
                    100, 0, 0, 0,
                    100, 0, 0, 0,
                    100, 0, 0, 0,
                    100, 0, 0, 0
                }},
            
            {"16th Notes", new int[] {
                    100, 100, 100, 100,
                    100, 100, 100, 100,
                    100, 100, 100, 100,
                    100, 100, 100, 100
                }},
            {"8th Notes", new int[] {
                    100, 0, 100, 0,
                    100, 0, 100, 0,
                    100, 0, 100, 0,
                    100, 0, 100, 0
                }},
            {"2 and 4", new int[] {
                    0, 0, 0, 0,
                    100, 0, 0, 0,
                    0, 0, 0, 0,
                    100, 0, 0, 0
                }}
    };
}
