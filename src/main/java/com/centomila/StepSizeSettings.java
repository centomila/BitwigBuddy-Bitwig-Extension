package com.centomila;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

public class StepSizeSettings {
      private static String CATEGORY_CLIP = "Clip";

      public static void init(BeatBuddyExtension extension) {
            DocumentState documentState = extension.getDocumentState();

            Setting spacerStepSize = ((Setting) documentState.getStringSetting(
                        "STEP SIZE/NOTE LENGTH---------",
                        CATEGORY_CLIP, 0,
                        "---------------------------------------------------"));

            disableSetting(spacerStepSize); // Spacers are always disabled

            Setting stepSizSetting = (Setting) documentState.getEnumSetting("Step Size",
                        CATEGORY_CLIP, Utils.STEPSIZE_OPTIONS, "1/16");
            Setting stepSizSubdivisionSetting = (Setting) documentState.getEnumSetting("Subdivisions",
                        CATEGORY_CLIP, Utils.STEPSIZE_CATEGORY_OPTIONS, "Straight");
            Setting noteLengthSetting = (Setting) documentState.getEnumSetting("Note Length",
                        CATEGORY_CLIP, Utils.STEPSIZE_OPTIONS,
                        "1/16");

            ((EnumValue) stepSizSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) stepSizSetting).set(newValue);
                  ((SettableEnumValue) noteLengthSetting).set(newValue);
            });

            ((EnumValue) noteLengthSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) noteLengthSetting).set(newValue);
            });

            extension.setStepSizSetting(stepSizSetting);
            extension.setStepSizSubdivisionSetting(stepSizSubdivisionSetting);
            extension.setNoteLengthSetting(noteLengthSetting);
      }
}
