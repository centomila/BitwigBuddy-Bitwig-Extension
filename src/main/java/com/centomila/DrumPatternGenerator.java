package com.centomila;

import java.util.Random;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

public class DrumPatternGenerator {
    private final GlobalPreferences preferences;

    public DrumPatternGenerator(GlobalPreferences preferences, ControllerHost host) {
        this.preferences = preferences;
    }

    public static void generatePattern(BeatBuddyExtension extension,
                                       Clip clip,
                                       Setting noteLengthSetting,
                                       Setting stepSizSubdivisionSetting,
                                       Setting stepSizSetting,
                                       NoteDestinationSettings noteDestSettings,
                                       Setting patternSelectorSetting,
                                       Setting patternTypeSetting,
                                       Setting autoReversePatternSetting,
                                       Setting autoResizeLoopLengthSetting,
                                       Setting zoomToFitAfterGenerateSetting) {

        // Retrieve note length and subdivision settings
        String noteLength = ((EnumValue) noteLengthSetting).get();
        String subdivision = ((EnumValue) stepSizSubdivisionSetting).get();
        double duration = Utils.getNoteLengthAsDouble(noteLength, subdivision);

        // Retrieve and set step size based on note settings
        String stepSize = ((EnumValue) stepSizSetting).get();
        double patternStepSize = Utils.getNoteLengthAsDouble(stepSize, subdivision);
        clip.setStepSize(patternStepSize);

        // Get channel and note destination values
        int channel = noteDestSettings.getCurrentChannelAsInt();
        int noteDestination = noteDestSettings.getCurrentNoteDestinationAsInt();
        clip.clearStepsAtY(channel, noteDestination);

        // Determine the type of pattern to generate
        int[] pattern;
        String patternType = ((EnumValue) patternTypeSetting).get();
        if (patternType.equals("Random")) {
            pattern = new int[16];
            generateRandomPattern(pattern);
        } else if (patternType.equals("Custom")) {
            // TODO: Implement custom pattern generation
            pattern = new int[16];
        } else {
            String selectedPattern = ((EnumValue) patternSelectorSetting).get();
            pattern = DefaultPatterns.getPatternByName(selectedPattern);
        }

        // Optionally reverse the pattern if required
        if (((EnumValue) autoReversePatternSetting).get().equals("Reverse")) {
            reversePattern(pattern);
        }

        // Apply pattern to the clip
        applyPatternToClip(clip, pattern, channel, noteDestination, duration);

        // Resize clip loop length if the option is enabled
        if (((EnumValue) autoResizeLoopLengthSetting).get().equals("On")) {
            double beatLength = patternStepSize * pattern.length;
            ClipUtils.setLoopLength(clip, 0.0, beatLength);
        }

        // Cleanup: deselect steps and zoom to fit if enabled in settings   
        clip.selectStepContents(channel, noteDestination, false);
        if (((EnumValue) zoomToFitAfterGenerateSetting).get().equals("On")) {
            extension.getApplication().zoomToFit();
        }
    }

    /**
     * Reverses the given pattern array in-place.
     *
     * @param pattern the pattern array to reverse.
     */
    private static void reversePattern(int[] pattern) {
        for (int i = 0; i < pattern.length / 2; i++) {
            int temp = pattern[i];
            pattern[i] = pattern[pattern.length - 1 - i];
            pattern[pattern.length - 1 - i] = temp;
        }
    }

    /**
     * Generates a randomized pattern with 16 steps.
     * Each step is assigned a random value between 0 and 127.
     * Occasionally, a step is set to 0.
     *
     * @param pattern the pattern array to populate.
     */
    private static void generateRandomPattern(int[] pattern) {
        Random random = new Random();
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = random.nextInt(128);
            if (random.nextInt(4) == 0) {
                pattern[i] = 0;
            }
        }
    }

    /**
     * Applies the provided pattern to the clip.
     * Only non-zero pattern values are used to set steps.
     *
     * @param clip         the clip on which to apply the pattern.
     * @param pattern      the pattern array.
     * @param channel      the channel to use.
     * @param noteDestination the destination note value.
     * @param duration     the duration of each step.
     */
    private static void applyPatternToClip(Clip clip,
                                           int[] pattern,
                                           int channel,
                                           int noteDestination,
                                           double duration) {
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] > 0) {
                clip.setStep(channel, i, noteDestination, pattern[i], duration);
            }
        }
    }

    /**
     * Returns the pattern sequence for a preset with the given name.
     * 
     * @param patternName The name of the pattern to find
     * @return The pattern sequence as an integer array, or null if not found
     */
    public int[] getCustomPatternByName(String patternName) {
        CustomPresetsHandler.CustomPreset[] presets = preferences.getCustomPresets();
        for (CustomPresetsHandler.CustomPreset preset : presets) {
            if (preset.getName().equals(patternName)) {
                return preset.getPattern();
            }
        }
        
        return null;
    }
}
