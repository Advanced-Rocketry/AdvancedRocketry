package zmaster587.advancedRocketry.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindings {

		@SubscribeEvent
		public void onKeyInput(InputEvent.KeyInputEvent event) {
			final Minecraft minecraft = FMLClientHandler.instance().getClient();
			final EntityPlayerSP player = minecraft.thePlayer;


			//Prevent control when a GUI is open
			if(Minecraft.getMinecraft().currentScreen != null)// && Minecraft.getMinecraft().currentScreen instanceof GuiChat)
				return;
			
			
			EntityRocket rocket;
			//If the space bar is pressed then send a packet to the server and launch the rocket
			if(launch.isPressed() && player.ridingEntity instanceof EntityRocket && !(rocket = (EntityRocket)player.ridingEntity).isInFlight()) {
				PacketHandler.sendToServer(new PacketEntity(rocket, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
				rocket.launch();
			}
		}
		
		static KeyBinding launch = new KeyBinding("Launch", Keyboard.KEY_SPACE, "key.controls." + Constants.modId);

		public static final void init() {

			ClientRegistry.registerKeyBinding(launch);
		}
	}