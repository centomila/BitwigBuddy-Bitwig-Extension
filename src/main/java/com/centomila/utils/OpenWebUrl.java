package com.centomila.utils;

import java.io.IOException;
import com.bitwig.extension.controller.api.ControllerHost;
import static com.centomila.utils.PopupUtils.showPopup;

public class OpenWebUrl {
    public enum PlatformCommand {
        WINDOWS("explorer.exe", "cmd", "/c", "start"),
        MAC("open", "open", "", ""),
        LINUX("xdg-open", "xdg-open", "", "");

        final String fileExplorer;
        final String browserCommand;
        final String browserParam1;
        final String browserParam2;

        PlatformCommand(String fileExplorer, String browserCommand, String browserParam1, String browserParam2) {
            this.fileExplorer = fileExplorer;
            this.browserCommand = browserCommand;
            this.browserParam1 = browserParam1;
            this.browserParam2 = browserParam2;
        }
    }

    public static void openUrl(ControllerHost host, String url, String pageName) {
        try {
            PlatformCommand cmd = getPlatformCommand(host);
            String[] command = cmd.browserParam1.isEmpty()
                    ? new String[] { cmd.browserCommand, url }
                    : new String[] { cmd.browserCommand, cmd.browserParam1, cmd.browserParam2, url };

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            host.errorln("Failed to open " + pageName + " page: " + e.getMessage());
            showPopup("Please visit " + url + " in your web browser.");
        }
    }

    private static PlatformCommand getPlatformCommand(ControllerHost host) {
        if (host.platformIsWindows())
            return PlatformCommand.WINDOWS;
        if (host.platformIsMac())
            return PlatformCommand.MAC;
        return PlatformCommand.LINUX;
    }
}
