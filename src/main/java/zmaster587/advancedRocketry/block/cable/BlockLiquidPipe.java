package zmaster587.advancedRocketry.block.cable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;

public class BlockLiquidPipe extends BlockPipe {
	
	public BlockLiquidPipe(Material material) {
		super(material);
	}

	
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileLiquidPipe();
	}
}
