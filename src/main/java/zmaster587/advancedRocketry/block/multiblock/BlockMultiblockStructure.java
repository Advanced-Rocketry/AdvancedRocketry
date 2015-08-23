package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.tile.multiblock.TileEntityMultiBlock;
import zmaster587.libVulpes.tile.IMultiblock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Block which is integrated into the multiblock structure.  When a structure is formed the block
 * continues to render and when broken will alert the master block
 * most significant damage bit indicates if the block is fully formed
 */
public class BlockMultiblockStructure extends Block {

	protected BlockMultiblockStructure(Material material) {
		super(material);
	}

	public void completeStructure(World world, int x, int y, int z, int meta) {
		world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
	}

	public void destroyStructure(World world, int x, int y, int z, int meta) {
		world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int meta) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof IMultiblock) {
			IMultiblock tileMulti = (IMultiblock)tile;
			
			if(tileMulti.hasMaster()) {
				if(tileMulti.getMasterBlock() instanceof TileEntityMultiBlock)
					((TileEntityMultiBlock)tileMulti.getMasterBlock()).deconstructMultiBlock(world,x,y,z,true);
			}
		}
	}
}
