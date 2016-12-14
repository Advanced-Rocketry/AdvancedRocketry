package zmaster587.advancedRocketry.block.cable;

import java.util.Random;

import zmaster587.advancedRocketry.tile.cables.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPipe extends Block {

	protected BlockPipe(Material material) {
		super(material);

		setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		super.updateTick(world, x, y, z, random);
		TilePipe pipe = ((TilePipe)world.getTileEntity(x, y, z));

		if (!pipe.isInitialized()) {
			pipe.onPlaced();
			pipe.markDirty();
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int a, float b, float c,float d) {

		//if(!player.worldObj.isRemote) 
			//player.addChatMessage(new ChatComponentText(((TilePipe)world.getTileEntity(x,y,z)).toString()));

		return super.onBlockActivated(world, x, y, z, player, a, b, c, d);
	}
	
	@Override
	public void onPostBlockPlaced(World world, int x,
			int y, int z, int p_149714_5_) {
		((TilePipe)world.getTileEntity(x, y, z)).onPlaced();
		
		super.onPostBlockPlaced(world, x, y, z,
				p_149714_5_);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		((TilePipe)world.getTileEntity(x, y, z)).onNeighborTileChange(tileX, tileY, tileZ);
	}

	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		if(!world.isRemote)
			((TilePipe)world.getTileEntity(x, y, z)).onPlaced();
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return false;
	}
}