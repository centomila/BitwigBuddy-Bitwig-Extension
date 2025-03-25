package com.centomila.utils.commands.bb;

import com.centomila.BitwigBuddyExtension;
import com.centomila.PostActionSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the auto-resize option for post-pattern-generation.
 */
public class BBPostActionAutoResizeCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        String setting = params[0].trim();
        if (setting.equals("true") || setting.equals("On")) {
            PostActionSettings.setAutoResizeLoopLengthSetting("On");
        } else {
            PostActionSettings.setAutoResizeLoopLengthSetting("Off");
        }
    }
}
