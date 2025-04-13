package com.centomila.utils.commands;

import com.centomila.utils.commands.bb.*;
import com.centomila.utils.commands.clip.*;
import com.centomila.utils.commands.device.*;
import com.centomila.utils.commands.drum.*;
import com.centomila.utils.commands.marker.*;
import com.centomila.utils.commands.navigation.*;
import com.centomila.utils.commands.project.*;
import com.centomila.utils.commands.step.*;
import com.centomila.utils.commands.track.*;
import com.centomila.utils.commands.transport.*;
import com.centomila.utils.commands.utility.*;
import com.centomila.macro.commands.MacroCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Centralizes command registration for all command implementations.
 */
public class CommandRegistration {

    /**
     * Registers all command implementations with the CommandFactory.
     * 
     * Note: Only commands that have been implemented are registered here.
     * As more command classes are created, they should be added to this method.
     */
    public static void registerAllCommands() {
        // Transport commands
        CommandFactory.registerCommand("Bpm", new BpmCommand());
        CommandFactory.registerCommand("Transport Position", new TransportPositionCommand());
        CommandFactory.registerCommand("Time Signature", new TimeSignatureCommand());
        CommandFactory.registerCommand("Arranger Loop Start", new ArrangerLoopStartCommand());
        CommandFactory.registerCommand("Arranger Loop End", new ArrangerLoopEndCommand());
        
        // Marker commands
        CommandFactory.registerCommand("CueMarkerName", new CueMarkerNameCommand());
        CommandFactory.registerCommand("DeleteAllCueMarkers", new DeleteAllCueMarkersCommand());
        
        // Clip commands
        CommandFactory.registerCommand("Clip Create", new ClipCreateCommand());
        CommandFactory.registerCommand("Clip Delete", new ClipDeleteCommand());
        CommandFactory.registerCommand("Clip Select", new ClipSelectCommand());
        CommandFactory.registerCommand("Clip Duplicate", new ClipDuplicateCommand());
        CommandFactory.registerCommand("Clip Loop On", new ClipLoopOnCommand());
        CommandFactory.registerCommand("Clip Loop Off", new ClipLoopOffCommand());
        CommandFactory.registerCommand("Clip Rename", new ClipRenameCommand());
        CommandFactory.registerCommand("Clip Color", new ClipColorCommand());
        CommandFactory.registerCommand("Clip Move", new ClipMoveCommand());
        CommandFactory.registerCommand("Clip Offset", new ClipOffsetCommand());
        CommandFactory.registerCommand("Clip Accent", new ClipAccentCommand());
        CommandFactory.registerCommand("Clip Length", new ClipLengthCommand());
        
        // Project commands
        CommandFactory.registerCommand("Project Name", new ProjectNameCommand());
        
        // Step commands
        CommandFactory.registerCommand("Step Selected Velocity", new StepSelectedVelocityCommand());
        CommandFactory.registerCommand("Step Selected Length", new StepSelectedLengthCommand());
        CommandFactory.registerCommand("Step Selected Chance", new StepSelectedChanceCommand());
        CommandFactory.registerCommand("Step Selected Transpose", new StepSelectedTransposeCommand());
        CommandFactory.registerCommand("Step Selected Gain", new StepSelectedGainCommand());
        CommandFactory.registerCommand("Step Selected Pressure", new StepSelectedPressureCommand());
        CommandFactory.registerCommand("Step Selected Timbre", new StepSelectedTimbreCommand());
        CommandFactory.registerCommand("Step Selected Pan", new StepSelectedPanCommand());
        CommandFactory.registerCommand("Step Selected Velocity Spread", new StepSelectedVelocitySpreadCommand());
        CommandFactory.registerCommand("Step Selected Release Velocity", new StepSelectedReleaseVelocityCommand());
        CommandFactory.registerCommand("Step Selected Is Chance Enabled", new StepSelectedIsChanceEnabledCommand());
        CommandFactory.registerCommand("Step Selected Is Muted", new StepSelectedIsMutedCommand());
        CommandFactory.registerCommand("Step Selected Is Occurrence Enabled", new StepSelectedIsOccurrenceEnabledCommand());
        CommandFactory.registerCommand("Step Selected Is Recurrence Enabled", new StepSelectedIsRecurrenceEnabledCommand());
        CommandFactory.registerCommand("Step Selected Is Repeat Enabled", new StepSelectedIsRepeatEnabledCommand());
        CommandFactory.registerCommand("Step Selected Occurrence", new StepSelectedOccurrenceCommand());
        CommandFactory.registerCommand("Step Selected Recurrence", new StepSelectedRecurrenceCommand());
        CommandFactory.registerCommand("Step Selected Repeat Count", new StepSelectedRepeatCountCommand());
        CommandFactory.registerCommand("Step Selected Repeat Curve", new StepSelectedRepeatCurveCommand());
        CommandFactory.registerCommand("Step Selected Repeat Velocity Curve", new StepSelectedRepeatVelocityCurveCommand());
        CommandFactory.registerCommand("Step Selected Repeat Velocity End", new StepSelectedRepeatVelocityEndCommand());
        // Register the new Step Set command
        CommandFactory.registerCommand("Step Set", new StepSetCommand());
        
        // Track commands
        CommandFactory.registerCommand("Track Select", new TrackSelectCommand());
        CommandFactory.registerCommand("Track Color", new TrackColorCommand());
        CommandFactory.registerCommand("Track Color All", new TrackColorAllCommand());
        CommandFactory.registerCommand("Track Rename", new TrackRenameCommand());
        CommandFactory.registerCommand("Track Delete", new TrackDeleteCommand());
        CommandFactory.registerCommand("Track Next", new TrackNextCommand());
        CommandFactory.registerCommand("Track Previous", new TrackPrevCommand());
        CommandFactory.registerCommand("Track Prev", new TrackPrevCommand());
        
        // Device commands
        CommandFactory.registerCommand("Insert Device", new InsertDeviceCommand());
        CommandFactory.registerCommand("Insert VST3", new InsertVST3Command());
        CommandFactory.registerCommand("Insert File", new InsertFileCommand());
        
        // Drum pad commands
        CommandFactory.registerCommand("Drum Pad Insert Empty", new CreateDrumPadCommand());
        CommandFactory.registerCommand("Drum Pad Insert Device", new InsertBitwigDeviceInDrumPadCommand());
        CommandFactory.registerCommand("Drum Pad Select", new SelectDrumPadCommand());
        CommandFactory.registerCommand("Insert File In Drum Pad", new InsertFileInDrumPadCommand());
        CommandFactory.registerCommand("Insert VST3 In Drum Pad", new InsertVST3InDrumPadCommand());
        
        // BeatBuddy specific commands
        CommandFactory.registerCommand("BB Preset", new BBPresetCommand());
        CommandFactory.registerCommand("BB Generate", new BBGenerateCommand());
        CommandFactory.registerCommand("BB Pattern Repeat", new BBPatternRepeatCommand());
        CommandFactory.registerCommand("BB Post Action AutoResize", new BBPostActionAutoResizeCommand());
        CommandFactory.registerCommand("BB Arranger Mode", new BBArrangerModeCommand());
        CommandFactory.registerCommand("BB Launcher Mode", new BBLauncherModeCommand());
        CommandFactory.registerCommand("BB Toggle Launcher Arranger Mode", new BBToggleLauncherArrangerModeCommand());
        CommandFactory.registerCommand("BB Close Panel", new BBClosePanelCommand());
        
        // Navigation commands
        CommandFactory.registerCommand("Left", new LeftCommand());
        CommandFactory.registerCommand("Right", new RightCommand());
        CommandFactory.registerCommand("Up", new UpCommand());
        CommandFactory.registerCommand("Down", new DownCommand());
        CommandFactory.registerCommand("Enter", new EnterCommand());
        CommandFactory.registerCommand("Escape", new EscapeCommand());
        
        // Utility commands
        CommandFactory.registerCommand("Message", new MessageCommand());
        CommandFactory.registerCommand("Console", new ConsoleCommand());
        CommandFactory.registerCommand("Wait", new WaitCommand());
        CommandFactory.registerCommand("Print Actions", new PrintActionsCommand());
        CommandFactory.registerCommand("List Commands", new ListCommandsCommand());
        
        // Macro commands
        CommandFactory.registerCommand("BB Macro", new MacroCommand());
        CommandFactory.registerCommand("Macro", new MacroCommand());

        // Debug commands
        // getAllRegisteredCommands()
    }
    
