package com.centomila.utils.commands.clip;

import com.bitwig.extension.controller.api.DeleteableObject;
import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to delete the currently selected clip.
 */
public class ClipDeleteCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        String currentMode = ModeSelectSettings.getCurrentLauncherArrangerToggleString();

        if (currentMode.equals("Arranger")) {
            // In Arranger mode, use the application's delete action to delete the selected clip
            extension.getApplication().remove();
            
        } else if (currentMode.equals("Launcher")) {
            // In Launcher mode, delete the selected clip
            extension.getLauncherOrArrangerAsClip().clipLauncherSlot().deleteObject();
        }
    }
}
