package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to close the BeatBuddy panel by using a workaround.
 * This command opens the Export Audio panel and then sends an Escape key press 
 * to force the BeatBuddy panel to close.
 */
public class BBClosePanelCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        // Open the Export Audio panel
        extension.getApplication().getAction("Export Audio").invoke();
        
        // Then press the Escape key to close it and force BB panel to close
        extension.getApplication().escape();
    }
}
