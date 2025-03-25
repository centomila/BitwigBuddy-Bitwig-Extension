package com.centomila.utils.commands;

import com.centomila.BitwigBuddyExtension;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating and retrieving command objects.
 * This class centralizes command registration and lookup.
 */
public class CommandFactory {
    
    /**
     * Interface for all commands that can be executed by the extension
     */
    public interface BitwigCommand {
        /**
         * Executes the command with the given parameters
         * 
         * @param params Parameters parsed from the command string
         * @param extension The extension instance providing access to Bitwig API
         */
        void execute(String[] params, BitwigBuddyExtension extension);
    }
    
    // Registry of all available commands
    private static final Map<String, BitwigCommand> commandRegistry = new HashMap<>();
    
    /**
     * Registers a command with the factory
     * 
     * @param commandName The name of the command (action ID)
     * @param command The command implementation
     */
    public static void registerCommand(String commandName, BitwigCommand command) {
        commandRegistry.put(commandName, command);
    }
    
    /**
     * Retrieves a command from the registry
     * 
     * @param commandName The name of the command to retrieve
     * @return The command implementation, or null if not found
     */
    public static BitwigCommand getCommand(String commandName) {
        return commandRegistry.get(commandName);
    }
    
    /**
     * Checks if a command exists in the registry
     * 
     * @param commandName The name of the command to check
     * @return true if the command exists, false otherwise
     */
    public static boolean hasCommand(String commandName) {
        return commandRegistry.containsKey(commandName);
    }
}
