package com.centomila.macro.state;

import com.centomila.BitwigBuddyExtension;
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
            // Register all the methods in this class that match the BitwigStateProvider interface
            for (Method method : BitwigStateProvider.class.getDeclaredMethods()) {
                if (method.getParameterCount() == 0 && !method.getName().equals("supportsMethod") && !method.getName().equals("callMethod")) {
                    methodMap.put(method.getName(), method);
                }
            }
        } catch (Exception e) {
            extension.getHost().errorln("Error registering methods: " + e.getMessage());
        }
    }
    
    @Override
    public String getCurrentTrackName() {
        return extension.getCursorTrack().name().get();
    }
    
    @Override
    public int getCurrentTrackNumber() {
        return extension.getTrackBank().cursorIndex().getAsInt() + 1; // 0-based index
    }
    
    @Override
    public String getCurrentDeviceName() {
        return extension.getCursorDevice().name().get();
    }
    
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
