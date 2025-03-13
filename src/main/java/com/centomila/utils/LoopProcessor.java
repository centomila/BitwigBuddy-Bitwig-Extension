package com.centomila.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoopProcessor {
    private static final Pattern LOOP_START = Pattern.compile("\\[Loop\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(\\d+)\\s+to\\s+(\\d+)\\]");
    private static final Pattern LOOP_END = Pattern.compile("\\[End\\s*Loop\\]");
    
    private final Map<String, Integer> variables = new HashMap<>();
    
    public List<String> processLoop(List<String> commands) {
        List<String> result = new ArrayList<>();
        int i = 0;
        
        while (i < commands.size()) {
            String command = commands.get(i);
            Matcher loopMatcher = LOOP_START.matcher(command);
            
            if (loopMatcher.matches()) {
                // Extract loop parameters
                String varName = loopMatcher.group(1);
                int start = Integer.parseInt(loopMatcher.group(2));
                int end = Integer.parseInt(loopMatcher.group(3));
                
                // Find matching loop end
                int loopEndIndex = findLoopEnd(commands, i + 1);
                if (loopEndIndex == -1) {
                    throw new RuntimeException("No matching [End Loop] found");
                }
                
                // Extract loop body
                List<String> loopBody = new ArrayList<>(commands.subList(i + 1, loopEndIndex));
                
                // Execute loop
                for (int j = start; j <= end; j++) {
                    variables.put(varName, j);
                    result.addAll(replaceVariables(loopBody));
                }
                
                i = loopEndIndex + 1;
            } else {
                result.add(replaceVariablesInLine(command));
                i++;
            }
        }
        
        return result;
    }
    
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
    
    private List<String> replaceVariables(List<String> commands) {
        List<String> result = new ArrayList<>();
        for (String command : commands) {
            result.add(replaceVariablesInLine(command));
        }
        return result;
    }
    
    private String replaceVariablesInLine(String line) {
        for (Map.Entry<String, Integer> var : variables.entrySet()) {
            line = line.replace("${" + var.getKey() + "}", var.getValue().toString());
        }
        return line;
    }
}