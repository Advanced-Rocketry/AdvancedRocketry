package zmaster587.advancedRocketry.integration;

import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GalacticCraftHandler {
	
	@SubscribeEvent
	public void GCSuffocationEvent(GCCoreOxygenSuffocationEvent event) {
		
		event.setCanceled(true);
	}
	
}
