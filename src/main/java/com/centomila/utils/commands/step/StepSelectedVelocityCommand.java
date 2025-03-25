package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to set the velocity of selected note steps.
 */
public class StepSelectedVelocityCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double stepVelocity = Double.parseDouble(params[0].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            extension.getHost().println("Selected notes: " + selectedNotes.size());

            for (NoteStep note : selectedNotes) {
                note.setVelocity(stepVelocity);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid velocity value: " + params[0], extension);
        }
    }
}
