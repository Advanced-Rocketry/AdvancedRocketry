package zmaster587.advancedRocketry.util;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import zmaster587.advancedRocketry.network.PacketChangeKeyState;
import zmaster587.advancedRocketry.network.PacketHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class InputSyncHandler {

	public static HashMap<EntityPlayer, Boolean> spaceDown = new HashMap<EntityPlayer, Boolean> ();
	boolean prevState;

	public static boolean isSpaceDown(EntityPlayer player) {
		Boolean bool = spaceDown.get(player);
		
		return bool != null && bool;
	}
	
	public static void updateKeyPress(EntityPlayer player, int key, boolean state) {
		switch(key) {
		case 57: //SPACE
			spaceDown.put(player, state);
			break;
			
			default:
				
		}
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent event) {
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) != prevState) {
			prevState = !prevState;
			PacketHandler.sendToServer(new PacketChangeKeyState(Keyboard.KEY_SPACE, prevState));
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent evt) {
		spaceDown.remove(evt.player);
	}

	@SubscribeEvent
	public void onDimChanged(PlayerChangedDimensionEvent evt) {
		spaceDown.remove(evt.player);
	}
}
