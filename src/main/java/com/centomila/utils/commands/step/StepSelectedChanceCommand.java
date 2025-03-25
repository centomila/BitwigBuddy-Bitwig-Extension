package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to set the chance value of selected note steps.
 */
public class StepSelectedChanceCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double chance = Double.parseDouble(params[0].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            extension.getHost().println("Selected notes: " + selectedNotes.size());
            for (NoteStep note : selectedNotes) {
                note.setChance(chance);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid chance value: " + params[0], extension);
        }
    }
}
