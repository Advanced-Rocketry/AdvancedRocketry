package zmaster587.advancedRocketry.block.cable;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;

public class BlockDataCable extends BlockPipe {
	
	public BlockDataCable(Material material) {
		super(material);
	}

	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileDataPipe();
	}

}
