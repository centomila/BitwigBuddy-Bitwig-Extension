package com.centomila.utils.commands.drum;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DrumPad;
import com.centomila.BitwigBuddyExtension;
import com.centomila.NoteDestinationSettings;
import com.centomila.Utils;
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
            // Temporarily subscribe to the device and drum pads if DM is not selected
            final Device device = extension.deviceBank.getDevice(0);

            if (!(NoteDestinationSettings.getLearnNoteSelectorAsString()).equals("DM")) {

                // Subscribe to the device and drum pads
                device.subscribe();
                extension.drumPadBank.scrollPosition().set(0);

                for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                    DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                    drumPad.subscribe();
                }
            }
            String noteNameFull = params[0].trim();
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);
            extension.drumPadBank.getItemAt(midiNote).selectInEditor();

            // Unsubscribe from the device and drum pads
            if (!(NoteDestinationSettings.getLearnNoteSelectorAsString()).equals("DM")) {
                for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                    DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                    drumPad.unsubscribe();
                }
                device.unsubscribe();
            }
        } catch (Exception e) {
            reportError("Failed to select drum pad: " + e.getMessage(), extension);
        }
    }
}
