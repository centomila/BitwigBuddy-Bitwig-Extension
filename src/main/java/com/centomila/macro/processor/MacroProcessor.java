package com.centomila.macro.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

/**
 * Processes macro scripts with support for variables, loops, and mathematical expressions.
 */
public class MacroProcessor {
    // Regular expressions for syntax parsing
    private static final Pattern LOOP_START = Pattern.compile("\\s*for\\s*\\(\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(\\d+)\\s+to\\s+(\\d+)\\s*\\)\\s*\\{\\s*");
    private static final Pattern LOOP_END = Pattern.compile("\\s*\\}\\s*");
    private static final Pattern VAR_ASSIGNMENT = Pattern.compile("\\s*var\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)\\s*");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    // Debug flag for troubleshooting
    private boolean debug = false;
    
    // Variables storage
    private final Map<String, Object> variables = new HashMap<>();
    
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
        
        // Extract loop body
        List<String> loopBody = new ArrayList<>(commands.subList(startIndex + 1, loopEndIndex));
        
        // Execute loop
        for (int j = start; j <= end; j++) {
            variables.put(varName, j);
            result.addAll(replaceVariables(loopBody));
        }
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
        // Find all expressions in the format ${...}
        Matcher exprMatcher = EXPRESSION_PATTERN.matcher(line);
        StringBuffer result = new StringBuffer();
        
        while (exprMatcher.find()) {
            String expression = exprMatcher.group(1);
            Object value = evaluateExpression(expression);
            exprMatcher.appendReplacement(result, value.toString().replace("$", "\\$"));
        }
        exprMatcher.appendTail(result);
        
        return result.toString();
    }
    
    // ...existing code for expression evaluation...
    
    /**
     * Evaluates a simple expression that can contain variables and basic arithmetic.
     */
    private Object evaluateExpression(String expression) {
        // For simple variable reference with no operations, return the variable directly
        if (variables.containsKey(expression)) {
            return variables.get(expression);
        }
        
        try {
            // Handle arithmetic expression
            return evaluateArithmeticExpression(expression);
        } catch (Exception e) {
            // If parsing fails or any other error occurs, return the expression as is
            return expression;
        }
    }
    
    /**
     * Evaluates a simple arithmetic expression with variables.
     */
    private Object evaluateArithmeticExpression(String expression) {
        // First replace all variables with their values
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            String pattern = "\\b" + var.getKey() + "\\b";  // word boundary to match whole words
            expression = expression.replaceAll(pattern, var.getValue().toString());
        }
        
        // Now parse the expression with values substituted
        return evaluateMathExpression(expression);
    }
    
    /**
     * Simple mathematical expression evaluator.
     */
    private Object evaluateMathExpression(String expression) {
        // Remove all spaces
        expression = expression.replaceAll("\\s+", "");
        
        // Use stacks for evaluation
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // If current character is a digit or decimal point, read the complete number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                
                // Read the entire number
                while (i < expression.length() && 
                      (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--; // Step back as i was incremented in the loop
                
                // Add the parsed number to values stack
                values.push(Double.parseDouble(sb.toString()));
            }
            // If current character is an operator
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                // Evaluate expressions with higher precedence
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                // Push current operator
                operators.push(c);
            }
        }
        
        // Evaluate remaining operations
        while (!operators.isEmpty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }
        
        // Final result should be the only value in the stack
        double result = values.pop();
        
        // If the result is an integer value, return it as an integer to avoid decimal point
        if (result == Math.floor(result) && !Double.isInfinite(result)) {
            return (int)result;
        }
        return result;
    }
    
    /**
     * Determines if op2 has higher or equal precedence than op1.
     */
    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        return true;
    }
    
    /**
     * Applies an arithmetic operation.
     */
    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
        }
        return 0;
    }
}
