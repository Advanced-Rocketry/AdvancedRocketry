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
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.items.ItemIngredient;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

public class ItemUpgrade extends ItemIngredient implements IArmorComponent {

	private int legUpgradeDamage = 2;
	private int bootsUpgradeDamage = 3;
	private Field walkSpeed;
	
	public ItemUpgrade(int num) {
		super(num);
		setMaxStackSize(1);
		
		walkSpeed = ReflectionHelper.findField(net.minecraft.entity.player.PlayerCapabilities.class, "walkSpeed", "field_75097_g");
		walkSpeed.setAccessible(true);
	}

	@Override
	public void onTick(World world, EntityPlayer player, @Nonnull ItemStack armorStack,
			IInventory modules, @Nonnull ItemStack componentStack) {

		if(componentStack.getItemDamage() == legUpgradeDamage) {
			if(player.isSprinting()) {
				int itemCount = 0;
				for(int i = 0; i < modules.getSizeInventory(); i++) {
					ItemStack stackInSlot = modules.getStackInSlot(i);
					if(!stackInSlot.isEmpty() && stackInSlot.getItem() == this && stackInSlot.getItemDamage() == legUpgradeDamage) {
						//Avoid extra calculation
						if(itemCount == 0 && stackInSlot != componentStack)
							return;
						itemCount++;
					}
				}
				//Walkspeed
				try {
					walkSpeed.setFloat(player.capabilities, (itemCount+1)*0.1f);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				//ReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, (itemCount+1)*0.1f, "walkSpeed", "field_75097_g");
			} else
				try {
					walkSpeed.setFloat(player.capabilities, 0.1f);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			//ReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, 0.1f,"walkSpeed", "field_75097_g");
		}
		else if(componentStack.getItemDamage() == bootsUpgradeDamage && 
				(!ARConfiguration.getCurrentConfig().lowGravityBoots || DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGravitationalMultiplier() < 1f))
			player.fallDistance = 0;
	}

	@Override
	public boolean onComponentAdded(World world, @Nonnull ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, @Nonnull ItemStack armorStack) {

	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack,
							   @Nonnull ItemStack componentStack, DamageSource source, int damage) {

	}

	@Override
	public boolean isAllowedInSlot(@Nonnull ItemStack componentStack, EntityEquipmentSlot targetSlot) {
		if(componentStack.getItemDamage() == legUpgradeDamage)
			return targetSlot == EntityEquipmentSlot.LEGS;
		else if(componentStack.getItemDamage() == bootsUpgradeDamage)
			return targetSlot == EntityEquipmentSlot.FEET;
		return targetSlot == EntityEquipmentSlot.HEAD;
	}

	@Override
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(@Nonnull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
		
	}
}
