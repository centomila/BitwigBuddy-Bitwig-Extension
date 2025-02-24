package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

public class StepSizeSettings {
      private static String CATEGORY_CLIP = "Clip";

      public static void init(BitwigBuddyExtension extension) {
            Setting spacerStepSize = (Setting) createStringSetting(
                        "STEP SIZE/NOTE LENGTH---------",
                        CATEGORY_CLIP, 0,
                        "---------------------------------------------------");

            disableSetting(spacerStepSize); // Spacers are always disabled

            extension.stepSizSetting = (Setting) createEnumSetting(
                        "Step Size",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");
            extension.stepSizSubdivisionSetting = (Setting) createEnumSetting(
                        "Subdivisions",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_CATEGORY_OPTIONS,
                        "Straight");
            extension.noteLengthSetting = (Setting) createEnumSetting(
                        "Note Length",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");

            setupStepSizeObservers(extension);

            // extension.setStepSizSetting(stepSizSetting);
            // extension.setStepSizSubdivisionSetting(stepSizSubdivisionSetting);
            // extension.setNoteLengthSetting(noteLengthSetting);
      }

      private static void setupStepSizeObservers(BitwigBuddyExtension extension) {
            ((EnumValue) extension.stepSizSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) extension.stepSizSetting).set(newValue);
                  ((SettableEnumValue) extension.noteLengthSetting).set(newValue);
            });

            ((EnumValue) extension.noteLengthSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) extension.noteLengthSetting).set(newValue);
            });
      }
}
