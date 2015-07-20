package zmaster587.advancedRocketry.block.multiblock;

import java.util.ArrayList;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMultiblockPlaceHolder extends BlockContainer {

	public BlockMultiblockPlaceHolder() {
		super(Material.iron);
	}

	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int x, int y, int z, int l) {
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube() {
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
	public ItemStack getPickBlock(MovingObjectPosition target, World world,
			int x, int y, int z, EntityPlayer player) {
		TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(x, y, z);


		return tile.getReplacedBlock().getPickBlock(target, world, x, y, z, player);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public void onBlockHarvested(World world, int x,
			int y, int z, int meta,
			EntityPlayer player) {

		super.onBlockHarvested(world, x, y, z, meta, player);

		if(!world.isRemote && !player.capabilities.isCreativeMode) {
			TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(x, y, z);

			Block newBlock = tile.getReplacedBlock();

			if(newBlock != null && newBlock != Blocks.air && player.canHarvestBlock(newBlock)) {
				ArrayList<ItemStack> stackList = newBlock.getDrops(world, x, y, z, meta, 0);

				for(ItemStack stack : stackList) {
					EntityItem entityItem = new EntityItem(world, x, y, z, stack);
					world.spawnEntityInWorld(entityItem);

				}
			}
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x,
			int y, int z, int meta) {

		super.onBlockDestroyedByPlayer(world, x, y,
				z, meta);


	}



	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int oldmeta) {
		super.onBlockPreDestroy(world, x, y, z,
				oldmeta);

		TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(x, y, z);

		if(tile != null) {

			TileMultiBlockMachine masterTile = (TileMultiBlockMachine)tile.getMasterBlock();

			if(masterTile != null)
				masterTile.deconstructMultiBlock(world,x,y,z,true);
		}

	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TilePlaceholder();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return createTileEntity(world, metadata);
	}
}
