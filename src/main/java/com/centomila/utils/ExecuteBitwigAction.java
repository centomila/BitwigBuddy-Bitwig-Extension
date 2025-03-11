package com.centomila.utils;

import static com.centomila.utils.ReturnBitwigDeviceUUID.getDeviceUUID;

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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ColorValue;

public class ExecuteBitwigAction {

    /**
     * @param actionId
     * @param extension
     */
    public static void executeBitwigAction(String actionId, BitwigBuddyExtension extension) {
        ControllerHost host = extension.getHost();
        host.println("Executing Bitwig action: " + actionId);
        // strip the bb: prefix - only take what's after the first colon
        actionId = actionId.substring(actionId.indexOf(":") + 1).trim();

        // If this line is a comment, show it and skip
        if (actionId.startsWith("//")) {
            host.println("Skipping comment line: " + actionId);
            return;
        }

        String[] params;
        // action id could be like bb:actionId(param1, param2). Parameters are separated
        // by comma. Parameters can contain any characters including parentheses and
        // colons.
        if (actionId.contains("(")) {
            int start = actionId.indexOf("(");
            int end = actionId.lastIndexOf(")");
            // Ensure we have both opening and closing parentheses
            if (end > start) {
                String paramsStr = actionId.substring(start + 1, end);
                actionId = actionId.substring(0, start).trim();

                // Detect bracketed arrays (rough approach)
                // Replace bracketed parts with placeholders to avoid splitting them by comma
                // and store them in an array for later processing
                List<String> bracketed = new ArrayList<>();
                Pattern bracketPattern = Pattern.compile("\\[.*?\\]");
                Matcher m = bracketPattern.matcher(paramsStr);
                int placeholderCount = 0;
                while (m.find()) {
                    bracketed.add(m.group());
                    paramsStr = m.replaceFirst("__ARRAY_PLACEHOLDER_" + placeholderCount + "__");
                    m = bracketPattern.matcher(paramsStr);
                    placeholderCount++;
                }

                // Split by commas outside quotes
                String[] rawParams = paramsStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                for (int i = 0; i < rawParams.length; i++) {
                    rawParams[i] = rawParams[i].trim();
                    // Restore bracketed parts for array-like params
                    for (int j = 0; j < bracketed.size(); j++) {
                        rawParams[i] = rawParams[i].replace(
                                "__ARRAY_PLACEHOLDER_" + j + "__",
                                bracketed.get(j));
                    }
                    // Remove surrounding quotes, if present
                    if (rawParams[i].startsWith("\"") && rawParams[i].endsWith("\"") && rawParams[i].length() > 1) {
                        rawParams[i] = rawParams[i].substring(1, rawParams[i].length() - 1);
                    }
                }
                params = rawParams;
            } else {
                params = new String[0];
            }
        } else {
            params = new String[0];
        }

        host.println("Executing Bitwig action: " + actionId);

        handleAction(actionId, params, extension);
    }

    // Extracted method:
    private static void handleAction(String actionId, String[] params, BitwigBuddyExtension extension) {
        int currentTrack = getCurrentTrackIndex(extension);

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
            case "Clip Loop Off":
                extension.getLauncherOrArrangerAsClip().isLoopEnabled().set(false);
                break;
            case "Clip Loop On":
                extension.getLauncherOrArrangerAsClip().isLoopEnabled().set(true);
                break;
            case "Clip Accent":
                extension.getLauncherOrArrangerAsClip().getAccent().setRaw(Double.parseDouble(params[0]));
                break;
            case "Insert File":
                // Create empty clip first
                int slotIndexInsertFile = Integer.parseInt(params[0].trim());
                extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().createEmptyClip(slotIndexInsertFile,
                        4);
                // Insert file into the clip
                extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().getItemAt(slotIndexInsertFile)
                        .replaceInsertionPoint().insertFile(params[1]);
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

                if (slotIndex >= 0) {
                    extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().createEmptyClip(slotIndex,
                            clipLength);
                    extension.getHost().println("Created empty clip with length: " + clipLength);
                } else {
                    extension.getHost().println("No clip slot selected. Please select a clip slot first.");
                }
                break;
            case "Track Color":
                String trackColorStr = params[0].trim();
                Color trackColor = Color.fromHex(trackColorStr);

                extension.trackBank.getItemAt(currentTrack).color().set(trackColor);
                break;
            case "Track Rename":
                String trackName = params[0].trim();

                extension.trackBank.getItemAt(currentTrack).name().set(trackName);
                break;
            case "Track Select":
                int trackIndex = Integer.parseInt(params[0].trim()) - 1;
                extension.trackBank.getItemAt(trackIndex).selectInMixer();
                extension.trackBank.getItemAt(trackIndex).makeVisibleInArranger();
                extension.trackBank.getItemAt(trackIndex).makeVisibleInMixer();
                break;
            case "Insert Device":
                UUID deviceUUID = getDeviceUUID(params[0]);
                if (deviceUUID != null) {
                    extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint()
                            .insertBitwigDevice(deviceUUID);
                } else {
                    extension.getHost().println("Device not found: " + params[0]);
                    showPopup("Device not found: " + params[0]);
                }
                break;
            case "Insert VST3":
            String VST3StringID = ReturnVST3StringID.getVST3StringID(params[0]);
            showPopup(params[0] + " - " + VST3StringID);
            if (VST3StringID != null) {
                extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint().insertVST3Device(VST3StringID);
            } else {
                extension.getHost().println("VST3 not found: " + params[0] + " - " + VST3StringID);
                showPopup(VST3StringID + " not found: " + params[0] + " - " + VST3StringID);
            }
                break;


            case "Arranger Loop Start":
                // Usage: Arranger Loop Start (loopStart) - E.g. Arranger Loop Start (2.0)
                extension.transport.arrangerLoopStart().set(Double.parseDouble(params[0]));
                break;
            case "Arranger Loop End":
                // Usage: Arranger Loop End (loopEnd) - E.g. Arranger Loop End (4.0)
                extension.transport.arrangerLoopDuration().set(Double.parseDouble(params[0]));
                break;
            case "Time Signature":
                // Usage: Time Signature (timeSignature) - E.g. Time Signature (3/4)
                String timeSign = params[0].trim();

                extension.transport.timeSignature().set(timeSign);
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
            case "Message":
                if (params.length > 0) {
                    showPopup(params[0]);
                }
                break;
        }
    }

    private static int getCurrentTrackIndex(BitwigBuddyExtension extension) {
        int trackIndex = extension.trackBank.cursorIndex().getAsInt();
        if (trackIndex < 0) {
            extension.getHost().println("No track selected, using first track (index 0)");
            trackIndex = 0;
        }
        return trackIndex;
    }

}
