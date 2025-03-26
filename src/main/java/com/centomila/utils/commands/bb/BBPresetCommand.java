package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.PatternSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to select a BeatBuddy preset.
 */
public class BBPresetCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        PatternSettings.setCustomPreset(params[0]);
    }
}
