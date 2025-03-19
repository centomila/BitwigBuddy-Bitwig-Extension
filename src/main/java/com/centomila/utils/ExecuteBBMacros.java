package com.centomila.utils;

import static com.centomila.utils.ReturnBitwigDeviceUUID.getDeviceUUID;
import static com.centomila.utils.PopupUtils.showPopup;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.ModeSelectSettings;
import com.centomila.NoteDestinationSettings;
import com.centomila.Utils;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.api.Color;
import com.centomila.MacroActionSettings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecuteBBMacros {

    public static boolean executeBitwigAction(String actionId, BitwigBuddyExtension extension) {
        ControllerHost host = extension.getHost();
        host.println("Executing Bitwig action: " + actionId);

        // If this line is a comment, show it and skip
        if (actionId.startsWith("//")) {
            host.println("Skipping comment line: " + actionId);
            return true;
        }

        String[] params;
        // Parse parameters from action string
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

        try {
            handleAction(actionId, params, extension);
            return true; // Action was handled by our switch statement
        } catch (IllegalArgumentException e) {
            // Action wasn't found in our switch statement
            return false;
        }
    }

    private static void handleAction(String actionId, String[] params, BitwigBuddyExtension extension) {
        int currentTrack = getCurrentTrackIndex(extension);

        switch (actionId) {
            case "Bpm":
                handleBpm(params, extension);
                break;
            case "CueMarkerName":
                handleCueMarkerName(params, extension);
                break;
            case "DeleteAllCueMarkers":
                handleDeleteAllCueMarkers(extension);
                break;
            case "Left":
                handleLeft(extension);
                break;
            case "Right":
                handleRight(extension);
                break;
            case "Up":
                handleUp(extension);
                break;
            case "Down":
                handleDown(extension);
                break;
            case "Enter":
                handleEnter(extension);
                break;
            case "Escape":
                handleEscape(extension);
                break;
            case "Clip Select":
                handleClipSelect(extension);
                break;
            case "Clip Duplicate":
                handleClipDuplicate(extension);
                break;
            case "Clip Loop Off":
                handleClipLoopOff(extension);
                break;
            case "Clip Loop On":
                handleClipLoopOn(extension);
                break;
            case "Clip Accent":
                handleClipAccent(params, extension);
                break;
            case "Project Name":
                handleProjectName(extension);
                break;
            case "Clip Delete":
                handleClipDelete(extension);
                break;
            case "Clip Rename":
                handleClipRename(params, extension);
                break;
            case "Clip Color":
                handleClipColor(params, extension);
                break;
            case "Clip Create":
                handleClipCreate(params, extension, currentTrack);
                break;
            case "Step Selected Length":
                handleStepSelectedLength(params, extension);
                break;
            case "Step Selected Velocity":
                handleStepSelectedVelocity(params, extension);
                break;
            case "Step Selected Chance":
                handleStepSelectedChance(params, extension);
                break;
            case "Step Selected Transpose":
                handleStepSelectedTranspose(params, extension);
                break;
            case "Step Selected Gain":
                handleStepSelectedGain(params, extension);
                break;
            case "Step Selected Pressure":
                handleStepSelectedPressure(params, extension);
                break;
            case "Step Selected Timbre":
                handleStepSelectedTimbre(params, extension);
                break;
            case "Step Selected Pan":
                handleStepSelectedPan(params, extension);
                break;
            case "Step Selected Duration":
                handleStepSelectedDuration(params, extension);
                break;
            case "Step Selected Velocity Spread":
                handleStepSelectedVelocitySpread(params, extension);
                break;
            case "Step Selected Release Velocity":
                handleStepSelectedReleaseVelocity(params, extension);
                break;
            case "Step Selected Is Chance Enabled":
                handleStepSelectedIsChanceEnabled(params, extension);
                break;
            case "Step Selected Is Muted":
                handleStepSelectedIsMuted(params, extension);
                break;
            case "Step Selected Is Occurrence Enabled":
                handleStepSelectedIsOccurrenceEnabled(params, extension);
                break;
            case "Step Selected Is Recurrence Enabled":
                handleStepSelectedIsRecurrenceEnabled(params, extension);
                break;
            case "Step Selected Is Repeat Enabled":
                handleStepSelectedIsRepeatEnabled(params, extension);
                break;
            case "Step Selected Occurrence":
                handleStepSelectedOccurrence(params, extension);
                break;
            case "Step Selected Recurrence":
                handleStepSelectedRecurrence(params, extension);
                break;
            case "Step Selected Repeat Count":
                handleStepSelectedRepeatCount(params, extension);
                break;
            case "Step Selected Repeat Curve":
                handleStepSelectedRepeatCurve(params, extension);
                break;
            case "Step Selected Repeat Velocity Curve":
                handleStepSelectedRepeatVelocityCurve(params, extension);
                break;
            case "Step Selected Repeat Velocity End":
                handleStepSelectedRepeatVelocityEnd(params, extension);
                break;
            case "Track Color":
                handleTrackColor(params, extension, currentTrack);
                break;
            case "Track Rename":
                handleTrackRename(params, extension, currentTrack);
                break;
            case "Track Select":
                handleTrackSelect(params, extension);
                break;
            case "Insert Device":
                handleInsertDevice(params, extension, currentTrack);
                break;
            case "Insert VST3":
                handleInsertVST3(params, extension, currentTrack);
                break;
            case "Insert File":
                handleInsertFile(params, extension, currentTrack);
                break;
            case "Insert Drum Pad":
                handleCreateDrumPad(params, extension);
                break;
            case "Insert Device In Drum Pad":
            handleInsertBitwigDeviceInDrumPad(params, extension);
                break;
            case "Select Drum Pad":
                handleSelectDrumPad(params, extension);
                break;
            case "Arranger Loop Start":
                handleArrangerLoopStart(params, extension);
                break;
            case "Arranger Loop End":
                handleArrangerLoopEnd(params, extension);
                break;
            case "Time Signature":
                handleTimeSignature(params, extension);
                break;
            case "Wait":
                handleWait(params, extension);
                break;
            case "Message":
                handleMessage(params);
                break;
            case "BB Macro":
            case "Macro":
                handleMacro(params, extension);
                break;
            case "BB Arranger Mode":
                handleArrangerMode(extension);
                break;
            case "BB Launcher Mode":
                handleLauncherMode(extension);
                break;
            case "BB Toggle Launcher Arranger Mode":
                handleToggleLauncherArrangerMode(extension);
                break;
            case "BB Close Panel":
                handleCloseBBPanel(extension);
                break;
            case "Transport Position":
                handleTransportPosition(params, extension);
                break;

            default:
                throw new IllegalArgumentException("Unknown action: " + actionId);
        }
    }

    private static void handleBpm(String[] params, BitwigBuddyExtension extension) {
        if (params.length == 1) {
            int bpm = Integer.parseInt(params[0].trim());
            extension.transport.tempo().setRaw(bpm);
        }
    }

    private static void handleCueMarkerName(String[] params, BitwigBuddyExtension extension) {
        if (params.length == 2) {
            int itemNumber = Integer.parseInt(params[0].trim()) - 1;
            String name = params[1].trim();
            CueMarker cueMarker = extension.cueMarkerBank.getItemAt(itemNumber);
            cueMarker.name().set(name);
        }
    }

    private static void handleDeleteAllCueMarkers(BitwigBuddyExtension extension) {
        for (int pass = 0; pass < 16; pass++) {
            for (int i = 0; i < 128; i++) {
                CueMarker cueMarker = extension.cueMarkerBank.getItemAt(i);
                if (cueMarker.exists().get()) {
                    cueMarker.deleteObject();
                }
            }
        }
    }

    private static void handleLeft(BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyLeft();
    }

    private static void handleRight(BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyRight();
    }

    private static void handleUp(BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyUp();
    }

    private static void handleDown(BitwigBuddyExtension extension) {
        extension.getApplication().arrowKeyDown();
    }

    private static void handleEnter(BitwigBuddyExtension extension) {
        extension.getApplication().enter();
    }

    private static void handleEscape(BitwigBuddyExtension extension) {
        extension.getApplication().escape();
    }

    private static void handleClipSelect(BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().clipLauncherSlot().select();

    }

    private static void handleClipDuplicate(BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().clipLauncherSlot().duplicateClip();
        int clipPosition = extension.getLauncherOrArrangerAsClip().clipLauncherSlot().sceneIndex().get();
        extension.sceneBank.scrollIntoView(clipPosition);

    }

    private static void handleClipLoopOff(BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().isLoopEnabled().set(false);
    }

    private static void handleClipLoopOn(BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().isLoopEnabled().set(true);
    }

    private static void handleClipAccent(String[] params, BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().getAccent().setRaw(Double.parseDouble(params[0]));
    }

    private static void handleProjectName(BitwigBuddyExtension extension) {
        extension.getApplication().projectName();
        showPopup(extension.getApplication().projectName().toString());
    }

    private static void handleClipDelete(BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().clipLauncherSlot().deleteObject();
    }

    private static void handleClipRename(String[] params, BitwigBuddyExtension extension) {
        extension.getLauncherOrArrangerAsClip().setName(params[0]);
    }

    private static void handleClipColor(String[] params, BitwigBuddyExtension extension) {
        String colorStr = params[0].trim();
        Color color = Color.fromHex(colorStr);
        extension.getLauncherOrArrangerAsClip().color().set(color);
    }

    private static void handleClipCreate(String[] params, BitwigBuddyExtension extension, int currentTrack) {

        int clipLength = 4;
        if (params.length > 1) {
            try {
                clipLength = Integer.parseInt(params[1].trim());
            } catch (NumberFormatException e) {
                extension.getHost().println("Invalid clip length parameter, using default of 4 beats");
            }
        }

        int slotIndex = Integer.parseInt(params[0].trim()) - 1;
        if (slotIndex >= 0) {
            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().getItemAt(slotIndex).deleteObject();
            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank()
                    .createEmptyClip(slotIndex, clipLength);
            extension.trackBank.getItemAt(currentTrack).makeVisibleInMixer();
            extension.trackBank.getItemAt(currentTrack).makeVisibleInArranger();
            extension.getHost().println("Created empty clip with length: " + clipLength);
        } else {
            extension.getHost().println("No clip slot selected. Please select a clip slot first.");
        }
    }

    private static void handleStepSelectedLength(String[] params, BitwigBuddyExtension extension) {
        double stepLength = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setDuration(stepLength);
        }
    }

    private static void handleStepSelectedVelocity(String[] params, BitwigBuddyExtension extension) {
        double stepVelocity = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setVelocity(stepVelocity);
        }
    }

    private static void handleStepSelectedChance(String[] params, BitwigBuddyExtension extension) {
        double chance = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setChance(chance);
        }
    }

    private static void handleStepSelectedTranspose(String[] params, BitwigBuddyExtension extension) {
        int transpose = Integer.parseInt(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setTranspose(transpose);
        }
    }

    private static void handleStepSelectedGain(String[] params, BitwigBuddyExtension extension) {
        double gain = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setGain(gain);
        }
    }

    private static void handleStepSelectedPressure(String[] params, BitwigBuddyExtension extension) {
        double pressure = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setPressure(pressure);
        }
    }

    private static void handleStepSelectedTimbre(String[] params, BitwigBuddyExtension extension) {
        double timbre = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setTimbre(timbre);
        }
    }

    private static void handleStepSelectedPan(String[] params, BitwigBuddyExtension extension) {
        double pan = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setPan(pan);
        }
    }

    private static void handleStepSelectedDuration(String[] params, BitwigBuddyExtension extension) {
        double duration = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setDuration(duration);
        }
    }

    private static void handleStepSelectedVelocitySpread(String[] params, BitwigBuddyExtension extension) {
        double velocitySpread = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setVelocitySpread(velocitySpread);
        }
    }

    private static void handleStepSelectedReleaseVelocity(String[] params, BitwigBuddyExtension extension) {
        double releaseVelocity = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        extension.getHost().println("Selected notes: " + selectedNotes.size());

        for (NoteStep note : selectedNotes) {
            note.setReleaseVelocity(releaseVelocity);
        }
    }

    private static void handleStepSelectedIsChanceEnabled(String[] params, BitwigBuddyExtension extension) {
        boolean isEnabled = Boolean.parseBoolean(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setIsChanceEnabled(isEnabled);
        }
    }

    private static void handleStepSelectedIsMuted(String[] params, BitwigBuddyExtension extension) {
        boolean isMuted = Boolean.parseBoolean(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setIsMuted(isMuted);
        }
    }

    private static void handleStepSelectedIsOccurrenceEnabled(String[] params, BitwigBuddyExtension extension) {
        boolean isEnabled = Boolean.parseBoolean(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setIsOccurrenceEnabled(isEnabled);
        }
    }

    private static void handleStepSelectedIsRecurrenceEnabled(String[] params, BitwigBuddyExtension extension) {
        boolean isEnabled = Boolean.parseBoolean(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setIsRecurrenceEnabled(isEnabled);
        }
    }

    private static void handleStepSelectedIsRepeatEnabled(String[] params, BitwigBuddyExtension extension) {
        boolean isEnabled = Boolean.parseBoolean(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setIsRepeatEnabled(isEnabled);
        }
    }

    private static void handleStepSelectedOccurrence(String[] params, BitwigBuddyExtension extension) {
        NoteOccurrence condition = NoteOccurrence.valueOf(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setOccurrence(condition);
        }
    }

    private static void handleStepSelectedRecurrence(String[] params, BitwigBuddyExtension extension) {
        int length = Integer.parseInt(params[0].trim());
        int mask = Integer.parseInt(params[1].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setRecurrence(length, mask);
        }
    }

    private static void handleStepSelectedRepeatCount(String[] params, BitwigBuddyExtension extension) {
        int count = Integer.parseInt(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setRepeatCount(count);
        }
    }

    private static void handleStepSelectedRepeatCurve(String[] params, BitwigBuddyExtension extension) {
        double curve = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setRepeatCurve(curve);
        }
    }

    private static void handleStepSelectedRepeatVelocityCurve(String[] params, BitwigBuddyExtension extension) {
        double curve = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setRepeatVelocityCurve(curve);
        }
    }

    private static void handleStepSelectedRepeatVelocityEnd(String[] params, BitwigBuddyExtension extension) {
        double velocityEnd = Double.parseDouble(params[0].trim());
        List<NoteStep> selectedNotes = ClipUtils.getSelectedNotes(extension);
        for (NoteStep note : selectedNotes) {
            note.setRepeatVelocityEnd(velocityEnd);
        }
    }

    private static void handleTrackColor(String[] params, BitwigBuddyExtension extension, int currentTrack) {
        String trackColorStr = params[0].trim();
        Color trackColor = Color.fromHex(trackColorStr);
        extension.trackBank.getItemAt(currentTrack).color().set(trackColor);
    }

    private static void handleTrackRename(String[] params, BitwigBuddyExtension extension, int currentTrack) {
        String trackName = params[0].trim();
        extension.trackBank.getItemAt(currentTrack).name().set(trackName);
    }

    private static void handleTrackSelect(String[] params, BitwigBuddyExtension extension) {
        int trackIndex = Integer.parseInt(params[0].trim()) - 1;
        extension.trackBank.getItemAt(trackIndex).selectInMixer();
        extension.trackBank.getItemAt(trackIndex).makeVisibleInArranger();
        extension.trackBank.getItemAt(trackIndex).makeVisibleInMixer();
    }

    private static void handleInsertDevice(String[] params, BitwigBuddyExtension extension, int currentTrack) {
        UUID deviceUUID = getDeviceUUID(params[0]);
        if (deviceUUID != null) {
            extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint()
                    .insertBitwigDevice(deviceUUID);
        } else {
            extension.getHost().println("Device not found: " + params[0]);
            showPopup("Device not found: " + params[0]);
        }
    }

    private static void handleInsertVST3(String[] params, BitwigBuddyExtension extension, int currentTrack) {
        String VST3StringID = ReturnVST3StringID.getVST3StringID(params[0]);
        showPopup(params[0] + " - " + VST3StringID);
        if (VST3StringID != null) {
            extension.trackBank.getItemAt(currentTrack).endOfDeviceChainInsertionPoint()
                    .insertVST3Device(VST3StringID);
        } else {
            extension.getHost().println("VST3 not found: " + params[0] + " - " + VST3StringID);
            showPopup(VST3StringID + " not found: " + params[0] + " - " + VST3StringID);
        }
    }

    private static void handleInsertFile(String[] params, BitwigBuddyExtension extension, int currentTrack) {
        int slotIndexInsertFile = Integer.parseInt(params[0].trim()) - 1;
        extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank()
                .createEmptyClip(slotIndexInsertFile, 4);
        extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank()
                .getItemAt(slotIndexInsertFile).replaceInsertionPoint().insertFile(params[1]);
    }

    private static void handleArrangerLoopStart(String[] params, BitwigBuddyExtension extension) {
        extension.transport.arrangerLoopStart().set(Double.parseDouble(params[0]));
    }

    private static void handleArrangerLoopEnd(String[] params, BitwigBuddyExtension extension) {
        extension.transport.arrangerLoopDuration().set(Double.parseDouble(params[0]));
    }

    private static void handleTimeSignature(String[] params, BitwigBuddyExtension extension) {
        extension.transport.timeSignature().set(params[0].trim());
    }

    private static void handleWait(String[] params, BitwigBuddyExtension extension) {
        int waitTime = params.length > 0 ? Integer.parseInt(params[0]) : 50;
        try {
            Thread.sleep(waitTime);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void handleMessage(String[] params) {
        if (params.length > 0) {
            showPopup(params[0]);
        }
    }

    private static void handleMacro(String[] params, BitwigBuddyExtension extension) {
        if (params.length != 1) {
            extension.getHost().errorln("Macro command requires exactly one parameter (macro name)");
            return;
        }

        String macroTitle = params[0];
        MacroActionSettings.MacroBB[] macros = MacroActionSettings.getMacros();

        for (MacroActionSettings.MacroBB macro : macros) {
            if (macro.getTitle().equals(macroTitle)) {
                // Execute in a synchronized block
                synchronized (MacroActionSettings.getExecutionLock()) {
                    // Reset execution state before nested execution
                    MacroActionSettings.resetExecutionState();
                    // Execute the nested macro
                    MacroActionSettings.executeMacroFromAction(macro, extension);
                    return;
                }
            }
        }

        extension.getHost().errorln("Macro not found: " + macroTitle);
    }

    private static void handleArrangerMode(BitwigBuddyExtension extension) {
        ((SettableEnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).set("Arranger");
    }

    private static void handleLauncherMode(BitwigBuddyExtension extension) {
        ((SettableEnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).set("Launcher");
    }

    private static void handleToggleLauncherArrangerMode(BitwigBuddyExtension extension) {
        String currentMode = ((EnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).get();
        String newMode = currentMode.equals("Launcher") ? "Arranger" : "Launcher";
        ((SettableEnumValue) ModeSelectSettings.toggleLauncherArrangerSetting).set(newMode);
    }

    private static void handleTransportPosition(String[] params, BitwigBuddyExtension extension) {
        if (params.length == 1) {
            double position = Double.parseDouble(params[0].trim());
            extension.transport.setPosition(position);
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

    /**
     * @param params
     * @param extension
     */
    /**
     * Handles the creation and configuration of a drum pad in Bitwig Studio.
     *
     * @param params An array of string parameters with the following elements:
     *               - params[0]: The note name (e.g., "C3", "F#4") to identify the drum pad position
     *               - params[1]: The name to assign to the drum bank/pad
     *               - params[2]: The hexadecimal color value (e.g., "#FF0000" for red)
     * @param extension The BitwigBuddyExtension instance providing access to Bitwig's API
     */
    private static void handleCreateDrumPad(String[] params, BitwigBuddyExtension extension) {
        String noteNameFull = params[0].trim();
        // Use the new utility function to get the MIDI note number directly
        int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
        extension.drumPadBank.scrollPosition().set(0);
        // Open the browser and then escape to create an empty drum pad
        extension.drumPadBank.getItemAt(midiNote).insertionPoint().browse();
        extension.application.getAction("Dialog: OK").invoke();
        String drumBankName = params[1].trim();
        extension.drumPadBank.getItemAt(midiNote).name().set(drumBankName);
        String drumBankColor = params[2].trim();
        Color color = Color.fromHex(drumBankColor);
        extension.drumPadBank.getItemAt(midiNote).color().set(color);
    }
    private static void handleInsertBitwigDeviceInDrumPad(String[] params, BitwigBuddyExtension extension) {
        String noteNameFull = params[0].trim();
        // Use the new utility function to get the MIDI note number directly
        int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
        extension.drumPadBank.scrollPosition().set(0);
        UUID deviceUUID = getDeviceUUID(params[1].trim());
        if (deviceUUID != null) {
            extension.drumPadBank.getItemAt(midiNote).insertionPoint().insertBitwigDevice(deviceUUID);
        } else {
            extension.getHost().println("Device not found: " + params[1]);
            showPopup("Device not found: " + params[1]);
        }
    }

    private static void handleSelectDrumPad(String[] params, BitwigBuddyExtension extension) {
        String noteNameFull = params[0].trim();
        // Use the new utility function to get the MIDI note number directly
        int midiNote = Utils.getMIDINoteNumberFromString(noteNameFull);
        extension.drumPadBank.scrollPosition().set(0);
        // Load the preset in the current device
        extension.drumPadBank.getItemAt(midiNote).selectInEditor();
        
    }

    private static void handleCloseBBPanel(BitwigBuddyExtension extension) {
        // Using actions, open the export audio panel and then send escape (still with
        // bitwig actions)

        extension.getApplication().getAction("Export Audio").invoke();
        extension.getApplication().escape();
    }
}
