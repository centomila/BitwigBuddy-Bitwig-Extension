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

/**
 * Manages the pattern settings for the BitwigBuddy extension including presets,
 * random, and custom patterns.
 */
public class PatternSettings {
    // Pattern settings
    public static Setting generateBtnSignalSetting; // Pattern Type "Preset", "Program", "Custom"
    public static Setting patternTypeSetting; // Pattern Type "Preset", "Program", "Custom"
    public static Setting patternSelectorSetting; // List of default patterns
    public static Setting customPresetSetting; // List of custom patterns
    public static Setting customRefreshPresetsSetting; // Refresh custom presets
    public static Setting customPresetDefaultNoteSetting;
    public static Setting customPresetNoteDestinationSelectorSetting;
    public static Setting customPresetSaveBtnSignal;
    public static Setting customPresetSaveNamSetting;
    public static Setting customPresetStepSizeSetting;
    public static Setting customPresetSubdivisionsSetting;
    public static Setting customPresetNoteLengthSetting;
    public static Setting customPresetStepSizeToggleSetting;
    public static Setting customPresetSubdivisionsToggleSetting;
    public static Setting customPresetNoteLengthToggleSetting;

    public static Setting presetPatternStringSetting; // Custom pattern string
    public static Setting reversePatternSetting;
    public static Setting spacerGenerate;
    public static Setting[] allSettings;
    private final BitwigBuddyExtension extension;
    private static String CATEGORY_GENERATE_PATTERN = "3 Generate Pattern";
    private static String lastDefaultPresetUsed = "Kick: Four on the Floor";
    private static String lastCustomPresetUsed = null;
    private String lastStringPatternUsed = "100,0,0,0,100,0,0,0,100,0,0,0,100,0,0,0";

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
     * Sets up UI controls including:
     * - Generate button
     * - Pattern type selector (Presets/Random/Custom)
     * - Pattern preset selector
     * - Custom preset selector
     * - Pattern reversal option
     * 
     * @param extension The BitwigBuddyExtension instance to configure settings for
     */
    public static void init(BitwigBuddyExtension extension) {
        PatternSettings settings = new PatternSettings(extension);
        settings.initPatternSetting();
    }

    /**
     * Initializes all pattern-related settings.
     */
    private void initPatternSetting() {
        DocumentState documentState = extension.getDocumentState();
        initSpacer(documentState);
        initGenerateButton(documentState);
        initPatternTypeSetting(documentState);
        initPatternSelectorSetting(documentState);
        initCustomSavePresetSetting(documentState);
        
        initCustomPresetParams(documentState);
        initCustomPresetPatternStringSetting(documentState);
        initRefreshCustomPresetsSetting(documentState);
        initReversePatternSetting(documentState);
        
        initCustomPresetDefaultNoteSetting(documentState);
        initCustomPresetStepSizeToggleSetting(documentState);
        initCustomPresetSubdivisionsToggleSetting(documentState);
        initCustomPresetNoteLengthToggleSetting(documentState);
        allSettings = new Setting[] {
                spacerGenerate,
                generateBtnSignalSetting,
                patternTypeSetting,
                patternSelectorSetting,
                customPresetSetting,
                presetPatternStringSetting,
                customRefreshPresetsSetting,
                reversePatternSetting,
                customPresetDefaultNoteSetting,
                customPresetNoteDestinationSelectorSetting,
                customPresetSaveBtnSignal,
                customPresetSaveNamSetting,
                customPresetStepSizeSetting,
                customPresetSubdivisionsSetting,
                customPresetNoteLengthSetting,
                customPresetStepSizeToggleSetting,
                customPresetSubdivisionsToggleSetting,
                customPresetNoteLengthToggleSetting
        };
    }

    /**
     * Initializes the generate button setting and binds it to the drum pattern
     * generation.
     *
     * @param documentState The current document state.
     */
    private void initGenerateButton(DocumentState documentState) {
        generateBtnSignalSetting = (Setting) documentState.getSignalSetting("Generate!", CATEGORY_GENERATE_PATTERN,
                "Generate!");

        ((Signal) generateBtnSignalSetting).addSignalObserver(extension::generateDrumPattern);
    }

