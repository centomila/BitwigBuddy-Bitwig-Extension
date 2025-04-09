package com.centomila.utils.commands.drum;

import com.bitwig.extension.controller.api.Device;
import com.centomila.BitwigBuddyExtension;
import com.centomila.Utils;
import com.centomila.utils.DrumPadUtils;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to select a drum pad by note name.
 */
public class SelectDrumPadCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }

        try {
            // Subscribe to the device and drum pads if needed
            final Device device = DrumPadUtils.subscribeToDrumPads(extension);
            
            String noteNameFull = params[0].trim();
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);
            extension.drumPadBank.getItemAt(midiNote).selectInEditor();

            // Unsubscribe from the device and drum pads if needed
            DrumPadUtils.unsubscribeFromDrumPads(extension, device);
        } catch (Exception e) {
            reportError("Failed to select drum pad: " + e.getMessage(), extension);
        }
    }
}
