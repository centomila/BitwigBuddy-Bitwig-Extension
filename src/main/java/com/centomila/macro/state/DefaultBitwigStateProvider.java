package com.centomila.macro.state;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.centomila.BitwigBuddyExtension;

import static com.centomila.utils.PopupUtils.console;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of BitwigStateProvider that uses BitwigBuddyExtension.
 */
public class DefaultBitwigStateProvider implements BitwigStateProvider {
    private final BitwigBuddyExtension extension;
    private final Map<String, Method> methodMap = new HashMap<>();

    public DefaultBitwigStateProvider(BitwigBuddyExtension extension) {
        this.extension = extension;
        registerMethods();
    }

    private void registerMethods() {
        try {
            // Register all the methods in this class that match the BitwigStateProvider
            // interface
            for (Method method : BitwigStateProvider.class.getDeclaredMethods()) {
                if (method.getParameterCount() == 0 && !method.getName().equals("supportsMethod")
                        && !method.getName().equals("callMethod")) {
                    methodMap.put(method.getName(), method);
                }
            }
        } catch (Exception e) {
            extension.getHost().errorln("Error registering methods: " + e.getMessage());
        }
    }

    // Track related methods
    @Override
    public String getCurrentTrackName() {
        return extension.getCursorTrack().name().get();
    }

    @Override
    public int getCurrentTrackNumber() {
        return extension.getTrackBank().cursorIndex().getAsInt() + 1; // 0-based index
    }

    @Override
    public String getCurrentTrackColor() {
        String color = extension.getCursorTrack().color().get().toHex();
        // remove #
        color = color.substring(1).toUpperCase();
        return color;
    }

    @Override
    public boolean isCurrentTrackMuted() {
        Boolean muted = extension.getCursorTrack().mute().get();
        return muted != null ? muted : false;
    }

    @Override
    public boolean isCurrentTrackSoloed() {
        Boolean soloed = extension.getCursorTrack().solo().get();
        return soloed != null ? soloed : false;
    }

    @Override
    public boolean isCurrentTrackArmed() {
        Boolean armed = extension.getTrackBank().getItemAt(extension.getTrackBank().cursorIndex().get()).arm().get();
        return armed != null ? armed : false;
    }

    @Override
    public double getCurrentTrackVolume() {
        return extension.getCursorTrack().volume().get();
    }

    @Override
    public double getCurrentTrackPan() {
        return extension.getCursorTrack().pan().get();
    }

    @Override
    public int getTrackCount() {
        return extension.getTrackBank().itemCount().get();
    }

    // Device related methods
    @Override
    public String getCurrentDeviceName() {
        return extension.getCursorDevice().name().get();
    }

    @Override
    public boolean isCurrentDeviceEnabled() {
        Boolean enabled = extension.getCursorDevice().isEnabled().get();
        return enabled != null ? enabled : false;
    }

    @Override
    public boolean isCurrentDeviceWindowOpen() {
        Boolean windowOpen = extension.getCursorDevice().isWindowOpen().get();
        return windowOpen != null ? windowOpen : false;
    }

    @Override
    public int getDeviceCount() {
        return extension.getDeviceBank().itemCount().get();
    }

    // Clip related methods
    @Override
    public String getCurrentClipName() {
        return extension.clipLauncherSlot.name().get();
    }

    @Override
    public String getCurrentClipColor() {
        String color = extension.clipLauncherSlot.color().get().toHex();
        // remove #
        color = color.substring(1).toUpperCase();
        return color;
    }

    @Override
    public boolean isCurrentClipLooping() {
        Boolean looping = extension.getLauncherOrArrangerAsClip().isLoopEnabled().get();
        return looping != null ? looping : false;
    }

    @Override
    public double getCurrentClipLength() {
        return extension.getLauncherOrArrangerAsClip().getLoopLength().get();
    }

    @Override
    public boolean isCurrentClipPlaying() {
        Boolean playing = extension.clipLauncherSlot.isPlaying().get();
        return playing != null ? playing : false;
    }

    @Override
    public boolean isCurrentClipRecording() {
        Boolean recording = extension.clipLauncherSlot.isRecording().get();
        return recording != null ? recording : false;
    }

    @Override
    public boolean isCurrentClipSelected() {
        Boolean selected = extension.clipLauncherSlot.isSelected().get();
        return selected != null ? selected : false;
    }

    // Transport related methods
    @Override
    public double getCurrentBpm() {
        return extension.getTransport().tempo().getRaw();
    }

    @Override
    public int getTimeSignatureNumerator() {
        return extension.getTransport().timeSignature().numerator().get();
    }

    @Override
    public int getTimeSignatureDenominator() {
        return extension.getTransport().timeSignature().denominator().get();
    }

    @Override
    public boolean isPlaying() {
        Boolean playing = extension.getTransport().isPlaying().get();
        return playing != null ? playing : false;
    }

    @Override
    public boolean isRecording() {
        Boolean recording = extension.getTransport().isArrangerRecordEnabled().get();
        return recording != null ? recording : false;
    }

    @Override
    public double getPlayPosition() {
        return extension.getTransport().playPosition().get();
    }

    @Override
    public boolean isMetronomeEnabled() {
        Boolean metronomeEnabled = extension.getTransport().isMetronomeEnabled().get();
        return metronomeEnabled != null ? metronomeEnabled : false;
    }

    @Override
    public boolean isArrangerLoopEnabled() {
        Boolean arrangerLoopEnabled = extension.getTransport().isArrangerLoopEnabled().get();
        return arrangerLoopEnabled != null ? arrangerLoopEnabled : false;
    }

    // Project related methods
    @Override
    public String getProjectName() {
        return extension.application.projectName().get();
    }

    // Scene related methods
    @Override
    public int getCurrentSceneIndex() {
        return extension.sceneBank.cursorIndex().get();
    }

    @Override
    public String getCurrentSceneName() {
        return extension.sceneBank.getItemAt(extension.sceneBank.cursorIndex().get()).name().get();
    }

    // Utility methods
    @Override
    public boolean supportsMethod(String methodName) {
        return methodMap.containsKey(methodName);
    }

    @Override
    public Object callMethod(String methodName) {
        try {
            Method method = methodMap.get(methodName);
            if (method != null) {
                Object result = method.invoke(this);
                extension.getHost().println("Method '" + methodName + "' returned: " + result);
                return result;
            } else {
                extension.getHost().errorln("Method not found: " + methodName);
            }
        } catch (Exception e) {
            extension.getHost().errorln("Error calling method " + methodName + ": " + e.getMessage());
        }
        return null;
    }
}
