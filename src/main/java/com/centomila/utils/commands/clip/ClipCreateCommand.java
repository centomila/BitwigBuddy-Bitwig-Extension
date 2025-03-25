package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.MacroActionSettings;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to create a new clip in the launcher or arranger.
 */
public class ClipCreateCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateMinParamCount(params, 1, extension)) {
            return;
        }

        // Get current track index
        int currentTrack = extension.trackBank.cursorIndex().getAsInt();
        if (currentTrack < 0) {
            extension.getHost().println("No track selected, using first track (index 0)");
            currentTrack = 0;
        }
        
        // Parse parameters
        int clipLength = 4; // Default value
        if (params.length > 1) {
            try {
                clipLength = Integer.parseInt(params[1].trim());
            } catch (NumberFormatException e) {
                extension.getHost().println("Invalid clip length parameter, using default of 4 beats");
            }
        }

        int slotIndex = Integer.parseInt(params[0].trim()) - 1;
        
        // Execute command based on mode
        if (ModeSelectSettings.getCurrentLauncherArrangerToggleString().equals("Arranger")) {
            handleArrangerModeClipCreation(extension, clipLength);
        } else {
            handleLauncherModeClipCreation(extension, currentTrack, slotIndex, clipLength);
        }
    }
    
    private void handleArrangerModeClipCreation(BitwigBuddyExtension extension, int clipLength) {
        // Schedule the actions sequentially
        extension.application.setPanelLayout("ARRANGE");
        extension.arrangerClip.clipLauncherSlot().showInEditor();

        List<String> actions = new ArrayList<>();
        actions.add("select_start_of_selection_range");
        for (int i = 0; i < clipLength; i++) {
            actions.add("extend_time_selection_range_to_next_step");
        }
        actions.add("Consolidate");
        actions.add("switch_between_event_and_time_selection");

        // Use the scheduler from MacroActionSettings to execute the actions
        MacroActionSettings.scheduleCommands(
                actions.toArray(new String[0]),
                0,
                extension
        );
        extension.arrangerClip.isLoopEnabled().set(true);
    }
    
    private void handleLauncherModeClipCreation(BitwigBuddyExtension extension, int trackIndex, int slotIndex, int clipLength) {
        if (slotIndex >= 0) {
            // Delete existing clip if present
            extension.trackBank.getItemAt(trackIndex).clipLauncherSlotBank().getItemAt(slotIndex).deleteObject();
            
            // Create new clip
            extension.trackBank.getItemAt(trackIndex).clipLauncherSlotBank()
                    .createEmptyClip(slotIndex, clipLength);
                    
            // Make track visible
            extension.trackBank.getItemAt(trackIndex).makeVisibleInMixer();
            extension.trackBank.getItemAt(trackIndex).makeVisibleInArranger();
            
            extension.getHost().println("Created empty clip with length: " + clipLength);
        } else {
            extension.getHost().println("No clip slot selected. Please select a clip slot first.");
        }
    }
}
