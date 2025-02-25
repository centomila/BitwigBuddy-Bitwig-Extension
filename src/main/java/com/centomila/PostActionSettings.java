package com.centomila;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;

public class PostActionSettings {
        // Post actions settings
        static Setting autoResizeLoopLengthSetting;
        static Setting zoomToFitAfterGenerateSetting;
        static Setting postActionsSetting;

        static Setting duplicateClipSetting;
        static Setting openInDetailEditorSetting;
        private static String CATEGORY_POST_ACTIONS = "Post Actions";

        public static void init(BitwigBuddyExtension extension) {
                // Initialize spacer for "Post Actions"
                Setting spacerPostActions = (Setting) createStringSetting(
                                "POST ACTIONS----------------------",
                                CATEGORY_POST_ACTIONS,
                                9999,
                                "---------------------------------------------------");

                disableSetting(spacerPostActions); // Spacers are always disabled

                // Setting for toggle hide/show post actions
                postActionsSetting = (Setting) createEnumSetting(
                                "Post Actions",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Show", "Hide" },
                                "Hide");

                // Initialize auto resize loop length setting
                autoResizeLoopLengthSetting = (Setting) createEnumSetting(
                                "Auto resize loop length",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "On");

                zoomToFitAfterGenerateSetting = (Setting) createEnumSetting(
                                "Zoom to fit after generate",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");

                duplicateClipSetting = (Setting) createEnumSetting(
                                "Duplicate Selected Clip",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");

                setupPostActionsObserver(extension);
        }

        // Observer for post actions setting
        private static void setupPostActionsObserver(BitwigBuddyExtension extension) {

                ((EnumValue) postActionsSetting).addValueObserver(newValue -> {
                        // Array with all settings to be hidden
                        Setting[] settingsToHideAndShow = {
                                        autoResizeLoopLengthSetting,
                                        zoomToFitAfterGenerateSetting,
                                        duplicateClipSetting };

                        if (newValue.equals("Hide")) {
                                hideSetting(settingsToHideAndShow);

                        } else {
                                showSetting(settingsToHideAndShow);
                        }
                });
        }
}
