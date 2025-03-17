# BitwigBuddy Manual

## 🎹 Introduction

### What is BitwigBuddy?

BitwigBuddy is a comprehensive extension for Bitwig Studio designed to simplify and accelerate your music production workflow. It enables efficient creation, editing, and automation of MIDI patterns, drum sequences, and complex macros. Perfect for streamlining tasks and boosting creativity!

### ⚙️ Installation

Follow these steps to install BitwigBuddy:

1. Download the latest BitwigBuddy extension package.
2. Extract the contents.
3. Move the extracted BitwigBuddy folder into the Bitwig extensions folder:
   - **Windows:** `%USERPROFILE%\Documents\Bitwig Studio\Extensions`
   - **Windows with OneDrive:** `%USERPROFILE%\OneDrive\Documents\Bitwig Studio\Extensions`
4. Restart Bitwig Studio.

## 1️⃣ Generate Mode

### 🎵 Presets

BitwigBuddy comes with numerous predefined rhythmic and melodic presets for quick pattern generation.

#### Preset Mode Panels

- **Generate/Edit/Macro Mode:** Set this to "Generate" to create patterns.
- **Destination:**
  - **Launcher:** Generated pattern is placed in the Clip Launcher view.
  - **Arranger:** Generated pattern is placed on the arranger timeline.
- **Preset/Program:** Set this to "Preset" to select from predefined patterns.
- **Replace/Add Pattern:**
  - **Replace:** Overwrites existing notes completely.
  - **Add:** Adds generated pattern to existing notes.
- **Reverse Pattern:**
  - **Normal:** Plays pattern as saved.
  - **Reverse:** Plays pattern in reverse order.

### 🛠️ Creating Customized Presets

Save custom presets as `.txt` files in the `BitwigBuddy/Custom Presets` folder. Presets follow this syntax:

```
# Custom Preset Example
Preset Name: MyCustomPreset
Steps: 80,0,80,0,80,0,80,0
DefaultNote: "C#1"
Note Channel: 1
Step Size: 1/16
Subdivisions: Straight
Note Length: 1/16
```

#### Preset File Settings Explained:
- **Preset Name:** Display name in BitwigBuddy.
- **Steps:** Comma-separated velocities (0-127), 0 = no note.
- **DefaultNote:** Sets default MIDI note and octave (e.g., "C#1").
- **Note Channel:** MIDI channel for generated notes.
- **Step Size:** Rhythmic value of each step.
- **Subdivisions:** Rhythm subdivisions (Straight, Triplets, Quintuplets).
- **Note Length:** Length of each generated note.

#### Tips & Ideas
- Rapidly browse presets for quick inspiration.
- Reverse patterns for creative rhythmic results.
- Save your favorite patterns for easy recall.

## 🎲 Generate - Program Mode

#### Program Mode Panels
- **Pattern Type:** Select "Program" for semi-random patterns.
- **Steps:** Shows the generated step sequence.
- **Velocity Range:**
  - **Min Velocity:** Minimum note velocity.
  - **Max Velocity:** Maximum note velocity.
- **Velocity Shape:** Distribution method (Random, Linear, Curve).
- **Density:** Frequency of notes occurrence (0% to 100%).
- **Step Quantity:** Number of generated steps.
- **Skip Step:** Skip steps systematically:
  - Example: Density 100%, Skip Step at 1 → Skips all odd-numbered steps.
  - Example: Density 100%, Skip Step at 2 → skips even steps, focusing on odd steps.

#### Tips & Ideas
- Adjust density to quickly vary patterns.
- Experiment with velocity shapes to enhance groove.
- Use Skip Step creatively for unique rhythmic variations.

## ✏️ Edit Mode

#### Edit Panels Explained

- **Update Selected Steps Velocity:** Modify velocity of highlighted notes.
- **Velocity Range:** Set the velocity limits (min/max).
- **Velocity Shape:** Choose distribution shape (Random, Linear).
- **Clear Current Clip/Note Destination:** Clear the selected pattern or MIDI assignment.

#### Tips & Ideas
- Quickly humanize existing MIDI patterns.
- Use Edit Mode for detailed pattern refinement.

## ⚡ Macro Mode

### 🎼 What is a Macro?

Macros in BitwigBuddy automate sequences of native Bitwig actions combined with custom commands, streamlining complex or repetitive tasks.

#### Macro Panels

- **Select Macro:** Choose macro sequences to execute.
- **Instant Macro:** Define up to 8 quick-execute actions.
- **Macro Execution Controls:** Execute, stop, or manage macros.

#### Preset Folder
Store macros as `.txt` files in the `BitwigBuddy/Macros` folder.

#### Example Macros Included

- **Create 8 Tracks:** Quickly sets up instrument and audio tracks.
- **Arrangement - Pop:** Auto-generates pop music structure cue markers.
- **Rename Loop Incremental:** Automatically renames multiple tracks incrementally.

#### Macro Syntax

Macros are written as commands, each line is an action. Commands with parameters require parentheses:

Single parameter example:
```
Message ("Hello World")
```

Multiple parameters separated by commas:

```
Insert File (3,"C:\midi\file.mid")
```

This inserts the MIDI file `file.mid` into clip slot number 3.

### 📂 Preset Folder

Macros are stored and managed as `.txt` files in the `BitwigBuddy/Macros` directory.

### 📋 Additional Manuals & References

- [Bitwig Embedded Actions](Macro-Bitwig-Actions.md)
- [BitwigBuddy Custom Actions Manual](Macro-BitwigBuddy-Actions.md)

### 🤝 Share Macros!

Contribute your macros to enrich the BitwigBuddy community!

---

## 🤝 Supporting BitwigBuddy

### 💖 How to Support
- Join and support on Patreon.
- Star and follow the project repository on GitHub.
- Enjoy and share Centomila's music!

### 🙌 Special Thanks

Immense gratitude goes out to all Patrons, GitHub followers, listeners, and the enthusiastic Bitwig community for continuous encouragement and support!

