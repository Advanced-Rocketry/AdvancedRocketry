package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.FullyRotatableBlock;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLaser extends RotatableBlock {

	public BlockLaser() {
		super(Material.IRON);
		setTickRandomly(true).setUnlocalizedName("spaceLaser");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileSpaceLaser();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		((TileSpaceLaser)world.getTileEntity(pos)).checkCanRun();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!world.isRemote)
			player.openGui(LibVulpes.instance, guiId.MODULAR.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos,
			IBlockState state) {
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
		((TileSpaceLaser)worldIn.getTileEntity(pos)).onDestroy();
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos,
			Explosion explosionIn) {
		// TODO Auto-generated method stub
		super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
		((TileSpaceLaser)worldIn.getTileEntity(pos)).onDestroy();
	}
	
	//To check if the laser is jammed
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state,
			Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		
		TileSpaceLaser tile = (TileSpaceLaser)worldIn.getTileEntity(pos);

		if(tile.isJammed())
			tile.attempUnjam();
		else if(!tile.isRunning() && !tile.isFinished()) {
			tile.checkCanRun();
		}
	}
}
