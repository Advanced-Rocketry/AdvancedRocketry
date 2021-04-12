package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.infrastructure.*;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.tile.TilePointer;

public class BlockARHatch extends BlockHatch {
<<<<<<< HEAD

=======
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
>>>>>>> 1.16.5
	public BlockARHatch(Properties material) {
		super(material);
		this.setDefaultState(this.stateContainer.getBaseState().with(VISIBLE,true).with(POWERED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(POWERED);
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if(blockAccess.getTileEntity(pos) instanceof TilePointer && !((TilePointer)blockAccess.getTileEntity(pos)).allowRedstoneOutputOnSide(side))
			return 0;

		return 15;
	}

	public void setRedstoneState(World world, BlockState bstate , BlockPos pos, boolean state) {
		if(bstate.getBlock() == this) {
			if(state && bstate.get(POWERED)) {
				world.setBlockState(pos, bstate.with(POWERED, false));
				world.notifyBlockUpdate(pos, bstate,  bstate, 3);
			}
			else if(!state && !bstate.get(POWERED)) {
				world.setBlockState(pos, bstate.with(POWERED, true));
				world.notifyBlockUpdate(pos, bstate,  bstate, 3);
			}
		}
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return !state.get(POWERED);
	}
}
