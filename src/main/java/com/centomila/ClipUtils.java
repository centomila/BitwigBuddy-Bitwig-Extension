package com.centomila;

import java.util.Comparator;
import java.util.List;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.NoteStep;
import com.bitwig.extension.controller.api.Setting;

public class ClipUtils {
    /**
     * Returns the Clip object for either the Arranger Clip Launcher or the Launcher
     * Clip depending on the value of the "Launcher/Arranger" setting.
     * 
     * @return A Clip object, either the Arranger Clip Launcher or the Launcher
     *         Clip.
     */
    public static Clip getLauncherOrArrangerAsClip(Setting toggleSetting, Clip arrangerClip, Clip cursorClip) {
        String launcherArrangerSelection = ((EnumValue) toggleSetting).get();
        return launcherArrangerSelection.equals("Arranger") ? arrangerClip : cursorClip;
    }

    /**
     * Sets the loop length of the given clip to a given start and end time in beats. 
     * Additionally sets the playback start and end times to the same values.
     */
    public static void setLoopLength(Clip clip, double loopStart, double loopEnd) {
        clip.getLoopStart().set(loopStart);
        clip.getLoopLength().set(loopEnd);
        clip.getPlayStart().set(loopStart);
        clip.getPlayStop().set(loopEnd);
    }

    /**
     * Moves steps in a clip by the given offset. The order of movement depends on the
     * direction to prevent overlapping.
     */
    public static void moveSteps(Clip clip, List<NoteStep> stepsToMove, int stepOffset, int channel, ControllerHost host) {
        if (stepOffset > 0) {
            stepsToMove.sort(Comparator.comparingInt(NoteStep::x).reversed());
        } else {
            stepsToMove.sort(Comparator.comparingInt(NoteStep::x));
        }

        for (NoteStep step : stepsToMove) {
            if (step.x() == 0 && stepOffset < 0) {
                stepOffset = 0;
                host.showPopupNotification("Cannot move steps before the start of the clip");
            } else {
                clip.moveStep(channel, step.x(), step.y(), stepOffset, 0);
            }
        }
    }

    /**
     * Rotates steps in a clip by the given offset, wrapping around at clip boundaries.
     */
    public static void rotateSteps(Clip clip, List<NoteStep> stepsToRotate, int stepOffset, int loopLengthInt, int channel) {
        stepsToRotate.sort(Comparator.comparingInt(NoteStep::x).reversed());

        if (stepOffset > 0) { // rotate forwards
            for (NoteStep step : stepsToRotate) {
                clip.moveStep(channel, step.x(), step.y(), stepOffset, 0);
            }

            for (NoteStep step : stepsToRotate) {
                if (step.x() + stepOffset >= loopLengthInt) {
                    clip.moveStep(channel, step.x() + stepOffset, step.y(), -loopLengthInt + 1 - 1, 0);
                }
            }
        }

        if (stepOffset < 0) { // rotate backwards 
            stepOffset = (loopLengthInt) - 1;
            for (NoteStep step : stepsToRotate) {
                clip.moveStep(channel, step.x(), step.y(), stepOffset, 0);
            }

            for (NoteStep step : stepsToRotate) {
                if (step.x() + stepOffset >= loopLengthInt) {
                    clip.moveStep(channel, step.x() + stepOffset, step.y(), -loopLengthInt + 1 - 1, 0);
                }
            }
        }
    }
}
