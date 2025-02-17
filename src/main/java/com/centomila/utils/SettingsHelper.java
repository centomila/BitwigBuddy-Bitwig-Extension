package com.centomila.utils;

import com.centomila.BeatBuddyExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.SettableRangedValue;

/**
 * Utility class to manage settings visibility and enabled state.
 */
public class SettingsHelper {
    private static BeatBuddyExtension extension;

    /**
     * Initializes the settings helper.
     *
     * @param extension the extension
     */
    public static void init(BeatBuddyExtension extension) {
        SettingsHelper.extension = extension;
    }

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

    /**
     * Creates a number setting.
     *
     * @param label the setting id
     * @param category the setting name
     * @param defaultValue the default value
     * @param minValue the minimum value
     * @param maxValue the maximum value
     * @param stepResolution the step resolution
     * @param unit the unit
     * @param initialValue the initial value
     * @return the created number setting
     */
    public static SettableRangedValue createNumberSetting(String label, String category, double defaultValue,
            double minValue, double maxValue) {
        DocumentState documentState = extension.getDocumentState();
        return documentState.getNumberSetting(
            label,
            category,
            minValue,
            maxValue,
            1.0,
            "",
            defaultValue
        );
    }
}
