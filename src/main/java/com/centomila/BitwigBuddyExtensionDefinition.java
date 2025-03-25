package com.centomila;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class BitwigBuddyExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("27615dbe-6d9f-4bb6-9b32-fe6707040a02");
   
   public BitwigBuddyExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "BitwigBuddy";
   }
   
   @Override
   public String getAuthor()
   {
      return "centomila";
   }

   @Override
   public String getVersion()
   {
      return "1.0.0 alpha 1";
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
      return "BitwigBuddy";
   }

   /** {@inheritDoc} */
   @Override
   public String getHelpFilePath() {
      return "https://github.com/centomila/BitwigBuddy-Bitwig-Extension";
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
   public BitwigBuddyExtension createInstance(final ControllerHost host)
   {
      return new BitwigBuddyExtension(this, host);
   }
}
