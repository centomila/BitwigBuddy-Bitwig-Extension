package com.centomila;

import static com.centomila.utils.SettingsHelper.createSignalSetting;
import static com.centomila.utils.SettingsHelper.createStringSetting;
import static com.centomila.utils.SettingsHelper.disableSetting;    

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

public class EditClipSettings {
    public static Setting editClipSpacer;
    public static Setting editClipBtnSignal;
    public static Setting[] allSettings;
    private static final String CATEGORY_EDIT_CLIP = "Edit Clip";

    public static void init(BitwigBuddyExtension extension) {
        initEditClipSettings();
    }

    private static void initEditClipSettings() {
        editClipSpacer = (Setting) createStringSetting("Edit Clip Spacer", CATEGORY_EDIT_CLIP, 0, "---------------------------------------------------");
        disableSetting(editClipSpacer);

        editClipBtnSignal = (Setting) createSignalSetting("Edit Clip", CATEGORY_EDIT_CLIP, "Edit Clip");

        allSettings = new Setting[] { editClipSpacer, editClipBtnSignal };
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
