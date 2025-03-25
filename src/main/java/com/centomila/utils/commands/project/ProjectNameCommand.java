package com.centomila.utils.commands.project;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Command to display the current project name.
 */
public class ProjectNameCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        extension.getApplication().projectName();
        showPopup(extension.getApplication().projectName().toString());
    }
}
