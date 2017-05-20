package zmaster587.advancedRocketry.item.components;

import java.util.List;

import net.minecraft.client.gui.Gui;
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
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.items.ItemIngredient;
import zmaster587.libVulpes.util.FluidUtils;

public class ItemPressureTank extends ItemIngredient implements IArmorComponent {

	ResourceIcon icon;
	
	int capacity;
	public ItemPressureTank( int number, int capacity) {
		super(number);
		this.capacity = capacity;
		this.maxStackSize = 1;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {
		super.addInformation(stack, player, list, bool);
		
		FluidStack fluidStack = FluidUtils.getFluidForItem(stack);
		
		if(fluidStack == null) {
			list.add("Empty");
		}
		else {
			list.add(fluidStack.getLocalizedName() + ": " + fluidStack.amount);
		}
	}
	
	@Override
	public void onTick(World world, EntityPlayer player, ItemStack armorStack, IInventory inv,
			ItemStack componentStack) {
		
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
	
	public int getCapacity(ItemStack container) {
		return capacity*(int)Math.pow(2, container.getItemDamage());
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		return null;
	}
	
	@Override
	public boolean isAllowedInSlot(ItemStack stack, EntityEquipmentSlot slot) {
		return slot == EntityEquipmentSlot.CHEST;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack,
			NBTTagCompound nbt) {
		return new TankCapabilityItemStack(stack, getCapacity(stack));
	}

}
