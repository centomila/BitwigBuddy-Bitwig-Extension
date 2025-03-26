package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to set the release velocity of selected note steps.
 */
public class StepSelectedReleaseVelocityCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double releaseVelocity = Double.parseDouble(params[0].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            extension.getHost().println("Selected notes: " + selectedNotes.size());
            for (NoteStep note : selectedNotes) {
                note.setReleaseVelocity(releaseVelocity);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid release velocity value: " + params[0], extension);
        }
    }
}
