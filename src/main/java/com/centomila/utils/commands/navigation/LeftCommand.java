package com.centomila.utils.commands.navigation;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to press the left arrow key.
 */
public class LeftCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyLeft();
    }
}
