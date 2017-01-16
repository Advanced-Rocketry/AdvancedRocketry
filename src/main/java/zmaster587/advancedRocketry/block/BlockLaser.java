package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLaser extends BlockMultiblockMachine {

	public BlockLaser() {
		super(TileSpaceLaser.class, (int)GuiHandler.guiId.MODULAR.ordinal());
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

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos,
			Block blockIn) {
		if(blockIn != this)
			((TileSpaceLaser)worldIn.getTileEntity(pos)).checkCanRun();
	}

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		if(!(world.getTileEntity(neighbor) instanceof TileSpaceLaser))
			((TileSpaceLaser)world.getTileEntity(pos)).checkCanRun();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		if(worldIn.getTileEntity(pos) instanceof TileSpaceLaser)
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
