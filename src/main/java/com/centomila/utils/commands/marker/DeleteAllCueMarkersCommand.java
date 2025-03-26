package com.centomila.utils.commands.marker;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.CueMarker;

/**
 * Command to delete all cue markers in the project.
 */
public class DeleteAllCueMarkersCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        // Pass through multiple times to ensure all markers are deleted
        for (int pass = 0; pass < 16; pass++) {
            for (int i = 0; i < 128; i++) {
                CueMarker cueMarker = extension.cueMarkerBank.getItemAt(i);
                if (cueMarker.exists().get()) {
                    cueMarker.deleteObject();
                }
            }
        }
    }
}
