package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This entire block class is simply a copy of Vanilla fire to make it more volatile and possibly better-behaved
 * It has a special render and lights fires much faster to the sides of it
 */
public class BlockRocketFire extends Block {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

	public BlockRocketFire() {
		super(Material.LAVA);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
		this.setTickRandomly(true);
	}

	@Override
	public int tickRate(World worldIn) { return 0; }

	@Override
	public boolean requiresUpdates() { return true; }

	@Override
	public boolean isCollidable()
	{
		return false;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isAreaLoaded(pos, 2)) return; // Forge: prevent loading unloaded chunks when spreading fire

		int i = state.getValue(AGE);
		worldIn.scheduleUpdate(pos, this, rand.nextInt(5));
		worldIn.setBlockState(pos, state.withProperty(AGE, Math.min(i + 1, 15)), 3);

		this.tryCatchFire(worldIn, pos.east(), 100, rand, i, EnumFacing.WEST);
		this.tryCatchFire(worldIn, pos.west(), 100, rand, i, EnumFacing.EAST);
		this.tryCatchFire(worldIn, pos.down(), 75, rand, i, EnumFacing.UP);
		this.tryCatchFire(worldIn, pos.up(), 75, rand, i, EnumFacing.DOWN);
		this.tryCatchFire(worldIn, pos.north(), 100, rand, i, EnumFacing.SOUTH);
		this.tryCatchFire(worldIn, pos.south(), 100, rand, i, EnumFacing.NORTH);

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
							int l1 = (k1 + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (i + 30);

							if (l1 > 0 && rand.nextInt(j1) <= l1 && (!worldIn.isRaining() || !canFireDie(worldIn, blockpos))) {
								worldIn.setBlockState(blockpos, Blocks.FIRE.getDefaultState().withProperty(AGE, 2), 3);
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

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.scheduleUpdate(pos, this, worldIn.rand.nextInt(10));
	}

	private int getNeighborEncouragement(World worldIn, BlockPos pos) {
		if (!worldIn.isAirBlock(pos)) {
			return 0;
		} else {
			int i = 0;
			for (EnumFacing enumfacing : EnumFacing.values()) {
				i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFireSpreadSpeed(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
			}
			return i + 150;
		}
	}

	private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face) {
		int i = worldIn.getBlockState(pos).getBlock().getFlammability(worldIn, pos, face);
		if (random.nextInt(chance) < i) {
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if (random.nextInt(age + 10) < 10) {
				int j = age + random.nextInt(5) / 4;
				if (j > 15) {
					j = 15;
				}

				worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState().withProperty(AGE, j), 3);
			} else {
				worldIn.setBlockToAir(pos);
			}

			if (iblockstate.getBlock() == Blocks.TNT) {
				Blocks.TNT.onBlockDestroyedByPlayer(worldIn, pos, iblockstate.withProperty(BlockTNT.EXPLODE, true));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(24) == 0) {
			worldIn.playSound((float) pos.getX() + 0.5F, ((float) pos.getY() + 0.5F), (float) pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}

		for (int i = 0; i < 3; ++i) {
			double d0 = (double) pos.getX() + rand.nextDouble();
			double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
			double d2 = (double) pos.getZ() + rand.nextDouble();
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}

	}

	@Nullable public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, meta);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AGE);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AGE);
	}

	protected boolean canFireDie(World worldIn, BlockPos pos) {
		return worldIn.isRainingAt(pos) || worldIn.isRainingAt(pos.west()) || worldIn.isRainingAt(pos.east()) || worldIn.isRainingAt(pos.north()) || worldIn.isRainingAt(pos.south());
	}
}
