package com.centomila;

// import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;
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
                                127,
                                1,
                                127,
                                "/127 Velocity",
                                1);

                extension.randomDensitySetting = (Setting) createNumberSetting(
                                "Density",
                                "Generate Pattern",
                                0.1,
                                100,
                                0.1,
                                "%",
                                50);
        }
}
