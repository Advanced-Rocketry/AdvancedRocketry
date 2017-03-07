package zmaster587.advancedRocketry.item.components;

import java.lang.reflect.Field;
import java.util.List;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.items.ItemIngredient;

public class ItemUpgrade extends ItemIngredient implements IArmorComponent {

	ResourceIcon icon[];
	private int legUpgradeDamage = 2;
	private int bootsUpgradeDamage = 3;
	Field walkSpeed;

	public ItemUpgrade(int num) {
		super(num);
		icon = new ResourceIcon[num];
		setMaxStackSize(1);

		walkSpeed = ReflectionHelper.findField(net.minecraft.entity.player.PlayerCapabilities.class, "walkSpeed", "field_75097_g");
		walkSpeed.setAccessible(true);
	}

	@Override
	public void onTick(World world, EntityPlayer player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {

		if(componentStack.getItemDamage() == legUpgradeDamage) {
			if(player.isSprinting()) {
				int itemCount = 0;
				for(int i = 0; i < modules.getSizeInventory(); i++) {
					ItemStack stackInSlot = modules.getStackInSlot(i);
					if(stackInSlot != null && stackInSlot.getItem() == this && stackInSlot.getItemDamage() == legUpgradeDamage) {
						//Avoid extra calculation
						if(itemCount == 0 && stackInSlot != componentStack)
							return;
						itemCount++;
					}
				}
				//Walkspeed
				try {
					walkSpeed.setFloat(player.capabilities, (itemCount+1)*0.1f);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				//ReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, (itemCount+1)*0.1f, "walkSpeed", "field_75097_g");
			} else
				try {
					walkSpeed.setFloat(player.capabilities, 0.1f);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			//ReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, 0.1f,"walkSpeed", "field_75097_g");
		}
		else if(componentStack.getItemDamage() == bootsUpgradeDamage && 
				(!Configuration.lowGravityBoots || DimensionManager.getInstance().getDimensionProperties(world.provider.dimensionId).getGravitationalMultiplier() < 1f))
			player.fallDistance = 0;
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
		if(componentStack.getItemDamage() == legUpgradeDamage)
			return targetSlot == 2;
		else if(componentStack.getItemDamage() == bootsUpgradeDamage)
			return targetSlot == 3;
		return targetSlot == 0;
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		if(icon[armorStack.getItemDamage()] == null)
			this.icon[armorStack.getItemDamage()] = new ResourceIcon(TextureMap.locationItemsTexture, this.getIcon(armorStack, 0));
		return this.icon[armorStack.getItemDamage()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub

	}
}
