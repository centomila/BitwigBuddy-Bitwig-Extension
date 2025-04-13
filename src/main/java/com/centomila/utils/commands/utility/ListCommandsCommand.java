package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.centomila.utils.commands.CommandRegistration;

import java.util.List;

/**
 * Command to list all registered commands in the system.
 * Useful for debugging and documentation purposes.
 */
public class ListCommandsCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        List<String> allCommands = CommandRegistration.getAllRegisteredCommands();
        
        // Display total count
        extension.getHost().println("[BeatBuddy] === ALL REGISTERED COMMANDS (" + allCommands.size() + " total) ===");
        
        // List all commands alphabetically
        allCommands.stream()
            .sorted()
            .forEach(cmd -> extension.getHost().println("[BeatBuddy] " + cmd));
        
        // Open console so the user can see the output
        extension.getApplication().getAction("show_controller_script_console").invoke();
    }
}