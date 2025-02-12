package com.centomila;

import java.util.Random;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

public class DrumPatternGenerator {

    public static void generatePattern(BeatBuddyExtension extension, Clip clip, Setting noteLengthSetting,
            Setting stepSizSubdivisionSetting,
            Setting stepSizSetting, NoteDestinationSettings noteDestSettings, Setting patternSelectorSetting,
            Setting patternTypeSetting, Setting autoReversePatternSetting, Setting autoResizeLoopLengthSetting,
            Setting zoomToFitAfterGenerateSetting) {

        String noteLength = ((EnumValue) noteLengthSetting).get();
        String subdivision = ((EnumValue) stepSizSubdivisionSetting).get();
        String stepSize = ((EnumValue) stepSizSetting).get();
        double duration = Utils.getNoteLengthAsDouble(noteLength, subdivision);

        double patternStepSize = Utils.getNoteLengthAsDouble(stepSize, subdivision);
        clip.setStepSize(patternStepSize);

        int channel = noteDestSettings.getCurrentChannelAsInt();
        int noteDestination = noteDestSettings.getCurrentNoteDestinationAsInt();

        clip.clearStepsAtY(channel, noteDestination);

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
            pattern = DrumPatterns.getPatternByName(selectedPattern);
        }

        if (((EnumValue) autoReversePatternSetting).get().equals("Reverse")) {
            reversePattern(pattern);
        }

        applyPatternToClip(clip, pattern, channel, noteDestination, duration);

        if (((EnumValue) autoResizeLoopLengthSetting).get().equals("On")) {
            double beatLength = patternStepSize * pattern.length;
            ClipUtils.setLoopLength(clip, 0.0, beatLength);
        }

        clip.selectStepContents(channel, noteDestination, false);

        if (((EnumValue) zoomToFitAfterGenerateSetting).get().equals("On")) {
            extension.getApplication().zoomToFit();
        }
    }

    private static void reversePattern(int[] pattern) {
        for (int i = 0; i < pattern.length / 2; i++) {
            int temp = pattern[i];
            pattern[i] = pattern[pattern.length - 1 - i];
            pattern[pattern.length - 1 - i] = temp;
        }
    }

    private static void generateRandomPattern(int[] pattern) {
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            pattern[i] = random.nextInt(128);
            if (random.nextInt(4) == 0) {
                pattern[i] = 0;
            }
        }
    }

    private static void applyPatternToClip(Clip clip, int[] pattern, int channel, int noteDestination,
            double duration) {
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] > 0) {
                clip.setStep(channel, i, noteDestination, pattern[i], duration);
            }
        }
    }
}
