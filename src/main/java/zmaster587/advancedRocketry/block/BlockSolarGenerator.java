package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import zmaster587.libVulpes.block.BlockTile;

/**
 * Yes, this class may seem useless, but isBlockNormalCube and isOpaqueCube can't be set in the registry, only by overriding the methods.
 */
public class BlockSolarGenerator extends BlockTile {

	public BlockSolarGenerator(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return true;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}
}
