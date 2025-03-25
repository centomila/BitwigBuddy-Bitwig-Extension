package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to wait for a specified duration in milliseconds.
 */
public class WaitCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        int waitTime = params.length > 0 ? Integer.parseInt(params[0]) : 50;
        
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            reportError("Wait interrupted: " + e.getMessage(), extension);
        }
    }
}