    /**
     * Gets a map of all registered commands for documentation or UI purposes.
     */
    public static Map<String, String> getCommandDocumentation() {
        Map<String, String> commandDocs = new HashMap<>();
        
        // Add documentation for implemented commands
        commandDocs.put("Bpm", "Sets the BPM (tempo) of the project. Parameters: [bpm]");
        commandDocs.put("Transport Position", "Sets the transport position. Parameters: [position]");
        commandDocs.put("Time Signature", "Sets the project time signature. Parameters: [signature]");
        commandDocs.put("Clip Create", "Creates a new clip. Parameters: [slot_index, length]");
        commandDocs.put("Clip Delete", "Deletes the selected clip. No parameters.");
        commandDocs.put("Clip Duplicate", "Duplicates the selected clip. No parameters.");
        commandDocs.put("Insert File", "Inserts a file into a track. Parameters: [slot_index, file_path]");
        commandDocs.put("Insert Device", "Inserts a Bitwig device. Parameters: [device_name]");
        commandDocs.put("Insert VST3", "Inserts a VST3 plugin. Parameters: [vst3_name]");
        commandDocs.put("Step Selected Velocity", "Sets velocity for selected notes. Parameters: [velocity]");
        commandDocs.put("Step Selected Length", "Sets length for selected notes. Parameters: [length]");
        commandDocs.put("Wait", "Waits for specified milliseconds. Parameters: [time_ms]");
        commandDocs.put("Message", "Shows a popup message. Parameters: [message_text]");
        commandDocs.put("Console", "Writes a message to the Bitwig console. Parameters: [message_text]");
        commandDocs.put("Macro", "Executes another macro. Parameters: [macro_name]");
        commandDocs.put("Track Color", "Sets the color of the currently selected track. Parameters: [color_hex]");
        commandDocs.put("Track Color All", "Sets the color of all tracks in the track bank. Parameters: [color_hex]");
        commandDocs.put("List Commands", "Lists all registered commands in the console. No parameters.");
        
        return commandDocs;
    }

    /**
     * Returns a list of all registered command names.
     * Useful for debugging.
     */
    public static List<String> getAllRegisteredCommands() {
        return new ArrayList<>(CommandFactory.getAllCommandNames());
    }
}
