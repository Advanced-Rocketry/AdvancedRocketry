package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.capability.TankCapabilityItemStack;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.items.ItemIngredient;
import zmaster587.libVulpes.util.FluidUtils;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ItemPressureTank extends ItemIngredient implements IArmorComponent {

	ResourceIcon icon;
	
	private int capacity;
	public ItemPressureTank( int number, int capacity) {
		super(number);
		this.capacity = capacity;
		this.maxStackSize = 1;
	}
	
	@Override
	public void addInformation(@NotNull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		super.addInformation(stack, player, list, bool);
		
		FluidStack fluidStack = FluidUtils.getFluidForItem(stack);
		
		if(fluidStack == null) {
			list.add(LibVulpes.proxy.getLocalizedString("msg.empty"));
		}
		else {
			list.add(fluidStack.getLocalizedName() + ": " + fluidStack.amount);
		}
	}
	
	@Override
	public void onTick(World world, EntityPlayer player, @NotNull ItemStack armorStack, IInventory inv,
					   @NotNull ItemStack componentStack) {
		
	}

	@Override
	public boolean onComponentAdded(World world, @NotNull ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, @NotNull ItemStack armorStack) {
		
	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, @NotNull ItemStack armorStack,
							   @NotNull ItemStack componentStack, DamageSource source, int damage) {
		
	}
	
	public int getCapacity(@NotNull ItemStack container) {
		return capacity*(int)Math.pow(2, container.getItemDamage());
	}

	@Override
	public ResourceIcon getComponentIcon(@NotNull ItemStack armorStack) {
		return null;
	}
	
	@Override
	public boolean isAllowedInSlot(@NotNull ItemStack stack, EntityEquipmentSlot slot) {
		return slot == EntityEquipmentSlot.CHEST;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(@NotNull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, NBTTagCompound nbt) {
		return new TankCapabilityItemStack(stack, getCapacity(stack));
	}

}
