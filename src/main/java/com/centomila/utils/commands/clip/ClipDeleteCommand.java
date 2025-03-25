package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to delete the currently selected clip.
 */
public class ClipDeleteCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().clipLauncherSlot().deleteObject();
    }
}
