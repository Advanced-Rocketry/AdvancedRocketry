package zmaster587.advancedRocketry.block;

<<<<<<< HEAD
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
=======
import net.minecraft.block.state.IBlockState;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;

import java.util.Random;

public class BlockOrbitalLaserDrill extends BlockMultiblockMachine {

<<<<<<< HEAD
	public BlockOrbitalLaserDrill(Properties properties) {
		super(properties.tickRandomly(), GuiHandler.guiId.MODULAR);
=======
	public BlockOrbitalLaserDrill() {
		super(TileOrbitalLaserDrill.class, GuiHandler.guiId.MODULAR.ordinal());
		setTickRandomly(true).setUnlocalizedName("spaceLaser");
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileOrbitalLaserDrill();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
<<<<<<< HEAD
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving)  {
		if(blockIn != this)
			((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).checkCanRun();
	}

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
=======

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
>>>>>>> origin/feature/nuclearthermalrockets
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
<<<<<<< HEAD
	public void onExplosionDestroy(World worldIn, BlockPos pos,
			Explosion explosionIn) {
		// TODO Auto-generated method stub
		super.onExplosionDestroy(worldIn, pos, explosionIn);
		((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).onDestroy();
=======
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
		super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
		if (worldIn.getTileEntity(pos) instanceof TileOrbitalLaserDrill)
		    ((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).onDestroy();
>>>>>>> origin/feature/nuclearthermalrockets
	}

	//To check if the laser is jammed
	@Override
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
