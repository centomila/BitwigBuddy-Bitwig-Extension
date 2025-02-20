package com.centomila;

// import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;
import static com.centomila.utils.PopupUtils.*;

import com.bitwig.extension.controller.api.SettableIntegerValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
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

                extension.randomDensitySetting = (Setting) createNumberSetting(
                                "Density",
                                "Generate Pattern",
                                1,
                                100,
                                1,
                                "%",
                                50);

                // init observers
                initiObserver(extension);
        }

        // Observers
        public static void initiObserver(BeatBuddyExtension extension) {
                ((SettableRangedValue) extension.randomMinVelocityVariationSetting).addValueObserver(newValue -> {
                        // convert from double with range 0.0-1.0 to int in the range 1-127
                        // int intValue = (int) Math.round(newValue * 126) + 1;
                        // showPopup("Min Velocity: " + intValue);
                        // if newValue is > maxVelocity, set maxVelocity to newValue
                        double scaledNewValue = newValue * 126 + 1;
                        double maxValue = ((SettableRangedValue) extension.randomMaxVelocityVariationSetting).get() * 126 + 1;
                        if (scaledNewValue > maxValue) {
                                ((SettableRangedValue) extension.randomMaxVelocityVariationSetting).set(newValue);
                        }

                });

                ((SettableRangedValue) extension.randomMaxVelocityVariationSetting).addValueObserver(newValue -> {
                        // convert from double with range 0.0-1.0 to int in the range 1-127
                        // int intValue = (int) Math.round(newValue * 126) + 1;
                        // showPopup("Max Velocity: " + intValue);
                        // if newValue is < minVelocity, set minVelocity to newValue
                        double scaledNewValue = newValue * 126 + 1;
                        double minValue = ((SettableRangedValue) extension.randomMinVelocityVariationSetting).get() * 126 + 1;
                        if (scaledNewValue < minValue) {
                                ((SettableRangedValue) extension.randomMinVelocityVariationSetting).set(newValue);
                        }
                });

                ((SettableRangedValue) extension.randomDensitySetting).addValueObserver(newValue -> {

                });

        }
}
