package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import static com.centomila.utils.PopupUtils.*;
import static com.centomila.VelocityShape.*;

import java.util.Arrays;
import java.util.Random;

import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Setting;

public class RandomPattern {
        public static Setting randomMinVelocityVariationSetting;
        public static Setting randomMaxVelocityVariationSetting;
        public static Setting randomDensitySetting;
        public static Setting randomVelocitySettingShape;
        public static Setting randomStepQtySetting;
        public static Setting[] allSettings;

        public static void init(BitwigBuddyExtension extension) {

                // Initialize Random Pattern Settings
                randomMinVelocityVariationSetting = (Setting) createNumberSetting(
                                "Min Velocity",
                                "Generate Pattern",
                                1,
                                127,
                                1,
                                "",
                                1);
                randomMaxVelocityVariationSetting = (Setting) createNumberSetting(
                                "Max Velocity",
                                "Generate Pattern",
                                1,
                                127,
                                1,
                                "",
                                127);

                randomVelocitySettingShape = (Setting) createEnumSetting(
                                "Velocity Shape",
                                "Generate Pattern",
                                VelocityShape.velocityShapes,
                                "Random");

                randomDensitySetting = (Setting) createNumberSetting(
                                "Density",
                                "Generate Pattern",
                                1,
                                100,
                                1,
                                "%",
                                50);

                randomStepQtySetting = (Setting) createNumberSetting(
                                "Step Quantity",
                                "Generate Pattern",
                                1,
                                128,
                                1,
                                "Steps",
                                16);

                // init observers
                initiObserver(extension);
                allSettings = new Setting[] { randomMinVelocityVariationSetting, randomMaxVelocityVariationSetting,
                                randomDensitySetting, randomVelocitySettingShape, randomStepQtySetting };
        }

        // Observers
        public static void initiObserver(BitwigBuddyExtension extension) {
                ((SettableRangedValue) randomMinVelocityVariationSetting).addValueObserver(newValue -> {
                        // Force max velocity to be greater than min velocity
                        double scaledNewValue = newValue * 126 + 1;
                        double maxValue = ((SettableRangedValue) randomMaxVelocityVariationSetting).get()
                                        * 126 + 1;
                        if (scaledNewValue > maxValue) {
                                ((SettableRangedValue) randomMaxVelocityVariationSetting).set(newValue);
                        }

                });

                ((SettableRangedValue) randomMaxVelocityVariationSetting).addValueObserver(newValue -> {
                        // Force max velocity to be greater than min velocity
                        double scaledNewValue = newValue * 126 + 1;
                        double minValue = ((SettableRangedValue) randomMinVelocityVariationSetting).get()
                                        * 126 + 1;
                        if (scaledNewValue < minValue) {
                                ((SettableRangedValue) randomMinVelocityVariationSetting).set(newValue);
                        }
                });

                ((SettableRangedValue) randomDensitySetting).addValueObserver(newValue -> {

                });

        }

        public static int[] generateRandomPattern(BitwigBuddyExtension extension) {
                Random random = new Random();
                int minVelocity = (int) Math
                                .round(((SettableRangedValue) randomMinVelocityVariationSetting).getRaw());
                int maxVelocity = (int) Math
                                .round(((SettableRangedValue) randomMaxVelocityVariationSetting).getRaw());
                double density = (double) (((SettableRangedValue) randomDensitySetting).getRaw());
                showPopup("Min Velocity: " + minVelocity + " Max Velocity: " + maxVelocity + " Density: " + density);

                int stepsQty = (int) Math.round(((SettableRangedValue) randomStepQtySetting).getRaw());

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
                String velocityType = ((SettableEnumValue) randomVelocitySettingShape).get();

                applyVelocityShape(pattern, velocityType, minVelocity, maxVelocity);

                // Count how many steps are > 0 and show a popup
                int count = 0;
                for (int i = 0; i < pattern.length; i++) {
                        if (pattern[i] > 0) {
                                count++;
                        }
                }
                showPopup("Random Pattern has filled " + count + " steps out of " + stepsQty + " Density: " + density);

                String patternString = Arrays.toString(pattern).replaceAll("[\\[\\]]", "");
                ((SettableStringValue) PatternSettings.presetPatternStringSetting).set(patternString);

                return pattern;
        }

}
