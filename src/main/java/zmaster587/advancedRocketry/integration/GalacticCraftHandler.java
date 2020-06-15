package zmaster587.advancedRocketry.integration;

import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.AdvancedRocketry;


public class GalacticCraftHandler {
	
	//  Pre.event cancels oxygen warning  overlay
	@SubscribeEvent
	public void GCCoreOxygenSuffocationEvent(GCCoreOxygenSuffocationEvent.Pre event) {
		if (AdvancedRocketry.isGalacticraftLoaded()) {
			EntityLivingBase e = event.getEntityLiving();
			if (e.world.provider instanceof IGalacticraftWorldProvider) {
				if (e instanceof EntityPlayer) {
					event.setCanceled(true);
				}
			}
		}
	}
}
