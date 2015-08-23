package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.tile.multiblock.TileEntityBlastFurnace;
import zmaster587.libVulpes.tile.TileEntityPointer;
import zmaster587.libVulpes.tile.TileInventoriedPointer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBlastBrick extends Block {

	protected IIcon textures[];

	public BlockBlastBrick() {
		super(Material.rock);
		this.setCreativeTab(CreativeTabs.tabTransport).setBlockName("blastBrick").setBlockTextureName("advancedRocketry:BlastBrick").setHardness(3F).setResistance(15F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par1, float par7, float par8, float par9)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if(meta == 1) {
			TileEntity e = ((TileEntityPointer)world.getTileEntity(x, y, z)).getFinalPointedTile();

			if(e != null && e instanceof TileEntityBlastFurnace) {
				return AdvRocketryBlocks.blockBlastFurnace.onBlockActivated(world, e.xCoord, e.yCoord, e.zCoord, player, par1, par7, par8, par9);
			}
		}
		return false;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {

		if(meta == 1) {
			TileEntity e = ((TileEntityPointer)world.getTileEntity(x, y, z)).getFinalPointedTile();

			if(e != null && e instanceof TileEntityBlastFurnace) {
				((TileEntityBlastFurnace)e).setIncomplete();
			}
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if(world.getBlockMetadata(x, y, z) == 0 && world.getTileEntity(x, y, z) != null)
			world.removeTileEntity(x, y, z);

	}


	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		if(metadata == 1)
			return new TileInventoriedPointer();
		return null;
	}
}
