# BitwigBuddy Actions           

| Action                                       | Description                                                   | Example                                    |
|----------------------------------------------|---------------------------------------------------------------|--------------------------------------------|
| **Bpm**                                      | Sets the tempo (beats per minute)                             | _Bpm (120)_                                |
| **CueMarkerName**                            | Sets the name of a specified cue marker by index              | _CueMarkerName (2, Verse 1)_               |
| **DeleteAllCueMarkers**                      | Deletes all existing cue markers                              |                                            |
| **Left / Right / Up / Down**                 | Navigates within the Bitwig UI using arrow-key commands       |                                            |
| **Enter**                                    | Triggers the Enter key action in Bitwig                       |                                            |
| **Escape**                                   | Triggers the Escape key action in Bitwig                      |                                            |
| **Clip Select**                              | Selects the currently focused clip slot                       |                                            |
| **Clip Duplicate**                           | Duplicates the currently focused clip                         |                                            |
| **Project Name**                             | Shows the current project name                                |                                            |
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
| **Insert Drum Bank**                         | Inserts a blank drum bank in the selected drum machine device |_Insert Drum Bank ("C#2")_                  |
| **Arranger Loop Start**                      | Sets the arranger loop start position                         |                                            |
| **Arranger Loop End**                        | Sets the arranger loop duration                               |                                            |
| **Time Signature**                           | Sets the time signature                                       |_Time Signature (3/4)_                      |
| **Wait**                                     | Waits for the specified number of ms before proceeding        |                                            |
| **Message**                                  | Shows a message box with the specified text                   |_Message (Hello World!)_                    |
| **Insert File**                              | Insert a file (Eg .mid) in the desired launcher slot          |_Insert File (3,"C:\midi\file.mid")_        |
| **Step Selected Length**                     | Sets the length of selected notes                              | _Step Selected Length (0.5)_               |
| **Step Selected Velocity**                   | Sets the velocity of selected notes                            | _Step Selected Velocity (0.75)_            |
| **Step Selected Chance**                     | Sets the chance value of selected notes                        | _Step Selected Chance (0.5)_               |
| **Step Selected Transpose**                  | Sets the transpose value of selected notes                     | _Step Selected Transpose (12)_              |
| **Step Selected Gain**                       | Sets the gain of selected notes                               | _Step Selected Gain (0.8)_                 |
| **Step Selected Pressure**                   | Sets the pressure (aftertouch) of selected notes              | _Step Selected Pressure (0.6)_             |
| **Step Selected Timbre**                     | Sets the timbre of selected notes                             | _Step Selected Timbre (0.7)_               |
| **Step Selected Pan**                        | Sets the pan value of selected notes                          | _Step Selected Pan (0.5)_                  |
| **Step Selected Duration**                   | Sets the duration of selected notes                           | _Step Selected Duration (0.25)_            |
| **Step Selected Velocity Spread**            | Sets the velocity spread of selected notes                    | _Step Selected Velocity Spread (0.2)_      |
| **Step Selected Release Velocity**           | Sets the release velocity of selected notes                   | _Step Selected Release Velocity (0.5)_     |
| **Step Selected Is Chance Enabled**          | Enables/disables chance for selected notes                    | _Step Selected Is Chance Enabled (true)_   |
| **Step Selected Is Muted**                   | Mutes/unmutes selected notes                                  | _Step Selected Is Muted (true)_            |
| **Step Selected Is Occurrence Enabled**      | Enables/disables occurrence for selected notes                | _Step Selected Is Occurrence Enabled (true)_ |
| **Step Selected Is Recurrence Enabled**      | Enables/disables recurrence for selected notes                | _Step Selected Is Recurrence Enabled (true)_ |
| **Step Selected Is Repeat Enabled**          | Enables/disables repeat for selected notes                    | _Step Selected Is Repeat Enabled (true)_   |
| **Step Selected Occurrence**                 | Sets the occurrence condition for selected notes              | _Step Selected Occurrence (FIRST)_         |
| **Step Selected Recurrence**                 | Sets recurrence pattern for selected notes                    | _Step Selected Recurrence (4,15)_          |
| **Step Selected Repeat Count**               | Sets the repeat count for selected notes                      | _Step Selected Repeat Count (4)_           |
| **Step Selected Repeat Curve**               | Sets the repeat timing curve for selected notes               | _Step Selected Repeat Curve (0.5)_         |
| **Step Selected Repeat Velocity Curve**      | Sets the repeat velocity curve for selected notes            | _Step Selected Repeat Velocity Curve (0.3)_ |
| **Step Selected Repeat Velocity End**        | Sets the end velocity for note repeats                       | _Step Selected Repeat Velocity End (0.4)_   |

| **Bitwig Buddy Panel**                       | Description                                                   | Example                                    |
|----------------------------------------------|---------------------------------------------------------------|--------------------------------------------|
| **BB Macro**                                 | Execute a BitwigBuddy Macro                                   |_BB Macro Message ("My Message")_           |
| **BB Close Panel **                          | Close the BitwigBuddy Panel to avoid problems on focusing     |_BB Close Panel_                            |
| **BB Arranger Mode**                         | Switches Bitwig Buddy to Arranger                             |                                            |
| **BB Launcher Mode**                         | Switches Bitwig Buddy to Launcher                             |                                            |
| **BB Toggle Launcher Arranger Mode**         | Toggles Bitwig Buddy between Launcher and Arranger Mode       |                                            |