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
            int itemNumber = Integer.parseInt(params[0].trim()) - 1;
            String name = params[1].trim();
            CueMarker cueMarker = extension.cueMarkerBank.getItemAt(itemNumber);
            cueMarker.name().set(name);
        } catch (NumberFormatException e) {
            reportError("Invalid cue marker index: " + params[0], extension);
        } catch (IndexOutOfBoundsException e) {
            reportError("Cue marker index out of range: " + params[0], extension);
        }
    }
}
