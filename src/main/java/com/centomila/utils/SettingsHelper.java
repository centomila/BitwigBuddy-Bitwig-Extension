package com.centomila.utils;

import com.bitwig.extension.controller.api.Setting;

/**
 * Utility class to manage settings visibility and enabled state.
 */
public class SettingsHelper {

    /**
     * Hides the specified settings.
     *
     * @param settings one or more settings to be hidden
     */
    public static void hideSetting(Setting... settings) {
        for (Setting setting : settings) {
            setting.hide();
        }
    }

    /**
     * Shows the specified settings.
     *
     * @param setting one or more settings to be shown
     */
    public static void showSetting(Setting... setting) {
        for (Setting s : setting) {
            s.show();
        }
    }

    /**
     * Disables the specified settings.
     *
     * @param setting one or more settings to be disabled
     */
    public static void disableSetting(Setting... setting) {
        for (Setting s : setting) {
            s.disable();
        }
    }

    /**
     * Enables the specified settings.
     *
     * @param setting one or more settings to be enabled
     */
    public static void enableSetting(Setting... setting) {
        for (Setting s : setting) {
            s.enable();
        }
    }

    /**
     * Hides and disables the specified settings.
     * 
     * Note: The method name contains a typo ("hideAndDisalbeSetting").
     * It hides and then disables the provided settings.
     *
     * @param settings one or more settings to be hidden and disabled
     */
    public static void hideAndDisalbeSetting(Setting... settings) {
        for (Setting setting : settings) {
            setting.hide();
            setting.disable();
        }
    }

    /**
     * Shows and enables the specified settings.
     *
     * @param setting one or more settings to be shown and enabled
     */
    public static void showAndEnableSetting(Setting... setting) {
        for (Setting s : setting) {
            s.show();
            s.enable();
        }
    }
}
