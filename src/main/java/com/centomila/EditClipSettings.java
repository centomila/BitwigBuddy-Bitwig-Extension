package com.centomila;

import static com.centomila.utils.PopupUtils.showPopup;
import static com.centomila.utils.SettingsHelper.createSignalSetting;
import static com.centomila.utils.SettingsHelper.createStringSetting;
import static com.centomila.utils.SettingsHelper.disableSetting;

import javax.swing.text.Document;

import com.centomila.VelocityShape;
import com.bitwig.extension.controller.api.BeatTimeFormatter;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.NoteStep;
import com.bitwig.extension.controller.api.SettableBeatTimeValue;
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
        editClipSpacer = (Setting) createStringSetting("Edit Clip------------------------", CATEGORY_EDIT_CLIP,9999,
                "---------------------------------------------------");
        disableSetting(editClipSpacer);

        editClipBtnSignal = (Setting) createSignalSetting("Edit Clip", CATEGORY_EDIT_CLIP, "Edit Clip");

        
        // Obsereve the edit clip button signal
        ((Signal) editClipBtnSignal).addSignalObserver(() -> {
            editClipAction();
        });
        allSettings = new Setting[] { editClipSpacer, editClipBtnSignal };
    }

    public static void editClipAction() {
        showPopup("Edit Clip");
        // Create an array with the selected notes and randomize the velocity of each note
        
        // Get the selected clip
        Clip clip = extension.getLauncherOrArrangerAsClip();
        if (clip == null) {
            showPopup("No clip selected");
            return;
        }

        // Get the clip size
        // SettableBeatTimeValue  clipStart = clip.getPlayStart();
        // SettableBeatTimeValue  clipStop = clip.getPlayStop();
        
        // // Get the clip length
        // double clipLength = clipStop.get() - clipStart.get();
        // showPopup("Clip length: " + clipLength);

        ClipUtils.returnSelectedSteps(extension);
        
        



    }

    public static void showEditClipSettings() {
        for (Setting setting : allSettings) {
            setting.show();
        }
    }

    public static void hideEditClipSettings() {
        for (Setting setting : allSettings) {
            setting.hide();
        }
    }

}
