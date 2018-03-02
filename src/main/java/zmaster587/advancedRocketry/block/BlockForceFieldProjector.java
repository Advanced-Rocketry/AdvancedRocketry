package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.TileForceFieldProjector;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class BlockForceFieldProjector extends BlockFullyRotatable {

	public BlockForceFieldProjector(Material par2Material) {
		super(par2Material);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile instanceof TileForceFieldProjector)
			((TileForceFieldProjector)tile).destroyField(getFront(state));
		
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileForceFieldProjector();
	}

}
