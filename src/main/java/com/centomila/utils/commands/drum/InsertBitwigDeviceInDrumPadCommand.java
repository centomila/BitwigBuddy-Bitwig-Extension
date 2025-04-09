package com.centomila.utils.commands.drum;

import com.bitwig.extension.controller.api.Device;
import com.centomila.BitwigBuddyExtension;
import com.centomila.Utils;
import com.centomila.utils.commands.BaseCommand;
import com.centomila.utils.DrumPadUtils;

import java.util.UUID;

import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Command to insert a Bitwig device in a drum pad.
 */
public class InsertBitwigDeviceInDrumPadCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }

        try {
            int bankSize = extension.deviceBank.getSizeOfBank();
            if (bankSize == 0) {
                extension.getHost().println("No devices found in the bank.");
                showPopup("No devices found in the bank.");
                return;
            }
            
            // Subscribe to the device and drum pads if needed
            final Device device = DrumPadUtils.subscribeToDrumPads(extension);

            String noteNameFull = params[0].trim();
            int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
            extension.drumPadBank.scrollPosition().set(0);

            UUID deviceUUID = getDeviceUUID(params[1].trim());
            if (deviceUUID != null) {
                extension.drumPadBank.getItemAt(midiNote).insertionPoint().insertBitwigDevice(deviceUUID);
            } else {
                extension.getHost().println("Device not found: " + params[1]);
                showPopup("Device not found: " + params[1]);
            }

            // Unsubscribe from the device and drum pads if needed
            DrumPadUtils.unsubscribeFromDrumPads(extension, device);
        } catch (Exception e) {
            reportError("Error inserting device in drum pad: " + e.getMessage(), extension);
        }
    }

    private UUID getDeviceUUID(String deviceName) {
        // Reuse existing method from ReturnBitwigDeviceUUID or use a simpler approach
        return com.centomila.utils.ReturnBitwigDeviceUUID.getDeviceUUID(deviceName);
    }
}
