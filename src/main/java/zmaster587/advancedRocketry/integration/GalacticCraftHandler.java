package zmaster587.advancedRocketry.integration;

public class GalacticCraftHandler {


	/*@SubscribeEvent
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
*/
}
