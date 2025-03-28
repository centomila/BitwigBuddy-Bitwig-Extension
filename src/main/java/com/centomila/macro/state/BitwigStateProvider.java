package com.centomila.macro.state;

/**
 * Interface for providing state information from Bitwig Studio.
 * Allows macro scripts to access runtime values from the DAW.
 */
public interface BitwigStateProvider {
    // Track related methods
    String getCurrentTrackName();
    int getCurrentTrackNumber();
    String getCurrentTrackColor();
    boolean isCurrentTrackMuted();
    boolean isCurrentTrackSoloed();
    boolean isCurrentTrackArmed();
    double getCurrentTrackVolume();
    double getCurrentTrackPan();
    int getTrackCount();
    
    // Device related methods
    String getCurrentDeviceName();
    boolean isCurrentDeviceEnabled();
    boolean isCurrentDeviceWindowOpen();
    int getDeviceCount();
    
    // Clip related methods
    String getCurrentClipName();
    String getCurrentClipColor();
    boolean isCurrentClipLooping();
    double getCurrentClipLength();
    boolean isCurrentClipPlaying();
    boolean isCurrentClipRecording();
    boolean isCurrentClipSelected();
    
    // Transport related methods
    double getCurrentBpm();
    int getTimeSignatureNumerator();
    int getTimeSignatureDenominator();
    boolean isPlaying();
    boolean isRecording();
    double getPlayPosition();
    boolean isMetronomeEnabled();
    boolean isArrangerLoopEnabled();
    
    // Project related methods
    String getProjectName();
    
    // Scene related methods
    int getCurrentSceneIndex();
    String getCurrentSceneName();
    
    // Utility methods
    boolean supportsMethod(String methodName);
    Object callMethod(String methodName);
}
