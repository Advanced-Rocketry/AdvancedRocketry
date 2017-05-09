package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.init.Blocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class TileBeacon extends TileMultiPowerConsumer {

	private static final Object[][][] structure = new Object[][][] 
			{
		{
			{Blocks.AIR,Blocks.AIR, Blocks.AIR},
			{Blocks.AIR, Blocks.REDSTONE_BLOCK, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{Blocks.AIR,Blocks.AIR, Blocks.AIR},
			{Blocks.AIR, LibVulpesBlocks.blockStructureBlock, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{Blocks.AIR,Blocks.AIR, Blocks.AIR},
			{Blocks.AIR, LibVulpesBlocks.blockStructureBlock, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{Blocks.AIR,Blocks.AIR, Blocks.AIR},
			{Blocks.AIR, LibVulpesBlocks.blockStructureBlock, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{null,'c', null},
			{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock},
			{null, LibVulpesBlocks.blockStructureBlock, null}
		}
			};

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.beacon.name";
	}

	@Override
	public String getMachineName() {
		// TODO Auto-generated method stub
		return getModularInventoryName();
	}
	
	@Override
	public void setMachineEnabled(boolean enabled) {
		super.setMachineEnabled(enabled);

		if(DimensionManager.getInstance().isDimensionCreated(worldObj.provider.getDimension())) {
			DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.getDimension());
			if(enabled) {
				props.addBeaconLocation(worldObj,new HashedBlockPosition(this.getPos()));
			}
			else
				props.removeBeaconLocation(worldObj,new HashedBlockPosition(getPos()));
		}
	}

}
