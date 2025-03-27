package com.centomila.macro.state;

/**
 * Interface for providing state information from Bitwig Studio.
 * Allows macro scripts to access runtime values from the DAW.
 */
public interface BitwigStateProvider {
    /**
     * Get the name of the currently selected track.
     */
    String getCurrentTrackName();
    
    /**
     * Get the number/index of the currently selected track.
     */
    int getCurrentTrackNumber();
    
    /**
     * Get the name of the currently selected device.
     */
    String getCurrentDeviceName();
    
    /**
     * Get the current project BPM.
     */
    double getCurrentBpm();
    
    /**
     * Get the current time signature numerator.
     */
    int getTimeSignatureNumerator();
    
    /**
     * Get the current time signature denominator.
     */
    int getTimeSignatureDenominator();
    
    /**
     * Check if a method is supported by this provider.
     */
    boolean supportsMethod(String methodName);
    
    /**
     * Call a method by name with no arguments.
     */
    Object callMethod(String methodName);
}
