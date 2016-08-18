package zmaster587.advancedRocketry.util;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import zmaster587.advancedRocketry.api.armor.IJetPack;
import zmaster587.advancedRocketry.api.armor.IModularArmor;
import zmaster587.advancedRocketry.network.PacketChangeKeyState;
import zmaster587.advancedRocketry.network.PacketHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class InputSyncHandler {

	public static HashMap<EntityPlayer, Boolean> spaceDown = new HashMap<EntityPlayer, Boolean>();
	boolean prevState;

	public static boolean isSpaceDown(EntityPlayer player) {
		Boolean bool = spaceDown.get(player);
		
		return bool != null && bool;
	}
	
	//Called on server
	public static void updateKeyPress(EntityPlayer player, int key, boolean state) {
		ItemStack stack;
		switch(key) {
		case 0:
			stack = player.getEquipmentInSlot(3);
			if(stack != null) {
				IJetPack pack;
				if(stack.getItem() instanceof IJetPack) {
					pack = ((IJetPack)stack.getItem());
					pack.setEnabledState(stack, !pack.isEnabled(stack));
				}
				else if(stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);
					
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof IJetPack) {
							pack = ((IJetPack)inv.getStackInSlot(i).getItem());
							pack.setEnabledState(inv.getStackInSlot(i), !pack.isEnabled(inv.getStackInSlot(i)));
						}
					}
					((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);
					
				}
			}
			break;
			
		case 1:
			stack = player.getEquipmentInSlot(3);
			if(stack != null) {
				IJetPack pack;
				if(stack.getItem() instanceof IJetPack) {
					pack = ((IJetPack)stack.getItem());
					pack.setEnabledState(stack, !pack.isEnabled(stack));
				}
				else if(stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);
					
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof IJetPack) {
							pack = ((IJetPack)inv.getStackInSlot(i).getItem());
							pack.changeMode(inv.getStackInSlot(i), inv, player);
						}
					}
					((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);
					
				}
			}
			break;
		case 57: //SPACE
			spaceDown.put(player, state);
			break;
			
			default:
				
		}
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent event) {
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) != prevState) {
			prevState = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
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
