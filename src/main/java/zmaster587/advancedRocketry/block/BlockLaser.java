package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;

public class BlockLaser extends BlockMultiblockMachine {

	public BlockLaser() {
		super(TileSpaceLaser.class, (int)GuiHandler.guiId.MODULAR.ordinal());
		setTickRandomly(true).setBlockName("spaceLaser");
	}
	
	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		((TileSpaceLaser)world.getTileEntity(x, y, z)).checkCanRun();
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		super.onBlockPreDestroy(world, x, y, z, meta);
		((TileSpaceLaser)world.getTileEntity(x, y, z)).onDestroy();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon)
	{
		this.top = icon.registerIcon("libvulpes:machineGeneric");
		this.sides = icon.registerIcon("advancedRocketry:MonitorSide");
		this.bottom = icon.registerIcon("advancedRocketry:laserBottom");
		this.front = icon.registerIcon("advancedRocketry:LaserFront");
		this.rear = this.sides;
	}

	//To check if the laser is jammed
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		TileSpaceLaser tile = (TileSpaceLaser)world.getTileEntity(x, y, z);

		if(tile.isJammed())
			tile.attempUnjam();
		else if(!tile.isRunning() && !tile.isFinished()) {
			tile.checkCanRun();
		}
	}
}
