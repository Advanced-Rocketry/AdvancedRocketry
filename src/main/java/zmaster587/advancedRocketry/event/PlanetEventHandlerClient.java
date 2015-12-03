package zmaster587.advancedRocketry.event;

import org.lwjgl.input.Keyboard;

import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.dimension.DimensionManager;
import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import zmaster587.advancedRocketry.api.network.PacketEntity;
import zmaster587.advancedRocketry.api.network.PacketHandler;
import zmaster587.advancedRocketry.entity.EntityRocket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PlanetEventHandlerClient {
	
	//Handle gravity
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if(event.player.ridingEntity != null && event.player.ridingEntity instanceof EntityRocket) {
			EntityRocket rocket = (EntityRocket)event.player.ridingEntity;
			if(!rocket.isInFlight() && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				
				PacketHandler.sendToServer(new PacketEntity(rocket, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
				rocket.launch();
			}
		}
	}
}
