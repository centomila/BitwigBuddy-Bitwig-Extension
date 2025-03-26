package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to set the end velocity for note repeats.
 */
public class StepSelectedRepeatVelocityEndCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double velocityEnd = Double.parseDouble(params[0].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            for (NoteStep note : selectedNotes) {
                note.setRepeatVelocityEnd(velocityEnd);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid repeat velocity end value: " + params[0], extension);
        }
    }
}
