package com.centomila.utils;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.DrumPad;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.EnumValue;
import com.centomila.BitwigBuddyExtension;
import com.centomila.NoteDestinationSettings;

public class DeviceMatcherDrumMachine {
    public static void initializeDeviceMatcherDM(BitwigBuddyExtension extension, ControllerHost host) {
        // Create a device matcher specifically for Bitwig's Drum Machine
        final DeviceMatcher deviceMatcher = host
                .createBitwigDeviceMatcher(ReturnBitwigDeviceUUID.getDeviceUUID("Drum Machine"));
        
        // Set the device matcher on the device bank
        extension.deviceBank.setDeviceMatcher(deviceMatcher);
        
        // Get the first found drum machine
        final Device device = extension.deviceBank.getDevice(0);
        device.exists().markInterested();
        device.name().markInterested();
        
        // Now create the drum pad bank from the found drum machine
        extension.drumPadBank = device.createDrumPadBank(128);
        
        // Mark drum pad bank properties as interested (moved from BitwigBuddyExtension.java)
        extension.drumPadBank.scrollPosition().markInterested();
        extension.drumPadBank.itemCount().markInterested();
        extension.drumPadBank.cursorIndex().markInterested();
        extension.drumPadBank.exists().markInterested();
        
        // Mark all drum pads as interested
        for (int j = 0; j < extension.drumPadBank.getSizeOfBank(); j++) {
            DrumPad drumPad = extension.drumPadBank.getItemAt(j);
            drumPad.name().markInterested();
            drumPad.exists().markInterested();
            drumPad.color().markInterested();
            drumPad.solo().markInterested();
            drumPad.mute().markInterested();
            drumPad.volume().markInterested();
            drumPad.pan().markInterested();
        }
        
        // Add an observer to handle when the device no longer exists or changes
        device.exists().addValueObserver(exists -> {
            if (!exists) {
                // Try to find another drum machine in the device chain
                scanForDrumMachine(extension, host);
            }
        });

        for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
            DrumPad drumPad = extension.drumPadBank.getItemAt(i);
            drumPad.name().markInterested();
            drumPad.playingNotes().markInterested();
            
            final int drumPadIndex = i;
            // popup the selected drumpad
            drumPad.addIsSelectedInEditorObserver((isSelected) -> {
                if (isSelected) {
                    PopupUtils.showPopup("Drum Pad " + (drumPadIndex) + " selected! - " + drumPad.name().get());
                    String drumPadNote = NoteDestinationSettings.getNoteNameFromKey(drumPadIndex);
                    NoteDestinationSettings.setNoteAndOctaveFromString(drumPadNote);
                }
            });
        }

        ((EnumValue) NoteDestinationSettings.learnNoteSetting).addValueObserver(value -> {
            if (value.equals(NoteDestinationSettings.LEARN_NOTE_OPTIONS[2])) {
                device.subscribe();
                extension.drumPadBank.scrollPosition().set(0);
            } else {
                if (device.isSubscribed()) {
                    device.unsubscribe();
                }
            }
            // repeat for the drumpads
            for (int i = 0; i < extension.drumPadBank.getSizeOfBank(); i++) {
                DrumPad drumPad = extension.drumPadBank.getItemAt(i);
                if (value.equals(NoteDestinationSettings.LEARN_NOTE_OPTIONS[2])) {
                    drumPad.subscribe();
                } else {
                    if (drumPad.isSubscribed()) {
                        drumPad.unsubscribe();
                    }
                }
            }
        });
    }
    
    /**
     * Scans the device chain for a drum machine and updates the drum pad bank accordingly
     */
    private static void scanForDrumMachine(BitwigBuddyExtension extension, ControllerHost host) {
        // Show a notification that we're looking for a drum machine
        PopupUtils.showPopup("Looking for Drum Machine...");
        
        // We need to change the device matcher to find all devices first
        extension.deviceBank.setDeviceMatcher(null);
        
        // Now scan through all devices to find a drum machine
        for (int i = 0; i < extension.deviceBank.getSizeOfBank(); i++) {
            Device device = extension.deviceBank.getItemAt(i);
            // Check if this is a drum machine (you may need to adjust this check)
            if (isDrumMachine(device)) {
                // We found a drum machine, update the drum pad bank
                extension.drumPadBank = device.createDrumPadBank(128);
                extension.drumPadBank.scrollPosition().markInterested();
                extension.drumPadBank.itemCount().markInterested();
                extension.drumPadBank.cursorIndex().markInterested();
                extension.drumPadBank.exists().markInterested();
                
                // Mark all the drum pads as interested
                for (int j = 0; j < extension.drumPadBank.getSizeOfBank(); j++) {
                    DrumPad drumPad = extension.drumPadBank.getItemAt(j);
                    drumPad.name().markInterested();
                    drumPad.exists().markInterested();
                    drumPad.color().markInterested();
                    drumPad.solo().markInterested();
                    drumPad.mute().markInterested();
                    drumPad.volume().markInterested();
                    drumPad.pan().markInterested();
                }
                
                PopupUtils.showPopup("Found Drum Machine: " + device.name().get());
                return;
            }
        }
        
        // If we get here, we didn't find a drum machine
        PopupUtils.showPopup("No Drum Machine found in current track");
    }
    
    /**
     * Check if a device is a drum machine
     */
    private static boolean isDrumMachine(Device device) {
        // This is a simple implementation - in reality, you might need to check
        // device.name() or other properties
        try {
            // If creating a drum pad bank throws an exception, it's not a drum machine
            DrumPadBank testBank = device.createDrumPadBank(1);
            return testBank != null;
        } catch (Exception e) {
            return false;
        }
    }
}
