package com.centomila.utils.commands.drum;

import com.centomila.BitwigBuddyExtension;
import com.centomila.Utils;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to insert a file into a drum pad.
 */
public class InsertFileInDrumPadCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }
        
        try {
            String noteNameFull = params[0].trim();
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);
            String filePath = params[1].trim();
            extension.drumPadBank.getItemAt(midiNote).insertionPoint().insertFile(filePath);
            extension.getHost().println("Inserted file into drum pad: " + noteNameFull + " with file: " + filePath);
        } catch (Exception e) {
            reportError("Failed to insert file in drum pad: " + e.getMessage(), extension);
        }
    }
}
