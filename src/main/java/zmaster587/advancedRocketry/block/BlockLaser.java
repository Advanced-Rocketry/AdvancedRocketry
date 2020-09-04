package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;

import java.util.Random;

public class BlockLaser extends BlockMultiblockMachine {

	public BlockLaser(Properties properties) {
		super(properties.tickRandomly(), (int)GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileSpaceLaser();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving)  {
		if(blockIn != this)
			((TileSpaceLaser)worldIn.getTileEntity(pos)).checkCanRun();
	}

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		if(!(world.getTileEntity(neighbor) instanceof TileSpaceLaser))
			((TileSpaceLaser)world.getTileEntity(pos)).checkCanRun();
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if(worldIn.getTileEntity(pos) instanceof TileSpaceLaser)
			((TileSpaceLaser)worldIn.getTileEntity(pos)).onDestroy();
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos,
			Explosion explosionIn) {
		// TODO Auto-generated method stub
		super.onExplosionDestroy(worldIn, pos, explosionIn);
		((TileSpaceLaser)worldIn.getTileEntity(pos)).onDestroy();
	}

	//To check if the laser is jammed
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);

		TileSpaceLaser tile = (TileSpaceLaser)worldIn.getTileEntity(pos);

		if(tile.isJammed())
			tile.attempUnjam();
		else if(!tile.isRunning() && !tile.isFinished()) {
			tile.checkCanRun();
		}
	}
}
