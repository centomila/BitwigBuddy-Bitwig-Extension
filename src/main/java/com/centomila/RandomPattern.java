package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import static com.centomila.utils.PopupUtils.*;

import java.util.Arrays;
import java.util.Random;

import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Setting;

public class RandomPattern {
        public static void init(BeatBuddyExtension extension) {

                // Initialize Random Pattern Settings
                extension.randomMinVelocityVariationSetting = (Setting) createNumberSetting(
                                "Min Velocity",
                                "Generate Pattern",
                                1,
                                127,
                                1,
                                "/127 Velocity",
                                1);
                extension.randomMaxVelocityVariationSetting = (Setting) createNumberSetting(
                                "Max Velocity",
                                "Generate Pattern",
                                1,
                                127,
                                1,
                                "/127 Velocity",
                                127);

                extension.randomVelocitySettingType = (Setting) createEnumSetting(
                                "Velocity Shape",
                                "Generate Pattern",
                                new String[] { "Random", "Linear Inc", "Linear Dec", "Arc", "Sine", "Cosine",
                                                "Double Cosine" },

                                "Random");

                extension.randomDensitySetting = (Setting) createNumberSetting(
                                "Density",
                                "Generate Pattern",
                                1,
                                100,
                                1,
                                "%",
                                50);

                extension.randomStepQtySetting = (Setting) createNumberSetting(
                                "Step Quantity",
                                "Generate Pattern",
                                1,
                                128,
                                1,
                                "Steps",
                                16);

                // init observers
                initiObserver(extension);
        }

        // Observers
        public static void initiObserver(BeatBuddyExtension extension) {
                ((SettableRangedValue) extension.randomMinVelocityVariationSetting).addValueObserver(newValue -> {
                        // Force max velocity to be greater than min velocity
                        double scaledNewValue = newValue * 126 + 1;
                        double maxValue = ((SettableRangedValue) extension.randomMaxVelocityVariationSetting).get()
                                        * 126 + 1;
                        if (scaledNewValue > maxValue) {
                                ((SettableRangedValue) extension.randomMaxVelocityVariationSetting).set(newValue);
                        }

                });

                ((SettableRangedValue) extension.randomMaxVelocityVariationSetting).addValueObserver(newValue -> {
                        // Force max velocity to be greater than min velocity
                        double scaledNewValue = newValue * 126 + 1;
                        double minValue = ((SettableRangedValue) extension.randomMinVelocityVariationSetting).get()
                                        * 126 + 1;
                        if (scaledNewValue < minValue) {
                                ((SettableRangedValue) extension.randomMinVelocityVariationSetting).set(newValue);
                        }
                });

                ((SettableRangedValue) extension.randomDensitySetting).addValueObserver(newValue -> {

                });

        }

        public static int[] generateRandomPattern(BeatBuddyExtension extension) {
                Random random = new Random();
                int minVelocity = (int) Math
                                .round(((SettableRangedValue) extension.randomMinVelocityVariationSetting).getRaw());
                int maxVelocity = (int) Math
                                .round(((SettableRangedValue) extension.randomMaxVelocityVariationSetting).getRaw());
                double density = (double) (((SettableRangedValue) extension.randomDensitySetting).getRaw());
                showPopup("Min Velocity: " + minVelocity + " Max Velocity: " + maxVelocity + " Density: " + density);

                int stepsQty = (int) Math.round(((SettableRangedValue) extension.randomStepQtySetting).getRaw());
                showPopup("Steps: " + stepsQty);
                int[] pattern = new int[stepsQty];

                for (int i = 0; i < pattern.length; i++) {
                        pattern[i] = minVelocity + random.nextInt(maxVelocity - minVelocity + 1);
                }

                // Calculate how many steps should be active based on density
                int activeSteps = (int) Math.round((density / 100.0) * pattern.length);

                // Create a boolean array to track which positions will be active
                boolean[] activePositions = new boolean[pattern.length];
                for (int i = 0; i < activeSteps; i++) {
                        int pos;
                        do {
                                pos = random.nextInt(pattern.length);
                        } while (activePositions[pos]);
                        activePositions[pos] = true;
                }

                // Zero out inactive positions
                for (int i = 0; i < pattern.length; i++) {
                        if (!activePositions[i]) {
                                pattern[i] = 0;
                        }
                }

                // Apply velocity type
                String velocityType = ((SettableEnumValue) extension.randomVelocitySettingType).get();

                // Count non-zero steps first
                int activeStepsCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                        if (pattern[i] > 0)
                                activeStepsCount++;
                }

