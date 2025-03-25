package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the playback start offset of a clip.
 */
public class ClipOffsetCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double offset = Double.parseDouble(params[0].trim());
            extension.getLauncherOrArrangerAsClip().getPlayStart().set(offset);
        } catch (NumberFormatException e) {
            reportError("Invalid offset value: " + params[0], extension);
        }
    }
}
