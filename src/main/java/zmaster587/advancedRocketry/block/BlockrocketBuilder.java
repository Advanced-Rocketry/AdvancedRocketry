package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.block.RotatableBlock;

public class BlockrocketBuilder extends RotatableBlock {

	public BlockrocketBuilder(Material par2Material) {
		super(par2Material);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileRocketBuilder();
	}

	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int a, float b, float c,
			float d) {
		
		player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.RocketBuilder.ordinal(), world, x, y, z);
		
		return true;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister icons) {
		front = icons.registerIcon("advancedrocketry:MonitorFront");
		rear = sides = icons.registerIcon("advancedrocketry:MonitorSide");
		bottom = top = icons.registerIcon("advancedrocketry:MonitorTop");
	}
	
}
