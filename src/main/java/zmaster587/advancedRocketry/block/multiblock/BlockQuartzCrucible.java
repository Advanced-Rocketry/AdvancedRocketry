package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockQuartzCrucible extends CauldronBlock {
	
	public BlockQuartzCrucible(Properties properties) {
		super(properties);
	}

	
	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
	}
    
	@Override
	@ParametersAreNonnullByDefault
	public void fillWithRain(World worldIn, BlockPos pos) {
	}
}
