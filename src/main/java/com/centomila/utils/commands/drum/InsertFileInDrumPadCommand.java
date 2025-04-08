package com.centomila.utils.commands.drum;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DrumPad;
import com.centomila.BitwigBuddyExtension;
import com.centomila.NoteDestinationSettings;
import com.centomila.Utils;
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
            String filePath = params[1].trim();
            extension.drumPadBank.getItemAt(midiNote).insertionPoint().insertFile(filePath);

            // Unsubscribe from the device and drum pads
            if (!(NoteDestinationSettings.getLearnNoteSelectorAsString()).equals("DM")) {
                for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                    DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                    drumPad.unsubscribe();
                }
                device.unsubscribe();
            }

            extension.getHost().println("Inserted file into drum pad: " + noteNameFull + " with file: " + filePath);
        } catch (Exception e) {
            reportError("Failed to insert file in drum pad: " + e.getMessage(), extension);
        }
    }
}
