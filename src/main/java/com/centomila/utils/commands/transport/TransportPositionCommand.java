package com.centomila.utils.commands.transport;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the transport position.
 */
public class TransportPositionCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            double position = Double.parseDouble(params[0].trim());
            extension.transport.setPosition(position);
        } catch (NumberFormatException e) {
            reportError("Invalid position value: " + params[0], extension);
        }
    }
}
