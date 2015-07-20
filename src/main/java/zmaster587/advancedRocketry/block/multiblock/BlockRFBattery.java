package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler.guiId;
import zmaster587.advancedRocketry.tile.TileRFBattery;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRFBattery extends BlockMultiblockStructure {

	public BlockRFBattery() {
		super();
	}
	
	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int meta) {

		IMultiblock tile = (IMultiblock)world.getTileEntity(x, y, z);
		if(tile.isComplete()) {
			((TileMultiBlockMachine)tile.getMasterBlock()).deconstructMultiBlock(world,x,y,z,true);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,	int y, int z, EntityPlayer player,	int meta, float arg0, float arg1,float arg2) {
		
		player.openGui(AdvancedRocketry.instance, guiId.PowerStorage.ordinal(), world, x, y, z);
		return true;
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileRFBattery(1);
	}
	
}
