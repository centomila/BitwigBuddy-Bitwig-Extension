package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ModeSelectSettings;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.ClipLauncherSlot;

import static com.centomila.utils.PopupUtils.showPopup;

/**
 * Command to move the selected clip by a specified amount.
 */
public class ClipMoveCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }

        try {
            int currentTrack = extension.trackBank.cursorIndex().getAsInt();
            if (currentTrack < 0) {
                extension.getHost().println("No track selected, using first track (index 0)");
                currentTrack = 0;
            }

            double moveAmount = Double.parseDouble(params[0].trim());

            if (ModeSelectSettings.getCurrentLauncherArrangerToggleString().equals("Arranger")) {
                moveInArranger(moveAmount, extension);
            } else {
                moveInLauncher(moveAmount, extension, currentTrack);
            }
        } catch (NumberFormatException e) {
            reportError("Invalid move amount: " + params[0], extension);
        }
    }

    private void moveInArranger(double moveAmount, BitwigBuddyExtension extension) {
        if (moveAmount > 0) {
            for (int i = 0; i < moveAmount; i++) {
                extension.getApplication().getAction("nudge_events_one_step_later").invoke();
            }
        } else if (moveAmount < 0) {
            for (int i = 0; i < Math.abs(moveAmount); i++) {
                extension.getApplication().getAction("nudge_events_one_step_earlier").invoke();
            }
        }
    }

    private void moveInLauncher(double moveAmount, BitwigBuddyExtension extension, int currentTrack) {
        ClipLauncherSlot sourceSlot = extension.getLauncherOrArrangerAsClip().clipLauncherSlot();

        int sourceSlotIndex = sourceSlot.sceneIndex().get();
        int targetSlotIndex = sourceSlotIndex + (int) moveAmount;

        showPopup("Source slot index: " + sourceSlotIndex + " Target slot index: " + targetSlotIndex);

        if (targetSlotIndex >= 0 && targetSlotIndex < 128) {
            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().getItemAt(targetSlotIndex)
                    .replaceInsertionPoint().moveSlotsOrScenes(sourceSlot);

            extension.trackBank.getItemAt(currentTrack).clipLauncherSlotBank().getItemAt(targetSlotIndex).select();
            extension.sceneBank.cursorIndex().set(targetSlotIndex);
        } else {
            showPopup("Invalid target slot index: " + targetSlotIndex);
        }
    }
}
