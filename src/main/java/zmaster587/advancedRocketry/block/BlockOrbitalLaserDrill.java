package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;

import java.util.Random;

public class BlockOrbitalLaserDrill extends BlockMultiblockMachine {

	public BlockOrbitalLaserDrill() {
		super(TileOrbitalLaserDrill.class, GuiHandler.guiId.MODULAR.ordinal());
		setTickRandomly(true).setTranslationKey("spaceLaser");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileOrbitalLaserDrill();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		if(!(world.getTileEntity(neighbor) instanceof TileOrbitalLaserDrill))
			((TileOrbitalLaserDrill)world.getTileEntity(pos)).checkCanRun();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		if(worldIn.getTileEntity(pos) instanceof TileOrbitalLaserDrill)
			((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).onDestroy();
	}

	@Override
	public void onBlockExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
		super.onBlockExploded(worldIn, pos, explosionIn);
		if (worldIn.getTileEntity(pos) instanceof TileOrbitalLaserDrill)
		    ((TileOrbitalLaserDrill)worldIn.getTileEntity(pos)).onDestroy();
	}

	//To check if the laser is jammed
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state,
			Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		TileOrbitalLaserDrill tile = (TileOrbitalLaserDrill)worldIn.getTileEntity(pos);

		if(tile.isJammed())
			tile.attemptUnjam();
		else if(!tile.isRunning() && !tile.isFinished()) {
			tile.checkCanRun();
		}
	}
}
