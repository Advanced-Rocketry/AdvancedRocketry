package zmaster587.advancedRocketry.integration;

//import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
//import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
//import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GalacticCraftHandler {


	/*@SubscribeEvent
	public void GCSuffocationEvent(GCCoreOxygenSuffocationEvent.Pre event) {
		
		if(event.getEntity() instanceof EntityPlayer) {
			GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP) event.getEntity());
			if(stats != null)
				stats.setLastOxygenSetupValid(true);
		}

		event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tickFixAnnoyingOverlay(TickEvent.RenderTickEvent event) {

		if(Minecraft.getMinecraft().player != null) {
			GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)Minecraft.getMinecraft().player);
			if(stats != null)
				stats.setOxygenSetupValid(true);
		}
	}*/

}
