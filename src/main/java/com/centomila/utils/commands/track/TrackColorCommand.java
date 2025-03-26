package com.centomila.utils.commands.track;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.api.Color;

/**
 * Command to set the color of the selected track.
 */
public class TrackColorCommand extends BaseCommand {
    
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
            
            String trackColorStr = params[0].trim();
            Color trackColor = Color.fromHex(trackColorStr);
            extension.trackBank.getItemAt(currentTrack).color().set(trackColor);
        } catch (Exception e) {
            reportError("Invalid color format or track index: " + e.getMessage(), extension);
        }
    }
}
