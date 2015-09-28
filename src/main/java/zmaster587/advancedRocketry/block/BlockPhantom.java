package zmaster587.advancedRocketry.block;

import java.util.ArrayList;

import zmaster587.advancedRocketry.tile.TileSchematic;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPhantom extends Block {

	public BlockPhantom(Material mat) {
		super(mat);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileSchematic();
	}

	@Override
	public IIcon getIcon(IBlockAccess access, int x,
			int y, int z, int side) {

		TileEntity tile = access.getTileEntity(x, y, z);

		if(tile instanceof TilePlaceholder) {
			TilePlaceholder placeHolder = (TilePlaceholder)tile;

			if(placeHolder.getReplacedBlock() != null)
				return placeHolder.getReplacedBlock().getIcon(side, placeHolder.getReplacedBlockMeta());
		}

		return super.getIcon(access, x, y, z,
				side);
	}

	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world,
			int x, int y, int z, EntityPlayer player) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile instanceof TilePlaceholder) {
			ItemStack stack = ((TilePlaceholder)tile).getReplacedBlock().getPickBlock(target, world, x, y, z, player);
			stack.setItemDamage(((TilePlaceholder)tile).getReplacedBlockMeta());
			return stack;
		}
		return super.getPickBlock(target, world, x, y, z, player);
	}
	
	@Override
	public int getDamageValue(World world, int x,
			int y, int z) {
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TilePlaceholder)
			return ((TilePlaceholder)tile).getReplacedBlockMeta();
		return super.getDamageValue(world, x, y, z);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_,
			int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_,
			int p_149668_2_, int p_149668_3_, int p_149668_4_) {
		return null;
	}
}