    /**
     * Initializes the pattern type setting and sets up observers to show/hide
     * related settings.
     *
     * @param documentState The current document state.
     */
    public void initPatternTypeSetting(DocumentState documentState) {
        String[] options = { "Presets", "Program", "Custom" };
        patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", CATEGORY_GENERATE_PATTERN,
                options, "Presets");

        ((EnumValue) patternTypeSetting).addValueObserver(newValue -> {
            generatorTypeSelector(newValue);
        });
    }

    public static void generatorTypeSelector(String newValue) {
        switch (newValue) {
            case "Presets":
                Setting[] settingsToShow = {
                        patternSelectorSetting,
                        reversePatternSetting };
                Setting[] settingsToHide = {
                        customPresetSetting,
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape,
                        customRefreshPresetsSetting, customPresetDefaultNoteSetting,
                        customPresetNoteDestinationSelectorSetting, customPresetSaveBtnSignal, customPresetSaveNamSetting,
                        customPresetStepSizeSetting, customPresetSubdivisionsSetting, customPresetNoteLengthSetting };
                showAndEnableSetting(settingsToShow);
                hideAndDisableSetting(settingsToHide);

                ((SettableEnumValue) patternSelectorSetting).set(lastDefaultPresetUsed);
                setPatternString(getDefaultPresetsContentPatternStrings(lastDefaultPresetUsed));
                break;
            case "Custom":
                Setting[] settingsToShowCustom = {
                        customPresetSetting,
                        customRefreshPresetsSetting,
                        reversePatternSetting, customPresetDefaultNoteSetting,
                        customPresetNoteDestinationSelectorSetting, customPresetSaveBtnSignal, customPresetSaveNamSetting,
                        customPresetStepSizeSetting, customPresetSubdivisionsSetting, customPresetNoteLengthSetting };
                Setting[] settingsToHideCustom = {
                        patternSelectorSetting,
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape };
                showAndEnableSetting(settingsToShowCustom);
                hideAndDisableSetting(settingsToHideCustom);

                if (lastCustomPresetUsed != null) {
                    try {
                        ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                    } catch (Exception e) {
                        showPopup("Custom Preset not found: " + lastCustomPresetUsed);
                        lastCustomPresetUsed = "NO CUSTOM PRESETS";
                        ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                    }
                }

                break;
            case "Program":
                Setting[] settingsToShowRandom = {
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape };
                Setting[] settingsToHideRandom = {
                        patternSelectorSetting,
                        customPresetSetting,
                        reversePatternSetting,
                        customRefreshPresetsSetting, customPresetDefaultNoteSetting,
                        customPresetNoteDestinationSelectorSetting, customPresetSaveBtnSignal, customPresetSaveNamSetting,
                        customPresetStepSizeSetting, customPresetSubdivisionsSetting, customPresetNoteLengthSetting };
                showAndEnableSetting(settingsToShowRandom);
                hideAndDisableSetting(settingsToHideRandom);
                break;
        }
        // Reset the note destination setting for custom presets
        toggleCustomPresetNoteDestinationSelectorSetting();
    }

    /**
     * Initializes the pattern selector setting.
     *
     * @param documentState The current document state.
     */
    private void initPatternSelectorSetting(DocumentState documentState) {
        final String[] LIST_OF_DEFAULT_PATTERNS = Arrays.stream(DefaultPatterns.patterns)
                .map(Pattern::getName)
                .toArray(String[]::new);

        patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", CATEGORY_GENERATE_PATTERN,
                LIST_OF_DEFAULT_PATTERNS,
                "Kick: Four on the Floor");

        ((EnumValue) patternSelectorSetting).addValueObserver(newValue -> {
            if (!((EnumValue) patternTypeSetting).get().equals("Presets")) {
                return;
            }
            lastDefaultPresetUsed = newValue.toString();
            showPopup(newValue.toString());
            String patternByName = getDefaultPresetsContentPatternStrings(newValue);
            setPatternString(patternByName);
        });
    }

    private static String getDefaultPresetsContentPatternStrings(String newValue) {
        String patternByName = Arrays.stream(DefaultPatterns.getPatternByName(newValue.toString()))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
        return patternByName;
    }

    /**
     * Initializes the custom preset setting and adds an observer for selection
     * events.
     *
     * @param documentState The current document state.
     */
    private void initCustomPresetParams(DocumentState documentState) {
        String[] presets = getCustomPresetsContentNameStrings();
        customPresetSetting = (Setting) documentState.getEnumSetting("Custom Presets",
                CATEGORY_GENERATE_PATTERN, presets,
                presets[0]);

        hideAndDisableSetting(customPresetSetting);

        customPresetStepSizeSetting = (Setting) createStringSetting(
                "Step Size from Preset",
                CATEGORY_GENERATE_PATTERN, 0,
                "1/16");

        customPresetSubdivisionsSetting = (Setting) createStringSetting(
                "Subdivisions from Preset",
                CATEGORY_GENERATE_PATTERN, 0,
                "Straight");

        customPresetNoteLengthSetting = (Setting) createStringSetting(
                "Note Length from Preset",
                CATEGORY_GENERATE_PATTERN, 0,
                "1/16");

        hideAndDisableSetting(customPresetStepSizeSetting);
        hideAndDisableSetting(customPresetSubdivisionsSetting);
        hideAndDisableSetting(customPresetNoteLengthSetting);

        ((EnumValue) customPresetSetting).addValueObserver(newValue -> {
            if (!((EnumValue) patternTypeSetting).get().equals("Custom") || newValue == null) {
                return;
            }
            
            lastCustomPresetUsed = newValue.toString();
            if (lastCustomPresetUsed.equals("NO CUSTOM PRESETS")) {
                setPatternString("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
                setDefaultNoteString("C1");
                setStepSizeString("1/16");
                setSubdivisionsString("Straight");
                setNoteLengthString("1/16");
                return;
            }

            String pattern = String.join(",", getCustomPresetsContentPatternStrings(newValue));
            showPopup("Custom Preset selected: " + newValue.toString());
            setPatternString(pattern);

            String defaultNote = getCustomPresetDefaultNote(newValue.toString());
            setDefaultNoteString(defaultNote.isEmpty() ? "C1" : defaultNote);
            if (((EnumValue) customPresetNoteDestinationSelectorSetting).get().equals("Preset Default Note")) {
                NoteDestinationSettings.setNoteAndOctaveFromString(defaultNote.isEmpty() ? "C1" : defaultNote);
            }

            String stepSize = getCustomPresetStepSize(newValue.toString());
            
            setStepSizeString(stepSize.isEmpty() ? "1/16" : stepSize);
            if (((EnumValue) customPresetStepSizeToggleSetting).get().equals("Enable") && 
                stepSize != null && !stepSize.trim().isEmpty()) {
                ((SettableEnumValue) StepSizeSettings.stepSizSetting).set(stepSize);
            }


            String subdivisions = getCustomPresetSubdivisions(newValue.toString());
            setSubdivisionsString(subdivisions.isEmpty() ? "Straight" : subdivisions);
            if (((EnumValue) customPresetSubdivisionsToggleSetting).get().equals("Enable") && 
                subdivisions != null && !subdivisions.trim().isEmpty()) {
                ((SettableEnumValue) StepSizeSettings.stepSizSubdivisionSetting).set(subdivisions);
            }


            String noteLength = getCustomPresetNoteLength(newValue.toString());
            setNoteLengthString(noteLength.isEmpty() ? "1/16" : noteLength);
            if (((EnumValue) customPresetNoteLengthToggleSetting).get().equals("Enable") && 
                noteLength != null && !noteLength.trim().isEmpty()) {
                ((SettableEnumValue) StepSizeSettings.noteLengthSetting).set(noteLength);
            }
        });
    }

    private static void setPatternString(String patternByName) {
        String patternType = ((EnumValue) patternTypeSetting).get();
        if (patternType.equals("Program")) {
            patternByName = new int[16].toString();
        } else {
            ((SettableStringValue) presetPatternStringSetting).set(patternByName);
        }
    }

    private static void setDefaultNoteString(String note) {
        ((SettableStringValue) customPresetDefaultNoteSetting).set(note);
    }

    private static void setStepSizeString(String stepSize) {
        ((SettableStringValue) customPresetStepSizeSetting).set(stepSize);
    }

    private static void setSubdivisionsString(String subdivisions) {
        ((SettableStringValue) customPresetSubdivisionsSetting).set(subdivisions);
    }

    private static void setNoteLengthString(String noteLength) {
        ((SettableStringValue) customPresetNoteLengthSetting).set(noteLength);
    }

    private void initCustomPresetPatternStringSetting(DocumentState documentState) {
        presetPatternStringSetting = (Setting) documentState.getStringSetting("Steps",
                CATEGORY_GENERATE_PATTERN, 0,
                lastStringPatternUsed);
    }

    private void initCustomSavePresetSetting(DocumentState documentState) {
        customPresetSaveBtnSignal = (Setting) documentState.getSignalSetting("Save Custom Preset",
                CATEGORY_GENERATE_PATTERN, "Save Custom Preset");

        ((Signal) customPresetSaveBtnSignal).addSignalObserver(() -> {
            String presetName = ((EnumValue) customPresetSetting).get();
            String patternString = ((SettableStringValue)presetPatternStringSetting).get().trim();
            int[] patternIntArray = Arrays.stream(patternString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();
            String defaultNote = ((SettableStringValue) customPresetDefaultNoteSetting).get();
            String stepSize = ((SettableEnumValue) StepSizeSettings.stepSizSetting).get();
            String subdivisions = ((SettableEnumValue) StepSizeSettings.stepSizSubdivisionSetting).get();
            String noteLength = ((SettableEnumValue) StepSizeSettings.noteLengthSetting).get();
            CustomPreset preset = new CustomPreset(presetName, presetName, defaultNote, patternIntArray, stepSize, subdivisions, noteLength);
            CustomPresetsHandler.saveCustomPreset(preset, extension.preferences, extension.getHost());
            showPopup("Custom Preset saved: " + presetName);
            extension.restart();
        });

        customPresetSaveNamSetting = (Setting) documentState.getStringSetting("Preset Name",
                CATEGORY_GENERATE_PATTERN, 0,
                "New Custom Preset");
    }

    private void initRefreshCustomPresetsSetting(DocumentState documentState) {
        customRefreshPresetsSetting = (Setting) documentState.getSignalSetting("Refresh Custom Files",
        CATEGORY_GENERATE_PATTERN, "Refresh Custom Files");
        ((Signal) customRefreshPresetsSetting).addSignalObserver(() -> {
                

                extension.restart();
            });
        }
        
        private void initCustomPresetDefaultNoteSetting(DocumentState documentState) {
        customPresetDefaultNoteSetting = (Setting) createStringSetting(
                "Default Note from Preset",
                CATEGORY_GENERATE_PATTERN, 0,
                "C1");

        disableSetting(customPresetDefaultNoteSetting);

        customPresetNoteDestinationSelectorSetting = (Setting) createEnumSetting(
                "Note Destination",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Preset Default Note", "Note Destination" },
                "Note Destination");

        ((EnumValue) customPresetNoteDestinationSelectorSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteDestinationSelectorSetting();
        });
    }

    public static void toggleCustomPresetNoteDestinationSelectorSetting() {
        String value = ((EnumValue) customPresetNoteDestinationSelectorSetting).get();
        String patternType = ((EnumValue) patternTypeSetting).get();
        if (!patternType.equals("Custom")) {
            enableSetting(NoteDestinationSettings.noteDestinationSetting);
            enableSetting(NoteDestinationSettings.noteOctaveSetting);
            hideAndDisableSetting(customPresetDefaultNoteSetting);
            return;
        }
        if (value.equals("Preset Default Note")) {
            disableSetting(NoteDestinationSettings.noteDestinationSetting);
            disableSetting(NoteDestinationSettings.noteOctaveSetting);
            showAndEnableSetting(customPresetDefaultNoteSetting);
        } else {
            enableSetting(NoteDestinationSettings.noteDestinationSetting);
            enableSetting(NoteDestinationSettings.noteOctaveSetting);
            hideAndDisableSetting(customPresetDefaultNoteSetting);
        }
        

    }

    /**
     * Initializes the reverse pattern setting.
     *
     * @param documentState The current document state.
     */
    private void initReversePatternSetting(DocumentState documentState) {
        reversePatternSetting = (Setting) createEnumSetting(
                "Reverse Pattern",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Normal", "Reverse" },
                "Normal");
    }

    /**
     * Initializes the spacer setting to visually separate groups of controls.
     *
     * @param documentState The current document state.
     */
    private void initSpacer(DocumentState documentState) {
        spacerGenerate = (Setting) createStringSetting(
                titleWithLine("GENERATE PATTERN"),
                CATEGORY_GENERATE_PATTERN, 0,
                "---------------------------------------------------");
        disableSetting(spacerGenerate); // Spacers are always disabled
    }

    /**
     * Retrieves an array of custom preset names.
     *
     * @return An array containing the names of the custom presets.
     */
    private String[] getCustomPresetsContentNameStrings() {
        String[] presets = Arrays.stream(extension.preferences.getCustomPresets())
            .map(CustomPreset::getName)
            .sorted((a, b) -> Utils.naturalCompare(a, b))
            .toArray(String[]::new);
        return presets.length == 0 ? new String[]{"NO CUSTOM PRESETS"} : presets;
    }

    private String[] getCustomPresetsContentPatternStrings(String presetName) {
        CustomPresetsHandler handler = new CustomPresetsHandler(extension.getHost(), extension.preferences);
        int[] pattern = handler.getCustomPatternByName(presetName);
        if (pattern == null) {
            // Return a default empty pattern if the preset is not found
            showPopup("Preset not found: " + presetName + ". Using default pattern.");
            return new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
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

    private void initCustomPresetStepSizeToggleSetting(DocumentState documentState) {
        customPresetStepSizeToggleSetting = (Setting) createEnumSetting(
                "Step Size Toggle",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Enable", "Disable" },
                "Disable");

        ((EnumValue) customPresetStepSizeToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetStepSizeSetting();
        });
    }

    private void initCustomPresetSubdivisionsToggleSetting(DocumentState documentState) {
        customPresetSubdivisionsToggleSetting = (Setting) createEnumSetting(
                "Subdivisions Toggle",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Enable", "Disable" },
                "Disable");

        ((EnumValue) customPresetSubdivisionsToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetSubdivisionsSetting();
        });
    }

    private void initCustomPresetNoteLengthToggleSetting(DocumentState documentState) {
        customPresetNoteLengthToggleSetting = (Setting) createEnumSetting(
                "Note Length Toggle",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Enable", "Disable" },
                "Disable");

        ((EnumValue) customPresetNoteLengthToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteLengthSetting();
        });
    }

    public static void toggleCustomPresetStepSizeSetting() {
        String value = ((EnumValue) customPresetStepSizeToggleSetting).get();
        if (value.equals("Enable")) {
            showAndEnableSetting(customPresetStepSizeSetting);
        } else {
            hideAndDisableSetting(customPresetStepSizeSetting);
        }
    }

    public static void toggleCustomPresetSubdivisionsSetting() {
        String value = ((EnumValue) customPresetSubdivisionsToggleSetting).get();
        if (value.equals("Enable")) {
            showAndEnableSetting(customPresetSubdivisionsSetting);
        } else {
            hideAndDisableSetting(customPresetSubdivisionsSetting);
        }
    }

    public static void toggleCustomPresetNoteLengthSetting() {
        String value = ((EnumValue) customPresetNoteLengthToggleSetting).get();
        if (value.equals("Enable")) {
            showAndEnableSetting(customPresetNoteLengthSetting);
        } else {
            hideAndDisableSetting(customPresetNoteLengthSetting);
        }
    }

}