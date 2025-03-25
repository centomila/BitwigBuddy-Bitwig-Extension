package com.centomila.utils.commands.drum;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.centomila.Utils;

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
        } catch (Exception e) {
            reportError("Error inserting device in drum pad: " + e.getMessage(), extension);
        }
    }
    
    private UUID getDeviceUUID(String deviceName) {
        // Reuse existing method from ReturnBitwigDeviceUUID or use a simpler approach
        return com.centomila.utils.ReturnBitwigDeviceUUID.getDeviceUUID(deviceName);
    }
}
