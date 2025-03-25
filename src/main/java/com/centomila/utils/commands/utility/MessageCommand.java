package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.PopupUtils;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to display a message in a popup.
 */
public class MessageCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (params.length > 0) {
            PopupUtils.showPopup(params[0]);
        } else {
            reportError("No message text provided", extension);
        }
    }
}
