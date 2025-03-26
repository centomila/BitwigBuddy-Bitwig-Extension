package com.centomila.utils.commands.navigation;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to press the up arrow key.
 */
public class UpCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyUp();
    }
}
