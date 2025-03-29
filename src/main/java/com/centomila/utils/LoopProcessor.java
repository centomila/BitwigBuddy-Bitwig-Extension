package com.centomila.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;
import com.centomila.macro.state.BitwigStateProvider;

public class LoopProcessor {
    private static final Pattern LOOP_START = Pattern.compile("\\s*for\\s*\\(\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(\\d+)\\s+to\\s+(\\d+)\\s*\\)\\s*\\{\\s*");
    private static final Pattern LOOP_END = Pattern.compile("\\s*\\}\\s*");
    private static final Pattern VAR_ASSIGNMENT = Pattern.compile("\\s*var\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)\\s*");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private final Map<String, Object> globalVariables = new HashMap<>();
    private final BitwigStateProvider stateProvider;

    public LoopProcessor() {
        this.stateProvider = null;
    }

    public LoopProcessor(BitwigStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }

    public List<String> processLoop(List<String> commands) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < commands.size()) {
            String command = commands.get(i).trim(); // Normalize whitespace

            if (debug) {
                System.out.println("Processing line " + i + ": " + command);
            }

            Matcher loopMatcher = LOOP_START.matcher(command);
            Matcher varMatcher = VAR_ASSIGNMENT.matcher(command);

            if (loopMatcher.matches()) {
                String varName = loopMatcher.group(1);
                int start = Integer.parseInt(loopMatcher.group(2));
                int end = Integer.parseInt(loopMatcher.group(3));

                int loopEndIndex = findLoopEnd(commands, i + 1);
                if (loopEndIndex == -1) {
                    throw new RuntimeException("No matching closing brace '}' found for loop starting at line " + (i + 1));
                }

                List<String> loopBody = new ArrayList<>(commands.subList(i + 1, loopEndIndex));

                for (int j = start; j <= end; j++) {
                    Map<String, Object> localVariables = new HashMap<>(globalVariables);
                    localVariables.put(varName, j);

                    for (String loopCommand : loopBody) {
                        Matcher innerVarMatcher = VAR_ASSIGNMENT.matcher(loopCommand.trim());
                        if (innerVarMatcher.matches()) {
                            String innerVarName = innerVarMatcher.group(1);
                            String valueStr = innerVarMatcher.group(2).trim();
                            Object value = evaluateExpression(valueStr, localVariables);
                            localVariables.put(innerVarName, value);
                        } else {
                            result.add(replaceVariablesInLine(loopCommand, localVariables));
                        }
                    }
                }

                i = loopEndIndex + 1;
            } else if (varMatcher.matches()) {
                String varName = varMatcher.group(1);
                String valueStr = varMatcher.group(2).trim();

                Object value = evaluateExpression(valueStr, globalVariables);
                globalVariables.put(varName, value);
                i++;
            } else {
                result.add(replaceVariablesInLine(command, globalVariables));
                i++;
            }
        }

        return result;
    }

    private int findLoopEnd(List<String> commands, int startIndex) {
        int nestedCount = 0;

        for (int i = startIndex; i < commands.size(); i++) {
            String command = commands.get(i).trim(); // Normalize whitespace
            if (LOOP_START.matcher(command).matches()) {
                nestedCount++;
            } else if (LOOP_END.matcher(command).matches()) {
                if (nestedCount == 0) {
                    return i;
                }
                nestedCount--;
            }
        }

        return -1;
    }

    // Ensure that all commands are trimmed of leading and trailing whitespaces before processing
    private String replaceVariablesInLine(String line, Map<String, Object> variables) {
        line = line.trim(); // Trim whitespaces
        Matcher exprMatcher = EXPRESSION_PATTERN.matcher(line);
        StringBuffer result = new StringBuffer();

        while (exprMatcher.find()) {
            String expression = exprMatcher.group(1);
            Object value = evaluateExpression(expression, variables);
            exprMatcher.appendReplacement(result, value.toString().replace("$", "\\$"));
        }
        exprMatcher.appendTail(result);

        // Replace standalone variable names with their values
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String varName = entry.getKey();
            Object varValue = entry.getValue();
            line = line.replaceAll("\\b" + varName + "\\b", varValue.toString());
        }

        return result.toString();
    }

    private Object evaluateExpression(String expression, Map<String, Object> variables) {
        // Check if the expression is a function call
        if (expression.endsWith("()")) {
            String functionName = expression.substring(0, expression.length() - 2);
            if (stateProvider != null && stateProvider.supportsMethod(functionName)) {
                return stateProvider.callMethod(functionName);
            } else {
                throw new IllegalArgumentException("Unsupported function call: " + functionName);
            }
        }

        if (variables.containsKey(expression)) {
            return variables.get(expression);
        }

        try {
            // Replace variable names in the expression with their values
            for (Map.Entry<String, Object> var : variables.entrySet()) {
                String pattern = "\\b" + var.getKey() + "\\b";
                expression = expression.replaceAll(pattern, var.getValue().toString());
            }

            // Infer type from the format of the expression
            if (expression.startsWith("\"") && expression.endsWith("\"")) {
                // String type
                return expression.substring(1, expression.length() - 1);
            } else if (expression.matches("^-?\\d+$")) {
                // Integer type
                return Integer.parseInt(expression);
            } else if (expression.matches("^-?\\d+\\.\\d+$")) {
                // Double type
                return Double.parseDouble(expression);
            }

            // Evaluate as a mathematical expression if no type matches
            return evaluateMathExpression(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid parameter format: " + expression, e);
        }
    }

    private Object evaluateMathExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;

                values.push(Double.parseDouble(sb.toString()));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }

        double result = values.pop();

        if (result == Math.floor(result) && !Double.isInfinite(result)) {
            return (int) result;
        }
        return result;
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        return true;
    }

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