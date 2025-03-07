package com.centomila.utils;

import com.centomila.BitwigBuddyExtension;
import com.centomila.ClipUtils;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableColorValue;
import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ColorValue;

public class ExecuteBitwigAction {
    
    public static void executeBitwigAction(String actionId, BitwigBuddyExtension extension) {
        ControllerHost host = extension.getHost();
        host.println("Executing Bitwig action: " + actionId);
        // strip the bb: prefix
        actionId = actionId.split(":")[1].trim();
        String[] params;
        // action id could be like bb:actionId(param1, param2). Parameters are separated by comma. Qty of parameters is not fixed.
        if (actionId.contains("(")) {
            int start = actionId.indexOf("(");
            int end = actionId.indexOf(")");
            String paramsStr = actionId.substring(start + 1, end).trim();
            actionId = actionId.substring(0, start).trim();
            params = paramsStr.split(",");
        } else {
            params = new String[0];
        }
        // actionId without parameters
        // Remove parameters from actionId if present
        if (actionId.contains("(")) {
            actionId = actionId.substring(0, actionId.indexOf("("));
        }
        
        host.println("Executing Bitwig action: " + actionId);
        // switch case actionId. Case 1 starts with NewCueMarker
        switch (actionId) {
            case "Left":
                extension.getApplication().arrowKeyLeft();
                break;
            case "Right":
                extension.getApplication().arrowKeyRight();
                break;
            case "Up":
                extension.getApplication().arrowKeyUp();
                break;
            case "Down":
                extension.getApplication().arrowKeyDown();
                break;
            case "Enter":
                extension.getApplication().enter();
                break;
            case "Escape":
                extension.getApplication().escape();
                break;
            case "Copy":
                extension.getApplication().copy();;
                break;
            case "Paste":
                extension.getApplication().paste();
                break;
            case "Cut":
                extension.getApplication().cut();
                break;
            case "Undo":
                extension.getApplication().undo();
                break;
            case "Redo":
                extension.getApplication().redo();
                break;
            case "Duplicate":
                extension.getApplication().duplicate();
                break;
            case "Select All":
                extension.getApplication().selectAll();
                break;
            case "Select None":
                extension.getApplication().selectNone();
                break;
            case "Select First":
                extension.getApplication().selectFirst();;
                break;
            case "Select Last":
                extension.getApplication().selectLast();
                break;
            case "Select Next":
                extension.getApplication().selectNext();
                break;
            case "SelectPrevious":
                extension.getApplication().selectPrevious();
                break;
            case "Project Name":
                extension.getApplication().projectName();
                break;
            case "Rename":
                extension.getApplication().rename();
                break;
            case "Clip Delete":
                extension.getLauncherOrArrangerAsClip().clipLauncherSlot().deleteObject();
                break;
            case "Clip Rename":
                extension.getLauncherOrArrangerAsClip().setName(params[0]);
                break;
            case "Clip Color":
                String colorStr = params[0].trim();
                Color color = Color.fromHex(colorStr);
                extension.getLauncherOrArrangerAsClip().color().set(color);
                break;
            
                
        }

    }
}
