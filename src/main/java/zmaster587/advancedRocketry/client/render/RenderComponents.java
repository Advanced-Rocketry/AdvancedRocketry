package zmaster587.advancedRocketry.client.render;

import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.client.render.armor.RenderJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderComponents {

	@SubscribeEvent
	public void renderPostSpecial(RenderPlayerEvent.Specials.Post event) {
		//RenderJet pack
		RenderJetPack pack = new RenderJetPack();
		ItemStack chest = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(chest != null && chest.getItem() instanceof IModularArmor) {
			for(ItemStack stack : ((IModularArmor)chest.getItem()).getComponents(chest)) {
				if(stack.getItem() == AdvancedRocketryItems.itemJetpack)
					pack.render(event.getEntityLiving(), 0, 0, 0, 0, 0, 0);
			}
		}
		
	}
}
