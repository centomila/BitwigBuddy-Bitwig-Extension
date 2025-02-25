package com.centomila.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.stage.Stage;
import com.bitwig.extension.controller.api.ControllerHost;

public class JavaFXInitializer {
    private static final Object jfxInitLock = new Object();
    private static boolean jfxInitialized = false;
    
    public static boolean initialize(ControllerHost host) {
        if (jfxInitialized) {
            host.println("JavaFX already initialized, skipping initialization");
            return true;
        }

        synchronized (jfxInitLock) {
            if (!jfxInitialized) {
                host.println("Starting JavaFX initialization...");
                try {
                    if (host.platformIsMac()) {
                        host.println("Setting Mac-specific JavaFX properties");
                        System.setProperty("javafx.toolkit", "com.sun.javafx.tk.quantum.QuantumToolkit");
                        System.setProperty("glass.platform", "mac");
                    }

                    if (!Platform.isFxApplicationThread()) {
                        host.println("Not on FX thread, starting platform...");
                        final CountDownLatch initLatch = new CountDownLatch(1);

                        Platform.startup(() -> {
                            host.println("In Platform.startup callback");
                            try {
                                host.println("Attempting to create test Stage");
                                new Stage();
                                host.println("JavaFX initialized successfully");
                                jfxInitialized = true;
                            } catch (Exception e) {
                                host.errorln("JavaFX Stage creation failed: " + e.getMessage());
                                e.printStackTrace();
                            } finally {
                                host.println("Countdown latch released");
                                initLatch.countDown();
                            }
                        });

                        host.println("Waiting for initialization completion...");
                        if (!initLatch.await(5, TimeUnit.SECONDS)) {
                            host.errorln("JavaFX initialization timed out after 5 seconds");
                            return false;
                        }
                        host.println("Initialization wait completed");

                    } else {
                        host.println("Already on FX thread, marking as initialized");
                        jfxInitialized = true;
                    }
                } catch (IllegalStateException e) {
                    host.println("JavaFX toolkit already running: " + e.getMessage());
                    jfxInitialized = true;
                } catch (Exception e) {
                    host.errorln("JavaFX initialization failed with: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
                host.println("JavaFX initialization process complete. Success: " + jfxInitialized);
            }
        }
        return jfxInitialized;
    }

    public static boolean isInitialized() {
        return jfxInitialized;
    }
}