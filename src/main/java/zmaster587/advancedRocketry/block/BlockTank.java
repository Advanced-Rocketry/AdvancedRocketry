package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.tile.TileModelRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTank extends Block implements IFuelTank{

	
	public BlockTank(Material mat) {
		super(mat);
	}
	
	@Override
	public void onBlockAdded(World world, int x,
			int y, int z) {
		
		int i = world.getBlock(x, y + 1, z) == this ? 1 : 0;
		i += world.getBlock(x, y - 1, z) == this ? 2 : 0;
		
		//If there is no tank below this one
		if( i == 1 && world.getBlockMetadata(x, y, z) != 1) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			((TileModelRender)world.getTileEntity(x, y, z)).setType(TileModelRender.models.TANKEND);
		}
		//If there is no tank above this one
		else if( i == 2  && world.getBlockMetadata(x, y, z) != 2) {
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
			((TileModelRender)world.getTileEntity(x, y, z)).setType(TileModelRender.models.TANKTOP);
		}
		//If there is a tank above and below this one
		else if(world.getBlockMetadata(x, y, z) != 0) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
			((TileModelRender)world.getTileEntity(x, y, z)).setType(TileModelRender.models.TANKMIDDLE);
		}
	}
	
	
	
	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		
		int i = world.getBlock(x, y + 1, z) == this ? 1 : 0;
		
		i += world.getBlock(x, y - 1, z) == this ? 2 : 0;
		
		//If there is no tank below this one
		if( i == 1 && world.getBlockMetadata(x, y, z) != 1) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			((TileModelRender)world.getTileEntity(x, y, z)).setType(TileModelRender.models.TANKEND);
			world.getTileEntity(x, y, z).markDirty();
			world.markBlockForUpdate(x, y, z);
		}
		//If there is no tank above this one
		else if( i == 2  && world.getBlockMetadata(x, y, z) != 2) {
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
			((TileModelRender)world.getTileEntity(x, y, z)).setType(TileModelRender.models.TANKTOP);
			world.getTileEntity(x, y, z).markDirty();
			world.markBlockForUpdate(x, y, z);
		}
		//If there is a tank above and below this one
		else if((i == 0 || i == 3) && world.getBlockMetadata(x, y, z) != 0) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
			((TileModelRender)world.getTileEntity(x, y, z)).setType(TileModelRender.models.TANKMIDDLE);
			world.getTileEntity(x, y, z).markDirty();
			world.markBlockForUpdate(x, y, z);
		}
		
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileModelRender(TileModelRender.models.TANKMIDDLE.ordinal() + metadata);
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {return false;}

	@Override
	public int getMaxFill(World world, int x, int y, int z , int meta) {
		return 1000;
	}
}
