package com.centomila.utils.commands.marker;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.CueMarker;

/**
 * Command to rename a cue marker.
 */
public class CueMarkerNameCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }
        
        try {
            final int itemNumber = Integer.parseInt(params[0].trim()) - 1;
            final String name = params[1].trim();
            
            // Use a longer delay (200ms) to ensure Bitwig has time to register the marker
            extension.getHost().scheduleTask(() -> {
                try {
                    // Force a refresh of the cue marker bank
                    extension.cueMarkerBank.scrollPosition().set(0);
                    
                    CueMarker cueMarker = extension.cueMarkerBank.getItemAt(itemNumber);
                    if (cueMarker != null && cueMarker.exists().get()) {
                        cueMarker.name().set(name);
                        extension.getHost().println("Renamed cue marker " + (itemNumber + 1) + " to: " + name);
                    } else {
                        // Try refreshing and retrying once if the marker isn't found
                        extension.getHost().scheduleTask(() -> {
                            try {
                                CueMarker retryMarker = extension.cueMarkerBank.getItemAt(itemNumber);
                                if (retryMarker != null && retryMarker.exists().get()) {
                                    retryMarker.name().set(name);
                                    extension.getHost().println("Renamed cue marker " + (itemNumber + 1) + " to: " + name + " (retry successful)");
                                } else {
                                    reportError("Cue marker at index " + (itemNumber + 1) + " doesn't exist after retry", extension);
                                }
                            } catch (Exception e) {
                                reportError("Error in retry setting cue marker name: " + e.getMessage(), extension);
                            }
                        }, 100); // Additional 100ms delay for retry
                    }
                } catch (IndexOutOfBoundsException e) {
                    reportError("Cue marker index out of range: " + (itemNumber + 1), extension);
                } catch (Exception e) {
                    reportError("Error setting cue marker name: " + e.getMessage(), extension);
                }
            }, 500); // Increased delay to 200ms
            
        } catch (NumberFormatException e) {
            reportError("Invalid cue marker index: " + params[0], extension);
        }
    }
}
