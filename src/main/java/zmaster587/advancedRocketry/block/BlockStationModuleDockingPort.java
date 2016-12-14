package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		top = icon.registerIcon("libvulpes:machineGeneric");
		rear = sides = bottom = top;
		front = icon.registerIcon("advancedrocketry:satelliteBay");
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileDockingPort();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(!world.isRemote)
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x,
			int y, int z, EntityLivingBase player,
			ItemStack items) {
		super.onBlockPlacedBy(world, x, y, z,
				player, items);
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileDockingPort) {
			((TileDockingPort) tile).registerTileWithStation(world, x, y, z);
		}
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int oldMeta) {
		super.onBlockPreDestroy(world, x, y, z,
				oldMeta);
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileLandingPad) {
			((TileDockingPort) tile).unregisterTileWithStation(world, x, y, z);
		}
	}

}
