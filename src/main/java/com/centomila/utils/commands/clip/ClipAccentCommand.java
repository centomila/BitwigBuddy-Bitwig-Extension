package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the accent value for the selected clip.
 */
public class ClipAccentCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double accentValue = Double.parseDouble(params[0].trim());
            extension.getLauncherOrArrangerAsClip().getAccent().setRaw(accentValue);
        } catch (NumberFormatException e) {
            reportError("Invalid accent value: " + params[0], extension);
        }
    }
}
