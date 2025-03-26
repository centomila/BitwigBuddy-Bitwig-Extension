package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteOccurrence;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to set the occurrence condition for selected note steps.
 */
public class StepSelectedOccurrenceCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            NoteOccurrence condition = NoteOccurrence.valueOf(params[0].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            for (NoteStep note : selectedNotes) {
                note.setOccurrence(condition);
            }
        } catch (IllegalArgumentException e) {
            reportError("Invalid occurrence value: " + params[0], extension);
        }
    }
}
