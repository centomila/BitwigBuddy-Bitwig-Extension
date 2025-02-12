package com.centomila;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

public class StepSizeSettings {
   public static void initStepSizeSetting(BeatBuddyExtension extension) {
      DocumentState documentState = extension.getDocumentState();
      Setting stepSizSetting = (Setting) documentState.getEnumSetting("Step Size", "Clip", Utils.STEPSIZE_OPTIONS, "1/16");
      Setting stepSizSubdivisionSetting = (Setting) documentState.getEnumSetting("Subdivisions", "Clip", Utils.STEPSIZE_CATEGORY_OPTIONS, "Straight");
      Setting noteLengthSetting = (Setting) documentState.getEnumSetting("Note Length", "Clip", Utils.STEPSIZE_OPTIONS,
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
