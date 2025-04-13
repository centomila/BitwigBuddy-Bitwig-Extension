package com.centomila.utils.commands.clip;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.centomila.utils.commands.BaseCommand;

/**
 * Command to set the length of the currently selected clip.
 */
public class ClipLengthCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateParamCount(params, 1, extension)) {
            return;
        }

        try {
            double length = Double.parseDouble(params[0].trim());
            // extension.getLauncherOrArrangerAsClip().getLoopLength().set(length);
            ClipUtils.setLoopLength(extension.getLauncherOrArrangerAsClip(), 0.0, length);

        } catch (NumberFormatException e) {
            reportError("Invalid length value: " + params[0], extension);
        }
    }
}