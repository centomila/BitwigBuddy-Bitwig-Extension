package com.centomila.utils.commands.track;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Track;

/**
 * Command to set the color of all tracks in the track bank.
 */
public class TrackColorAllCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            String trackColorStr = params[0].trim();
            Color trackColor = Color.fromHex(trackColorStr);
            int trackCount = extension.trackBank.getSizeOfBank();
            
            // Apply color to all visible tracks in the trackbank
            int coloredCount = 0;
            for (int i = 0; i < trackCount; i++) {
                Track track = extension.trackBank.getItemAt(i);
                if (track.exists().get()) {
                    track.color().set(trackColor);
                    extension.getHost().println("Applied color to track: " + track.name().get());
                    coloredCount++;
                }
            }
            
            if (coloredCount > 0) {
                extension.getHost().println("Applied color to " + coloredCount + " tracks");
            } else {
                extension.getHost().println("No tracks found to apply color");
            }
            
        } catch (Exception e) {
            reportError("Invalid color format or track error: " + e.getMessage(), extension);
        }
    }
}