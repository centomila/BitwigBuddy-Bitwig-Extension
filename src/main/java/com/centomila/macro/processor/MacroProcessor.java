package com.centomila.macro.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.centomila.macro.state.BitwigStateProvider;

/**
 * Processes macro scripts with support for variables, loops, and mathematical expressions.
 */
public class MacroProcessor {
    // Regular expressions for syntax parsing
    private static final Pattern LOOP_START = Pattern.compile("\\s*for\\s*\\(\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(\\d+)\\s+to\\s+(\\d+)\\s*\\)\\s*\\{\\s*");
    private static final Pattern LOOP_END = Pattern.compile("\\s*\\}\\s*");
    private static final Pattern VAR_ASSIGNMENT = Pattern.compile("\\s*var\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)\\s*");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Pattern FUNCTION_CALL = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\(\\)");
    
    // Debug flag for troubleshooting
    private boolean debug = false;
    
    // Variables storage
    private final Map<String, Object> variables = new HashMap<>();
    
    // Script engine for expression evaluation
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    
    // Bitwig state provider for function calls
    private BitwigStateProvider stateProvider;
    
    /**
     * Default constructor without state provider (limited functionality)
     */
    public MacroProcessor() {
        this.stateProvider = null;
    }
    
    /**
     * Constructor with state provider for Bitwig integration
     */
    public MacroProcessor(BitwigStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /**
     * Processes a list of macro commands, handling loops, variables, and expressions.
     * 
     * @param commands List of raw command strings from the macro file
     * @return List of processed command strings ready for execution
     */
    public List<String> processCommands(List<String> commands) {
        List<String> result = new ArrayList<>();
        int i = 0;
        
        while (i < commands.size()) {
            String command = commands.get(i);
            
            // Debug output to identify issues with regex matching
            if (debug) {
                System.out.println("Processing line " + i + ": " + command);
            }
            
            Matcher loopMatcher = LOOP_START.matcher(command);
            Matcher varMatcher = VAR_ASSIGNMENT.matcher(command);
            
            if (loopMatcher.matches()) {
                // Handle loop construct
                processLoop(commands, loopMatcher, i, result);
                i = findLoopEnd(commands, i + 1) + 1;
            } else if (varMatcher.matches()) {
                // Handle variable assignment
                processVariableAssignment(varMatcher);
                i++;
            } else {
                // Process regular command with variable substitution
                result.add(replaceVariablesInLine(command));
                i++;
            }
        }
        
        return result;
    }
    
    /**
     * Processes a loop construct in the macro
     */
    private void processLoop(List<String> commands, Matcher loopMatcher, int startIndex, List<String> result) {
        if (debug) {
            System.out.println("Loop match found: var=" + loopMatcher.group(1) + 
                               " start=" + loopMatcher.group(2) + 
                               " end=" + loopMatcher.group(3));
        }
        
        // Extract loop parameters
        String varName = loopMatcher.group(1);
        int start = Integer.parseInt(loopMatcher.group(2));
        int end = Integer.parseInt(loopMatcher.group(3));
        
        // Find matching loop end
        int loopEndIndex = findLoopEnd(commands, startIndex + 1);
        if (loopEndIndex == -1) {
            throw new RuntimeException("No matching closing brace '}' found for loop starting at line " + (startIndex + 1));
        }
        
        // Extract loop body - use subList view instead of creating new ArrayList for better performance
        List<String> loopBody = commands.subList(startIndex + 1, loopEndIndex);
        
        // Execute loop
        List<String> processedCommands = new ArrayList<>(loopBody.size() * (end - start + 1));
        for (int j = start; j <= end; j++) {
            variables.put(varName, j);
            processedCommands.addAll(replaceVariables(loopBody));
        }
        result.addAll(processedCommands);
    }
    
    /**
     * Processes a variable assignment in the macro
     */
    private void processVariableAssignment(Matcher varMatcher) {
        if (debug) {
            System.out.println("Variable assignment found: var=" + varMatcher.group(1) + 
                               " value=" + varMatcher.group(2));
        }
        
        String varName = varMatcher.group(1);
        String valueStr = varMatcher.group(2).trim();
        
        // Check if the value is a function call
        Matcher functionMatcher = FUNCTION_CALL.matcher(valueStr);
        if (functionMatcher.matches()) {
            String functionName = functionMatcher.group(1);
            Object value = processFunctionCall(functionName);
            variables.put(varName, value);
            return;
        }
        
        // Parse the variable value based on its format
        Object value;
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            // String value
            value = valueStr.substring(1, valueStr.length() - 1);
        } else {
            try {
                // Try to parse as number
                if (valueStr.contains(".")) {
                    value = Double.parseDouble(valueStr);
                } else {
                    value = Integer.parseInt(valueStr);
                }
            } catch (NumberFormatException e) {
                // If not a number, treat as string without quotes
                value = valueStr;
            }
        }
        
        variables.put(varName, value);
    }
    
