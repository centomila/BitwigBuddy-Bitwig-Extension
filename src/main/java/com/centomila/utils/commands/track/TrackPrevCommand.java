package com.centomila.utils.commands.track;

import com.centomila.utils.commands.BaseCommand;
import com.centomila.BitwigBuddyExtension;

/**
 * Command to navigate to the previous track.
 */
public class TrackPrevCommand extends BaseCommand {

    @Override
    public void execute(String[] args, BitwigBuddyExtension extension) {
        // Logic to navigate to the previous track using the provided extension
        extension.getCursorTrack().selectPrevious();
    }
}