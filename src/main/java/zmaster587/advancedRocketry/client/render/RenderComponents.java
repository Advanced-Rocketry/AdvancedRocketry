package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.client.render.armor.RenderJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderComponents {

	@SubscribeEvent
	public void renderPostSpecial(RenderPlayerEvent.Post event) {
		//RenderJet pack
		//RenderJetPack pack = new RenderJetPack();
		ItemStack chest = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(chest != null && chest.getItem() instanceof IModularArmor) {
			for(ItemStack stack : ((IModularArmor)chest.getItem()).getComponents(chest)) {
				if(stack.getItem() == AdvancedRocketryItems.itemJetpack) {
					GL11.glPushMatrix();
					float f = event.getEntityPlayer().prevRotationYaw + (event.getEntityPlayer().rotationYaw - event.getEntityPlayer().prevRotationYaw);
					GL11.glRotatef(f  + 180, 0,-1,0); 
					//pack.render(event.getEntityLiving(), 0, 0, 0, 0, 0, 0);
					GL11.glPopMatrix();
				}
			}
		}

	}
}
