package zmaster587.advancedRocketry.block;

import net.minecraft.tileentity.TileEntity;
import zmaster587.libVulpes.block.BlockTile;

public class BlockSolarGenerator extends BlockTile {

	public BlockSolarGenerator(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public boolean isBlockNormalCube() {
		return true;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
}
