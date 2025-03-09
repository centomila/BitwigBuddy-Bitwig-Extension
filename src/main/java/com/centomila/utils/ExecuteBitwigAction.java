package com.centomila.utils;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.SettableColorValue;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;

import static com.centomila.utils.PopupUtils.showPopup;

import java.util.UUID;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ColorValue;

public class ExecuteBitwigAction {

    public static void executeBitwigAction(String actionId, BitwigBuddyExtension extension) {
        ControllerHost host = extension.getHost();
        host.println("Executing Bitwig action: " + actionId);
        // strip the bb: prefix - only take what's after the first colon
        actionId = actionId.substring(actionId.indexOf(":") + 1).trim();
        String[] params;
        // action id could be like bb:actionId(param1, param2). Parameters are separated
        // by comma. Parameters can contain any characters including parentheses and colons.
        if (actionId.contains("(")) {
            int start = actionId.indexOf("(");
            int end = actionId.lastIndexOf(")");
            // Ensure we have both opening and closing parentheses
            if (end > start) {
            String paramsStr = actionId.substring(start + 1, end);
            actionId = actionId.substring(0, start).trim();
            // Split by comma but not if inside quotes
            params = paramsStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            // Trim each parameter
            for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim();
            }
            } else {
            params = new String[0];
            }
        } else {
            params = new String[0];
        }

        host.println("Executing Bitwig action: " + actionId);
        // switch case actionId. Case 1 starts with NewCueMarker
        switch (actionId) {
            case "Bpm":
                if (params.length == 1) {
                    int bpm = Integer.parseInt(params[0].trim());
                    extension.transport.tempo().setRaw(bpm);
                }
                break;
            case "CueMarkerName":
                if (params.length == 2) {
                    int itemNumber = Integer.parseInt(params[0].trim()) - 1;
                    String name = params[1].trim();
                    CueMarker cueMarker = extension.cueMarkerBank.getItemAt(itemNumber);
                    cueMarker.name().set(name);

                }
                break;
            case "DeleteAllCueMarkers":
                for (int pass = 0; pass < 4; pass++) {
                    for (int i = 0; i < 128; i++) {
                        CueMarker cueMarker = extension.cueMarkerBank.getItemAt(i);
                        if (cueMarker.exists().get()) {
                            cueMarker.deleteObject();
                        }
                    }
                }
                break;
            case "Left":
                extension.getApplication().arrowKeyLeft();
                break;
            case "Right":
                extension.getApplication().arrowKeyRight();
                break;
            case "Up":
                extension.getApplication().arrowKeyUp();
                break;
            case "Down":
                extension.getApplication().arrowKeyDown();
                break;
            case "Enter":
                extension.getApplication().enter();
                break;
            case "Escape":
                extension.getApplication().escape();
                break;
            case "Copy":
                extension.getApplication().copy();
                break;
            case "Paste":
                extension.getApplication().paste();
                break;
            case "Cut":
                extension.getApplication().cut();
                break;
            case "Undo":
                extension.getApplication().undo();
                break;
            case "Redo":
                extension.getApplication().redo();
                break;
            case "Duplicate":
                extension.getApplication().duplicate();
                break;
            case "Select All":
                extension.getApplication().selectAll();
                break;
            case "Select None":
                extension.getApplication().selectNone();
                break;
            case "Select First":
                extension.getApplication().selectFirst();
                break;
            case "Select Last":
                extension.getApplication().selectLast();
                break;
            case "Select Next":
                extension.getApplication().selectNext();
                break;
            case "Select Previous":
                extension.getApplication().selectPrevious();
                break;
            case "Clip Select":
                extension.getLauncherOrArrangerAsClip().clipLauncherSlot().select();
                break;
            case "Clip Duplicate":
                extension.getLauncherOrArrangerAsClip().clipLauncherSlot().duplicateClip();
                break;
            case "Project Name":
                extension.getApplication().projectName();
                showPopup(extension.getApplication().projectName().toString());
                break;
            case "Rename":
                extension.getApplication().rename();
                break;
            case "Clip Delete":
                extension.getLauncherOrArrangerAsClip().clipLauncherSlot().deleteObject();
                break;
            case "Clip Rename":
                extension.getLauncherOrArrangerAsClip().setName(params[0]);
                break;
            case "Clip Color":
                String colorStr = params[0].trim();
                Color color = Color.fromHex(colorStr);
                extension.getLauncherOrArrangerAsClip().color().set(color);
                break;
            case "Clip Create":
            // Use Clip Create (slot, length) to create a clip in the selected slot with the specified length
                int clipLength = 4; // Default length
                if (params.length > 1) {
                    try {
                        clipLength = Integer.parseInt(params[1].trim());
                    } catch (NumberFormatException e) {
                        extension.getHost().println("Invalid clip length parameter, using default of 4 beats");
                    }
                }
                
                // Get the currently selected slot in the clip launcher
                int slotIndex = Integer.parseInt(params[0].trim()) - 1;
                
                // Get the current track - first try to get selected track, fall back to first track if none selected
                int currentTrack = extension.trackBank.cursorIndex().get();
                if (currentTrack < 0) {
                    // No track selected, use track 0 as fallback
                    currentTrack = 0;
                    extension.getHost().println("No track selected, using first track (index 0)");
                }
                
                host.println("Creating clip in slot: " + slotIndex + " with length: " + clipLength + " in track: " + currentTrack);
                
                if (slotIndex >= 0) {
                    extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().createEmptyClip(slotIndex, clipLength);
                    extension.getHost().println("Created empty clip with length: " + clipLength);
                } else {
                    extension.getHost().println("No clip slot selected. Please select a clip slot first.");
                }
                break;
            case "Instrument Track Create":
                extension.getApplication().createInstrumentTrack(128);
                break;
            case "Audio Track Create":
                extension.getApplication().createAudioTrack(128);
                break;
            case "Fx Track Create":
                extension.getApplication().createEffectTrack(128);
                break;
            case "Track Color":
                String trackColorStr = params[0].trim();
                Color trackColor = Color.fromHex(trackColorStr);
                // get the current track
                int currentTrackIndex = extension.trackBank.cursorIndex().get();
                if (currentTrackIndex < 0) {
                    // No track selected, use track 0 as fallback
                    currentTrackIndex = 0;
                    extension.getHost().println("No track selected, using first track (index 0)");
                }
                extension.trackBank.getItemAt(currentTrackIndex).color().set(trackColor);
                break;
            case "Arranger Loop Start":
                extension.transport.arrangerLoopStart().set(Double.parseDouble(params[0]));
                break;
            case "Arranger Loop End":
                extension.transport.arrangerLoopDuration().set(Double.parseDouble(params[0]));
                break;
            case "Wait":
                int waitTime = 250; // Default wait time in ms
                if (params.length > 0) {
                    try {
                        waitTime = Integer.parseInt(params[0]);
                    } catch (NumberFormatException e) {
                        // Use default if parameter is not a valid number
                    }
                }
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
        }

    }
}
