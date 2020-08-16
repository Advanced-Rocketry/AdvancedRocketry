package zmaster587.advancedRocketry.capability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;

public class CapabilityProtectiveArmor {
	
	public static void registerCap() {
	    //MinecraftForge.EVENT_BUS.register(CapabilityProtectiveArmor.class);
	    //LibVulpes.logger.info("Forge Energy integration loaded");
	}
	
	private static final ResourceLocation KEY = new ResourceLocation("advancedRocketry:ProtectiveArmor");

	
	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt)
	{
		if (evt.getCapabilities().containsKey(KEY)) {
			return;
		}
		Item item = evt.getObject().getItem();
		if (item instanceof ItemSpaceArmor) {
			evt.addCapability(KEY, (ICapabilityProvider) item);
		}
	}
	
}
