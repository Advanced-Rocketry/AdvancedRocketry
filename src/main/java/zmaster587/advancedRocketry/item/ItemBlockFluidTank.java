package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.tile.TileFluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemBlockFluidTank extends ItemBlock {

	public ItemBlockFluidTank(Block block) {
		super(block);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag bool) {
		super.addInformation(stack, world, list, bool);

		FluidStack fluidStack = getFluid(stack);

		if(fluidStack == null) {
			list.add("Empty");
		}
		else {
			list.add(fluidStack.getLocalizedName() + ": " + fluidStack.amount + "/64000mb");
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean placeBlockAt(@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof TileFluidTank) {
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
			ItemStack stack2 = stack.copy();
			stack2.setCount(1);
			handler.fill(drain(stack2, Integer.MAX_VALUE), true);
		}
		
		return true;
	}
	
	public void fill(@Nonnull ItemStack stack, FluidStack fluid) {
		
		NBTTagCompound nbt;
		FluidTank tank = new FluidTank(640000);
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
			tank.readFromNBT(nbt);
		}
		else
			nbt = new NBTTagCompound();
		
		tank.fill(fluid, true);
		
		tank.writeToNBT(nbt);
		stack.setTagCompound(nbt);
	}
	
	public FluidStack drain(@Nonnull ItemStack stack, int amt) {
		NBTTagCompound nbt;
		FluidTank tank = new FluidTank(640000);
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
			tank.readFromNBT(nbt);
		}
		else
			nbt = new NBTTagCompound();
		
		FluidStack stack2 = tank.drain(amt, true);
		
		tank.writeToNBT(nbt);
		stack.setTagCompound(nbt);
		
		return stack2;
	}
	
	public FluidStack getFluid(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;
		FluidTank tank = new FluidTank(640000);
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
			tank.readFromNBT(nbt);
		}
		
		return tank.getFluid();
	}
}
