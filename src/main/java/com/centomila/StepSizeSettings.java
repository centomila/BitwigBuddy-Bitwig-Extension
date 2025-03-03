package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

public class StepSizeSettings {
      // Step Size / Note Length settings
      public static Setting noteLengthSetting; // How long each note should be
      public static Setting stepSizSetting;
      public static Setting stepSizSubdivisionSetting;
      public static Setting[] allSettings;
      private static String CATEGORY_CLIP = "5 Clip";

      public static void init(BitwigBuddyExtension extension) {
            Setting spacerStepSize = (Setting) createStringSetting(
                        titleWithLine("STEP SIZE - NOTE LENGTH"),
                        CATEGORY_CLIP, 0,
                        "---------------------------------------------------");

            disableSetting(spacerStepSize); // Spacers are always disabled

            stepSizSetting = (Setting) createEnumSetting(
                        "Step Size",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");
            stepSizSubdivisionSetting = (Setting) createEnumSetting(
                        "Subdivisions",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_CATEGORY_OPTIONS,
                        "Straight");
            noteLengthSetting = (Setting) createEnumSetting(
                        "Note Length",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");

            setupStepSizeObservers(extension);
            allSettings = new Setting[] { spacerStepSize, stepSizSetting, stepSizSubdivisionSetting, noteLengthSetting };

      }

      private static void setupStepSizeObservers(BitwigBuddyExtension extension) {
            ((EnumValue) stepSizSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) stepSizSetting).set(newValue);
                  ((SettableEnumValue) noteLengthSetting).set(newValue);
            });

            ((EnumValue) noteLengthSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) noteLengthSetting).set(newValue);
            });
      }


}
