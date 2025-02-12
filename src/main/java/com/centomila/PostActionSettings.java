package com.centomila;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;

public class PostActionSettings {
        public static void init(BeatBuddyExtension extension) {
                DocumentState documentState = extension.getDocumentState();

                // Initialize spacer3 for "Post Actions"
                Setting spacer3 = (Setting) documentState.getStringSetting("----", "Post Actions", 0,
                                "---------------------------------------------------");
                spacer3.disable();
                extension.setSpacer3(spacer3);

                // Setting for toggle hide/show post actions
                Setting postActionsSetting = (Setting) documentState.getEnumSetting("Post Actions", "Post Actions",
                                new String[] { "Show", "Hide" }, "Show");

                ((EnumValue) postActionsSetting).addValueObserver(newValue -> {
                        if (newValue.equals("Hide")) {
                                extension.getAutoResizeLoopLengthSetting().hide();
                                extension.getZoomToFitAfterGenerateSetting().hide();

                        } else {
                                extension.getAutoResizeLoopLengthSetting().show();
                                extension.getZoomToFitAfterGenerateSetting().show();

                        }
                });

                // Initialize auto resize loop length setting
                Setting autoResizeLoopLengthSetting = (Setting) documentState.getEnumSetting("Auto resize loop length",
                                "Post Actions",
                                new String[] { "Off", "On" }, "On");
                extension.setAutoResizeLoopLengthSetting(autoResizeLoopLengthSetting);

                Setting zoomToFitAfterGenerateSetting = (Setting) documentState.getEnumSetting(
                                "Zoom to fit after generate",
                                "Post Actions", new String[] { "Off", "On" }, "On");
                extension.setZoomToFitAfterGenerateSetting(zoomToFitAfterGenerateSetting);
        }
}
