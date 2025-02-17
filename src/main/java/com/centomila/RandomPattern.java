package com.centomila;

// import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;
import com.bitwig.extension.controller.api.Setting;

public class RandomPattern {
    public static void init(BeatBuddyExtension extension) {
        
        // Initialize Random Pattern Settings
        extension.randomVelocityVariationSetting = (Setting)createNumberSetting(
            "randomVelocityVariation",
            "Velocity Variation",
            0,
            0,
            127
        );
        
        extension.randomDensitySetting = (Setting)createNumberSetting(
            "randomDensity",
            "Density",
            0,
            0,
            127
        );
    }
}