    /**
     * Processes a function call and returns its result
     */
    private Object processFunctionCall(String functionName) {
        if (stateProvider != null) {
            if (debug) {
                System.out.println("Checking if stateProvider supports method: " + functionName);
            }
            if (stateProvider.supportsMethod(functionName)) {
                Object result = stateProvider.callMethod(functionName);
                if (debug) {
                    System.out.println("Called function " + functionName + " with result: " + result);
                }
                return result;
            } else {
                if (debug) {
                    System.out.println("Function not supported: " + functionName);
                }
                return "Function not supported: " + functionName;
            }
        } else {
            if (debug) {
                System.out.println("StateProvider is null. Cannot call function: " + functionName);
            }
            return "StateProvider is not initialized";
        }
    }
    
    /**
     * Finds the matching end of a loop construct
     */
    private int findLoopEnd(List<String> commands, int startIndex) {
        int nestedCount = 0;
        
        for (int i = startIndex; i < commands.size(); i++) {
            if (LOOP_START.matcher(commands.get(i)).matches()) {
                nestedCount++;
            } else if (LOOP_END.matcher(commands.get(i)).matches()) {
                if (nestedCount == 0) {
                    return i;
                }
                nestedCount--;
            }
        }
        
        return -1;
    }
    
    /**
     * Replaces variables in a list of commands
     */
    private List<String> replaceVariables(List<String> commands) {
        List<String> result = new ArrayList<>();
        for (String command : commands) {
            // Skip variable replacement for comment lines to preserve them exactly as they are
            if (command.trim().startsWith("//")) {
                result.add(command);
            } else {
                result.add(replaceVariablesInLine(command));
            }
        }
        return result;
    }
    
    /**
     * Replaces variables in a single command line
     */
    private String replaceVariablesInLine(String line) {
        if (!line.contains("${")) {
            return line; // Fast path for lines without expressions
        }
        
        // Find all expressions in the format ${...}
        Matcher exprMatcher = EXPRESSION_PATTERN.matcher(line);
        StringBuffer result = new StringBuffer(line.length() + 16);
        
        while (exprMatcher.find()) {
            String expression = exprMatcher.group(1);
            Object value = evaluateExpression(expression);
            exprMatcher.appendReplacement(result, value.toString().replace("$", "\\$"));
        }
        exprMatcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Evaluates a simple expression that can contain variables and basic arithmetic.
     */
    private Object evaluateExpression(String expression) {
        // Check if the expression is a function call
        Matcher functionMatcher = FUNCTION_CALL.matcher(expression);
        if (functionMatcher.matches()) {
            String functionName = functionMatcher.group(1);
            return processFunctionCall(functionName);
        }
        
        // For simple variable reference with no operations, return the variable directly
        if (variables.containsKey(expression)) {
            return variables.get(expression);
        }
        
        try {
            // Substitute variables in the expression
            String preparedExpression = expression;
            for (Map.Entry<String, Object> var : variables.entrySet()) {
                String pattern = "\\b" + var.getKey() + "\\b";
                Object value = var.getValue();
                String replacement = value instanceof String ? 
                    "'" + value.toString().replace("'", "\\'") + "'" : 
                    value.toString();
                preparedExpression = preparedExpression.replaceAll(pattern, replacement);
            }
            
            // Evaluate using ScriptEngine
            Object result = scriptEngine.eval(preparedExpression);
            
            // Convert to integer if it's a whole number
            if (result instanceof Number) {
                double doubleValue = ((Number) result).doubleValue();
                if (doubleValue == Math.floor(doubleValue) && !Double.isInfinite(doubleValue)) {
                    return ((Number) result).intValue();
                }
            }
            
            return result;
        } catch (ScriptException e) {
            if (debug) {
                System.out.println("Expression evaluation error: " + e.getMessage());
            }
            // If evaluation fails, return the original expression
            return expression;
        }
    }
}
