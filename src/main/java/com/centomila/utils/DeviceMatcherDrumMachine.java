package com.centomila.utils;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.DrumPad;
import com.bitwig.extension.controller.api.EnumValue;
import com.centomila.BitwigBuddyExtension;
import com.centomila.NoteDestinationSettings;

public class DeviceMatcherDrumMachine {
    public static void initializeDeviceMatcherDM(BitwigBuddyExtension extension, ControllerHost host) {
        final DeviceMatcher deviceMatcher = host
                .createBitwigDeviceMatcher(ReturnBitwigDeviceUUID.getDeviceUUID("Drum Machine"));
        extension.deviceBank.setDeviceMatcher(deviceMatcher);
        final Device device = extension.deviceBank.getDevice(0);
        device.exists().markInterested();
        device.name().markInterested();
        

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
}
