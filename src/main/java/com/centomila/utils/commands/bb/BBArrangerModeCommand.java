package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.SettableEnumValue;

/**
 * Command to switch to Arranger mode.
 */
public class BBArrangerModeCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        ((SettableEnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).set("Arranger");
    }
}
