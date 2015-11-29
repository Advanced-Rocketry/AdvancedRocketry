package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler.guiId;
import zmaster587.advancedRocketry.tile.TileRFBattery;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.IMultiblock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRFBattery extends BlockMultiblockStructure {

	public BlockRFBattery(Material material) {
		super(material);
	}
	
	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,	int y, int z, EntityPlayer player,	int meta, float arg0, float arg1,float arg2) {
		
		player.openGui(AdvancedRocketry.instance, guiId.MODULAR.ordinal(), world, x, y, z);
		return true;
	}
	
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileRFBattery(1);
	}
}
