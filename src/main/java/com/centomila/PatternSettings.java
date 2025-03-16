package com.centomila;

import static com.centomila.utils.PopupUtils.showPopup;
import static com.centomila.utils.SettingsHelper.*;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.CustomPresetsHandler.CustomPreset;
import com.centomila.utils.SettingsHelper;

/**
 * Manages the pattern settings for the BitwigBuddy extension including presets,
 * random, and custom patterns.
 */
public class PatternSettings {
    // Static constants
    private static final String CATEGORY_GENERATE_PATTERN = "3 Generate Pattern";
    private static final String CATEGORY_CUSTOM_PATTERN_TOGGLE = "Custom Pattern Toggle";
    private static final String CATEGORY_CUSTOM_PATTERN_SAVE = "Custom Pattern Save";

    // // Static state variables
    // private static String lastDefaultPresetUsed = "Kick: Four on the Floor";
    // private static String lastCustomPresetUsed = null;

    // UI Settings - Pattern Generation
    public static Setting headerGenerate;
    public static Setting generateBtnSignalSetting;
    public static Setting patternTypeSetting;
    // public static Setting patternSelectorSetting;
    public static Setting reversePatternSetting;
    public static Setting presetPatternStringSetting;

    // UI Settings - Custom Presets
    public static Setting customPresetSetting;
    public static Setting customRefreshPresetsSetting;

    // UI Settings - Custom Preset Toggles
    public static Setting customPresetHeaderToggles;
    public static Setting customPresetDefaultNoteToggleSetting;
    public static Setting customPresetStepSizeToggleSetting;
    public static Setting customPresetSubdivisionsToggleSetting;
    public static Setting customPresetNoteLengthToggleSetting;

    // UI Settings - Save Presets
    public static Setting customPresetSaveHeaderSetting;
    public static Setting customPresetSaveBtnSignal;
    public static Setting customPresetSaveNamSetting;

    // All settings collection
    public static Setting[] allSettings;

    // Instance variables
    private final BitwigBuddyExtension extension;
    private String lastStringPatternUsed = "100,0,0,0,100,0,0,0,100,0,0,0,100,0,0,0";

    // Constructor and main initialization methods
    /**
     * Constructs a new instance of PatternSettings.
     *
     * @param extension The BitwigBuddyExtension instance.
     */
    public PatternSettings(BitwigBuddyExtension extension) {
        this.extension = extension;
    }

    /**
     * Creates and initializes pattern-related settings for the extension.
     */
    public static void init(BitwigBuddyExtension extension) {
        PatternSettings settings = new PatternSettings(extension);
        settings.initPatternSetting();
    }

    // Main initialization method
    private void initPatternSetting() {
        DocumentState documentState = extension.getDocumentState();
        initSpacer(documentState);
        initGenerateButton(documentState);
        initPatternTypeSetting(documentState);
        // initPatternSelectorSetting(documentState);

        initCustomPresetParams(documentState);
        initCustomPresetPatternStringSetting(documentState);
        initRefreshCustomPresetsSetting(documentState);
        initReversePatternSetting(documentState);

        initCustomPresetDefaultNoteToggleSetting(documentState);
        initCustomPresetStepSizeToggleSetting(documentState);
        initCustomPresetSubdivisionsToggleSetting(documentState);
        initCustomPresetNoteLengthToggleSetting(documentState);

        initCustomSavePresetSetting(documentState);
        allSettings = new Setting[] {
                headerGenerate,
                generateBtnSignalSetting,
                patternTypeSetting,

                customPresetSetting,
                presetPatternStringSetting,
                customRefreshPresetsSetting,
                reversePatternSetting,

                customPresetStepSizeToggleSetting,
                customPresetSubdivisionsToggleSetting,
                customPresetNoteLengthToggleSetting,

                customPresetSaveBtnSignal,
                customPresetSaveNamSetting,
                customPresetSaveHeaderSetting
        };
    }

