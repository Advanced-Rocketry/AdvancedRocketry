package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
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

		if(DimensionManager.getInstance().isDimensionCreated(world.provider.getDimension())) {
			DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
			if(enabled) {
				props.addBeaconLocation(world,new HashedBlockPosition(this.getPos()));
			}
			else
				props.removeBeaconLocation(world,new HashedBlockPosition(getPos()));
		}
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) { return true; }

	@Override
	public AxisAlignedBB getRenderBoundingBox() {

		return new AxisAlignedBB(pos.add(-5,-0,-5), pos.add(5,5,5));
	}
}
