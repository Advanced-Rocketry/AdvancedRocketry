package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.inventory.GuiHandler.guiId;
import zmaster587.advancedRocketry.tile.TileRFPlug;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.IMultiblock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMultiMachineBattery extends BlockMultiblockStructure {

	protected Class<? extends TileEntity> tileClass;
	protected int guiId;
	
	public BlockMultiMachineBattery(Material material, Class<? extends TileEntity> tileClass, int guiId) {
		super(material);
		this.tileClass = tileClass;
		this.guiId = guiId;
	}
	
	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,	int y, int z, EntityPlayer player,	int meta, float arg0, float arg1,float arg2) {
		
		player.openGui(AdvancedRocketry.instance, guiId, world, x, y, z);
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		try {
			return tileClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
