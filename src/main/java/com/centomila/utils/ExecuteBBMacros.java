package com.centomila.utils;

import static com.centomila.utils.PopupUtils.*;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.CommandFactory;
import com.centomila.utils.commands.CommandRegistration;
import com.centomila.ModeSelectSettings;
import com.centomila.ClipUtils;
import com.centomila.GlobalPreferences;
import com.centomila.PatternSettings;
import com.centomila.PostActionSettings;
import com.centomila.MacroActionSettings;
import com.centomila.Utils;

import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.api.Color;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Main class for executing commands in Bitwig Studio.
 * Uses the Command Pattern to delegate action handling to specialized command classes.
 */
public class ExecuteBBMacros {

    // Register all commands on class initialization
    static {
        CommandRegistration.registerAllCommands();
    }

    /**
     * Executes a Bitwig action by its ID, with optional parameters.
     * 
     * @param actionId The ID of the action to execute
     * @param extension The BitwigBuddyExtension instance
     * @return true if the action was executed successfully, false otherwise
     */
    public static boolean executeBitwigAction(String actionId, BitwigBuddyExtension extension) {
        ControllerHost host = extension.getHost();
        console("Executing Bitwig action: " + actionId);

        // If this line is a comment, show it and skip
        if (actionId.startsWith("//")) {
            console("Skipping comment line: " + actionId);
            return true;
        }

        // Extract parameters if present
        String[] params = extractParameters(actionId);
        if (params != null) {
            // Strip parameters from action ID
            actionId = actionId.substring(0, actionId.indexOf("(")).trim();
        } else {
            params = new String[0];
        }

        console("Executing Bitwig action: " + actionId);

        try {
            // Try getting the command from the CommandFactory
            CommandFactory.BitwigCommand command = CommandFactory.getCommand(actionId);
            if (command != null) {
                command.execute(params, extension);
                return true;
            } else {
                // If no command is found in our registry, try default Bitwig action
                Action action = extension.getApplication().getAction(actionId);
                if (action != null) {
                    action.invoke();
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            host.errorln("Error executing command '" + actionId + "': " + e.getMessage());
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

        // Split by commas outside quotes
        String[] rawParams = paramsStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

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

    /**
     * Gets the index of the currently selected track.
     * 
     * @param extension The BitwigBuddyExtension instance
     * @return The index of the selected track, or 0 if no track is selected
     */
    public static int getCurrentTrackIndex(BitwigBuddyExtension extension) {
        int trackIndex = extension.trackBank.cursorIndex().getAsInt();
        if (trackIndex < 0) {
            extension.getHost().println("No track selected, using first track (index 0)");
            trackIndex = 0;
        }
        return trackIndex;
    }
}