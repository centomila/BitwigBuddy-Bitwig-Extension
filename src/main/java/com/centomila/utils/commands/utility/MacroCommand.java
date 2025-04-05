package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.macro.MacroExecutor;
import com.centomila.utils.LoopProcessor;
import com.centomila.utils.PopupUtils;
import com.centomila.utils.commands.BaseCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Command for executing a macro file which can contain loops and variable assignments.
 */
public class MacroCommand extends BaseCommand {
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateMinParamCount(params, 1, extension)) {
            return;
        }

        String macroName = params[0];
        String macrosFolder = extension.getMacrosFolder();
        Path macroPath = Paths.get(macrosFolder, macroName + ".txt");

        try {
            if (!Files.exists(macroPath)) {
                reportError("Macro file not found: " + macroPath, extension);
                return;
            }

            List<String> macroLines = Files.readAllLines(macroPath);
            
            // Normalize indentation to avoid tab issues
            List<String> normalizedLines = new ArrayList<>();
            for (String line : macroLines) {
                // Convert all whitespace sequences (including tabs) to single spaces
                // except for spaces around braces and within expressions
                normalizedLines.add(line);
            }
            
            // Process loops and variables
            LoopProcessor loopProcessor = new LoopProcessor(extension.getStateProvider());
            
            // Enable debug if a debug parameter is passed
            if (params.length > 1 && params[1].equalsIgnoreCase("debug")) {
                loopProcessor.setDebug(true);
                extension.getHost().println("Debug mode enabled for macro processing");
            }
            
            List<String> processedLines;
            try {
                processedLines = loopProcessor.processLoop(normalizedLines);
            } catch (RuntimeException e) {
                reportError("Error parsing macro: " + e.getMessage(), extension);
                
                // Print the lines for debugging
                extension.getHost().println("Macro content:");
                for (int i = 0; i < normalizedLines.size(); i++) {
                    extension.getHost().println(i + ": " + normalizedLines.get(i));
                }
                return;
            }
            
            // Execute each processed line
            for (String line : processedLines) {
                line = line.trim();
                // Skip empty lines, comments, variable definitions and loop closing braces
                if (!line.isEmpty() && !line.startsWith("//") && 
                    !line.startsWith("var ") && !line.matches("\\s*\\}\\s*")) {
                    
                    // Execute the actual Bitwig command
                    boolean success = MacroExecutor.executeCommand(line, extension);
                    
                    if (!success && !line.trim().startsWith("//")) {
                        // Only report errors for non-comment lines
                        reportError("Failed to execute command: " + line, extension);
                    }
                }
            }
            
            PopupUtils.showPopup("Macro executed: " + macroName);
            
        } catch (IOException e) {
            reportError("Error reading macro file: " + e.getMessage(), extension);
        } catch (RuntimeException e) {
            reportError("Error processing macro: " + e.getMessage(), extension);
        }
    }
}
