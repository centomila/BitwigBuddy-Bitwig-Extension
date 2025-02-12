package com.centomila;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.DocumentState;

public class PostActionInitializer {
    public static void initPostActionSetting(BeatBuddyExtension extension) {
        DocumentState documentState = extension.getDocumentState();

        // Initialize spacer3 for "Post Actions"
        Setting spacer3 = (Setting) documentState.getStringSetting("----", "Post Actions", 0,
                "---------------------------------------------------");
        spacer3.disable();
        extension.setSpacer3(spacer3);

        // Initialize auto resize loop length setting
        Setting autoResizeLoopLengthSetting = (Setting) documentState.getEnumSetting("Auto resize loop length",
                "Post Actions",
                new String[] { "Off", "On" }, "On");
        extension.setAutoResizeLoopLengthSetting(autoResizeLoopLengthSetting);

        Setting zoomToFitAfterGenerateSetting = (Setting) documentState.getEnumSetting("Zoom to fit after generate",
                "Post Actions", new String[] { "Off", "On" }, "On");
        extension.setZoomToFitAfterGenerateSetting(zoomToFitAfterGenerateSetting);
    }
}
