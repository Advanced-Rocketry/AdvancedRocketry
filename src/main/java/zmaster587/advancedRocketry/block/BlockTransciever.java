package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import zmaster587.libVulpes.block.BlockTile;

public class BlockTransciever extends BlockTile {

	private static AxisAlignedBB[] bb = {new AxisAlignedBB(.25, .25, .75, .75, .75, 1),
		new AxisAlignedBB(.25, .25, 0, .75, .75, 0.25),
		new AxisAlignedBB(.75, .25, .25, 1, .75, .75),
		new AxisAlignedBB(0, .25, .25, 0.25, .75, .75)};
	
	public BlockTransciever(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		
		
		return bb[state.getValue(FACING).ordinal() - 2];
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}
}
