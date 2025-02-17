package com.centomila.utils;

import com.centomila.BeatBuddyExtension;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

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
     * Creates a numeric setting that can be displayed as a number field in Bitwig
     * Studio.
     * 
     * @param label          The name of the setting, must not be null
     * @param category       The name of the category, must not be null
     * @param defaultValue   The initial numeric value of the setting
     * @param minValue       The minimum value that the user is allowed to enter
     * @param maxValue       The maximum value that the user is allowed to enter
     * @param stepResolution The step resolution used for the number field
     * @param unit           The string that should be used to display the unit of
     *                       the number
     * @return A SettableRangedValue object that encapsulates the requested numeric
     *         setting
     * @since API version 1
     */
    public static SettableRangedValue createNumberSetting(String label, String category, double defaultValue,
            double minValue, double maxValue, double stepResolution, String unit) {
        DocumentState documentState = extension.getDocumentState();
        return documentState.getNumberSetting(
                label,
                category,
                minValue,
                maxValue,
                stepResolution,
                unit,
                defaultValue);
    }

    /**
     * Creates an enum setting.
     *
     * @param label        the setting id
     * @param category     the setting name
     * @param options      the options as an array of strings
     * @param initialValue the initial value
     * @return the created enum setting
     */
    public static SettableEnumValue createEnumSetting(String label, String category, String[] options,
            String initialValue) {
        DocumentState documentState = extension.getDocumentState();
        return documentState.getEnumSetting(
                label,
                category,
                options,
                initialValue);
    }
}
