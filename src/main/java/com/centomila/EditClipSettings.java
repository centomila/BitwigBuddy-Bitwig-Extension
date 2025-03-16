package com.centomila;

import static com.centomila.utils.PopupUtils.showPopup;
import static com.centomila.utils.SettingsHelper.createSignalSetting;
import static com.centomila.utils.SettingsHelper.createStringSetting;
import static com.centomila.utils.SettingsHelper.disableSetting;
import static com.centomila.utils.SettingsHelper.hideSetting;
import static com.centomila.utils.SettingsHelper.showSetting;
import static com.centomila.utils.SettingsHelper.titleWithLine;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

public class EditClipSettings {
    private static BitwigBuddyExtension extension;
    public static Setting editClipSpacer;
    public static Setting editClipBtnSignal;
    public static Setting[] allSettings;
    private static final String CATEGORY_EDIT_CLIP = "3 Edit Clip";

    public static void init(BitwigBuddyExtension extension) {
        EditClipSettings.extension = extension;
        initEditClipSettings();
    }

    private static void initEditClipSettings() {
        editClipSpacer = (Setting) createStringSetting(titleWithLine("EDIT CLIP ------------------------------------"), CATEGORY_EDIT_CLIP,9999,
                "---------------------------------------------------");
        disableSetting(editClipSpacer);

        editClipBtnSignal = (Setting) createSignalSetting("Update Selected Steps Velocity", CATEGORY_EDIT_CLIP, "Update Selected Steps Velocity");

        
        // Obsereve the edit clip button signal
        ((Signal) editClipBtnSignal).addSignalObserver(() -> {
            editClipAction();
        });
        allSettings = new Setting[] { editClipSpacer, editClipBtnSignal };
    }

    public static void editClipAction() {
        // Create an array with the selected notes and randomize the velocity of each note
        
        // Get the selected clip
        Clip clip = extension.getLauncherOrArrangerAsClip();
        if (clip == null) {
            showPopup("No clip selected");
            return;
        }


        try {
            ClipUtils.applyVelocityShapeToSelectedNotes(extension);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
        



    }

    public static void showAllSettings() {
        showSetting(allSettings);
    }

    public static void hideAllSettings() {
        hideSetting(allSettings);
    }

}