    // Base UI element initialization methods
    private void initSpacer(DocumentState documentState) {
        headerGenerate = (Setting) createStringSetting(
                titleWithLine("GENERATE PATTERN"),
                CATEGORY_GENERATE_PATTERN, 0,
                "---------------------------------------------------");
        disableSetting(headerGenerate); // Spacers are always disabled
    }

    private void initGenerateButton(DocumentState documentState) {
        generateBtnSignalSetting = (Setting) documentState.getSignalSetting("Generate!", CATEGORY_GENERATE_PATTERN,
                "Generate!");

        ((Signal) generateBtnSignalSetting).addSignalObserver(extension::generateDrumPattern);
    }

    public void initPatternTypeSetting(DocumentState documentState) {
        String[] options = { "Custom", "Program" };
        patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", CATEGORY_GENERATE_PATTERN,
                options, "Custom");

        ((EnumValue) patternTypeSetting).addValueObserver(newValue -> {
            generatorTypeSelector(newValue);
        });
    }

    private void initReversePatternSetting(DocumentState documentState) {
        reversePatternSetting = (Setting) createEnumSetting(
                "Reverse Pattern",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Normal", "Reverse" },
                "Normal");
    }

    // Custom preset initialization methods
    private void initCustomPresetParams(DocumentState documentState) {
        String[] presets = getCustomPresetsContentNameStrings();
        customPresetSetting = (Setting) documentState.getEnumSetting("Custom Presets",
                CATEGORY_GENERATE_PATTERN, presets,
                presets[0]);

        hideSetting(customPresetSetting);

        ((EnumValue) customPresetSetting).addValueObserver(newValue -> {
            if (!getPatternType().equals("Custom") || newValue == null) {
                return;
            }

            String pattern = String.join(",", getCustomPresetsContentPatternStrings(newValue));
            showPopup("Custom Preset selected: " + newValue.toString());
            setPatternString(pattern);

            String defaultNote = getCustomPresetDefaultNote(newValue.toString());
            NoteDestinationSettings.setCustomPresetDefaultNoteAndOctave(defaultNote.isEmpty() ? "" : defaultNote);
            if (getCustomPresetDefaultNoteToggle().equals("Preset Default Note")) {
                NoteDestinationSettings.setNoteAndOctaveFromString(defaultNote);
            }

            String stepSize = getCustomPresetStepSize(newValue.toString());
            StepSizeSettings.setCustomStepSize(stepSize.isEmpty() ? "" : stepSize);
            if (getCustomPresetStepSizeToggle().equals("From Preset") &&
                    stepSize != null && !stepSize.trim().isEmpty()) {
                StepSizeSettings.setStepSize(stepSize);
            }

            String subdivisions = getCustomPresetSubdivisions(newValue.toString());
            // setSubdivisionsString(subdivisions.isEmpty() ? "" : subdivisions);
            StepSizeSettings.setCustomSubdivisions(subdivisions.isEmpty() ? "" : subdivisions);
            if (getCustomPresetSubdivisionsToggle().equals("From Preset") &&
                    subdivisions != null && !subdivisions.trim().isEmpty()) {
                StepSizeSettings.setSubdivisions(subdivisions);
            }

            String noteLength = getCustomPresetNoteLength(newValue.toString());
            // setNoteLengthString(noteLength.isEmpty() ? "" : noteLength);
            StepSizeSettings.setCustomNoteLength(noteLength.isEmpty() ? "" : noteLength);
            if (getCustomPresetNoteLengthToggle().equals("From Preset") &&
                    noteLength != null && !noteLength.trim().isEmpty()) {
                StepSizeSettings.setNoteLength(noteLength);
            }
        });
    }

    private void initCustomPresetPatternStringSetting(DocumentState documentState) {
        presetPatternStringSetting = (Setting) documentState.getStringSetting("Steps",
                CATEGORY_GENERATE_PATTERN, 0,
                lastStringPatternUsed);
    }

    private void initRefreshCustomPresetsSetting(DocumentState documentState) {
        customRefreshPresetsSetting = (Setting) documentState.getSignalSetting("Refresh Custom Files",
                CATEGORY_GENERATE_PATTERN, "Refresh Custom Files");
        ((Signal) customRefreshPresetsSetting).addSignalObserver(() -> {

            extension.restart();
        });
    }

    private void initCustomSavePresetSetting(DocumentState documentState) {
        customPresetSaveHeaderSetting = (Setting) documentState.getStringSetting(
                titleWithLine("SAVE THIS PRESET"),
                CATEGORY_CUSTOM_PATTERN_SAVE, 0,
                "---------------------------------------------------");
        disableSetting(customPresetSaveHeaderSetting);

        customPresetSaveBtnSignal = (Setting) documentState.getSignalSetting("Save Custom Preset",
                CATEGORY_CUSTOM_PATTERN_SAVE, "Save Custom Preset");

        ((Signal) customPresetSaveBtnSignal).addSignalObserver(() -> {
            String presetName = ((EnumValue) customPresetSetting).get();
            String patternString = ((SettableStringValue) presetPatternStringSetting).get().trim();
            int[] patternIntArray = Arrays.stream(patternString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();
            String defaultNote = ((SettableStringValue) NoteDestinationSettings.customPresetDefaultNoteSetting).get();
            String stepSize = ((SettableEnumValue) StepSizeSettings.stepSizSetting).get();
            String subdivisions = ((SettableEnumValue) StepSizeSettings.stepSizSubdivisionSetting).get();
            String noteLength = ((SettableEnumValue) StepSizeSettings.noteLengthSetting).get();
            CustomPreset preset = new CustomPreset(presetName, presetName, defaultNote, patternIntArray, stepSize,
                    subdivisions, noteLength);
            CustomPresetsHandler.saveCustomPreset(preset, extension.preferences, extension.getHost());
            showPopup("Custom Preset saved: " + presetName);
            extension.restart();
        });

        customPresetSaveNamSetting = (Setting) documentState.getStringSetting("Preset Name",
                CATEGORY_CUSTOM_PATTERN_SAVE, 0,
                "New Custom Preset");
    }

    // Toggle initialization methods
    private void initCustomPresetDefaultNoteToggleSetting(DocumentState documentState) {
        customPresetHeaderToggles = (Setting) createStringSetting(
                titleWithLine("Settings from Preset/Custom"),
                CATEGORY_CUSTOM_PATTERN_TOGGLE, 0,
                "---------------------------------------------------");
        disableSetting(customPresetHeaderToggles);

        customPresetDefaultNoteToggleSetting = (Setting) createEnumSetting(
                "Note Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "Preset Default Note", "Custom" },
                "Preset Default Note");

        ((EnumValue) customPresetDefaultNoteToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteDestinationSelectorSetting();
        });
    }

    private void initCustomPresetStepSizeToggleSetting(DocumentState documentState) {
        customPresetStepSizeToggleSetting = (Setting) createEnumSetting(
                "Step Size Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "From Preset", "Custom" },
                "From Preset");

        ((EnumValue) customPresetStepSizeToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetStepSizeSetting();
        });
    }

    private void initCustomPresetSubdivisionsToggleSetting(DocumentState documentState) {
        customPresetSubdivisionsToggleSetting = (Setting) createEnumSetting(
                "Subdivisions Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "From Preset", "Custom" },
                "From Preset");

        ((EnumValue) customPresetSubdivisionsToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetSubdivisionsSetting();
        });
    }

    private void initCustomPresetNoteLengthToggleSetting(DocumentState documentState) {
        customPresetNoteLengthToggleSetting = (Setting) createEnumSetting(
                "Note Length Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "From Preset", "Custom" },
                "From Preset");

        ((EnumValue) customPresetNoteLengthToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteLengthSetting();
        });
    }

    // Static utility methods for UI state management
    public static void generatorTypeSelector(String newValue) {
        switch (newValue) {
            case "Custom":
                Setting[] settingsToShowCustom = {
                        customPresetSetting,
                        customRefreshPresetsSetting,
                        reversePatternSetting,
                        customPresetDefaultNoteToggleSetting,
                        customPresetSaveBtnSignal,
                        customPresetSaveBtnSignal,
                        customPresetSaveHeaderSetting,
                        customPresetSaveNamSetting,
                        customPresetDefaultNoteToggleSetting,
                        customPresetStepSizeToggleSetting,
                        customPresetSubdivisionsToggleSetting,
                        customPresetNoteLengthToggleSetting };
                Setting[] settingsToHideCustom = {
                        // patternSelectorSetting,
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape };
                showSetting(settingsToShowCustom);
                hideSetting(settingsToHideCustom);

                // if (lastCustomPresetUsed != null) {
                // try {
                // ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                // } catch (Exception e) {
                // showPopup("Custom Preset not found: " + lastCustomPresetUsed);
                // lastCustomPresetUsed = "NO CUSTOM PRESETS";
                // ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                // }
                // }

                break;
            case "Program":
                Setting[] settingsToShowInProgramMode = {
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape,
                        customPresetSaveBtnSignal,
                        customPresetSaveHeaderSetting,
                        customPresetSaveNamSetting };
                Setting[] settingsToHideInProgramMode = {
                        customPresetSetting,
                        reversePatternSetting,
                        customRefreshPresetsSetting,
                        customPresetDefaultNoteToggleSetting,
                        customPresetSaveNamSetting,
                        NoteDestinationSettings.customPresetDefaultNoteSetting,
                        StepSizeSettings.customPresetStepSizeSetting,
                        StepSizeSettings.customPresetSubdivisionsSetting,
                        StepSizeSettings.customPresetNoteLengthSetting,
                        customPresetDefaultNoteToggleSetting,
                        customPresetStepSizeToggleSetting,
                        customPresetSubdivisionsToggleSetting,
                        customPresetNoteLengthToggleSetting
                };
                showSetting(settingsToShowInProgramMode);
                hideSetting(settingsToHideInProgramMode);
                break;
        }
        // Reset the note destination setting for custom presets
        toggleCustomPresetNoteDestinationSelectorSetting();
        toggleCustomPresetNoteLengthSetting();
        toggleCustomPresetSubdivisionsSetting();
        toggleCustomPresetStepSizeSetting();
    }

    public static void toggleCustomPresetNoteDestinationSelectorSetting() {
        if (getCustomPresetDefaultNoteToggle().equals("Preset Default Note")) {
            hideSetting(NoteDestinationSettings.noteDestinationSetting);
            hideSetting(NoteDestinationSettings.noteOctaveSetting);
            showSetting(NoteDestinationSettings.customPresetDefaultNoteSetting);
            hideSetting(NoteDestinationSettings.noteChannelSetting);
            
            // update the value of the note destination setting
            // if not empty
            String defaultNoteString = NoteDestinationSettings.getCustomPresetDefaultNoteString();
            if (defaultNoteString != null && !defaultNoteString.isEmpty() ) {
                NoteDestinationSettings.setNoteAndOctaveFromString(defaultNoteString);
            }

        } else {
            showSetting(NoteDestinationSettings.noteDestinationSetting);
            showSetting(NoteDestinationSettings.noteOctaveSetting);
            hideSetting(NoteDestinationSettings.customPresetDefaultNoteSetting);
            if (GlobalPreferences.showChannelDestinationPref.get()) {
                showSetting(NoteDestinationSettings.noteChannelSetting);
            }
        }
    }

    public static void toggleCustomPresetStepSizeSetting() {
        
        if (getCustomPresetStepSizeToggle().equals("From Preset")) {
            showSetting(StepSizeSettings.customPresetStepSizeSetting);
            hideSetting(StepSizeSettings.stepSizSetting);

            if (!StepSizeSettings.getStepSize().isEmpty()) {
                StepSizeSettings.setStepSize(StepSizeSettings.getStepSize());
            }

        } else {
            hideSetting(StepSizeSettings.customPresetStepSizeSetting);
            showSetting(StepSizeSettings.stepSizSetting);
        }
    }

    public static void toggleCustomPresetSubdivisionsSetting() {
        
        if (getCustomPresetSubdivisionsToggle().equals("From Preset")) {
            showSetting(StepSizeSettings.customPresetSubdivisionsSetting);
            hideSetting(StepSizeSettings.stepSizSubdivisionSetting);

            if (!StepSizeSettings.getSubdivisions().isEmpty()) {
                StepSizeSettings.setSubdivisions(StepSizeSettings.getSubdivisions());
            }
        } else {
            hideSetting(StepSizeSettings.customPresetSubdivisionsSetting);
            showSetting(StepSizeSettings.stepSizSubdivisionSetting);
        }
    }

    public static void toggleCustomPresetNoteLengthSetting() {
        
        if (getCustomPresetDefaultNoteToggle().equals("From Preset")) {
            showSetting(StepSizeSettings.customPresetNoteLengthSetting);
            hideSetting(StepSizeSettings.noteLengthSetting);

            if (!StepSizeSettings.getNoteLength().isEmpty()) {
                StepSizeSettings.setNoteLength(StepSizeSettings.getNoteLength());
            }
        } else {
            hideSetting(StepSizeSettings.customPresetNoteLengthSetting);
            showSetting(StepSizeSettings.noteLengthSetting);
        }
    }

    // Data setter utility methods
    private static void setPatternString(String patternByName) {
        String patternType = ((EnumValue) patternTypeSetting).get();
        if (patternType.equals("Program")) {
            patternByName = new int[16].toString();
        } else {
            ((SettableStringValue) presetPatternStringSetting).set(patternByName);
        }
    }


    // Custom preset data retrieval methods
    private String[] getCustomPresetsContentNameStrings() {
        String[] presets = Arrays.stream(extension.preferences.getCustomPresets())
                .map(CustomPreset::getName)
                .sorted((a, b) -> Utils.naturalCompare(a, b))
                .toArray(String[]::new);
        return presets.length == 0 ? new String[] { "NO CUSTOM PRESETS" } : presets;
    }

    private String[] getCustomPresetsContentPatternStrings(String presetName) {
        CustomPresetsHandler handler = new CustomPresetsHandler(extension.getHost(), extension.preferences);
        int[] pattern = handler.getCustomPatternByName(presetName);
        if (pattern == null) {
            // Return a default empty pattern if the preset is not found
            showPopup("Preset not found: " + presetName + ". Using default pattern.");
            return new String[] { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
        }
        return Arrays.stream(pattern).mapToObj(String::valueOf).toArray(String[]::new);
    }

    private String getCustomPresetDefaultNote(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                extension.getHost()
                        .println("Found preset: " + presetName + " with default note: " + preset.getDefaultNote());
                return preset.getDefaultNote();
            }
        }

        return "";
    }

    private String getCustomPresetStepSize(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                extension.getHost()
                        .println("Found preset: " + presetName + " with step size: " + preset.getStepSize());
                return preset.getStepSize();
            }
        }
        return "";
    }

    private String getCustomPresetSubdivisions(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                return preset.getSubdivisions();
            }
        }
        return "";
    }

    private String getCustomPresetNoteLength(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                return preset.getNoteLength();
            }
        }
        return "";
    }

    // Getters for toggles
    public static String getCustomPresetDefaultNoteToggle() {
        return ((EnumValue) customPresetDefaultNoteToggleSetting).get();
    }

    public static String getCustomPresetStepSizeToggle() {
        return ((EnumValue) customPresetStepSizeToggleSetting).get();
    }

    public static String getCustomPresetSubdivisionsToggle() {
        return ((EnumValue) customPresetSubdivisionsToggleSetting).get();
    }

    public static String getCustomPresetNoteLengthToggle() {
        return ((EnumValue) customPresetNoteLengthToggleSetting).get();
    }

    public static String getPatternType() {
        return ((EnumValue) patternTypeSetting).get();
    }
}