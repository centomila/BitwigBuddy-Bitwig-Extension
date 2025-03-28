package com.centomila.macro.state;

import com.centomila.BitwigBuddyExtension;
import com.bitwig.extension.api.Color;
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
        return extension.getCursorTrack().mute().get();
    }

    @Override
    public boolean isCurrentTrackSoloed() {
        return extension.getCursorTrack().solo().get();
    }

    @Override
    public boolean isCurrentTrackArmed() {
        return extension.getCursorTrack().arm().get();
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
        return extension.getCursorDevice().isEnabled().get();
    }

    @Override
    public boolean isCurrentDeviceWindowOpen() {
        return extension.getCursorDevice().isWindowOpen().get();
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
        // Need to check which clip is active (launcher or arranger)
        return extension.getLauncherOrArrangerAsClip().isLoopEnabled().get();
    }

    @Override
    public double getCurrentClipLength() {
        return extension.getLauncherOrArrangerAsClip().getLoopLength().get();
    }

    @Override
    public boolean isCurrentClipPlaying() {
        return extension.clipLauncherSlot.isPlaying().get();
    }

    @Override
    public boolean isCurrentClipRecording() {
        return extension.clipLauncherSlot.isRecording().get();
    }

    @Override
    public boolean isCurrentClipSelected() {
        return extension.clipLauncherSlot.isSelected().get();
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
        return extension.getTransport().isPlaying().get();
    }

    @Override
    public boolean isRecording() {
        return extension.getTransport().isArrangerRecordEnabled().get();
    }

    @Override
    public double getPlayPosition() {
        return extension.getTransport().playPosition().get();
    }

    @Override
    public boolean isMetronomeEnabled() {
        return extension.getTransport().isMetronomeEnabled().get();
    }

    @Override
    public boolean isArrangerLoopEnabled() {
        return extension.getTransport().isArrangerLoopEnabled().get();
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
                return method.invoke(this);
            }
        } catch (Exception e) {
            extension.getHost().errorln("Error calling method " + methodName + ": " + e.getMessage());
        }
        return null;
    }
}
