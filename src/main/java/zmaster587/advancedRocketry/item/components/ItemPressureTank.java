package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.capability.TankCapabilityItemStack;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.util.FluidUtils;

import javax.annotation.Nonnull;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ItemPressureTank extends Item implements IArmorComponent {

	ResourceIcon icon;
	
<<<<<<< HEAD
	int capacity;
	public ItemPressureTank(Properties props, int capacity) {
		super(props);
=======
	private int capacity;
	public ItemPressureTank( int number, int capacity) {
		super(number);
>>>>>>> origin/feature/nuclearthermalrockets
		this.capacity = capacity;
	}
	
	@Override
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		super.addInformation(stack, player, list, bool);
		
		FluidStack fluidStack = FluidUtils.getFluidForItem(stack);
		
		if(fluidStack == null) {
			list.add(new TranslationTextComponent("msg.empty"));
		}
		else {
			list.add(new StringTextComponent(fluidStack.getDisplayName().getString() + ": " + fluidStack.getAmount()));
		}
	}
	
	@Override
<<<<<<< HEAD
	public void onTick(World world, PlayerEntity player, ItemStack armorStack, IInventory inv,
			ItemStack componentStack) {
=======
	public void onTick(World world, EntityPlayer player, @Nonnull ItemStack armorStack, IInventory inv,
					   @Nonnull ItemStack componentStack) {
>>>>>>> origin/feature/nuclearthermalrockets
		
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
		
	}
	
	public int getCapacity(ItemStack container) {
		return (int)(capacity * ARConfiguration.getCurrentConfig().suitTankCapacity.get());
=======
	public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack,
							   @Nonnull ItemStack componentStack, DamageSource source, int damage) {
		
	}
	
	public int getCapacity(@Nonnull ItemStack container) {
		return capacity*(int)Math.pow(2, container.getItemDamage());
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		return null;
	}
	
	@Override
<<<<<<< HEAD
	public boolean isAllowedInSlot(ItemStack stack, EquipmentSlotType slot) {
		return slot == EquipmentSlotType.CHEST;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack,
			CompoundNBT nbt) {
		return new TankCapabilityItemStack(stack, getCapacity(stack));
	}

	@Override
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event,
			Screen gui) {
		
=======
	public boolean isAllowedInSlot(@Nonnull ItemStack stack, EntityEquipmentSlot slot) {
		return slot == EntityEquipmentSlot.CHEST;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(@Nonnull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
		return new TankCapabilityItemStack(stack, getCapacity(stack));
>>>>>>> origin/feature/nuclearthermalrockets
	}

}
