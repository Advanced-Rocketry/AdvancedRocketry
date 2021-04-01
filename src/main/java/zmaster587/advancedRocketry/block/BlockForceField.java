package zmaster587.advancedRocketry.block;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockForceField extends AbstractGlassBlock {

	public BlockForceField(Properties materialIn) {
		super(materialIn);
	}
    
    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
    	return false;
    }
}
