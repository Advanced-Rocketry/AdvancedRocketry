package zmaster587.advancedRocketry.block;
import net.minecraft.block.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This entire block class is simply a copy of Vanilla fire to make it more volatile and possibly better-behaved
 * It has a special render and lights fires much faster to the sides of it
 */
public class BlockRocketFire extends AbstractFireBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;

	public BlockRocketFire(AbstractBlock.Properties builder) {
		super(builder, 1.0F);
		this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
	}

	public void tick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if (!worldIn.isAreaLoaded(pos, 2)) return; // Forge: prevent loading unloaded chunks when spreading fire

		int i = state.get(AGE);
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, rand.nextInt(5));
		worldIn.setBlockState(pos, state.with(AGE, Math.min(i + 1, 15)), 3);

		this.tryCatchFire(worldIn, pos.east(), 100, rand, i, Direction.WEST);
		this.tryCatchFire(worldIn, pos.west(), 100, rand, i, Direction.EAST);
		this.tryCatchFire(worldIn, pos.down(), 75, rand, i, Direction.UP);
		this.tryCatchFire(worldIn, pos.up(), 75, rand, i, Direction.DOWN);
		this.tryCatchFire(worldIn, pos.north(), 100, rand, i, Direction.SOUTH);
		this.tryCatchFire(worldIn, pos.south(), 100, rand, i, Direction.NORTH);

		for (int k = -1; k <= 1; ++k) {
			for (int l = -1; l <= 1; ++l) {
				for (int i1 = -1; i1 <= 4; ++i1) {
					if (k != 0 || i1 != 0 || l != 0) {
						int j1 = 100;

						if (i1 > 1) {
							j1 += (i1 - 1) * 100;
						}

						BlockPos blockpos = pos.add(k, i1, l);
						int k1 = this.getNeighborEncouragement(worldIn, blockpos);

						if (k1 > 0) {
							int l1 = (k1 + 40 + worldIn.getDifficulty().getId() * 7) / (i + 30);

							if (l1 > 0 && rand.nextInt(j1) <= l1 && (!worldIn.isRaining() || !canFireDie(worldIn, blockpos))) {
								worldIn.setBlockState(blockpos, Blocks.FIRE.getDefaultState().with(FireBlock.AGE, 2), 3);
							}
						}
					}
				}
			}
		}
		if (i == 15) {
			worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
		}
	}

	public void onBlockAdded(World worldIn, BlockPos pos, BlockState state) {
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, worldIn.rand.nextInt(10));
	}

	private int getNeighborEncouragement(World worldIn, BlockPos pos) {
		if (!worldIn.isAirBlock(pos)) {
			return 0;
		} else {
			int i = 0;
			for (Direction enumfacing : Direction.values()) {
				i =Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getFireSpreadSpeed(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
			}
			return i + 150;
		}
	}


	@Deprecated //Forge: Use canCatchFire with more context
	protected boolean canBurn(BlockState state) {
		return true;
	}

	private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, Direction face) {
		int i = worldIn.getBlockState(pos).getFlammability(worldIn, pos, face);
		if (random.nextInt(chance) < i) {

			if (random.nextInt(age + 10) < 10) {
				int j = age + random.nextInt(5) / 4;
				if (j > 15) {
					j = 15;
				}

				worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState().with(FireBlock.AGE, j), 3);
			} else {
				worldIn.removeBlock(pos, false);
			}
		}
	}

	protected boolean canFireDie(World worldIn, BlockPos pos) {
		return worldIn.isRainingAt(pos) || worldIn.isRainingAt(pos.west()) || worldIn.isRainingAt(pos.east()) || worldIn.isRainingAt(pos.north()) || worldIn.isRainingAt(pos.south());
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
