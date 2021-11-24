package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockOrbitalLaserDrill extends BlockMultiblockMachine {

	public BlockOrbitalLaserDrill(Properties properties) {
		super(properties.tickRandomly(), GuiHandler.guiId.MODULAR);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileOrbitalLaserDrill();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)  {
		if(blockIn != this)
			((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).checkCanRun();
	}

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		if(!(world.getTileEntity(neighbor) instanceof TileOrbitalLaserDrill))
			((TileOrbitalLaserDrill)world.getTileEntity(pos)).checkCanRun();
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if(worldIn.getTileEntity(pos) instanceof TileOrbitalLaserDrill)
			((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).onDestroy();
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		super.onExplosionDestroy(worldIn, pos, explosionIn);
		if (worldIn.getTileEntity(pos) instanceof TileOrbitalLaserDrill)
		((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).onDestroy();
	}

	//To check if the laser is jammed
	@Override
	@ParametersAreNonnullByDefault
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);

		TileOrbitalLaserDrill tile = (TileOrbitalLaserDrill)worldIn.getTileEntity(pos);

		if(tile.isJammed())
			tile.attemptUnjam();
		else if(!tile.isRunning() && !tile.isFinished()) {
			tile.checkCanRun();
		}
	}
}
