package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import static com.centomila.utils.VelocityShape.*;
import static com.centomila.utils.PopupUtils.*;

import java.util.Arrays;
import java.util.Random;

import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Setting;
import com.centomila.utils.VelocityShape;

public class ProgramPattern {
        public static Setting programMinVelocityVariationSetting;
        public static Setting programMaxVelocityVariationSetting;
        public static Setting programDensitySetting;
        public static Setting programVelocitySettingShape;
        public static Setting programStepQtySetting;
        public static Setting programSkipStep;
        public static Setting[] allSettings;

        public static void init(BitwigBuddyExtension extension) {

                // Initialize Random Pattern Settings
                programMinVelocityVariationSetting = (Setting) createNumberSetting(
                                "Min Velocity",
                                "Generate Pattern",
                                1,
                                127,
                                1,
                                "",
                                1);
                programMaxVelocityVariationSetting = (Setting) createNumberSetting(
                                "Max Velocity",
                                "Generate Pattern",
                                1,
                                127,
                                1,
                                "",
                                127);

                programVelocitySettingShape = (Setting) createEnumSetting(
                                "Velocity Shape",
                                "Generate Pattern",
                                VelocityShape.velocityShapes,
                                "Random");

                programDensitySetting = (Setting) createNumberSetting(
                                "Density",
                                "Generate Pattern",
                                1,
                                100,
                                1,
                                "%",
                                50);

                programStepQtySetting = (Setting) createNumberSetting(
                                "Step Quantity",
                                "Generate Pattern",
                                1,
                                128,
                                1,
                                "Steps",
                                16);

                programSkipStep = (Setting) createNumberSetting(
                                "Skip a Step Every",
                                "Generate Pattern",
                                0,
                                128,
                                1,
                                "",
                                0);

                // init observers
                initiObserver(extension);
                allSettings = new Setting[] {
                                programMinVelocityVariationSetting,
                                programMaxVelocityVariationSetting,
                                programDensitySetting,
                                programVelocitySettingShape,
                                programStepQtySetting,
                                programSkipStep
                };
        }

        // Observers
        public static void initiObserver(BitwigBuddyExtension extension) {
                ((SettableRangedValue) programMinVelocityVariationSetting).addValueObserver(newValue -> {
                        // Force max velocity to be greater than min velocity
                        double scaledNewValue = newValue * 126 + 1;
                        double maxValue = ((SettableRangedValue) programMaxVelocityVariationSetting).get()
                                        * 126 + 1;
                        if (scaledNewValue > maxValue) {
                                ((SettableRangedValue) programMaxVelocityVariationSetting).set(newValue);
                        }

                });

                ((SettableRangedValue) programMaxVelocityVariationSetting).addValueObserver(newValue -> {
                        // Force max velocity to be greater than min velocity
                        double scaledNewValue = newValue * 126 + 1;
                        double minValue = ((SettableRangedValue) programMinVelocityVariationSetting).get()
                                        * 126 + 1;
                        if (scaledNewValue < minValue) {
                                ((SettableRangedValue) programMinVelocityVariationSetting).set(newValue);
                        }
                });

                ((SettableRangedValue) programDensitySetting).addValueObserver(newValue -> {

                });

        }

        public static int[] generateRandomPattern(BitwigBuddyExtension extension) {
                Random random = new Random();
                int minVelocity = (int) Math
                                .round(((SettableRangedValue) programMinVelocityVariationSetting).getRaw());
                int maxVelocity = (int) Math
                                .round(((SettableRangedValue) programMaxVelocityVariationSetting).getRaw());
                double density = (double) (((SettableRangedValue) programDensitySetting).getRaw());
                showPopup("Min Velocity: " + minVelocity + " Max Velocity: " + maxVelocity + " Density: " + density);

                int stepsQty = (int) Math.round(((SettableRangedValue) programStepQtySetting).getRaw());

                int[] pattern = new int[stepsQty];

                for (int i = 0; i < pattern.length; i++) {
                        pattern[i] = 127;
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
                String velocityType = ((SettableEnumValue) programVelocitySettingShape).get();

                int[] velocityPattern = applyVelocityShape(pattern, velocityType, minVelocity, maxVelocity);

                pattern = velocityPattern;

                // Apply skip step
                int skipStepValue = (int) Math.round(((SettableRangedValue) programSkipStep).getRaw());
                if (skipStepValue > 0) {
                        for (int i = 0; i < pattern.length; i++) {
                                // if 1 skip all the odd steps
                                if (skipStepValue == 1) {
                                        if ((i + 2) % 2 == 0) {
                                                pattern[i] = 0;
                                        }

                                } else if ((i + 1) % (skipStepValue) == 0) { // other case
                                        pattern[i] = 0;
                                }
                        }
                }

                // Count how many steps are > 0 and show a popup
                int count = 0;
                for (int i = 0; i < pattern.length; i++) {
                        if (pattern[i] > 0) {
                                count++;
                        }
                }
                showPopup("Program Pattern has filled " + count + " steps out of " + stepsQty + " Density: " + density);

                String patternString = Arrays.toString(pattern).replaceAll("[\\[\\]]", "");
                ((SettableStringValue) PatternSettings.presetPatternStringSetting).set(patternString);

                return pattern;
        }

        public static void showAndEnableAllSettings() {
                showAndEnableSetting(allSettings);
        }

        public static void hideAndDisableAllSettings() {
                hideAndDisableSetting(allSettings);
        }

        public static int getMinVelocityAsInt() {
                return (int) Math.round(((SettableRangedValue) programMinVelocityVariationSetting).getRaw());
        }

        public static int getMaxVelocityAsInt() {
                return (int) Math.round(((SettableRangedValue) programMaxVelocityVariationSetting).getRaw());
        }

}
