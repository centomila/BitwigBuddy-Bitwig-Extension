package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

/**
 * Command to toggle between Launcher and Arranger modes.
 */
public class BBToggleLauncherArrangerModeCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        // String currentMode = ((EnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).get();
        String currentMode = ModeSelectSettings.getCurrentLauncherArrangerToggleString();
        String newMode = currentMode.equals("Launcher") ? "Arranger" : "Launcher";
        ((SettableEnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).set(newMode);
    }
}
