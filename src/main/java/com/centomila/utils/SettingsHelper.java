package com.centomila.utils;

import com.centomila.BeatBuddyExtension;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;

/**
 * Utility class to manage settings visibility and enabled state.
 */
public class SettingsHelper {
    private static DocumentState documentState;

    /**
     * Initializes the settings helper.
     *
     * @param extension the extension
     */
    public static void init(BeatBuddyExtension extension) {
        documentState = extension.getDocumentState();
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
    public static void hideAndDisableSetting(Setting... settings) {
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
     * @param minValue       The minimum value that the user is allowed to enter
     * @param maxValue       The maximum value that the user is allowed to enter
     * @param stepResolution The step resolution used for the number field
     * @param unit           The string that should be used to display the unit of
     * @param defaultValue   The initial numeric value of the setting
     *                       the number
     * @return A SettableRangedValue object that encapsulates the requested numeric
     *         setting
     * @since API version 1
     */
    public static SettableRangedValue createNumberSetting(
            String label,
            String category,
            double minValue,
            double maxValue,
            double stepResolution,
            String unit,
            double defaultValue) {

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

        return documentState.getEnumSetting(
                label,
                category,
                options,
                initialValue);
    }

    /**
     * Creates a string setting.
     *
     * @param label       the setting id
     * @param category    the setting name
     * @param numChars    the number of characters
     * @param initialText the initial text
     * @return the created string setting
     */
    public static SettableStringValue createStringSetting(String label, String category, int numChars,
            String initialText) {

        return documentState.getStringSetting(
                label,
                category,
                numChars,
                initialText);
    }

    /**
     * Creates a signal setting (A button that sends a signal when pressed).
     *
     * @param label  the setting id
     * @param category the setting name
     * @param action the action string as displayed on the related Bitwig Studio button
     * @return the created signal setting
     */
    public static Signal createSignalSetting(String label, String category, String action) {
        return documentState.getSignalSetting(label, category, action);
    }
}
