package com.centomila.utils.commands.device;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;
import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Command to insert a file (preset or sample) into a track.
 */
public class InsertFileCommand extends BaseCommand {
    
    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 2, extension)) {
            return;
        }
        
        try {
            int currentTrack = extension.trackBank.cursorIndex().getAsInt();
            if (currentTrack < 0) {
                extension.getHost().println("No track selected, using first track (index 0)");
                currentTrack = 0;
            }
            
            int slotIndexInsertFile = Integer.parseInt(params[0].trim());
            String filePath = params[1].trim();
            
            if (filePath.endsWith(".bwpreset")) {
                handlePresetFile(extension, currentTrack, slotIndexInsertFile, filePath);
            } else {
                handleSampleFile(extension, currentTrack, slotIndexInsertFile, filePath);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid slot index: " + params[0], extension);
        } catch (Exception e) {
            reportError("Error inserting file: " + e.getMessage(), extension);
        }
    }
    
    private void handlePresetFile(BitwigBuddyExtension extension, int currentTrack, int slotIndex, String filePath) {
        if (slotIndex == 0) {
            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().getItemAt(0).replaceInsertionPoint()
                    .insertFile(filePath);
        } else if (slotIndex > 0) {
            extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint().insertFile(filePath);
        } else if (slotIndex < 0) {
            extension.trackBank.getItemAt(currentTrack).startOfDeviceChainInsertionPoint().insertFile(filePath);
        }
    }
    
    private void handleSampleFile(BitwigBuddyExtension extension, int currentTrack, int slotIndex, String filePath) {
        if (ModeSelectSettings.getCurrentLauncherArrangerToggleString().equals("Launcher")) {
            slotIndex = slotIndex - 1;
            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank()
                    .createEmptyClip(slotIndex, 4);
            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().getItemAt(slotIndex)
                    .replaceInsertionPoint().insertFile(filePath);
        } else if (ModeSelectSettings.getCurrentLauncherArrangerToggleString().equals("Arranger")) {
            showPopup("I can't insert files in the arranger. Launcher only.");
        }
    }
}
