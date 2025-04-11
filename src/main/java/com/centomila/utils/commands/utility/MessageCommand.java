package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.macro.state.BitwigStateProvider;
import com.centomila.macro.state.DefaultBitwigStateProvider;
import com.centomila.utils.PopupUtils;
import com.centomila.utils.commands.BaseCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shows a popup message to the user.
 * Supports variable and function expression interpolation.
 */
public class MessageCommand extends BaseCommand {
    // Match function calls in both formats: direct and wrapped in ${}
    private static final Pattern FUNCTION_CALL_DIRECT = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\(\\)");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateMinParamCount(params, 1, extension)) {
            return;
        }

        // Join all parameters with spaces to handle multi-part messages
        String message = String.join(" ", params);
        
        // Process any expressions within the message
        message = processExpressions(message, extension);
        
        // Show the message
        PopupUtils.showPopup(message);
    }
    
    /**
     * Process expressions in ${...} format and direct function calls
     */
    private String processExpressions(String message, BitwigBuddyExtension extension) {
        BitwigStateProvider stateProvider = new DefaultBitwigStateProvider(extension);
        
        // First process wrapped expressions with ${...} syntax
        message = processWrappedExpressions(message, stateProvider);
        
        // Then process direct function calls like getCurrentTrackName()
        message = processDirectFunctionCalls(message, stateProvider);
        
        return message;
    }
    
    /**
     * Process expressions wrapped in ${...}
     */
    private String processWrappedExpressions(String message, BitwigStateProvider stateProvider) {
        // If no expressions are present, return the original message
        if (!message.contains("${")) {
            return message;
        }
        
        // Find and replace all expressions
        Matcher matcher = EXPRESSION_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String expression = matcher.group(1);
            String replacement;
            
            // Check if the expression is a function call
            Matcher funcMatcher = FUNCTION_CALL_DIRECT.matcher(expression);
            if (funcMatcher.matches()) {
                String functionName = funcMatcher.group(1);
                replacement = executeFunctionCall(functionName, stateProvider);
            } else {
                // For other expressions, just use as is for now
                replacement = expression;
            }
            
            // Replace the expression with its result
            matcher.appendReplacement(result, replacement.replace("$", "\\$"));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Process direct function calls not wrapped in ${}
     */
    private String processDirectFunctionCalls(String message, BitwigStateProvider stateProvider) {
        Matcher matcher = FUNCTION_CALL_DIRECT.matcher(message);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String functionName = matcher.group(1);
            String replacement = executeFunctionCall(functionName, stateProvider);
            matcher.appendReplacement(result, replacement.replace("$", "\\$"));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Execute a function call and return its result as a string
     */
    private String executeFunctionCall(String functionName, BitwigStateProvider stateProvider) {
        if (stateProvider.supportsMethod(functionName)) {
            Object value = stateProvider.callMethod(functionName);
            return value != null ? value.toString() : "null";
        }
        return "Function not supported: " + functionName;
    }
}
