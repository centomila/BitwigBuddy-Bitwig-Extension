[View Changelog](CHANGELOG.md)

# BeatBuddy - Bitwig Studio Extension for Generating Drum Patterns (v0.7.0)

![Screenshot v0.7.0](image.png)

## ⚙️ Installation:

1. ⬇️ Download **_BeatBuddy.bwextension_**  
2. 📂 Copy **_BeatBuddy.bwextension_** to the **_/Bitwig Studio/Extensions/_** folder  
3. 🟧 In Bitwig, go to **Settings > Controller > Add Extension > Centomila > BeatBuddy**  

---

## Quick Guide

BeatBuddy is a Bitwig extension for creating and manipulating single lane MIDI patterns. Here's a guide to the interface and its controls:

### Main Interface Controls

#### Move Steps
- **<<<**: Move steps to the left.
- **|**: Center steps (reset position).
- **>>>**: Move steps to the right.

⚠️ Note: This creates a lot of actions! Once done, you will not be able to undo any action prior to the "Move Steps" action.

---

#### Generate
- **Generate!**: Generates a MIDI pattern based on the selected settings.
- **Pattern Dropdown**: Choose from predefined patterns (e.g., "Various: 16th Decreasing Velocity").

💡 Note: Try the _Random_ pattern to infinitely generate patterns!

---

#### Reverse Pattern
- **Normal**: Use the default direction of the pattern.
- **Reverse**: Flip the pattern direction.

---

#### Note Destination
- Dropdown to select the target note for the generated pattern (e.g., E, C#, etc.).

---

#### Note Octave
- Buttons to set the octave (-2 to 7).

---

#### Note Channel
- Number box to input the destination MIDI channel (e.g., "1 Channel MIDI").

---

#### Step Size
- Dropdown to set the step resolution (e.g., 1/16, 1/8, etc.).

---

#### Subdivisions
- **Straight**: Default subdivision.
- **.**: Dotted notes.
- **3t**: Triplet notes.
- **5t**: Quintuplet notes.
- **7t**: Septuplet notes.

---

#### Note Length
- Dropdown to set the duration of each note (e.g., 1/16, 1/8, etc.).

ℹ️ Note: The note length is automatically adjusted to match the selected step size.

---

#### Auto Resize Loop Length
- **Off**: Disable automatic resizing of the loop length.
- **On**: Enable automatic resizing of the loop length.

---

#### Clear Current Clip
- **Clear**: Deletes all the notes in the currently selected Bitwig clip.

---

#### Destination Launcher/Arranger
- **Launcher**: Set the generated pattern and all the other actions to work in the Launcher clips.
- **Arranger**: Set the generated pattern and all the other actions to work in the Arranger clips.

---


# 🚀 Coming Soon

- [ ] Setting for custom clip length  
- [ ] Setting for random patterns  
- [ ] Option to remove excess notes if the user generates a pattern longer than the clip  
- [ ] MOAAAR PATTERNS!
- [ ] Support for custom patterns via TXT files  
- [ ] A decent README!
