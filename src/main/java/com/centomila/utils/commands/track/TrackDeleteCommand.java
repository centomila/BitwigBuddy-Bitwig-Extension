package com.centomila.utils.commands.track;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to delete a track by index.
 */
public class TrackDeleteCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            int trackIndex = Integer.parseInt(params[0].trim()) - 1; // Convert from 1-based to 0-based index
            extension.trackBank.getItemAt(trackIndex).deleteObject();
        } catch (NumberFormatException e) {
            reportError("Invalid track index: " + params[0], extension);
        } catch (IndexOutOfBoundsException e) {
            reportError("Track index out of range: " + params[0], extension);
        }
    }
}
