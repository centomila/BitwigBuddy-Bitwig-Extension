package com.centomila.utils;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DrumPad;
import com.centomila.BitwigBuddyExtension;
import com.centomila.NoteDestinationSettings;

/**
 * Utility class for drum pad operations.
 */
public class DrumPadUtils {

    /**
     * Subscribes to drum pads if they are not already subscribed.
     * 
     * @param extension The BitwigBuddy extension instance
     * @return The device that was subscribed to
     */
    public static Device subscribeToDrumPads(BitwigBuddyExtension extension) {
        Device device = extension.deviceBank.getDevice(0);
        
        if (!(NoteDestinationSettings.getLearnNoteSelectorAsString()).equals("DM")) {
            // Subscribe to the device and drum pads
            device.subscribe();
            extension.drumPadBank.scrollPosition().set(0);

            for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                drumPad.subscribe();
            }
        }
        
        return device;
    }
    
    /**
     * Unsubscribes from drum pads if they were temporarily subscribed.
     * 
     * @param extension The BitwigBuddy extension instance
     * @param device The device to unsubscribe from
     */
    public static void unsubscribeFromDrumPads(BitwigBuddyExtension extension, Device device) {
        if (!(NoteDestinationSettings.getLearnNoteSelectorAsString()).equals("DM")) {
            for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                drumPad.unsubscribe();
            }
            device.unsubscribe();
        }
    }
}