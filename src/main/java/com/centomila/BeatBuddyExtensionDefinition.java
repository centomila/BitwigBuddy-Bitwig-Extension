package com.centomila;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class BeatBuddyExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("8afdc6aa-bfe9-41f2-a689-8738031806ee");
   
   public BeatBuddyExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "BeatBuddy";
   }
   
   @Override
   public String getAuthor()
   {
      return "centomila";
   }

   @Override
   public String getVersion()
   {
      return "0.8.8";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "centomila";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "BeatBuddy";
   }

   /** {@inheritDoc} */
   @Override
   public String getHelpFilePath() {
      return "https://github.com/centomila/BeatBuddy-Bitwig-Extension-MIDI-Drum-Generator";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 19;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 0;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 0;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public BeatBuddyExtension createInstance(final ControllerHost host)
   {
      return new BeatBuddyExtension(this, host);
   }
}
