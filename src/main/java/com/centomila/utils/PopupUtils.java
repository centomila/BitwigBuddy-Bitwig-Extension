package com.centomila.utils;

import com.bitwig.extension.controller.api.ControllerHost;

public class PopupUtils {
    private static ControllerHost host;

    /**
     * @param controllerHost
     */
    public static void init(ControllerHost controllerHost) {
        host = controllerHost;
    }

    /**
     * @param message
     */
    public static void showPopup(String message) {
        if (host != null) {
            host.showPopupNotification(message);
        }
    }

    // Console message
    public static void console(String message) {
        if (host != null) {
            host.println(message);
        }
    }
}
