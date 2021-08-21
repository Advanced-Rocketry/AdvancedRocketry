package zmaster587.advancedRocketry.integration;

//import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
//import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
//import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GalacticCraftHandler {


	/*@SubscribeEvent
	public void GCSuffocationEvent(GCCoreOxygenSuffocationEvent.Pre event) {

		if(event.getEntity() instanceof PlayerEntity) {
			GCPlayerStats stats = GCPlayerStats.get((ServerPlayerEntity) event.getEntity());
			if(stats != null)
				stats.setLastOxygenSetupValid(true);
		}

		event.setCanceled(true);
	}

	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void tickFixAnnoyingOverlay(TickEvent.RenderTickEvent event) {

		if(Minecraft.getInstance().player != null) {
			GCPlayerStatsClient stats = GCPlayerStatsClient.get((ClientPlayerEntity)Minecraft.getInstance().player);
			if(stats != null)
				stats.setOxygenSetupValid(true);
		}
	}*/

}
