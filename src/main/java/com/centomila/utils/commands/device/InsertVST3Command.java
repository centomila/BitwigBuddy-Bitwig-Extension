package com.centomila.utils.commands.device;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.centomila.utils.ReturnVST3StringID;
import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Command to insert a VST3 plugin in the selected track.
 */
public class InsertVST3Command extends BaseCommand {
    
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
            
            String vst3Name = params[0].trim();
            String VST3StringID = ReturnVST3StringID.getVST3StringID(vst3Name);
            showPopup(vst3Name + " - " + VST3StringID);
            
            if (VST3StringID != null) {
                extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint()
                        .insertVST3Device(VST3StringID);
            } else {
                extension.getHost().println("VST3 not found: " + vst3Name + " - " + VST3StringID);
                showPopup(VST3StringID + " not found: " + vst3Name + " - " + VST3StringID);
            }
        } catch (Exception e) {
            reportError("Error inserting VST3 device: " + e.getMessage(), extension);
        }
    }
}
