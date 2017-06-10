package zmaster587.advancedRocketry.item;

import java.util.List;

import zmaster587.advancedRocketry.tile.TileFluidTank;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ItemBlockFluidTank extends ItemBlock implements IFluidContainerItem {

	public ItemBlockFluidTank(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		FluidStack fluidStack = ((IFluidContainerItem)stack.getItem()).getFluid(stack); //FluidUtils.getFluidForItem(stack);

		if(fluidStack == null) {
			list.add("Empty");
		}
		else {
			list.add(fluidStack.getLocalizedName() + ": " + fluidStack.amount + "/64000mb");
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		boolean bool = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY,
				hitZ, metadata);

		if(!world.isRemote) {
			TileEntity tile = world.getTileEntity(x,y,z);

			if(tile != null && tile instanceof TileFluidTank) {
				TileFluidTank handler = ((TileFluidTank) tile);
				ItemStack stack2 = stack.copy();
				stack2.stackSize = 1;

				handler.fill(ForgeDirection.DOWN, ((IFluidContainerItem)stack.getItem()).getFluid(stack), true);
			}
		}
		
		return bool;
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		if(!container.hasTagCompound())
			return null;
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
	}

	@Override
	public int getCapacity(ItemStack container) {
		return 64000;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		FluidTank tank = new FluidTank(64000);
		NBTTagCompound nbt;
		if(container.hasTagCompound()) {
			tank.readFromNBT(nbt = container.getTagCompound());
		}
		else
			nbt = new NBTTagCompound();

		int i = tank.fill(resource, doFill);

		tank.writeToNBT(nbt);
		container.setTagCompound(nbt);

		return i;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		FluidTank tank = new FluidTank(64000);
		NBTTagCompound nbt;
		if(container.hasTagCompound()) {
			tank.readFromNBT(nbt = container.getTagCompound());
		}
		else
			nbt = new NBTTagCompound();

		FluidStack i = tank.drain(maxDrain, doDrain);

		tank.writeToNBT(nbt);
		container.setTagCompound(nbt);

		return i;
	}
}
