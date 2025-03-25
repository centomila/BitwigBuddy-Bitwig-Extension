package com.centomila.utils.commands.transport;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the start position of the arranger loop.
 */
public class ArrangerLoopStartCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double startPos = Double.parseDouble(params[0].trim());
            extension.transport.arrangerLoopStart().set(startPos);
        } catch (NumberFormatException e) {
            reportError("Invalid loop start position: " + params[0], extension);
        }
    }
}
