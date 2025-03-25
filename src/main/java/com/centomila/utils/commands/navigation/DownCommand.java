package com.centomila.utils.commands.navigation;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to press the down arrow key.
 */
public class DownCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyDown();
    }
}
