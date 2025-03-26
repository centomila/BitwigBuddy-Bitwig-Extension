package com.centomila.macro.commands;

import com.centomila.BitwigBuddyExtension;
import com.centomila.macro.MacroExecutor;
import com.centomila.macro.processor.MacroProcessor;
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
            
            // Process loops and variables
            MacroProcessor macroProcessor = new MacroProcessor();
            
            // Enable debug if a debug parameter is passed
            if (params.length > 1 && params[1].equalsIgnoreCase("debug")) {
                macroProcessor.setDebug(true);
                extension.getHost().println("Debug mode enabled for macro processing");
            }
            
            List<String> processedLines;
            try {
                processedLines = macroProcessor.processCommands(macroLines);
            } catch (RuntimeException e) {
                reportError("Error parsing macro: " + e.getMessage(), extension);
                
                // Print the lines for debugging
                extension.getHost().println("Macro content:");
                for (int i = 0; i < macroLines.size(); i++) {
                    extension.getHost().println(i + ": " + macroLines.get(i));
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
