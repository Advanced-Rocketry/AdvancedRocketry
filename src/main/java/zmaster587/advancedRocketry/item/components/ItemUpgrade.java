package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.items.ItemIngredient;

import java.lang.reflect.Field;
import java.util.List;

public class ItemUpgrade extends ItemIngredient implements IArmorComponent {

	private int legUpgradeDamage = 2;
	private int bootsUpgradeDamage = 3;
	Field walkSpeed;
	
	public ItemUpgrade(int num) {
		super(num);
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
				(!Configuration.lowGravityBoots || DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGravitationalMultiplier() < 1f))
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
	public boolean isAllowedInSlot(ItemStack componentStack, EntityEquipmentSlot targetSlot) {
		if(componentStack.getItemDamage() == legUpgradeDamage)
			return targetSlot == EntityEquipmentSlot.LEGS;
		else if(componentStack.getItemDamage() == bootsUpgradeDamage)
			return targetSlot == EntityEquipmentSlot.FEET;
		return targetSlot == EntityEquipmentSlot.HEAD;
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
		
	}
}
