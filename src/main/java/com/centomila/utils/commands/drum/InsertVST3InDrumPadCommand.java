package com.centomila.utils.commands.drum;

import com.centomila.BitwigBuddyExtension;
import com.centomila.NoteDestinationSettings;
import com.centomila.Utils;
import com.centomila.utils.ReturnVST3StringID;
import com.centomila.utils.commands.BaseCommand;
import static com.centomila.utils.PopupUtils.showPopup;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DrumPad;

/**
 * Command to insert a VST3 plugin into a drum pad.
 */
public class InsertVST3InDrumPadCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }

        try {

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
            String vst3Name = params[1].trim();
            String vst3StringID = ReturnVST3StringID.getVST3StringID(vst3Name);
            if (vst3StringID != null) {
                extension.drumPadBank.getItemAt(midiNote).insertionPoint().insertVST3Device(vst3StringID);
                extension.getHost().println("Inserted VST3 into drum pad: " + noteNameFull + " with VST3: " + vst3Name);
            } else {
                extension.getHost().println("VST3 not found: " + vst3Name);
                showPopup("VST3 not found: " + vst3Name);
            }

            // Unsubscribe from the device and drum pads
            if (!(NoteDestinationSettings.getLearnNoteSelectorAsString()).equals("DM")) {
                for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                    DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                    drumPad.unsubscribe();
                }
                device.unsubscribe();
            }
        } catch (Exception e) {
            reportError("Failed to insert VST3 in drum pad: " + e.getMessage(), extension);
        }
    }
}
