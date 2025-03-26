package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to rename the currently selected clip.
 */
public class ClipRenameCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        String newName = params[0].trim();
        extension.getLauncherOrArrangerAsClip().setName(newName);
    }
}
