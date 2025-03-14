# BitwigBuddy Actions           

| Action                                       | Description                                                   | Example                                    |
|----------------------------------------------|---------------------------------------------------------------|--------------------------------------------|
| **Bpm**                                      | Sets the tempo (beats per minute)                             | _Bpm (120)_                                |
| **CueMarkerName**                            | Sets the name of a specified cue marker by index              | _CueMarkerName (2, Verse 1)_               |
| **DeleteAllCueMarkers**                      | Deletes all existing cue markers                              |                                            |
| **Left / Right / Up / Down**                 | Navigates within the Bitwig UI using arrow-key commands       |                                            |
| **Enter**                                    | Triggers the Enter key action in Bitwig                       |                                            |
| **Escape**                                   | Triggers the Escape key action in Bitwig                      |                                            |
| **Copy / Paste / Cut**                       | Performs clipboard operations (copy, paste, cut)              |                                            |
| **Undo / Redo**                              | Performs undo or redo in Bitwig                               |                                            |
| **Duplicate**                                | Duplicates the currently selected item                        |                                            |
| **Select All / Select None**                 | Select Actions                                                |                                            |
| **Select First / Select Last**               | Select Actions                                                |                                            |
| **Select Next / Select Previous**            | Select Actions                                                |                                            |
| **Clip Select**                              | Selects the currently focused clip slot                       |                                            |
| **Clip Duplicate**                           | Duplicates the currently focused clip                         |                                            |
| **Project Name**                             | Shows the current project name                                |                                            |
| **Rename**                                   | Renames the currently selected item                           |                                            |
| **Clip Delete**                              | Deletes the currently focused clip                            |                                            |
| **Clip Rename**                              | Renames the currently focused clip                            |_Clip Rename (Verse 1)_                     |
| **Clip Color**                               | Sets the color of the currently focused clip                  |_Clip Color (FF0000)_                       |
| **Clip Create**                              | Create an empty clip. Param 1 = Slot ~ Param 2 Loop Length    |_Clip Create (3,32)_                        |
| **Clip Loop On**                             | Enable Clip Looping Mode for the selected clip                |_Clip Loop On_                              |
| **Clip Loop Off**                            | Disable Clip Looping Mode for the selected clip               |_Clip Loop Off_                             |
| **Clip Accent**                              | Set the Clip Accent value (range 0.0-1.0. 0.752 = Acc 75.2%)  |_Clip Accent (0.75)_                        |
| **Track Color**                              | Sets the color of the currently selected track                |                                            |
| **Track Rename**                             | Renames the currently selected track                          |                                            |
| **Track Select**                             | Selects a track by index                                      |                                            |
| **Insert Device**                            | Inserts a Bitwig device on the current track as last          |_Insert Device (Drum Machine)_              |
| **Insert VST3**                              | Inserts a VST3 on the current track as last                   |_Insert VST3 (Drum Machine)_                |
| **Arranger Loop Start**                      | Sets the arranger loop start position                         |                                            |
| **Arranger Loop End**                        | Sets the arranger loop duration                               |                                            |
| **Time Signature**                           | Sets the time signature                                       |_Time Signature (3/4)_                      |
| **Wait**                                     | Waits for the specified number of ms before proceeding        |                                            |
| **Message**                                  | Shows a message box with the specified text                   |_Message (Hello World!)_                    |
| **Insert File**                              | Insert a file (Eg .mid) in the desired launcher slot          |_Insert File (3,"C:\midi\file.mid")_        |

| **Bitwig Buddy Panel**                       | Description                                                   | Example                                    |
|----------------------------------------------|---------------------------------------------------------------|--------------------------------------------|
| **BB Arranger Mode**                         | Switches Bitwig Buddy to Arranger                             |                                            |
| **BB Launcher Mode**                         | Switches Bitwig Buddy to Launcher                             |                                            |
| **BB Toggle Launcher Arranger Mode**         | Toggles Bitwig Buddy between Launcher and Arranger Mode       |                                            |