package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.List;

/**
 * Command to mute/unmute selected note steps.
 */
public class StepSelectedIsMutedCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            boolean isMuted = Boolean.parseBoolean(params[0].trim());
            List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
            for (NoteStep note : selectedNotes) {
                note.setIsMuted(isMuted);
            }
        } catch (Exception e) {
            reportError("Invalid boolean value: " + params[0], extension);
        }
    }
}
