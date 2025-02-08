package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;

public class PopupUtils {
    private static ControllerHost host;

    public static void initialize(ControllerHost controllerHost) {
        host = controllerHost;
    }

    public static void showPopup(String message) {
        if (host != null) {
            host.showPopupNotification(message);
        }
    }
}
