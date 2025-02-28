package com.centomila;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;

public class PostActionSettings {
        // Post actions settings
        static Setting postActionsToggleCategorySetting;

        static Setting autoResizeLoopLengthSetting;
        static Setting zoomToFitAfterGenerateSetting;
        static Setting switchToEditLayoutSetting;
        static Setting duplicateClipSetting;
        static Setting launchClipSetting;
        static Setting[] allSettings;

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
                postActionsToggleCategorySetting = (Setting) createEnumSetting(
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
                switchToEditLayoutSetting = (Setting) createEnumSetting(
                                "Switch to Edit View Layout",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");

                duplicateClipSetting = (Setting) createEnumSetting(
                                "Duplicate Selected Clip",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");

                launchClipSetting = (Setting) createEnumSetting(
                                "Launch Clip",
                                CATEGORY_POST_ACTIONS,
                                new String[] { "Off", "On" },
                                "Off");

                setupPostActionsObserver(extension);
                allSettings = new Setting[] { spacerPostActions, postActionsToggleCategorySetting,
                                autoResizeLoopLengthSetting,
                                zoomToFitAfterGenerateSetting, switchToEditLayoutSetting, duplicateClipSetting,
                                launchClipSetting };
        }

        // Observer for post actions setting
        private static void setupPostActionsObserver(BitwigBuddyExtension extension) {

                ((EnumValue) postActionsToggleCategorySetting).addValueObserver(newValue -> {
                        showPostActionsSettings();
                });
        }

        public static void showPostActionsSettings() {
                String showPostActionToggle = ((EnumValue) postActionsToggleCategorySetting).get();

                // Array with all settings to be hidden
                Setting[] settingsToHideAndShow = {
                                autoResizeLoopLengthSetting,
                                zoomToFitAfterGenerateSetting,
                                switchToEditLayoutSetting,
                                duplicateClipSetting,
                                launchClipSetting
                };

                if (showPostActionToggle.equals("Hide")) {
                        hideSetting(settingsToHideAndShow);

                } else {
                        showSetting(settingsToHideAndShow);
                }
        }
}
