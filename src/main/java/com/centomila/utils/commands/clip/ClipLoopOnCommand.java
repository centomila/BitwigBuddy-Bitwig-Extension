package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to enable looping for the selected clip.
 */
public class ClipLoopOnCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().isLoopEnabled().set(true);
    }
}
