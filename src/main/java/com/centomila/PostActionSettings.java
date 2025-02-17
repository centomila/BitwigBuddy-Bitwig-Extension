package com.centomila;

import com.bitwig.extension.controller.api.Setting;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;

public class PostActionSettings {
        private static String CATEGORY_POST_ACTIONS = "Post Actions";

        public static void init(BeatBuddyExtension extension) {
                DocumentState documentState = extension.getDocumentState();

                // Initialize spacer for "Post Actions"
                Setting spacerPostActions = (Setting) documentState.getStringSetting(
                                "POST ACTIONS----------------------",
                                CATEGORY_POST_ACTIONS, 0,
                                "---------------------------------------------------");

                disableSetting(spacerPostActions); // Spacers are always disabled

                // Setting for toggle hide/show post actions
                extension.postActionsSetting = (Setting) createEnumSetting(
                                "Post Actions",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Show", "Hide" },
                                "Hide");

                // Initialize auto resize loop length setting
                extension.autoResizeLoopLengthSetting = (Setting) createEnumSetting(
                                "Auto resize loop length",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "On");

                extension.zoomToFitAfterGenerateSetting = (Setting) createEnumSetting(
                                "Zoom to fit after generate",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");

                extension.duplicateClipSetting = (Setting) createEnumSetting(
                                "Duplicate Selected Clip",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");



                ((EnumValue) extension.postActionsSetting).addValueObserver(newValue -> {
                        if (newValue.equals("Hide")) {
                                extension.autoResizeLoopLengthSetting.hide();
                                extension.zoomToFitAfterGenerateSetting.hide();
                                extension.duplicateClipSetting.hide();

                        } else {
                                extension.autoResizeLoopLengthSetting.show();
                                extension.zoomToFitAfterGenerateSetting.show();
                                extension.duplicateClipSetting.show();

                        }
                });

        }
}
