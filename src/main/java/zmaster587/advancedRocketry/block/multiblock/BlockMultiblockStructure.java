package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Block which is integrated into the multiblock structure.  When a structure is formed the block
 * continues to render and when broken will alert the master block
 * most significant damage bit indicates if the block is fully formed
 */
public class BlockMultiblockStructure extends Block {

	protected BlockMultiblockStructure(Material material) {
		super(material);
	}

	/**
	 * Turns the block invisible or in the case of BlockMultiBlockComponentVisible makes it create a tileEntity
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 */
	public void hideBlock(World world, int x, int y, int z, int meta) {
		world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
	}
	
	public void completeStructure(World world, int x, int y, int z, int meta) {
		
	}

	public void destroyStructure(World world, int x, int y, int z, int meta) {
		world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		return super.shouldSideBeRendered(access, x, y, z, side) && access.getBlockMetadata(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ) < 8;
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
				if(tileMulti.getMasterBlock() instanceof TileMultiBlock)
					((TileMultiBlock)tileMulti.getMasterBlock()).deconstructMultiBlock(world,x,y,z,true);
			}
		}
	}
}
