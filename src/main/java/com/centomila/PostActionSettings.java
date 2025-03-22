package com.centomila;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

public class PostActionSettings {
        // Post actions settings
        static Setting postActionsToggleCategorySetting;

        static Setting autoResizeLoopLengthSetting;
        static Setting zoomToFitAfterGenerateSetting;
        static Setting switchToEditLayoutSetting;
        static Setting duplicateClipSetting;
        static Setting launchClipSetting;
        static Setting[] allSettings;

        private static String CATEGORY_POST_ACTIONS = "9 Post Actions";

        public static void init(BitwigBuddyExtension extension) {
                // Initialize spacer for "Post Actions"
                Setting spacerPostActions = (Setting) createStringSetting(
                                titleWithLine("POST ACTIONS ----------------------------"),
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
                        toggleVisibilityPostActionsSettings();
                });
        }

        public static void toggleVisibilityPostActionsSettings() {
                String showPostActionToggle = getPostActionsToggleCategorySetting();

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

        // getter for toggle visibility post actions setting
        public static String getPostActionsToggleCategorySetting() {
                return ((EnumValue) postActionsToggleCategorySetting).get();
        }

        public static void showPostActionAllSettings() {
                showSetting(allSettings);
        }

        public static void hideAllSettings() {
                hideSetting(allSettings);
        }

        // Setters
        public static void setAutoResizeLoopLengthSetting(String value) {
                ((SettableEnumValue) autoResizeLoopLengthSetting).set(value);
        }

        public static void setZoomToFitAfterGenerateSetting(String value) {
                ((SettableEnumValue) zoomToFitAfterGenerateSetting).set(value);
        }

        public static void setSwitchToEditLayoutSetting(String value) {
                ((SettableEnumValue) switchToEditLayoutSetting).set(value);
        }

        public static void setDuplicateClipSetting(String value) {
                ((SettableEnumValue) duplicateClipSetting).set(value);
        }

        public static void setLaunchClipSetting(String value) {
                ((SettableEnumValue) launchClipSetting).set(value);
        }

}
