package zmaster587.advancedRocketry.event;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.util.Configuration;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent;

public class RocketEventHandler extends Gui {
	
	private ResourceLocation background = new ResourceLocation("advancedrocketry:textures/gui/rocketHUD.png");
	
	@SubscribeEvent
	public void onScreenRender(RenderGameOverlayEvent event) {
		Entity ride;
		
		
		if(event.type == ElementType.HOTBAR && (ride = Minecraft.getMinecraft().thePlayer.ridingEntity) instanceof EntityRocket) {
			EntityRocket rocket = (EntityRocket)ride;
			
			//If the space bar is pressed then send a packet to the server and launch the rocket
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !rocket.isInFlight()) {
				PacketHandler.sendToServer(new PacketEntity(rocket, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
				rocket.launch();
			}
			
			GL11.glEnable(GL11.GL_BLEND);
			
			Minecraft.getMinecraft().renderEngine.bindTexture(background);
			
			this.drawTexturedModalRect(0, 0, 0, 0, 17, 252);
			
			//Draw altitude indicator
			float percentOrbit = MathHelper.clamp_float((float) ((rocket.posY - rocket.worldObj.provider.getAverageGroundLevel())/(float)(Configuration.orbit-rocket.worldObj.provider.getAverageGroundLevel())), 0f, 1f);
			this.drawTexturedModalRect(3, 8 + (int)(79*(1 - percentOrbit)), 17, 0, 6, 6); //6 to 83
			
			this.drawTexturedModalRect(3, 94 + (int)(69*(0.5 - (MathHelper.clamp_float((float) (rocket.motionY), -1f, 1f)/2f))), 17, 0, 6, 6); //94 to 161
		
			//Draw fuel indicator
			int size = (int)(68*(rocket.getFuelAmount() /(float)rocket.getFuelCapacity()));
			this.drawTexturedModalRect(3, 242 - size, 17, 75 - size, 3, size); //94 to 161
			
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
