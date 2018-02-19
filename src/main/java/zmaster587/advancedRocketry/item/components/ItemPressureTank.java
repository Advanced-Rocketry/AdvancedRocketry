package zmaster587.advancedRocketry.item.components;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.items.ItemIngredient;

public class ItemPressureTank extends ItemIngredient implements IArmorComponent, IFluidContainerItem {

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
		
		if(stack.getTagCompound() == null) {
			list.add(LibVulpes.proxy.getLocalizedString("msg.empty"));
		}
		else {
			FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
			if(fluidStack != null)
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

	@Override
	public FluidStack getFluid(ItemStack container) {
		
		if(container.hasTagCompound()) {
			return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
		}
		return null;
	}

	@Override
	public int getCapacity(ItemStack container) {
		return capacity*(int)Math.pow(2, container.getItemDamage());
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		
		if(container.hasTagCompound()) {
			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound());
			
			if(stack != null) {
				if(stack.isFluidEqual(resource)) {
					int amtToAdd = Math.min(resource.amount, getCapacity(container) - stack.amount);
					
					if(doFill) {
						stack.amount += amtToAdd;
						stack.writeToNBT(container.getTagCompound());
					}
					return amtToAdd;
				}
				else
					return 0;
			}
		}
		
		int amtToAdd = Math.min(resource.amount, getCapacity(container));
		
		if(doFill) {
			NBTTagCompound nbt = new NBTTagCompound();
			FluidStack stack = new FluidStack(resource, amtToAdd);
			stack.writeToNBT(nbt);
			container.setTagCompound(nbt);
		}
		
		return amtToAdd;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		
		if(container.hasTagCompound()) {
			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound());
			
			if(stack == null)
				return null;
			
			int amountDrain = Math.min(maxDrain, stack.amount);
			
			if(doDrain) {
				if(amountDrain == stack.amount)
					container.setTagCompound(null);
				else {
					stack.amount -= amountDrain;
					stack.writeToNBT(container.getTagCompound());
				}
			}
			
			return new FluidStack(stack, amountDrain);
		}
		
		return null;
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		if(icon == null)
			this.icon = new ResourceIcon(TextureMap.locationItemsTexture, this.getIcon(armorStack, 0));
		
		return this.icon;
	}
	
	@Override
	public boolean isAllowedInSlot(ItemStack stack, int slot) {
		return slot == 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		// TODO Auto-generated method stub
		
	}

}
