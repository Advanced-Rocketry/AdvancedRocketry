package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.TilePointer;

public class BlockMultiBlockComponentVisible extends BlockMultiblockStructure {

	
	public BlockMultiBlockComponentVisible(Material material) {
		super(material);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return metadata > 7;
	}
	
	public void completeStructure(World world, int x, int y, int z, int meta) {
		world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y,
			int z, int side) {
		//Yes this is hacky...
		return side == 0 && this.minY > 0.0D ? true : (side == 1 && this.maxY < 1.0D ? true : (side == 2 && this.minZ > 0.0D ? true : (side == 3 && this.maxZ < 1.0D ? true : (side == 4 && this.minX > 0.0D ? true : (side == 5 && this.maxX < 1.0D ? true : !access.getBlock(x, y, z).isOpaqueCube())))));
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TilePointer();
	}
}
