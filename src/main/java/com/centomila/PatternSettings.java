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
    // Remove CATEGORY_CUSTOM_PATTERN_SAVE constant as it's now in CustomPresetSaver

    // Pattern type constants
    private static final String PATTERN_TYPE_PRESET = "Preset";
    private static final String PATTERN_TYPE_PROGRAM = "Program";

    // Toggle option constants
    private static final String TOGGLE_FROM_PRESET = "From Preset";
    private static final String TOGGLE_CUSTOM = "Custom";

    // UI Settings - Pattern Generation
    public static Setting headerGenerate;
    public static Setting generateBtnSignalSetting;
    public static Setting patternTypeSetting;
    public static Setting reversePatternSetting;
    public static Setting patternReplaceAddToggle; // New setting for replace/add toggle
    public static Setting presetPatternStringSetting;

    // UI Settings - Custom Presets
    public static Setting customPresetSetting;
    public static Setting customRefreshPresetsSetting;

    // UI Settings - Custom Preset Toggles
    public static Setting customPresetHeaderToggles;
    public static Setting customTogglesToggle; // New setting to toggle visibility
    public static Setting customPresetDefaultNoteToggleSetting;
    public static Setting customPresetStepSizeToggleSetting;
    public static Setting customPresetSubdivisionsToggleSetting;
    public static Setting customPresetNoteLengthToggleSetting;

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
        initPatternTypeSetting(documentState);
        initSpacer(documentState);
        initGenerateButton(documentState);
        initPatternReplaceAddToggle(documentState); // Initialize new toggle

        initCustomPresetParams(documentState);
        initCustomPresetPatternStringSetting(documentState);
        initRefreshCustomPresetsSetting(documentState);
        initReversePatternSetting(documentState);

        // Initialize toggle for showing/hiding custom preset toggles
        initCustomTogglesToggle(documentState);

        initCustomPresetDefaultNoteToggleSetting(documentState);
        initCustomPresetStepSizeToggleSetting(documentState);
        initCustomPresetSubdivisionsToggleSetting(documentState);
        initCustomPresetNoteLengthToggleSetting(documentState);

        allSettings = new Setting[] {
                headerGenerate,
                generateBtnSignalSetting,
                patternTypeSetting,
                patternReplaceAddToggle, // Add to settings array

                customPresetSetting,
                presetPatternStringSetting,
                customRefreshPresetsSetting,
                reversePatternSetting,

                customPresetHeaderToggles,
                customTogglesToggle,
                customPresetDefaultNoteToggleSetting,
                customPresetStepSizeToggleSetting,
                customPresetSubdivisionsToggleSetting,
                customPresetNoteLengthToggleSetting
        };

        // Initial hide/show state based on default toggle value
        updateCustomTogglesVisibility();
    }

    // Initialize the Replace/Add toggle
    private void initPatternReplaceAddToggle(DocumentState documentState) {
        patternReplaceAddToggle = (Setting) createEnumSetting(
                "Replace/Add Pattern",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Replace", "Add" },
                "Replace");
    }

    // Initialize the toggle for hiding/showing custom preset toggles
    private void initCustomTogglesToggle(DocumentState documentState) {
        customPresetHeaderToggles = (Setting) createStringSetting(
                titleWithLine("FROM PRESET / CUSTOM OPTIONS"),
                CATEGORY_CUSTOM_PATTERN_TOGGLE, 0,
                "---------------------------------------------------");
        disableSetting(customPresetHeaderToggles);

        customTogglesToggle = (Setting) createEnumSetting(
                "From Preset / Custom Options",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "Show", "Hide" },
                "Hide");

        ((EnumValue) customTogglesToggle).addValueObserver(newValue -> {
            updateCustomTogglesVisibility();
        });
    }

    // Method to update visibility of custom toggle settings
    private static void updateCustomTogglesVisibility() {
        String showCustomToggles = ((EnumValue) customTogglesToggle).get();

        Setting[] toggleSettings = {
                customPresetDefaultNoteToggleSetting,
                customPresetStepSizeToggleSetting,
                customPresetSubdivisionsToggleSetting,
                customPresetNoteLengthToggleSetting
        };

        if (showCustomToggles.equals("Hide")) {
            hideSetting(toggleSettings);
        } else {
            showSetting(toggleSettings);
        }
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
        String[] options = { PATTERN_TYPE_PRESET, PATTERN_TYPE_PROGRAM };
        patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", CATEGORY_GENERATE_PATTERN,
                options, PATTERN_TYPE_PRESET);

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
        customPresetSetting = (Setting) documentState.getEnumSetting("Presets",
                CATEGORY_GENERATE_PATTERN, presets,
                presets[0]);

        hideSetting(customPresetSetting);

        ((EnumValue) customPresetSetting).addValueObserver(newValue -> {
            if (!getPatternType().equals(PATTERN_TYPE_PRESET) || newValue == null) {
                return;
            }

            String pattern = String.join(",", getCustomPresetsContentPatternStrings(newValue));
            showPopup("Custom Preset selected: " + newValue.toString());
            setPatternString(pattern);

            // Get preset values and store them
            String defaultNote = getCustomPresetDefaultNote(newValue.toString());
            String stepSize = getCustomPresetStepSize(newValue.toString());
            String subdivisions = getCustomPresetSubdivisions(newValue.toString());
            String noteLength = getCustomPresetNoteLength(newValue.toString());

            // Store preset values in the respective classes
            NoteDestinationSettings.setCustomPresetDefaultNoteString(defaultNote);
            StepSizeSettings.setCustomStepSize(stepSize);
            StepSizeSettings.setCustomSubdivisions(subdivisions);
            StepSizeSettings.setCustomNoteLength(noteLength);

            // Apply the values based on toggle settings
            applySettingsBasedOnToggles(defaultNote, stepSize, subdivisions, noteLength);
        });
    }

    // New helper method to apply settings based on toggle states
    private void applySettingsBasedOnToggles(String defaultNote, String stepSize, String subdivisions,
            String noteLength) {
        // Apply note destination if toggle is set to "From Preset"
        if (getCustomPresetDefaultNoteToggle().equals(TOGGLE_FROM_PRESET) &&
                defaultNote != null && !defaultNote.isEmpty()) {
            NoteDestinationSettings.setNoteAndOctaveFromString(defaultNote);
        }

        // Apply step size if toggle is set to "From Preset"
        if (getCustomPresetStepSizeToggle().equals(TOGGLE_FROM_PRESET) &&
                stepSize != null && !stepSize.isEmpty()) {
            StepSizeSettings.setStepSize(stepSize);
        }

        // Apply subdivisions if toggle is set to "From Preset"
        if (getCustomPresetSubdivisionsToggle().equals(TOGGLE_FROM_PRESET) &&
                subdivisions != null && !subdivisions.isEmpty()) {
            StepSizeSettings.setSubdivisions(subdivisions);
        }

        // Apply note length if toggle is set to "From Preset"
        if (getCustomPresetNoteLengthToggle().equals(TOGGLE_FROM_PRESET) &&
                noteLength != null && !noteLength.isEmpty()) {
            StepSizeSettings.setNoteLength(noteLength);
        }
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

    // Toggle initialization methods
    private void initCustomPresetDefaultNoteToggleSetting(DocumentState documentState) {

        customPresetDefaultNoteToggleSetting = (Setting) createEnumSetting(
                "Note Destination Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { TOGGLE_FROM_PRESET, TOGGLE_CUSTOM },
                TOGGLE_FROM_PRESET);

        ((EnumValue) customPresetDefaultNoteToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteDestinationSelectorSetting(newValue);
        });

        // Initially hide based on toggle setting
        if (((EnumValue) customTogglesToggle).get().equals("Hide")) {
            hideSetting(customPresetDefaultNoteToggleSetting);
        }
    }

    private void initCustomPresetStepSizeToggleSetting(DocumentState documentState) {
        customPresetStepSizeToggleSetting = (Setting) createEnumSetting(
                "Step Size Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { TOGGLE_FROM_PRESET, TOGGLE_CUSTOM },
                TOGGLE_FROM_PRESET);

        ((EnumValue) customPresetStepSizeToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetStepSizeSetting();
        });

        // Initially hide based on toggle setting
        if (((EnumValue) customTogglesToggle).get().equals("Hide")) {
            hideSetting(customPresetStepSizeToggleSetting);
        }
    }

    private void initCustomPresetSubdivisionsToggleSetting(DocumentState documentState) {
        customPresetSubdivisionsToggleSetting = (Setting) createEnumSetting(
                "Subdivisions Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { TOGGLE_FROM_PRESET, TOGGLE_CUSTOM },
                TOGGLE_FROM_PRESET);

        ((EnumValue) customPresetSubdivisionsToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetSubdivisionsSetting();
        });

        // Initially hide based on toggle setting
        if (((EnumValue) customTogglesToggle).get().equals("Hide")) {
            hideSetting(customPresetSubdivisionsToggleSetting);
        }
    }

    private void initCustomPresetNoteLengthToggleSetting(DocumentState documentState) {
        customPresetNoteLengthToggleSetting = (Setting) createEnumSetting(
                "Note Length Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { TOGGLE_FROM_PRESET, TOGGLE_CUSTOM },
                TOGGLE_FROM_PRESET);

        ((EnumValue) customPresetNoteLengthToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteLengthSetting(newValue);
        });

        // Initially hide based on toggle setting
        if (((EnumValue) customTogglesToggle).get().equals("Hide")) {
            hideSetting(customPresetNoteLengthToggleSetting);
        }
    }

    // Static utility methods for UI state management
    public static void generatorTypeSelector(String newValue) {
        switch (newValue) {
            
            // Preset mode
            case PATTERN_TYPE_PRESET:
                Setting[] settingsToShowCustom = {
                        customPresetSetting,
                        customRefreshPresetsSetting,
                        reversePatternSetting,
                        customTogglesToggle,
                        CustomPresetSaver.getCustomPresetSaveBtnSignal(),
                        CustomPresetSaver.getCustomPresetSaveHeaderSetting(),
                        CustomPresetSaver.getCustomPresetSaveNameSetting(),
                        customPresetHeaderToggles };
                Setting[] settingsToHideCustom = {
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape };
                showSetting(settingsToShowCustom);
                hideSetting(settingsToHideCustom);

                // Update visibility of toggle settings based on the current toggle state
                updateCustomTogglesVisibility();
                break;

            // Program mode
            case PATTERN_TYPE_PROGRAM:
                Setting[] settingsToShowInProgramMode = {
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape,
                        CustomPresetSaver.getCustomPresetSaveBtnSignal(),
                        CustomPresetSaver.getCustomPresetSaveHeaderSetting(),
                        CustomPresetSaver.getCustomPresetSaveNameSetting(),
                        NoteDestinationSettings.noteDestinationSetting,
                        NoteDestinationSettings.noteOctaveSetting,
                        NoteDestinationSettings.noteChannelSetting,
                        StepSizeSettings.stepSizSetting,
                        StepSizeSettings.stepSizSubdivisionSetting,
                        StepSizeSettings.noteLengthSetting

                };
                Setting[] settingsToHideInProgramMode = {
                        customPresetSetting,
                        reversePatternSetting,
                        customRefreshPresetsSetting,
                        customTogglesToggle,
                        customPresetDefaultNoteToggleSetting,
                        customPresetStepSizeToggleSetting,
                        customPresetSubdivisionsToggleSetting,
                        customPresetNoteLengthToggleSetting,
                        customPresetHeaderToggles
                };
                showAndEnableSetting(settingsToShowInProgramMode);
                disableSetting(CustomPresetSaver.customPresetSaveHeaderSetting);
                hideSetting(settingsToHideInProgramMode);
                break;
        }

        // Reset the note destination setting for custom presets
        toggleCustomPresetNoteDestinationSelectorSetting(getCustomPresetDefaultNoteToggle());
        toggleCustomPresetNoteLengthSetting(getCustomPresetDefaultNoteToggle());
        toggleCustomPresetSubdivisionsSetting();
        toggleCustomPresetStepSizeSetting();
    }

    public static void toggleCustomPresetNoteDestinationSelectorSetting(String newValue) {
        if (newValue.equals(TOGGLE_FROM_PRESET) && getPatternType().equals(PATTERN_TYPE_PRESET)) {
            disableSetting(NoteDestinationSettings.noteDestinationSetting);
            disableSetting(NoteDestinationSettings.noteOctaveSetting);
            disableSetting(NoteDestinationSettings.noteChannelSetting);

            // Apply the stored preset default note if available
            String defaultNoteString = NoteDestinationSettings.getCustomPresetDefaultNoteString();
            if (defaultNoteString != null && !defaultNoteString.isEmpty()) {
                NoteDestinationSettings.setNoteAndOctaveFromString(defaultNoteString);
            }
        } else {
            enableSetting(NoteDestinationSettings.noteDestinationSetting);
            enableSetting(NoteDestinationSettings.noteOctaveSetting);
            if (GlobalPreferences.showChannelDestinationPref.get()) {
                enableSetting(NoteDestinationSettings.noteChannelSetting);
            }
        }
    }

    public static void toggleCustomPresetStepSizeSetting() {
        if (getCustomPresetStepSizeToggle().equals(TOGGLE_FROM_PRESET)
                && getPatternType().equals(PATTERN_TYPE_PRESET)) {
            disableSetting(StepSizeSettings.stepSizSetting);

            // Apply the stored preset step size if available
            String customStepSize = StepSizeSettings.getCustomStepSize();
            if (customStepSize != null && !customStepSize.isEmpty()) {
                StepSizeSettings.setStepSize(customStepSize);
            }
        } else {
            enableSetting(StepSizeSettings.stepSizSetting);
        }
    }

    public static void toggleCustomPresetSubdivisionsSetting() {
        if (getCustomPresetSubdivisionsToggle().equals(TOGGLE_FROM_PRESET)
                && getPatternType().equals(PATTERN_TYPE_PRESET)) {
            disableSetting(StepSizeSettings.stepSizSubdivisionSetting);

            // Apply the stored preset subdivisions if available
            String customSubdivisions = StepSizeSettings.getCustomSubdivisions();
            if (customSubdivisions != null && !customSubdivisions.isEmpty()) {
                StepSizeSettings.setSubdivisions(customSubdivisions);
            }
        } else {
            enableSetting(StepSizeSettings.stepSizSubdivisionSetting);
        }
    }

    public static void toggleCustomPresetNoteLengthSetting(String newValue) {
        if (newValue.equals(TOGGLE_FROM_PRESET) && getPatternType().equals(PATTERN_TYPE_PRESET)) {
            disableSetting(StepSizeSettings.noteLengthSetting);

            // Apply the stored preset note length if available
            String customNoteLength = StepSizeSettings.getCustomNoteLength();
            if (customNoteLength != null && !customNoteLength.isEmpty()) {
                StepSizeSettings.setNoteLength(customNoteLength);
            }
        } else {
            enableSetting(StepSizeSettings.noteLengthSetting);
        }
    }

    // Data setter utility methods
    private static void setPatternString(String patternByName) {
        String patternType = getPatternType();
        if (patternType.equals(PATTERN_TYPE_PROGRAM)) {
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

    // Getter for pattern replace/add toggle
    public static String getPatternReplaceAddMode() {
        return ((EnumValue) patternReplaceAddToggle).get();
    }
}