package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.gui.screen.Screen;
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

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ItemUpgrade extends Item implements IArmorComponent {

	private int legUpgradeDamage = 2;
	private int bootsUpgradeDamage = 3;
	private Field walkSpeed;
	
	public ItemUpgrade(Properties props) {
		super(props);
		
	}

	@Override
<<<<<<< HEAD
	public void onTick(World world, PlayerEntity player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {
=======
	public void onTick(World world, EntityPlayer player, @Nonnull ItemStack armorStack,
			IInventory modules, @Nonnull ItemStack componentStack) {
>>>>>>> origin/feature/nuclearthermalrockets

		if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeSpeed) {
			if(player.isSprinting()) {
				int itemCount = 0;
				for(int i = 0; i < modules.getSizeInventory(); i++) {
					ItemStack stackInSlot = modules.getStackInSlot(i);
<<<<<<< HEAD
					if(stackInSlot != null && stackInSlot.getItem() == this && stackInSlot.getItem() == AdvancedRocketryItems.itemUpgradeSpeed) {
=======
					if(!stackInSlot.isEmpty() && stackInSlot.getItem() == this && stackInSlot.getItemDamage() == legUpgradeDamage) {
>>>>>>> origin/feature/nuclearthermalrockets
						//Avoid extra calculation
						if(itemCount == 0 && stackInSlot != componentStack)
							return;
						itemCount++;
					}
				}
				//Walkspeed
<<<<<<< HEAD
				player.abilities.setWalkSpeed((itemCount+1)*0.1f);
			} else
				player.abilities.setWalkSpeed(0.1f);
=======
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
>>>>>>> origin/feature/nuclearthermalrockets
		}
		else if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeFallBoots && 
				(!ARConfiguration.getCurrentConfig().lowGravityBoots.get() || DimensionManager.getInstance().getDimensionProperties(world).getGravitationalMultiplier() < 1f))
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
<<<<<<< HEAD
	public void onArmorDamaged(LivingEntity entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {
=======
	public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack,
							   @Nonnull ItemStack componentStack, DamageSource source, int damage) {
>>>>>>> origin/feature/nuclearthermalrockets

	}

	@Override
<<<<<<< HEAD
	public boolean isAllowedInSlot(ItemStack componentStack, EquipmentSlotType targetSlot) {
		if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeSpeed)
			return targetSlot == EquipmentSlotType.LEGS;
		else if(componentStack.getItem() == AdvancedRocketryItems.itemUpgradeFallBoots)
			return targetSlot == EquipmentSlotType.FEET;
		return targetSlot == EquipmentSlotType.HEAD;
=======
	public boolean isAllowedInSlot(@Nonnull ItemStack componentStack, EntityEquipmentSlot targetSlot) {
		if(componentStack.getItemDamage() == legUpgradeDamage)
			return targetSlot == EntityEquipmentSlot.LEGS;
		else if(componentStack.getItemDamage() == bootsUpgradeDamage)
			return targetSlot == EntityEquipmentSlot.FEET;
		return targetSlot == EntityEquipmentSlot.HEAD;
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		return null;
	}

	@Override
<<<<<<< HEAD
	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event,
			Screen gui) {
=======
	@SideOnly(Side.CLIENT)
	public void renderScreen(@Nonnull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
>>>>>>> origin/feature/nuclearthermalrockets
		
	}
}
