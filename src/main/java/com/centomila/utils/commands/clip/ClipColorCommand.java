package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.api.Color;

/**
 * Command to set the color of the currently selected clip.
 */
public class ClipColorCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            String colorStr = params[0].trim();
            Color color = Color.fromHex(colorStr);
            extension.getLauncherOrArrangerAsClip().color().set(color);
        } catch (Exception e) {
            reportError("Invalid color format: " + params[0], extension);
        }
    }
}
