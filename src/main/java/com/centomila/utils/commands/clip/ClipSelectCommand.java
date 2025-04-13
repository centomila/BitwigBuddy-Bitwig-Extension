package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to select a clip.
 * If an index parameter is provided, selects a specific clip in the launcher.
 * Without a parameter, selects the current clip.
 */
public class ClipSelectCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        // If no parameters, select the current clip
        if (params.length == 0) {
            extension.getLauncherOrArrangerAsClip().clipLauncherSlot().select();
            return;
        }
        
        try {
            // If we're in launcher mode and an index is provided, select the clip at that index
            if (ModeSelectSettings.getCurrentLauncherArrangerToggleString().equals("Launcher")) {
                // Get the current track index
                int trackIndex = extension.trackBank.cursorIndex().getAsInt();
                if (trackIndex < 0) {
                    extension.getHost().println("No track selected, using first track (index 0)");
                    trackIndex = 0;
                }
                
                // Parse the clip index parameter (1-based in the command, converted to 0-based)
                int slotIndex = Integer.parseInt(params[0].trim()) - 1;
                
                if (slotIndex >= 0 && slotIndex < 128) {
                    // Select the clip at the specified index
                    extension.trackBank.getItemAt(trackIndex).clipLauncherSlotBank().getItemAt(slotIndex).select();
                    extension.getHost().println("Selected clip at index: " + (slotIndex + 1));
                } else {
                    reportError("Invalid slot index: " + (slotIndex + 1), extension);
                }
            } else {
                // In arranger mode, we can't select by index, so just select current clip
                extension.getLauncherOrArrangerAsClip().clipLauncherSlot().select();
                extension.getHost().println("In Arranger mode, index parameter is ignored");
            }
        } catch (NumberFormatException e) {
            reportError("Invalid index parameter: " + params[0], extension);
        } catch (IndexOutOfBoundsException e) {
            reportError("Index out of bounds: " + params[0], extension);
        }
    }
}
