package com.centomila.utils.commands.drum;

import com.bitwig.extension.controller.api.Device;
import com.centomila.BitwigBuddyExtension;
import com.centomila.Utils;
import com.centomila.utils.DrumPadUtils;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to insert a file into a drum pad.
 */
public class InsertFileInDrumPadCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }

        try {
            // Subscribe to the device and drum pads if needed
            final Device device = DrumPadUtils.subscribeToDrumPads(extension);

            String noteNameFull = params[0].trim();
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);
            String filePath = params[1].trim();
            extension.drumPadBank.getItemAt(midiNote).insertionPoint().insertFile(filePath);

            // Unsubscribe from the device and drum pads if needed
            DrumPadUtils.unsubscribeFromDrumPads(extension, device);

            extension.getHost().println("Inserted file into drum pad: " + noteNameFull + " with file: " + filePath);
        } catch (Exception e) {
            reportError("Failed to insert file in drum pad: " + e.getMessage(), extension);
        }
    }
}
