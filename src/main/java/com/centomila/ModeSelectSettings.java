package com.centomila;

import static com.centomila.utils.SettingsHelper.disableSetting;
import static com.centomila.utils.SettingsHelper.enableSetting;
import static com.centomila.utils.SettingsHelper.createEnumSetting;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.utils.PopupUtils;

public class ModeSelectSettings {

    static Setting modeSelectSetting;
    static Setting toggleLauncherArrangerSetting;
    
        

    public static void init(BitwigBuddyExtension extension) {
        
        initToggleModesSettings();
        initiObservers();
    }

    /**
     * Initializes the clip destination toggle.
     * Allows switching between launcher and arranger clip modes,
     * determining where patterns will be generated.
     */
    private static void initToggleModesSettings() {
        // Mode select setting
        final String[] MODE_SELECT_OPTIONS = new String[] { "Generate", "Edit" };

        modeSelectSetting = (Setting) createEnumSetting("Mode Select", "Z",
                MODE_SELECT_OPTIONS,
                MODE_SELECT_OPTIONS[0]);

        // Launcher/Arranger toggle
        final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };

        toggleLauncherArrangerSetting = (Setting) createEnumSetting("Destination Launcher/Arranger", "Z",
                TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
                TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);

    }

    private static void initiObservers() {
        ((EnumValue) modeSelectSetting).addValueObserver(newValue -> {
            PopupUtils.showPopup("Mode: " + newValue);
        });

        ((EnumValue) toggleLauncherArrangerSetting).addValueObserver(newValue -> {
            PopupUtils.showPopup("Destination: " + newValue);
            if (newValue.equals("Arranger")) {
                disableSetting(PostActionSettings.duplicateClipSetting);
            } else {
                enableSetting(PostActionSettings.duplicateClipSetting);
            }
        });

    }

}
