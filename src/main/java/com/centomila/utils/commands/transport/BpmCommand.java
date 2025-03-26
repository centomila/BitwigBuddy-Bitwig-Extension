package com.centomila.utils.commands.transport;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the BPM (tempo) of the project.
 */
public class BpmCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            int bpm = Integer.parseInt(params[0].trim());
            extension.transport.tempo().setRaw(bpm);
        } catch (NumberFormatException e) {
            reportError("Invalid BPM value: " + params[0], extension);
        }
    }
}
