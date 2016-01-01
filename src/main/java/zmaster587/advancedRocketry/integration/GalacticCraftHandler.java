package zmaster587.advancedRocketry.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GalacticCraftHandler {


	@SubscribeEvent
	public void GCSuffocationEvent(GCCoreOxygenSuffocationEvent.Pre event) {
		if(event.entity instanceof EntityPlayer) {
			GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP) event.entity);
			if(stats != null)
				stats.oxygenSetupValid = true;
		}

		event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tickFixAnnoyingOverlay(TickEvent.RenderTickEvent event) {

		if(Minecraft.getMinecraft().thePlayer != null) {
			GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)Minecraft.getMinecraft().thePlayer);
			if(stats != null)
				stats.oxygenSetupValid = true;
		}
	}

}
