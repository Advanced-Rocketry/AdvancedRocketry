package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.libVulpes.block.BlockMeta;

public class TileWarpCore extends TileMultiBlock {
	
	public static final Object[][][] structure = { 
		{{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(Blocks.gold_block), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}},
			
			{{null, new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), null},
				{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(Blocks.gold_block), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
				{null, new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), null}},
				
		{{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), 'c', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}, 
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(Blocks.gold_block), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}},

	};
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}

}
