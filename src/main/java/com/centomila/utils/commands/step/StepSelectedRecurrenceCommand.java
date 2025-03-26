package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to set recurrence pattern for selected note steps.
 */
public class StepSelectedRecurrenceCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }
        
        try {
            int length = Integer.parseInt(params[0].trim());
            int mask = Integer.parseInt(params[1].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            for (NoteStep note : selectedNotes) {
                note.setRecurrence(length, mask);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid recurrence parameters: " + params[0] + ", " + params[1], extension);
        }
    }
}
