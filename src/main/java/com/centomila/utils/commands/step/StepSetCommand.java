package com.centomila.utils.commands.step;

import com.centomila.BitwigBuddyExtension;
import com.centomila.utils.commands.BaseCommand;
import com.bitwig.extension.controller.api.Clip;

/**
 * Command to set a step value using the Bitwig API.
 */
public class StepSetCommand extends BaseCommand {

    @Override
    public void execute(String[] params, BitwigBuddyExtension extension) {
        if (!validateMinParamCount(params, 5, extension)) {
            return;
        }

        try {
            int channel = Integer.parseInt(params[0]);
            int stepIndex = Integer.parseInt(params[1]);
            int noteDestination = Integer.parseInt(params[2]);
            int velocity = Integer.parseInt(params[3]);
            double duration = Double.parseDouble(params[4]);

            Clip clip = extension.getLauncherOrArrangerAsClip();
            if (clip == null) {
                extension.getHost().errorln("No clip is currently selected.");
                return;
            }

            // Call the API method to set the step value
            extension.getHost().println("Setting step " + stepIndex + " on channel " + channel + " to value: " + velocity);
            clip.setStep(channel, stepIndex, noteDestination, velocity, duration);
        } catch (NumberFormatException e) {
            extension.getHost().errorln("Invalid number format in parameters: " + e.getMessage());
        } catch (Exception e) {
            extension.getHost().errorln("Error setting step: " + e.getMessage());
        }
    }
}