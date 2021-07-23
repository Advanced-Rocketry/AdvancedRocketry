package zmaster587.advancedRocketry.client.render;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.libVulpes.api.IModularArmor;

public class RenderComponents {

	//@SubscribeEvent
	public void renderPostSpecial(RenderPlayerEvent.Post event) {
		//RenderJet pack
		//RenderJetPack pack = new RenderJetPack();
		
		MatrixStack matrix = event.getMatrixStack();
		
		ItemStack chest = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST);
		if(chest != null && chest.getItem() instanceof IModularArmor) {
			for(ItemStack stack : ((IModularArmor)chest.getItem()).getComponents(chest)) {
				if(stack.getItem() == AdvancedRocketryItems.itemJetpack) {
					
					matrix.push();
					//float f = event.getEntityPlayer().prevRotationYaw + (event.getEntityPlayer().rotationYaw - event.getEntityPlayer().prevRotationYaw);
					//GL11.glRotatef(f  + 180, 0,-1,0); 
					//pack.render(event.getEntityLiving(), 0, 0, 0, 0, 0, 0);
					matrix.pop();
				}
			}
		}

	}
}
