package zmaster587.advancedRocketry.integration;

//import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
//import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
//import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import net.minecraft.client.Minecraft;
<<<<<<< HEAD
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
=======
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
>>>>>>> origin/feature/nuclearthermalrockets

public class GalacticCraftHandler {


	/*@SubscribeEvent
	public void GCSuffocationEvent(GCCoreOxygenSuffocationEvent.Pre event) {
		
<<<<<<< HEAD
		if(event.getEntity() instanceof PlayerEntity) {
			GCPlayerStats stats = GCPlayerStats.get((ServerPlayerEntity) event.getEntity());
=======
		if(event.getEntity() instanceof EntityPlayer) {
			GCPlayerStats stats = GCPlayerStats.get(event.getEntity());
>>>>>>> origin/feature/nuclearthermalrockets
			if(stats != null)
				stats.setLastOxygenSetupValid(true);
		}

		event.setCanceled(true);
	}

	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void tickFixAnnoyingOverlay(TickEvent.RenderTickEvent event) {

<<<<<<< HEAD
		if(Minecraft.getInstance().player != null) {
			GCPlayerStatsClient stats = GCPlayerStatsClient.get((ClientPlayerEntity)Minecraft.getInstance().player);
=======
		if(Minecraft.getMinecraft().player != null) {
			GCPlayerStatsClient stats = GCPlayerStatsClient.get(Minecraft.getMinecraft().player);
>>>>>>> origin/feature/nuclearthermalrockets
			if(stats != null)
				stats.setOxygenSetupValid(true);
		}
	}*/

}
