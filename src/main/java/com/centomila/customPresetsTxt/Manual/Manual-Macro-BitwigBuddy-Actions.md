# BitwigBuddy Actions                   

| Action                                  | Description                                                   | Example                                           |
| --------------------------------------- | ------------------------------------------------------------- | ------------------------------------------------- |
| **Bpm**                                 | Sets the tempo (beats per minute)                             | _Bpm (120)_                                       |
| **CueMarkerName**                       | Sets the name of a specified cue marker by index              | _CueMarkerName (2, Verse 1)_                      |
| **DeleteAllCueMarkers**                 | Deletes all existing cue markers                              |                                                   |
| **Left**                                | Navigates left in the Bitwig UI                               |                                                   |
| **Right**                               | Navigates right in the Bitwig UI                              |                                                   |
| **Up**                                  | Navigates up in the Bitwig UI                                 |                                                   |
| **Down**                                | Navigates down in the Bitwig UI                               |                                                   |
| **Enter**                               | Triggers the Enter key action in Bitwig                       |                                                   |
| **Escape**                              | Triggers the Escape key action in Bitwig                      |                                                   |
| **Clip Select**                         | Selects the currently focused clip slot                       |                                                   |
| **Clip Duplicate**                      | Duplicates the currently focused clip                         |                                                   |
| **Project Name**                        | Shows the current project name                                |                                                   |
| **Clip Delete**                         | Deletes the currently focused clip                            |                                                   |
| **Clip Rename**                         | Renames the currently focused clip                            | _Clip Rename ("Verse 1")_                         |
| **Clip Color**                          | Sets the color of the currently focused clip                  | _Clip Color (FF0000)_                             |
| **Clip Create**                         | Creates an empty clip. Param 1 = Slot ~ Param 2 = Loop Length | _Clip Create (3,32)_                              |
|                                         | In arranger mode, Param 1 can be anything                     | _Clip Create (0,16)_                              |
| **Clip Length**                         | Sets the length of the currently focused clip                 | _Clip Length (16)_                                |
| **Clip Loop On**                        | Enables Clip Looping Mode for the selected clip               | _Clip Loop On_                                    |
| **Clip Loop Off**                       | Disables Clip Looping Mode for the selected clip              | _Clip Loop Off_                                   |
| **Clip Accent**                         | Sets the Clip Accent value (range 0.0-1.0)                    | _Clip Accent (0.75)_                              |
| **Track Color**                         | Sets the color of the currently selected track                | _Track Color ("FF0000")_                          |
| **Track Rename**                        | Renames the currently selected track                          | _Track Rename ("Drums")_                          |
| **Track Select**                        | Selects a track by index                                      | _Track Select (2)_                                |
| **Insert Device**                       | Inserts a Bitwig device on the current track as last          | _Insert Device ("Drum Machine")_                  |
| **Insert VST3**                         | Inserts a VST3 on the current track as last                   | _Insert VST3 ("Drum Machine")_                    |
| **Insert File**                         | Inserts a file (e.g., .mid) in the desired launcher slot      | _Insert File (3,"C:\midi\file.mid")_              |
| **Insert Drum Pad**                     | Inserts a blank drum pad in the selected drum machine         | _Insert Drum Pad ("C#2","Clap","333300")_         |
| **Drum Pad Insert Device**              | Inserts a Bitwig device in the selected drum pad              | _Drum Pad Insert Device_ ("C1", "V0 Kick")_       |
| **Insert File In Drum Pad**             | Inserts a file into the selected drum pad                     | _Insert File In Drum Pad ("C1", "C:\sample.wav")_ |
| **Insert VST3 In Drum Pad**             | Inserts a VST3 plugin into the selected drum pad              | _Insert VST3 In Drum Pad ("C1", "Reverb")_        |
| **Select Drum Pad**                     | Selects a drum pad by note                                    | _Select Drum Pad ("C1")_                          |
| **Arranger Loop Start**                 | Sets the arranger loop start position                         | _Arranger Loop Start (4.0)_                       |
| **Arranger Loop End**                   | Sets the arranger loop duration                               | _Arranger Loop End (8.0)_                         |
| **Time Signature**                      | Sets the time signature                                       | _Time Signature (3/4)_                            |
| **Wait**                                | Waits for the specified number of ms before proceeding        | _Wait (500)_                                      |
| **Message**                             | Shows a message box with the specified text                   | _Message (Hello World!)_                          |
| **Step Selected Length**                | Sets the length of selected notes                             | _Step Selected Length (0.5)_                      |
| **Step Selected Velocity**              | Sets the velocity of selected notes                           | _Step Selected Velocity (0.75)_                   |
| **Step Selected Chance**                | Sets the chance value of selected notes                       | _Step Selected Chance (0.5)_                      |
| **Step Selected Transpose**             | Sets the transpose value of selected notes                    | _Step Selected Transpose (12)_                    |
| **Step Selected Gain**                  | Sets the gain of selected notes                               | _Step Selected Gain (0.8)_                        |
| **Step Selected Pressure**              | Sets the pressure (aftertouch) of selected notes              | _Step Selected Pressure (0.6)_                    |
| **Step Selected Timbre**                | Sets the timbre of selected notes                             | _Step Selected Timbre (0.7)_                      |
| **Step Selected Pan**                   | Sets the pan value of selected notes                          | _Step Selected Pan (0.5)_                         |
| **Step Selected Duration**              | Sets the duration of selected notes                           | _Step Selected Duration (0.25)_                   |
| **Step Selected Velocity Spread**       | Sets the velocity spread of selected notes                    | _Step Selected Velocity Spread (0.2)_             |
| **Step Selected Release Velocity**      | Sets the release velocity of selected notes                   | _Step Selected Release Velocity (0.5)_            |
| **Step Selected Is Chance Enabled**     | Enables/disables chance for selected notes                    | _Step Selected Is Chance Enabled (true)_          |
| **Step Selected Is Muted**              | Mutes/unmutes selected notes                                  | _Step Selected Is Muted (true)_                   |
| **Step Selected Is Occurrence Enabled** | Enables/disables occurrence for selected notes                | _Step Selected Is Occurrence Enabled (true)_      |
| **Step Selected Is Recurrence Enabled** | Enables/disables recurrence for selected notes                | _Step Selected Is Recurrence Enabled (true)_      |
| **Step Selected Is Repeat Enabled**     | Enables/disables repeat for selected notes                    | _Step Selected Is Repeat Enabled (true)_          |
| **Step Selected Occurrence**            | Sets the occurrence condition for selected notes              | _Step Selected Occurrence (FIRST)_                |
| **Step Selected Recurrence**            | Sets recurrence pattern for selected notes                    | _Step Selected Recurrence (4,15)_                 |
| **Step Selected Repeat Count**          | Sets the repeat count for selected notes                      | _Step Selected Repeat Count (4)_                  |
| **Step Selected Repeat Curve**          | Sets the repeat timing curve for selected notes               | _Step Selected Repeat Curve (0.5)_                |
| **Step Selected Repeat Velocity Curve** | Sets the repeat velocity curve for selected notes             | _Step Selected Repeat Velocity Curve (0.3)_       |
| **Step Selected Repeat Velocity End**   | Sets the end velocity for note repeats                        | _Step Selected Repeat Velocity End (0.4)_         |
| **Transport Position**                  | Sets the transport position                                   | _Transport Position (16.0)_                       |

| **Bitwig Buddy Panel**               | Description                                             | Example                            |
| ------------------------------------ | ------------------------------------------------------- | ---------------------------------- |
| **BB Macro**                         | Executes a BitwigBuddy Macro                            | _BB Macro ("My Macro")_            |
| **BB Close Panel**                   | Closes the BitwigBuddy Panel to avoid focus issues      | _BB Close Panel_                   |
| **BB Arranger Mode**                 | Switches BitwigBuddy to Arranger mode                   | _BB Arranger Mode_                 |
| **BB Launcher Mode**                 | Switches BitwigBuddy to Launcher mode                   | _BB Launcher Mode_                 |
| **BB Toggle Launcher Arranger Mode** | Toggles BitwigBuddy between Launcher and Arranger modes | _BB Toggle Launcher Arranger Mode_ |
| **BB Generate**                      | Press the BitwigBuddy Generate Preset Button            | _BB Generate_                      |
| **BB Preset**                      | Press the BitwigBuddy Generate Preset Button            | _BB Generate_                      |