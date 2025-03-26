package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to duplicate the currently selected clip.
 */
public class ClipDuplicateCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (ModeSelectSettings.getCurrentLauncherArrangerToggleString().equals("Arranger")) {
            extension.application.duplicate();
        } else {
            extension.getLauncherOrArrangerAsClip().clipLauncherSlot().duplicateClip();
            int clipPosition = extension.getLauncherOrArrangerAsClip().clipLauncherSlot().sceneIndex().get();
            extension.sceneBank.scrollIntoView(clipPosition);
        }
    }
}
