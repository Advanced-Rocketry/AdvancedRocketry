package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;

import java.lang.reflect.Field;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ItemUpgrade extends Item implements IArmorComponent {

	private int legUpgradeDamage = 2;
	private int bootsUpgradeDamage = 3;
	Field walkSpeed;
	
	public ItemUpgrade(Properties props) {
		super(props);
		
	}

	@Override
	public void onTick(World world, PlayerEntity player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {

		if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeSpeed) {
			if(player.isSprinting()) {
				int itemCount = 0;
				for(int i = 0; i < modules.getSizeInventory(); i++) {
					ItemStack stackInSlot = modules.getStackInSlot(i);
					if(stackInSlot != null && stackInSlot.getItem() == this && stackInSlot.getItem() == AdvancedRocketryItems.itemUpgradeSpeed) {
						//Avoid extra calculation
						if(itemCount == 0 && stackInSlot != componentStack)
							return;
						itemCount++;
					}
				}
				//Walkspeed
				player.abilities.setWalkSpeed((itemCount+1)*0.1f);
				//ReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, (itemCount+1)*0.1f, "walkSpeed", "field_75097_g");
			} else
				player.abilities.setWalkSpeed(0.1f);
				//ReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, 0.1f,"walkSpeed", "field_75097_g");
		}
		else if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeFallBoots && 
				(!ARConfiguration.getCurrentConfig().lowGravityBoots || DimensionManager.getInstance().getDimensionProperties(world).getGravitationalMultiplier() < 1f))
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
	public void onArmorDamaged(LivingEntity entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {

	}

	@Override
	public boolean isAllowedInSlot(ItemStack componentStack, EquipmentSlotType targetSlot) {
		if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeSpeed)
			return targetSlot == EquipmentSlotType.LEGS;
		else if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeFallBoots)
			return targetSlot == EquipmentSlotType.FEET;
		return targetSlot == EquipmentSlotType.HEAD;
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		return null;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event,
			ContainerScreen<? extends Container> gui) {
		
	}
}
