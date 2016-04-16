package zmaster587.advancedRocketry.event;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PlanetEventHandlerClient {

	//Handle gravity
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if(event.player.ridingEntity != null && event.player.ridingEntity instanceof EntityRocket) {
			EntityRocket rocket = (EntityRocket)event.player.ridingEntity;
			if(!rocket.isInFlight() && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if(Minecraft.getMinecraft().inGameHasFocus) {
					PacketHandler.sendToServer(new PacketEntity(rocket, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
					rocket.launch();
				}
			}
			
		}
	}
}
