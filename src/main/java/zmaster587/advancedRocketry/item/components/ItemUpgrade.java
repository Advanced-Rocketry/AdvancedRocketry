package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
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
import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ItemUpgrade extends Item implements IArmorComponent {
	Field walkSpeed;
	UUID speedUUID = new UUID(2319, 9001);
	
	public ItemUpgrade(Properties props) {
		super(props);
		
	}

	@Override
	public void onTick(World world, PlayerEntity player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {

		if(componentStack.getItem() == AdvancedRocketryItems.itemBionicLegsUpgrade) {
			if(player.isSprinting()) {
				int itemCount = 0;
				for(int i = 0; i < modules.getSizeInventory(); i++) {
					ItemStack stackInSlot = modules.getStackInSlot(i);
					if(!stackInSlot.isEmpty() && stackInSlot.getItem() == this && stackInSlot.getItem() == AdvancedRocketryItems.itemBionicLegsUpgrade) {
						//Avoid extra calculation
						if(itemCount == 0 && stackInSlot != componentStack)
							return;
						itemCount++;
					}
				}
				//Walkspeed
				player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedUUID);
				AttributeModifier speed = new AttributeModifier(speedUUID, "bioniclegs", (itemCount+1)*0.1, Operation.ADDITION);
				player.getAttribute(Attributes.MOVEMENT_SPEED).applyNonPersistentModifier(speed);
				player.abilities.setWalkSpeed((itemCount+1)*0.1f);
			} else
				player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedUUID);
				player.abilities.setWalkSpeed(0.1f);
		}
		else if(componentStack.getItem() == AdvancedRocketryItems.itemPaddedBootsUpgrade &&
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
	public void onArmorDamaged(LivingEntity entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {

	}

	@Override
	public boolean isAllowedInSlot(ItemStack componentStack, EquipmentSlotType targetSlot) {
		if(componentStack.getItem() == AdvancedRocketryItems.itemBionicLegsUpgrade)
			return targetSlot == EquipmentSlotType.LEGS;
		else if(componentStack.getItem() == AdvancedRocketryItems.itemPaddedBootsUpgrade)
			return targetSlot == EquipmentSlotType.FEET;
		return targetSlot == EquipmentSlotType.HEAD;
	}

	@Override
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		return null;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event,
			Screen gui) {
		
	}
}
