package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.multiblock.TileDataBus;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.tile.ISidedRedstoneTile;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockARHatch extends BlockHatch {
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	public BlockARHatch(Properties material) {
		super(material);
		this.setDefaultState(this.stateContainer.getBaseState().with(VISIBLE,true).with(POWERED, false));
	}

	@Override
	@ParametersAreNonnullByDefault
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(POWERED);
	}

	@Override
	@ParametersAreNonnullByDefault
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity tile = blockAccess.getTileEntity(pos);
		if (tile instanceof ISidedRedstoneTile) {
			if (((ISidedRedstoneTile)tile).allowRedstoneOutputOnSide(side))
				return 0;
			return 15;
		}
		return 0;
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
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileDataBus) {
			return 15 * ((TileDataBus)tile).getData() / ((TileDataBus)tile).getDataObject().getMaxData();
		} else
			return super.getComparatorInputOverride(blockState, worldIn, pos);
	}
}
