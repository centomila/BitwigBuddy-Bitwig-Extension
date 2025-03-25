package com.centomila.utils.commands.utility;

import com.centomila.BitwigBuddyExtension;
import com.centomila.MacroActionSettings;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to execute another macro by name.
 */
public class MacroCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        String macroTitle = params[0];
        MacroActionSettings.MacroBB[] macros = MacroActionSettings.getMacros();
        
        for (MacroActionSettings.MacroBB macro : macros) {
            if (macro.getTitle().equals(macroTitle)) {
                synchronized (MacroActionSettings.getExecutionLock()) {
                    MacroActionSettings.resetExecutionState();
                    MacroActionSettings.executeMacroFromAction(macro, extension);
                    return;
                }
            }
        }
        
        reportError("Macro not found: " + macroTitle, extension);
    }
}
