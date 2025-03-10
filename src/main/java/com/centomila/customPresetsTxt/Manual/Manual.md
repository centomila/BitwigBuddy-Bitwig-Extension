# BitwigBuddy Manual

BitwigBuddy is a Bitwig extension designed to assist with creating drum/note patterns, editing clips, and executing macros.

---

# 🎛️ Generate Mode

## 🎵 Overview
The **Generate** mode in BitwigBuddy allows users to quickly generate drum or note patterns based on predefined presets, programmed sequences, or custom patterns. It helps streamline beat-making and melodic pattern creation without manual input for each note.

### 🎼 Usage Tips
- ⚡ **Use Presets** for quick pattern creation.
- 🎹 **Enable Learn Note** to map notes interactively.
- 🔄 **Use Move/Rotate** to adjust patterns dynamically.
- 🎶 **Experiment with Subdivisions** for groove variations.
- 🛠️ **Activate Post Actions** like **Auto Resize Loop Length** to optimize workflow.
- 🎲 **Use Program Mode** to generate semi-random patterns with controlled density.
- 💾 **Store custom patterns** in **Custom Mode** for easy access and reuse.

---

## ⚙️ Controls & Parameters

### **🎚️ Mode Selection**
- 🔘 **Generate/Edit Mode:** Select **Generate** to enable pattern generation.
- 🎵 **Destination Launcher/Arranger:** Choose whether the generated pattern should be placed in the **Launcher** (clip view) or **Arranger** (timeline view).

---

### **🔄 Move - Rotate Steps**
- 🔄 **Move/Rotate:** Move or rotate existing steps within a generated pattern.
- ⏩ **Move Steps Forward (`>>>`)**: Shift steps forward.
- ⏪ **Move Steps Backward (`<<<`)**: Shift steps backward.

---

## **📌 Presets Mode**

### Overview
The **Presets Mode** allows users to quickly generate patterns from predefined templates. These presets provide structured rhythms for instant use.

### 🛠️ Options
- 🎼 **Pattern Type:** `Presets`
- 📂 **Pattern Selection:** Users can select from a list of predefined patterns (e.g., *Kick: Four on the Floor*).
- 🎛️ **Steps:** Displays the sequence of note velocities for the selected pattern.
- 🔄 **Reverse Pattern:**
  - ✅ **Normal:** Uses the pattern as stored.
  - 🔀 **Reverse:** Flips the pattern sequence.

### 🎼 Usage Tips
- 🔥 **Use Presets Mode** for structured and well-known rhythm patterns.
- 🎵 **Reverse the pattern** for creative variations.
- ⚡ **Quickly swap presets** to test different rhythm ideas.

---

## **🎲 Program Mode**

### Overview
The **Program Mode** allows users to create semi-random patterns, controlling note density and velocity behavior.

### 🛠️ Options
- 🎼 **Pattern Type:** `Program`
- 🎛️ **Steps:** Displays the generated step values.
- 🎚️ **Min Velocity:** Defines the lowest possible velocity for notes.
- 🎚️ **Max Velocity:** Defines the highest possible velocity for notes.
- 📈 **Velocity Shape:** Determines how velocity is distributed (e.g., *Random*, *Linear*, *Curve*).
- 🎶 **Density:** Controls how many steps contain active notes (e.g., 50% means half the steps have notes).
- 🔢 **Step Quantity:** Sets the number of steps in the generated pattern.

### 🎼 Usage Tips
- 🎛️ **Adjust Density** to create more sparse or crowded patterns.
- 🔀 **Try different velocity shapes** to create dynamic grooves.
- 🎶 **Use Program Mode for inspiration** when manually programming rhythms.

---

## **📂 Custom Mode**

### Overview
The **Custom Mode** enables users to load, save, and manage their own pattern presets stored as text files.

### 🛠️ Options
- 🎼 **Pattern Type:** `Custom`
- 📂 **Custom Presets:** Selects a saved pattern from the `BitwigBuddy/Custom Presets` folder.
- 🎛️ **Steps:** Displays the step sequence of the selected preset.
- 💾 **Save Custom Preset:** Saves the current step sequence as a new preset.
- 🏷️ **Preset Name:** Defines the name for saving a new custom preset.
- 🔄 **Refresh Custom Files:** Reloads available custom presets from storage.
- 🔄 **Reverse Pattern:**
  - ✅ **Normal:** Uses the preset as stored.
  - 🔀 **Reverse:** Flips the preset sequence.
- 🎵 **Preset Default Note / Note Destination:** Allows setting a default note for the custom preset.

### 🎼 Usage Tips
- 💾 **Save your favorite patterns** for quick access.
- 🔄 **Use Reverse** to create variations of stored rhythms.
- 🏷️ **Organize custom presets** with meaningful names.

---

# ✏️ Edit Mode

## 📝 Overview
The **Edit Mode** in BitwigBuddy allows users to modify existing note patterns by adjusting velocity and step values.

### 🛠️ Options
- 🎚️ **Update Selected Steps Velocity:** Adjusts velocity for selected steps.
- 🎚️ **Min Velocity:** Sets the minimum velocity for the selected notes.
- 🎚️ **Max Velocity:** Sets the maximum velocity for the selected notes.
- 📈 **Velocity Shape:** Controls the velocity curve (e.g., *Random*, *Linear*).

### 🎼 Usage Tips
- 🔄 **Use Edit Mode** to refine generated patterns.
- 🎛️ **Adjust velocity dynamically** for more humanized rhythms.
- ⚡ **Combine with Generate Mode** to create and tweak patterns quickly.

---

# ⚡ Macro Mode

## 🏗️ Overview
The **Macro Mode** in BitwigBuddy allows users to execute pre-defined sequences of actions using macros. These macros are stored as text files and can include both native Bitwig actions and additional custom actions provided by BitwigBuddy.

### 🛠️ Options
- 📂 **Select a Macro:** Loads a macro from the available macro files.
- 📝 **Macro Description:** Displays details about the selected macro.
- ▶️ **Execute Macro:** Runs the selected macro sequence.
- 🔄 **Refresh Custom Files:** Reloads the available macro files.

### 📜 Available Actions
BitwigBuddy macros support two types of actions:
1. **Bitwig Native Actions** (Predefined DAW commands) - See [Bitwig Embedded Actions](Macro-Bitwig-Actions.md)
2. **BitwigBuddy Custom Actions** (Additional programmable commands) - See [BitwigBuddy Actions](Macro-BitwigBuddy-Actions.md)

### 🎼 Usage Tips
- ⚡ **Use macros to automate repetitive tasks** like track renaming or cue marker adjustments.
- 🔄 **Refresh the list** when adding new macro files to ensure they appear in the selection.
- 📜 **Combine multiple actions** into a single macro for complex workflows.

---

This guide covers the essential features of **BitwigBuddy**, helping users efficiently create, edit, and automate note patterns and workflows. Explore the available options to customize your workflow! 🚀

