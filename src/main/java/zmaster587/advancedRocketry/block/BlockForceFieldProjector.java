package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.TileForceFieldProjector;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class BlockForceFieldProjector extends BlockFullyRotatable {

	public BlockForceFieldProjector(Material par2Material) {
		super(par2Material);
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister icon) {
		//super.registerBlockIcons(icon);
		front= icon.registerIcon("advancedrocketry:forceFieldProjectorFront");
		top = bottom = sides = icon.registerIcon("advancedrocketry:forcefieldProjector");
		rear = icon.registerIcon("advancedrocketry:machineGeneric");
	}
	
	
	@Override
	public void breakBlock(World worldIn, int x, int y,
			int z, Block block, int meta) {
		TileEntity tile = worldIn.getTileEntity(x,y,z);
		
		if(tile instanceof TileForceFieldProjector)
			((TileForceFieldProjector)tile).destroyField(getFront(meta));
		
		super.breakBlock(worldIn, x, y, z,
				block, meta);
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileForceFieldProjector();
	}

}
