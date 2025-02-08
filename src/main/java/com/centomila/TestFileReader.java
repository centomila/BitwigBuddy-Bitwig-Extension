package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Signal;
import com.bitwig.extension.controller.api.StringValue;

public class TestFileReader {
    private final ControllerHost host;
    private final DocumentState documentState;
    private final SettableStringValue filePathSetting;

    public TestFileReader(ControllerHost host, DocumentState documentState) {
        this.host = host;
        this.documentState = documentState;
        
        // Initialize file path setting
        Preferences preferences = host.getPreferences();
        this.filePathSetting = preferences.getStringSetting(
            "Test File Path", 
            "File Settings", 
            1024,
            "test.txt"
        );

        // initTestButton(); // Uncomment this line to enable the test button
    }

    private void initTestButton() {
        Signal testButton = documentState.getSignalSetting("Test", "Generate", "Test");
        testButton.addSignalObserver(() -> {
            String path = ((StringValue) filePathSetting).get();
            host.println("File path: " + path);
            ReadFile readFile = new ReadFile(path);
            String fileContent = readFile.readFileAsString();
            host.println("File content: " + fileContent);
        });
    }
}