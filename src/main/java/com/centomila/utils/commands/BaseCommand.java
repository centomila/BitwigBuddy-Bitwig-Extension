package com.centomila.utils.commands;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.PopupUtils;

/**
 * Base abstract class for all command implementations.
 * Provides common functionality for command execution.
 */
public abstract class BaseCommand implements CommandFactory.BitwigCommand {
    
    /**
     * Validates that the command has a specific number of parameters
     * 
     * @param params The parameters array
     * @param expectedCount The expected number of parameters
     * @param extension The extension instance for error reporting
     * @return true if validation passes, false if it fails
     */
    protected boolean validateParamCount(String[] params, int expectedCount, BitwigBuddyExtension extension) {
        if (params.length != expectedCount) {
            extension.getHost().errorln(getClass().getSimpleName() + 
                " requires " + expectedCount + " parameters, but " + params.length + " were provided");
            PopupUtils.showPopup("Command error: Wrong number of parameters");
            return false;
        }
        return true;
    }
    
    /**
     * Validates that the command has at least a minimum number of parameters
     * 
     * @param params The parameters array
     * @param minCount The minimum number of parameters
     * @param extension The extension instance for error reporting
     * @return true if validation passes, false if it fails
     */
    protected boolean validateMinParamCount(String[] params, int minCount, BitwigBuddyExtension extension) {
        if (params.length < minCount) {
            extension.getHost().errorln(getClass().getSimpleName() + 
                " requires at least " + minCount + " parameters, but " + params.length + " were provided");
            PopupUtils.showPopup("Command error: Not enough parameters");
            return false;
        }
        return true;
    }
    
    /**
     * Reports an error during command execution
     * 
     * @param message The error message
     * @param extension The extension instance for error reporting
     */
    protected void reportError(String message, BitwigBuddyExtension extension) {
        extension.getHost().errorln(getClass().getSimpleName() + ": " + message);
        PopupUtils.showPopup("Command error: " + message);
    }
}
