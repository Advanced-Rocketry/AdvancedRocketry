package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import zmaster587.libVulpes.block.BlockTile;

public class BlockHalfTile  extends BlockTile{
	private static AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, .5f, 1);
	public BlockHalfTile(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		return bb;
	}
}
