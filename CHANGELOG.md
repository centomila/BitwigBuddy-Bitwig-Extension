# 📋 Changelog:

**0.7.0**
- [X] Fixed Quintuplets (5t) and Septuplets (7t).
- [X] Added 1/1 step size/note length.
- [X] Added a "Move Steps" button to shift the pattern by one step in the x-direction.
  - ⚠️ This creates a lot of actions! Once done, you will not be able to undo any action prior to the "Move Steps" action.
- [X] Added a "Reverse Pattern" checkbox to reverse the pattern before it is generated.
  - This is not the _Reverse_ or _Reverse Pattern_ action in Bitwig Studio. It simply flips the order of the pattern selected by the user.


0.6.2
- [X] Link to github project in the extension panel
- [X] Support for Quintuplets (5t) and Septuplets (7t)

0.6.0
- [X] Added more patterns  
- [X] Support for dotted notes in note length and step size  
- [X] Note length now automatically follows the step size  
- [X] The clip is automatically resized to match the pattern length  
  - [X] Option to disable this behavior  
- [X] Button to manually clear the clip completely  
- [X] Extracted some generic methods in Utils class

0.5.3
- [X] Added more patterns

0.5.2
- [X] Replaced the old method to select note destination with a new method to select note destination based on Note Name+Octave

0.5.0
- [X] Support for multiple step sizes and note lengths in the pattern!
  - 1/2  |  1/4  |  1/8  |  1/8  |  1/16  |  1/32  |  1/32  |  1/64  |  1/128
  - 1/2 - 3t  |  1/4 - 3t  | 1/8 - 3t  |  1/16 - 3t  |  1/32 - 3t  |  1/32 - 3t  |  1/64 - 3t  |  1/128 - 3t
- [X] Button to clear the pattern of all notes
- [X] Renamed some fields for clarity
- [X] Tooltip notification with the current note name when the user change the note destination or a MIDI Note number.

0.4.5
- [X] Support for clip in the arranger with the _Launcher/Arranger_ option

0.4
- [X] Separated fields for changing the note destination  
- [X] Added a Show/Hide button for note destination fields  
- [X] Removed GMDrums as the default note destination selector  
- [X] Removed _Track Cursor_  
- [X] Moved the Generate button to the top of the UI  
- [X] Refactored the code  
- [X] Added Random pattern!