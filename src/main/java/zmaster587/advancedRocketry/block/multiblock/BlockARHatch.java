package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
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

	TileEntityType<? extends TileEntity> tileType;
	
	public BlockARHatch(Properties material) {
		super(material);
	}
	
	public BlockARHatch _setTile(TileEntityType<? extends TileEntity> type)
	{
		this.tileType = type;
		return this;
	}
	
	
	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState adjacentBlockState, Direction direction) {
		if(blockState.get(VARIANT) < 2)
			return super.isSideInvisible(blockState, adjacentBlockState, direction);
		return false;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if(blockAccess.getTileEntity(pos) instanceof TilePointer && !((TilePointer)blockAccess.getTileEntity(pos)).allowRedstoneOutputOnSide(side))
			return 0;
		
		return blockState.get(VARIANT) >= 2 ? 15 : 0;
	}
	
	public void setRedstoneState(World world, BlockState bstate , BlockPos pos, boolean state) {
		if(bstate.getBlock() == this) {
			if(state && (bstate.get(VARIANT) & 8) == 0) {
				world.setBlockState(pos, bstate.with(VARIANT, bstate.get(VARIANT) | 8));
				world.notifyBlockUpdate(pos, bstate,  bstate, 3);
			}
			else if(!state && (bstate.get(VARIANT) & 8) != 0) {
				world.setBlockState(pos, bstate.with(VARIANT, bstate.get(VARIANT) & 7));
				world.notifyBlockUpdate(pos, bstate,  bstate, 3);
			}
		}
	}
	
	@Override
	public boolean canProvidePower(BlockState state) {
		return state.get(VARIANT) >= 10;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return tileType.create();
	}
}