                switch (velocityType) {
                        case "Linear Inc":
                                int stepCount = 0;
                                for (int i = 0; i < pattern.length; i++) {
                                        if (pattern[i] > 0) {
                                                pattern[i] = minVelocity + (int) Math
                                                                .round((maxVelocity - minVelocity) * stepCount
                                                                                / (double) (activeStepsCount - 1));
                                                stepCount++;
                                        }
                                }
                                break;
                        case "Linear Dec":
                                stepCount = 0;
                                for (int i = 0; i < pattern.length; i++) {
                                        if (pattern[i] > 0) {
                                                pattern[i] = maxVelocity - (int) Math
                                                                .round((maxVelocity - minVelocity) * stepCount
                                                                                / (double) (activeStepsCount - 1));
                                                stepCount++;
                                        }
                                }
                                break;
                        case "Arc":
                                stepCount = 0;
                                for (int i = 0; i < pattern.length; i++) {
                                        if (pattern[i] > 0) {
                                                pattern[i] = minVelocity + (int) Math.round((maxVelocity - minVelocity)
                                                                * Math.sin(Math.PI * stepCount
                                                                                / (activeStepsCount - 1)));
                                                stepCount++;
                                        }
                                }
                                break;
                        case "Sine":
                                stepCount = 0;
                                for (int i = 0; i < pattern.length; i++) {
                                        if (pattern[i] > 0) {
                                                double normalizedValue = (Math.sin(2 * Math.PI * stepCount
                                                                / (activeStepsCount - 1)) + 1) * 0.5;
                                                pattern[i] = minVelocity + (int) Math.round((maxVelocity - minVelocity)
                                                                * normalizedValue);
                                                stepCount++;
                                        }
                                }
                                break;
                        case "Cosine":
                                stepCount = 0;
                                for (int i = 0; i < pattern.length; i++) {
                                        if (pattern[i] > 0) {
                                                double normalizedValue = (Math.cos(2 * Math.PI * stepCount
                                                                / (activeStepsCount - 1))) * 0.5 + 0.5;
                                                pattern[i] = Math.max(1, minVelocity + (int) Math.round((maxVelocity
                                                                - minVelocity) * normalizedValue));
                                                stepCount++;
                                        }
                                }
                                break;
                        case "Double Cosine":
                                stepCount = 0;
                                for (int i = 0; i < pattern.length; i++) {
                                        if (pattern[i] > 0) {
                                                double normalizedValue = (Math.cos(4 * Math.PI * stepCount
                                                                / (activeStepsCount - 1))) * 0.5 + 0.5;
                                                pattern[i] = Math.max(1, minVelocity + (int) Math.round((maxVelocity
                                                                - minVelocity) * normalizedValue));
                                                stepCount++;
                                        }
                                }
                                break;
                        default:
                                break;
                }

                // Count how many steps are > 0 and show a popup
                int count = 0;
                for (int i = 0; i < pattern.length; i++) {
                        if (pattern[i] > 0) {
                                count++;
                        }
                }
                showPopup("Random Pattern has filled " + count + " steps out of " + stepsQty + " Density: " + density);

                String patternString = Arrays.toString(pattern).replaceAll("[\\[\\]]", "");
                ((SettableStringValue) extension.presetPatternStringSetting).set(patternString);

                return pattern;
        }
}
