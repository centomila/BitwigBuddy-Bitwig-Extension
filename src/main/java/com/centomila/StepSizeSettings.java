package com.centomila;

import static com.centomila.utils.SettingsHelper.*;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;

public class StepSizeSettings {
      // Step Size / Note Length settings
      public static Setting noteLengthSetting; // How long each note should be
      public static Setting stepSizSetting;
      public static Setting stepSizSubdivisionSetting;
      
      // Class variables to store preset values (instead of UI settings)
      private static String customPresetStepSize = "";
      private static String customPresetSubdivisions = "";
      private static String customPresetNoteLength = "";
      
      // Other settings
      public static Setting[] allSettings;
      public static String CATEGORY_CLIP = "5 Clip";

      public static void init(BitwigBuddyExtension extension) {
            Setting spacerStepSize = (Setting) createStringSetting(
                        titleWithLine("STEP SIZE - NOTE LENGTH"),
                        CATEGORY_CLIP, 0,
                        "---------------------------------------------------");

            disableSetting(spacerStepSize); // Spacers are always disabled

            // Step Size
            stepSizSetting = (Setting) createEnumSetting(
                        "Step Size",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");

            // Subdivisions
            stepSizSubdivisionSetting = (Setting) createEnumSetting(
                        "Subdivisions",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_CATEGORY_OPTIONS,
                        "Straight");

            // Note Length
            noteLengthSetting = (Setting) createEnumSetting(
                        "Note Length",
                        CATEGORY_CLIP,
                        Utils.STEPSIZE_OPTIONS,
                        "1/16");

            setupStepSizeObservers(extension);
            allSettings = new Setting[] {
                        spacerStepSize,
                        stepSizSetting,
                        stepSizSubdivisionSetting,
                        noteLengthSetting
            };
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

      public static String getCustomStepSize() {
            return customPresetStepSize;
      }

      public static String getCustomSubdivisions() {
            return customPresetSubdivisions;
      }

      public static String getCustomNoteLength() {
            return customPresetNoteLength;
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

      public static void setCustomStepSize(String stepSize) {
            customPresetStepSize = stepSize;
      }

      public static void setCustomSubdivisions(String subdivisions) {
            customPresetSubdivisions = subdivisions;
      }

      public static void setCustomNoteLength(String noteLength) {
            customPresetNoteLength = noteLength;
      }

      // Hide and show settings

      public static void hideAllStepSizeSettings() {
            hideSetting(allSettings);
      }

      public static void showAllStepSizeSettings() {
            showSetting(allSettings);
      }
}
