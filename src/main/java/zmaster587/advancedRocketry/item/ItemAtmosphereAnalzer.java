package zmaster587.advancedRocketry.item;

import java.util.List;


import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ItemAtmosphereAnalzer extends Item implements IArmorComponent {

	private ResourceIcon icon;

	@Override
	public void onTick(World world, EntityPlayer player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {

	}

	private String[] getAtmosphereReadout(ItemStack stack, AtmosphereType atm) {
		if(atm == null)
			atm = AtmosphereType.AIR;

		String str[] = new String[2];

		str[0] = "Atmosphere Type: " + LibVulpes.proxy.getLocalizedString(atm.getUnlocalizedName());
		str[1] = "Breathable: " + (atm.isBreathable() ? "Yes" : "No");

		return str;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {
		if(!world.isRemote) {
			String str[] = getAtmosphereReadout(stack, (AtmosphereType) AtmosphereHandler.getOxygenHandler(world.provider.dimensionId).getAtmosphereType(player));
			for(String str1 : str)
					player.addChatMessage(new ChatComponentText(str1));
		}
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onComponentAdded(World world, ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, ItemStack armorStack) {
	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {

	}

	@Override
	public boolean isAllowedInSlot(ItemStack componentStack, int targetSlot) {
		return targetSlot == 0;
	}

	@Override
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules,
			RenderGameOverlayEvent event, Gui gui) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		
		int screenX = 8;
		int screenY = event.resolution.getScaledHeight() - fontRenderer.FONT_HEIGHT*3;

		String str[] = getAtmosphereReadout(componentStack, (AtmosphereType) AtmosphereHandler.currentAtm);
		//Draw BG
		gui.drawString(fontRenderer, str[0], screenX, screenY, 0xFFFFFF);
		gui.drawString(fontRenderer, str[1], screenX, screenY + fontRenderer.FONT_HEIGHT*4/3, 0xFFFFFF);
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		if(icon == null)
			this.icon = new ResourceIcon(TextureMap.locationItemsTexture, this.getIcon(armorStack, 0));

		return this.icon;
	}

}
