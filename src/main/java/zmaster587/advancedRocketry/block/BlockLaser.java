package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.inventory.GuiHandler.guiId;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.libVulpes.block.RotatableBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLaser extends RotatableBlock {

	public BlockLaser() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabTransport).setTickRandomly(true).setBlockName("spaceLaser");
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileSpaceLaser();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	//can happen when lever is flipped... Update the state of the tile
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		((TileSpaceLaser)world.getTileEntity(x, y, z)).checkCanRun();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(!world.isRemote)
			player.openGui(AdvancedRocketry.instance, guiId.MODULAR.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int par5) {
		((TileSpaceLaser)world.getTileEntity(x, y, z)).onDestroy();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon)
	{
		this.top = icon.registerIcon("advancedRocketry:machineGeneric");
		this.sides = icon.registerIcon("advancedRocketry:MonitorSide");
		this.bottom = icon.registerIcon("advancedRocketry:LaserBottom");
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
