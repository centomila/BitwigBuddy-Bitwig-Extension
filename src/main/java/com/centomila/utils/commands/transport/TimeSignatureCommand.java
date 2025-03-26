package com.centomila.utils.commands.transport;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the project time signature.
 */
public class TimeSignatureCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            extension.transport.timeSignature().set(params[0].trim());
        } catch (Exception e) {
            reportError("Invalid time signature format: " + params[0], extension);
        }
    }
}
