package com.centomila;

import com.bitwig.extension.controller.api.Setting;

import static com.centomila.utils.SettingsHelper.disableSetting;

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
                
                disableSetting( spacerPostActions ); // Spacers are always disabled

                // Setting for toggle hide/show post actions
                Setting postActionsSetting = (Setting) documentState.getEnumSetting(
                                "Post Actions",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Show", "Hide" }, "Hide");

                ((EnumValue) postActionsSetting).addValueObserver(newValue -> {
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

                // Initialize auto resize loop length setting
                Setting autoResizeLoopLengthSetting = (Setting) documentState.getEnumSetting(
                                "Auto resize loop length",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" }, "On");
                extension.setAutoResizeLoopLengthSetting(autoResizeLoopLengthSetting);

                Setting zoomToFitAfterGenerateSetting = (Setting) documentState.getEnumSetting(
                                "Zoom to fit after generate",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" }, "Off");
                extension.setZoomToFitAfterGenerateSetting(zoomToFitAfterGenerateSetting);

                // extension.openInDetailEditorSetting = (Setting) documentState.getEnumSetting(
                //                 "Open in Detail Editor after Generate",
                //                 CATEGORY_POST_ACTIONS,
                //                 new String[] { "Off", "On" }, "On");
                
                extension.duplicateClipSetting = (Setting) documentState.getEnumSetting(
                                "Duplicate Selected Clip",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" }, "Off");
                
        }
}
