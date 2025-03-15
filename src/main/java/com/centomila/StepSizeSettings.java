package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;

public class StepSizeSettings {
      // Step Size / Note Length settings
      public static Setting noteLengthSetting; // How long each note should be
      public static Setting stepSizSetting;
      public static Setting stepSizSubdivisionSetting;
      // Custom Preset settings
      public static Setting customPresetStepSizeSetting;
      public static Setting customPresetSubdivisionsSetting;
      public static Setting customPresetNoteLengthSetting;
      // Other settings
      public static Setting[] allSettings;
      public static String CATEGORY_CLIP = "5 Clip";

      public static void init(BitwigBuddyExtension extension) {
            Setting spacerStepSize = (Setting) createStringSetting(
                        titleWithLine("STEP SIZE - NOTE LENGTH"),
                        CATEGORY_CLIP, 0,
                        "---------------------------------------------------");

            disableSetting(spacerStepSize); // Spacers are always disabled

            stepSizSetting = (Setting) createEnumSetting(
                        "Step Size",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");
            stepSizSubdivisionSetting = (Setting) createEnumSetting(
                        "Subdivisions",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_CATEGORY_OPTIONS,
                        "Straight");
            noteLengthSetting = (Setting) createEnumSetting(
                        "Note Length",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");

            customPresetStepSizeSetting = (Setting) createStringSetting(
                        "Preset Step Size",
                        StepSizeSettings.CATEGORY_CLIP + " 2", 0,
                        "1/16");

            customPresetSubdivisionsSetting = (Setting) createStringSetting(
                        "Preset Subdivisions",
                        StepSizeSettings.CATEGORY_CLIP + " 2", 0,
                        "Straight");

            customPresetNoteLengthSetting = (Setting) createStringSetting(
                        "Preset Note Length",
                        StepSizeSettings.CATEGORY_CLIP + " 2", 0,
                        "1/16");

            setupStepSizeObservers(extension);
            allSettings = new Setting[] { spacerStepSize, stepSizSetting, stepSizSubdivisionSetting,
                        noteLengthSetting, customPresetStepSizeSetting, customPresetSubdivisionsSetting,
                        customPresetNoteLengthSetting };

      }

      private static void setupStepSizeObservers(BitwigBuddyExtension extension) {
            ((EnumValue) stepSizSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) stepSizSetting).set(newValue);
                  ((SettableEnumValue) noteLengthSetting).set(newValue);
            });

            ((EnumValue) noteLengthSetting).addValueObserver(newValue -> {
                  ((SettableEnumValue) noteLengthSetting).set(newValue);
            });
      }

      // Getters

      public static String getStepSize() {
            return ((EnumValue) stepSizSetting).get();
      }

      public static String getSubdivisions() {
            return ((EnumValue) stepSizSubdivisionSetting).get();
      }

      public static String getNoteLength() {
            return ((EnumValue) noteLengthSetting).get();
      }

      // Setters

      public static void setStepSize(String stepSize) {
            ((SettableEnumValue) stepSizSetting).set(stepSize);
      }

      public static void setSubdivisions(String subdivisions) {
            ((SettableEnumValue) stepSizSubdivisionSetting).set(subdivisions);
      }

      public static void setNoteLength(String noteLength) {
            ((SettableEnumValue) noteLengthSetting).set(noteLength);
      }

      // Hide and show settings

      public static void hideAllStepSizeSettings() {
            hideSetting(allSettings);
      }

      public static void showAllStepSizeSettings() {
            showSetting(allSettings);
      }

      public static void hideCustomPresetStepSizeSettings() {
            hideSetting(customPresetStepSizeSetting);
      }

      public static void showCustomPresetStepSizeSettings() {
            showSetting(customPresetStepSizeSetting);
      }

      public static void hideCustomPresetSubdivisionsSetting() {
            hideSetting(customPresetSubdivisionsSetting);
      }

      public static void showCustomPresetSubdivisionsSetting() {
            showSetting(customPresetSubdivisionsSetting);
      }

      public static void hideCustomPresetNoteLengthSetting() {
            hideSetting(customPresetNoteLengthSetting);
      }

      public static void showCustomPresetNoteLengthSetting() {
            showSetting(customPresetNoteLengthSetting);
      }

      public static void showCustomPresetSettings() {
            showSetting(customPresetStepSizeSetting);
            showSetting(customPresetSubdivisionsSetting);
            showSetting(customPresetNoteLengthSetting);
      }

      public static void hideCustomPresetSettings() {
            hideSetting(customPresetStepSizeSetting);
            hideSetting(customPresetSubdivisionsSetting);
            hideSetting(customPresetNoteLengthSetting);
      }

}
