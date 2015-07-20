package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.TileEntityFuelingStation;
import zmaster587.libVulpes.block.RotatableBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFuelingStation extends RotatableBlock {

	public BlockFuelingStation(Material mat) {
		super(mat);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityFuelingStation();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int a, float b, float c,
			float d) {
		
		//Open the gui when the player right clicks
		player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.FuelingStation.ordinal(), world, x, y, z);
		return true;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister icons) {
		rear = sides = front = icons.registerIcon("advancedrocketry:FuelingMachine");
		bottom = top = icons.registerIcon("advancedrocketry:MonitorTop");
	}
}
