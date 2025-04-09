package com.centomila.utils.commands.drum;

import com.centomila.BitwigBuddyExtension;
import com.centomila.Utils;
import com.centomila.utils.DrumPadUtils;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Device;

/**
 * Command to create and configure a drum pad.
 * `Drum Pad Insert Empty("C#2", "Name", "#D00000)`
 */
public class CreateDrumPadCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 3, extension)) {
            return;
        }

        try {
            // Subscribe to the device and drum pads if needed
            final Device device = DrumPadUtils.subscribeToDrumPads(extension);

            // Parse parameters
            String noteNameFull = params[0].trim();
            String drumBankName = params[1].trim();
            String drumBankColor = params[2].trim();

            // Convert note name to MIDI note number
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);

            // Create the drum pad
            extension.drumPadBank.getItemAt(midiNote).insertionPoint().browse();
            extension.application.getAction("Dialog: OK").invoke();

            // Set name and color
            extension.drumPadBank.getItemAt(midiNote).name().set(drumBankName);
            Color color = Color.fromHex(drumBankColor);
            extension.drumPadBank.getItemAt(midiNote).color().set(color);

            // Unsubscribe from the device and drum pads if needed
            DrumPadUtils.unsubscribeFromDrumPads(extension, device);
        } catch (Exception e) {
            reportError("Failed to create drum pad: " + e.getMessage(), extension);
        }
    }
}
