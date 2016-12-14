package zmaster587.advancedRocketry.block.cable;

import zmaster587.advancedRocketry.tile.cables.TileEnergyPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnergyPipe extends BlockPipe {
	
	public BlockEnergyPipe(Material material) {
		super(material);
	}

	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEnergyPipe();
	}
}
