package com.centomila.macro;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.CommandFactory;
import com.centomila.utils.commands.CommandRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.centomila.utils.PopupUtils.console;

import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ControllerHost;

/**
 * Unified class for executing macro commands and Bitwig actions.
 */
public class MacroExecutor {
    // Static initializer to ensure CommandRegistration is loaded
    static {
        CommandRegistration.registerAllCommands();
    }

    /**
     * Executes a command by its ID, with optional parameters.
     *
     * @param actionId The ID of the action to execute
     * @param extension The BitwigBuddyExtension instance
     * @return true if the action was executed successfully, false otherwise
     */
    public static boolean executeCommand(String actionId, BitwigBuddyExtension extension) {
        ControllerHost host = extension.getHost();

        // If this line is a comment (with or without leading whitespace), show it and skip
        if (actionId.trim().startsWith("//")) {
            console("Skipping comment line: " + actionId);
            return true;
        }

        console("Executing command: " + actionId);

        // Extract parameters if present
        String[] params = extractParameters(actionId);
        String commandName = actionId;
        if (params != null) {
            // Strip parameters from action ID
            commandName = actionId.substring(0, actionId.indexOf("(")).trim();
        } else {
            params = new String[0];
        }

        try {
            // Try getting the command from the CommandFactory
            CommandFactory.BitwigCommand command = CommandFactory.getCommand(commandName);

            if (command != null) {
                console("Found registered command: " + commandName);
                command.execute(params, extension);
                return true;
            } else {
                // If no command is found in our registry, try default Bitwig action
                Action action = extension.getApplication().getAction(commandName);
                if (action != null) {
                    action.invoke();
                    return true;
                }

                host.errorln("Command not found: " + commandName);
                return false;
            }
        } catch (Exception e) {
            host.errorln("Error executing command '" + commandName + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts parameters from an action string.
     *
     * @param actionId The action string which may contain parameters
     * @return Array of parameter strings, or null if no parameters
     */
    private static String[] extractParameters(String actionId) {
        if (!actionId.contains("(")) {
            return null;
        }

        int start = actionId.indexOf("(");
        int end = actionId.lastIndexOf(")");

        // Ensure we have both opening and closing parentheses
        if (end <= start) {
            return null;
        }

        String paramsStr = actionId.substring(start + 1, end);

        // Detect bracketed arrays
        List<String> bracketed = new ArrayList<>();
        Pattern bracketPattern = Pattern.compile("\\[.*?\\]");
        Matcher m = bracketPattern.matcher(paramsStr);
        int placeholderCount = 0;
        while (m.find()) {
            bracketed.add(m.group());
            paramsStr = m.replaceFirst("__ARRAY_PLACEHOLDER_" + placeholderCount + "__");
            m = bracketPattern.matcher(paramsStr);
            placeholderCount++;
        }

        // Corrected the regular expression to properly escape double quotes
        String[] rawParams = paramsStr.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

        // Process each parameter
        for (int i = 0; i < rawParams.length; i++) {
            rawParams[i] = rawParams[i].trim();

            // Restore bracketed parts for array-like params
            for (int j = 0; j < bracketed.size(); j++) {
                rawParams[i] = rawParams[i].replace(
                        "__ARRAY_PLACEHOLDER_" + j + "__",
                        bracketed.get(j));
            }

            // Remove surrounding quotes, if present
            if (rawParams[i].startsWith("\"") && rawParams[i].endsWith("\"") && rawParams[i].length() > 1) {
                rawParams[i] = rawParams[i].substring(1, rawParams[i].length() - 1);
            }
        }

        return rawParams;
    }
}
