package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.GlobalPreferences;
import com.centomila.utils.commands.BaseCommand;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Command to print all available actions to the console and save them to a file.
 */
public class PrintActionsCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        int actionsQty = extension.application.getActions().length;
        
        // Print actions to console
        for (int i = 0; i < actionsQty; i++) {
            extension.getHost().println(extension.application.getActions()[i].getName());
        }
        
        // Open console 
        extension.getApplication().getAction("show_controller_script_console").invoke();
        
        // Save actions to file
        String path = GlobalPreferences.getCurrentPresetsPath() + "/Actions.txt";
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            for (int i = 0; i < actionsQty; i++) {
                writer.println(extension.application.getActions()[i].getId() + "  |  "
                        + extension.application.getActions()[i].getName());
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            reportError("Failed to save actions list: " + e.getMessage(), extension);
            e.printStackTrace();
        }
    }
}
