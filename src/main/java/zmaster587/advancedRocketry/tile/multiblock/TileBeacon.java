package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

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
			{Blocks.AIR, LibVulpesBlocks.blockMachineStructure, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{Blocks.AIR,Blocks.AIR, Blocks.AIR},
			{Blocks.AIR, LibVulpesBlocks.blockMachineStructure, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{Blocks.AIR,Blocks.AIR, Blocks.AIR},
			{Blocks.AIR, LibVulpesBlocks.blockMachineStructure, Blocks.AIR},
			{Blocks.AIR,Blocks.AIR, Blocks.AIR}
		},
		{
			{null,'c', null},
			{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure},
			{null, LibVulpesBlocks.blockMachineStructure, null}
		}
			};

			public TileBeacon()
			{
				super(AdvancedRocketryTileEntityType.TILE_BEACON);
			}
			
	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.beacon";
	}

	@Override
	public String getMachineName() {
		return getModularInventoryName();
	}
	
	@Override
	public void setMachineEnabled(boolean enabled) {
		super.setMachineEnabled(enabled);

		if(DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(world))) {
			DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(world);
			if(enabled) {
				props.addBeaconLocation(world,new HashedBlockPosition(this.getPos()));
			}
			else
				props.removeBeaconLocation(world,new HashedBlockPosition(getPos()));
		}
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) { return true; }

	@Override
	public AxisAlignedBB getRenderBoundingBox() {

		return new AxisAlignedBB(pos.add(-5,-0,-5), pos.add(5,5,5));
	}
}
