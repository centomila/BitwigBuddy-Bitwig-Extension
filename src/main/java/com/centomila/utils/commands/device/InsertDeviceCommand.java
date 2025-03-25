package com.centomila.utils.commands.device;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.centomila.utils.ReturnBitwigDeviceUUID;

import java.util.UUID;
import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Command to insert a Bitwig device in the selected track.
 */
public class InsertDeviceCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }
        
        try {
            int currentTrack = extension.trackBank.cursorIndex().getAsInt();
            if (currentTrack < 0) {
                extension.getHost().println("No track selected, using first track (index 0)");
                currentTrack = 0;
            }
            
            UUID deviceUUID = ReturnBitwigDeviceUUID.getDeviceUUID(params[0]);
            if (deviceUUID != null) {
                extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint()
                        .insertBitwigDevice(deviceUUID);
            } else {
                extension.getHost().println("Device not found: " + params[0]);
                showPopup("Device not found: " + params[0]);
            }
        } catch (Exception e) {
            reportError("Error inserting device: " + e.getMessage(), extension);
        }
    }
}
