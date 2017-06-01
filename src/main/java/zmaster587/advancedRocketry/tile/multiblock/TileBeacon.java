package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.BlockPosition;

public class TileBeacon extends TileMultiPowerConsumer {

	private static final Object[][][] structure = new Object[][][] 
			{
		{
			{Blocks.air,Blocks.air, Blocks.air},
			{Blocks.air, Blocks.redstone_block, Blocks.air},
			{Blocks.air,Blocks.air, Blocks.air}
		},
		{
			{Blocks.air,Blocks.air, Blocks.air},
			{Blocks.air, LibVulpesBlocks.blockStructureBlock, Blocks.air},
			{Blocks.air,Blocks.air, Blocks.air}
		},
		{
			{Blocks.air,Blocks.air, Blocks.air},
			{Blocks.air, LibVulpesBlocks.blockStructureBlock, Blocks.air},
			{Blocks.air,Blocks.air, Blocks.air}
		},
		{
			{Blocks.air,Blocks.air, Blocks.air},
			{Blocks.air, LibVulpesBlocks.blockStructureBlock, Blocks.air},
			{Blocks.air,Blocks.air, Blocks.air}
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

		if(DimensionManager.getInstance().isDimensionCreated(worldObj.provider.dimensionId)) {
			DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);
			if(enabled) {
				props.addBeaconLocation(worldObj,new BlockPosition(xCoord, yCoord, zCoord));
			}
			else
				props.removeBeaconLocation(worldObj,new BlockPosition(xCoord, yCoord, zCoord));
		}
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {

		return AxisAlignedBB.getBoundingBox(xCoord-5, yCoord, zCoord-5, xCoord + 5, yCoord + 5, zCoord + 5);
	}

}
