package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.station.TileDockingPort;
import zmaster587.advancedRocketry.tile.station.TileLandingPad;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.inventory.GuiHandler;

public class BlockStationModuleDockingPort extends BlockFullyRotatable {

	public BlockStationModuleDockingPort(Material par2Material) {
		super(par2Material);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDockingPort();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!worldIn.isRemote)
			playerIn.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileDockingPort) {
			((TileDockingPort) tile).registerTileWithStation(world, pos);
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileLandingPad) {
			((TileDockingPort) tile).unregisterTileWithStation(world, pos);
		}
		super.breakBlock(world, pos, state);
	}

}
