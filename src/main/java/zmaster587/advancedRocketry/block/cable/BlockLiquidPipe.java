package zmaster587.advancedRocketry.block.cable;

import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLiquidPipe extends BlockPipe {
	
	public BlockLiquidPipe(Material material) {
		super(material);
	}

	
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileLiquidPipe();
	}
}
