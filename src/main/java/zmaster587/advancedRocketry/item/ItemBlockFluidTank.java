package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import zmaster587.advancedRocketry.tile.TilePressureTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemBlockFluidTank extends BlockItem {

	public ItemBlockFluidTank( Properties props, Block block) {
		super(block, props);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag bool) {
		super.addInformation(stack, world, list, bool);

		FluidStack fluidStack = getFluid(stack);

		if(fluidStack.isEmpty()) {
			list.add(new StringTextComponent("Empty"));
		}
		else {
			list.add(new StringTextComponent(fluidStack.getDisplayName() + ": " + fluidStack.getAmount() + "/64000mb"));
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
		super.placeBlock(context, state);
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		ItemStack stack = context.getItem();
		
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof TilePressureTank) {
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);

			ItemStack stack2 = stack.copy();
			stack2.setCount(1);
			handler.fill(drain(stack2, Integer.MAX_VALUE), FluidAction.EXECUTE);
		}
		
		return true;
	}
	
	public void fill(@Nonnull ItemStack stack, FluidStack fluid) {
		
		CompoundNBT nbt;
		FluidTank tank = new FluidTank(640000);
		if(stack.hasTag()) {
			nbt = stack.getTag();
			tank.readFromNBT(nbt);
		}
		else
			nbt = new CompoundNBT();
		
		tank.fill(fluid, FluidAction.EXECUTE);
		
		tank.writeToNBT(nbt);
		stack.setTag(nbt);
	}

	public FluidStack drain(ItemStack stack, int amt) {
		CompoundNBT nbt;
		FluidTank tank = new FluidTank(640000);
		if(stack.hasTag()) {
			nbt = stack.getTag();
			tank.readFromNBT(nbt);
		}
		else
			nbt = new CompoundNBT();
		
		FluidStack stack2 = tank.drain(amt, FluidAction.EXECUTE);
		
		tank.writeToNBT(nbt);
		stack.setTag(nbt);
		
		return stack2;
	}

	public FluidStack getFluid(ItemStack stack) {
		CompoundNBT nbt;
		FluidTank tank = new FluidTank(640000);
		if(stack.hasTag()) {
			nbt = stack.getTag();
			tank.readFromNBT(nbt);
		}
		
		return tank.getFluid();
	}
}
