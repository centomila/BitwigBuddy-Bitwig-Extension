package com.centomila.utils.commands.track;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to rename the selected track.
 */
public class TrackRenameCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            int currentTrack = extension.trackBank.cursorIndex().getAsInt();
            if (currentTrack < 0) {
                extension.getHost().println("No track selected, using first track (index 0)");
                currentTrack = 0;
            }
            
            String trackName = params[0].trim();
            extension.trackBank.getItemAt(currentTrack).name().set(trackName);
        } catch (Exception e) {
            reportError("Error renaming track: " + e.getMessage(), extension);
        }
    }
}
