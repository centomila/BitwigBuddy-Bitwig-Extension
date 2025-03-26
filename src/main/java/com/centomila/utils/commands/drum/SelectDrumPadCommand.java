package com.centomila.utils.commands.drum;

import com.centomila.BitwigBuddyExtension;
import com.centomila.Utils;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to select a drum pad by note name.
 */
public class SelectDrumPadCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            String noteNameFull = params[0].trim();
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);
            extension.drumPadBank.getItemAt(midiNote).selectInEditor();
        } catch (Exception e) {
            reportError("Failed to select drum pad: " + e.getMessage(), extension);
        }
    }
}
