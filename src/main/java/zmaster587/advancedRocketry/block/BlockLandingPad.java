package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.tile.station.TileLandingPad;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockLandingPad extends Block {

	public BlockLandingPad(Material mat) {
		super(mat);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileLandingPad();
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileLandingPad) {
			((TileLandingPad) tile).registerTileWithStation(world, pos);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!world.isRemote)
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), world, pos.getX(), pos.getY() , pos.getZ());
		return true;
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos,
			IBlockState state) {
		super.onBlockDestroyedByPlayer(world, pos, state);
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileLandingPad) {
			((TileLandingPad) tile).unregisterTileWithStation(world, pos);
		}
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos,
			Explosion explosion) {
		super.onBlockDestroyedByExplosion(world, pos, explosion);
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileLandingPad) {
			((TileLandingPad) tile).unregisterTileWithStation(world, pos);
		}
	}
}
