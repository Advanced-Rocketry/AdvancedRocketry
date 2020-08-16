package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BlockQuartzCrucible extends CauldronBlock {
	
	public BlockQuartzCrucible(Properties properties) {
		super(properties);
	}

	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
	}
    
	@Override
	public void fillWithRain(World worldIn, BlockPos pos) {
	}
}
