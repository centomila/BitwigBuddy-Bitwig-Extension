package com.centomila.utils.commands.transport;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the end position of the arranger loop.
 */
public class ArrangerLoopEndCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double duration = Double.parseDouble(params[0].trim());
            extension.transport.arrangerLoopDuration().set(duration);
        } catch (NumberFormatException e) {
            reportError("Invalid loop duration: " + params[0], extension);
        }
    }
}
