package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to generate a BeatBuddy drum pattern preset.
 */
public class BBGenerateCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.generateDrumPattern();
    }
}
