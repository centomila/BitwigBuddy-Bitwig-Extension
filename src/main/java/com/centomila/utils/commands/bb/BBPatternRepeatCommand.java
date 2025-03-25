package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.PatternSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the pattern repeat quantity for BeatBuddy.
 */
public class BBPatternRepeatCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            int repeatQty = Integer.parseInt(params[0]);
            PatternSettings.setRepeatPattern(repeatQty);
        } catch (NumberFormatException e) {
            reportError("Invalid repeat quantity: " + params[0], extension);
        }
    }
}
