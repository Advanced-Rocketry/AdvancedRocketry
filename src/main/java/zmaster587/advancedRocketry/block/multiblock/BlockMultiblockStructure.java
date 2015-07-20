package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockMultiblockStructure extends Block {

	protected BlockMultiblockStructure() {
		super(Material.circuits);
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

		IMultiblock tile = (IMultiblock)world.getTileEntity(x, y, z);
		if(tile.isComplete()) {
			((TileMultiBlockMachine)tile.getMasterBlock()).deconstructMultiBlock(world,x,y,z,true);
		}
	}
}
