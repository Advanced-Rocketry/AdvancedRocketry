package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import java.util.LinkedList;
import java.util.List;

import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenAlienTree extends StraightTrunkPlacer {
	
   public static final Codec<WorldGenAlienTree> codec = RecordCodecBuilder.create((p_236902_0_) -> getAbstractTrunkCodec(p_236902_0_).apply(p_236902_0_, WorldGenAlienTree::new));

	public WorldGenAlienTree(int i, int j, int k) {
		//3, 11, 0
		super(i, j, k);
	}

	public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader world, Random random, int p_230382_3_, BlockPos pos, Set<BlockPos> p_230382_5_, MutableBoundingBox boundingBox, BaseTreeFeatureConfig p_230382_7_) {
		int treeHeight = random.nextInt(10) + 20;
		boolean flag = true;

		int y = pos.getY();
		int x = pos.getX();
		int z = pos.getZ();
		//Make sure tree can generate
		/*if (y >= 1 && y + treeHeight + 1 <= 256)
		{
			int j1;
			int k1;

			for (int treeHeightIterator = y; treeHeightIterator <= y + 1 + treeHeight; ++treeHeightIterator)
			{
				byte xzIterator = 3;

				if (treeHeightIterator == y)
				{
					xzIterator = 0;
				}

				if (treeHeightIterator >= y + 1 + treeHeight - 2)
				{
					xzIterator = 3;
				}

				for (j1 = x - xzIterator; j1 <= x + xzIterator && flag; ++j1)
				{
					for (k1 = z - xzIterator; k1 <= z + xzIterator && flag; ++k1)
					{
						if (treeHeightIterator >= 0 && treeHeightIterator < 256)
						{

							BlockPos newPos =  new BlockPos(j1, treeHeightIterator, k1);
							if (!this.isReplaceable(world,newPos))
							{
								flag = false;
							}
						}
						else
						{
							flag = false;
						}
					}
				}
			}

			if (!flag)
			{
				return false;
			}
			else *///Actually generate tree
		{
			//BlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
			//Block block2 = state.getBlock();

			boolean isSoil = true;//block2.canSustainPlant(state, world, new BlockPos(x, y - 1, z), Direction.UP, (SaplingBlock)Blocks.SAPLING);
			if (isSoil && y < 256 - treeHeight - 1) {
				//Throw events
				int i2 = 0;
				int j2;
				int trunkY;

				for (j2 = 0; j2 < treeHeight; ++j2) {
					trunkY = y + j2;

					func_236913_a_(world, new BlockPos(x, trunkY, z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
					func_236913_a_(world, new BlockPos(x + 1, trunkY, z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
					func_236913_a_(world, new BlockPos(x, trunkY, z + 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
					func_236913_a_(world, new BlockPos(x + 1, trunkY, z + 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
					i2 = trunkY;
				}
				
				//Genthe root
				func_236913_a_(world, new BlockPos(x - 1, y, z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x + 2, y, z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x + 2, y, z + 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x - 1, y, z + 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);

				func_236913_a_(world, new BlockPos(x, y, z - 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x + 1, y, z - 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x + 1, y, z + 2), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x, y, z + 2), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);

				func_236913_a_(world, new BlockPos(x, y, z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x + 1, y, z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x, y, z +1 ), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);
				func_236913_a_(world, new BlockPos(x + 1, y, z + 1), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState(), boundingBox);


				generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 1, 1, boundingBox);
				generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, -1, -1, boundingBox);
				generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, -1, 1, boundingBox);
				generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 1, -1, boundingBox);

				generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 1, 0, boundingBox);
				generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, -1, 0, boundingBox);
				generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, 0, 1, boundingBox);
				generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 0, -1, boundingBox);

				generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z, 1, 1, boundingBox);
				generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z + 1, -1, -1, boundingBox);
				generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z + 1, -1, 1, boundingBox);
				generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z, 1, -1, boundingBox);

				generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z, 1, 0, boundingBox);
				generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight- (treeHeight / 3), z + 1, -1, 0, boundingBox);
				generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight - (treeHeight / 3), z + 1, 0, 1, boundingBox);
				generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight - (treeHeight / 3), z, 0, -1, boundingBox);

				for (j2 = -3; j2 <= 3; ++j2) {
					for (trunkY = -3; trunkY <= 1; ++trunkY) {
						byte b1 = -1;

						for(int c = 0; c < treeHeight - 4; c++) {
							int radius = Math.abs(x + j2) + Math.abs(z + trunkY);
							if( (c < treeHeight/3 && radius < 3 ) || ((c >= treeHeight/3) && radius < 4)){
								this.replaceAirWithLeaves(world, x + j2, i2 + b1 - c, z + trunkY, boundingBox);
								this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b1 - c, z + trunkY, boundingBox);
								this.replaceAirWithLeaves(world, x + j2, i2 + b1 - c, 1 + z - trunkY, boundingBox);
								this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b1 - c, 1 + z - trunkY, boundingBox);
							}
						}

						if ((j2 > -2 || trunkY > -1) && (j2 != -1 || trunkY != -2)) {
							byte b2 = 1;
							this.replaceAirWithLeaves(world, x + j2, i2 + b2, z + trunkY, boundingBox);
							this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b2, z + trunkY, boundingBox);
							this.replaceAirWithLeaves(world, x + j2, i2 + b2, 1 + z - trunkY, boundingBox);
							this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b2, 1 + z - trunkY, boundingBox);
						}
					}
				}

				if (random.nextBoolean()) {
					this.replaceAirWithLeaves(world, x, i2 + 2, z, boundingBox);
					this.replaceAirWithLeaves(world, x + 1, i2 + 2, z, boundingBox);
					this.replaceAirWithLeaves(world, x + 1, i2 + 2, z + 1, boundingBox);
					this.replaceAirWithLeaves(world, x, i2 + 2, z + 1, boundingBox);
				}

				for (j2 = -3; j2 <= 4; ++j2) {
					for (trunkY = -3; trunkY <= 4; ++trunkY) {
						if ((j2 != -3 || trunkY != -3) && (j2 != -3 || trunkY != 4) && (j2 != 4 || trunkY != -3) && (j2 != 4 || trunkY != 4) && (Math.abs(j2) < 3 || Math.abs(trunkY) < 3)) {
							this.replaceAirWithLeaves(world, x + j2, i2, z + trunkY, boundingBox);
						}
					}
				}
			}
		}
		return new LinkedList<>();
	}


	private void generatePod(IWorldGenerationReader world, Random random, int intitalDist, int x, int y, int z, int dirX, int dirZ, MutableBoundingBox boundingBox) {
		int branchLength = random.nextInt(5) + intitalDist;

		Direction direction = Direction.byIndex((dirX != 0 && dirZ != 0) ? Math.abs(dirX)*4 : Math.abs(dirX)*4 + Math.abs(dirZ)*8);

		boolean flag = true;

		for(int l = 0; l < branchLength && flag; l++) {
			int newX = x + (dirX*l);
			int newY = l >= branchLength/2 ? y + 2 : y;
			int newZ = z + (dirZ*l);

			flag = flag && this.replaceBlockWithWood(world, newX, newY, newZ, direction, boundingBox);
			flag = flag && this.replaceBlockWithWood(world, newX, newY - 1, newZ, direction, boundingBox);
			flag = flag && this.replaceBlockWithWood(world, newX + dirZ, newY, newZ + dirX, direction, boundingBox);
			flag = flag && this.replaceBlockWithWood(world, newX + dirZ, newY - 1, newZ + dirX, direction, boundingBox);
		}

		int radius = 4;

		for(int offX = -radius; offX < radius; offX++) {
			for(int offY = -radius; offY < radius; offY++) {
				for(int offZ = -radius; offZ < radius; offZ++) {
					if(offX*offX + offY*offY + offZ*offZ < radius*radius + 1)
						replaceAirWithLeaves(world, x + offX + (dirX*branchLength), y + offY + 1, z + offZ + (dirZ*branchLength), boundingBox);
				}
			}
		}

	}

	private boolean replaceBlockWithWood(IWorldGenerationReader world, int x, int y, int z, Direction direction, MutableBoundingBox boundingBox) {

		func_236913_a_(world, new BlockPos(x,y,z), AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState().with(RotatedPillarBlock.AXIS, direction.getAxis()), boundingBox);
		return true;

		/*BlockPos pos = new BlockPos(x,y,z);
		BlockState state = world.getBlockState(pos);

		Block block = state.getBlock();

		if( block.isReplaceable(world, pos) ||  block.isLeaves(state, world, pos) || block == AdvancedRocketryBlocks.blockAlienWood || block == AdvancedRocketryBlocks.blockAlienSapling) {
			func_236913_a_(world, pos, AdvancedRocketryBlocks.blockAlienWood.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(direction.getAxis())));
			return true;
		}
		else
			return false;*/
	}

	private void replaceAirWithLeaves(IWorldGenerationReader world, int x, int y, int z, MutableBoundingBox boundingBox) {
		BlockPos pos = new BlockPos(x, y, z);

		//if (world.isAirBlock(pos))
		{
			func_236913_a_(world, pos, AdvancedRocketryBlocks.blockLightwoodLeaves.getDefaultState(), boundingBox);
		}
	}

	static class Foliage {
		private final FoliagePlacer.Foliage field_236892_a_;
		private final int field_236893_b_;

		public Foliage(BlockPos p_i232055_1_, int p_i232055_2_) {
			this.field_236892_a_ = new FoliagePlacer.Foliage(p_i232055_1_, 0, false);
			this.field_236893_b_ = p_i232055_2_;
		}

		public int func_236894_a_() {
			return this.field_236893_b_;
		}
	}
}
