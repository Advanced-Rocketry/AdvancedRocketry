package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import javax.annotation.Nonnull;
import java.util.Random;

public class WorldGenAlienTree extends WorldGenAbstractTree {

	public WorldGenAlienTree(boolean p_i45461_1_)
	{
		super(p_i45461_1_);
	}

	
	@Override
	protected boolean canGrowInto(Block blockType) {
		return super.canGrowInto(blockType) || blockType == AdvancedRocketryBlocks.blockLightwoodSapling || blockType == AdvancedRocketryBlocks.blockLightwoodWood || blockType == AdvancedRocketryBlocks.sblockLightwoodLeaves;
	}

	@Override
	public boolean generate(@Nonnull World world, Random random, BlockPos pos) {
		int treeHeight = random.nextInt(10) + 20;
		boolean flag = true;

		int y = pos.getY();
		int x = pos.getX();
		int z = pos.getZ();
		//Make sure tree can generate
		if (y >= 1 && y + treeHeight + 1 <= 256)
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
			else //Actually generate tree
			{
				IBlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
				Block block2 = state.getBlock();

				boolean isSoil = block2.canSustainPlant(state, world, new BlockPos(x, y - 1, z), EnumFacing.UP, (BlockSapling)Blocks.SAPLING);
				if (isSoil && y < 256 - treeHeight - 1)
				{
					//Throw events
					onPlantGrow(world, x,     y - 1, z,     x, y, z);
					onPlantGrow(world, x + 1, y - 1, z,     x, y, z);
					onPlantGrow(world, x + 1, y - 1, z + 1, x, y, z);
					onPlantGrow(world, x,     y - 1, z + 1, x, y, z);
					int i2 = 0;
					int j2;
					int trunkY;

					for (j2 = 0; j2 < treeHeight; ++j2)
					{
						trunkY = y + j2;

						IBlockState state1 = world.getBlockState(new BlockPos(x, trunkY, z));
						Block block1 = state1.getBlock();

						if (world.isAirBlock(new BlockPos(x, trunkY, z)) || block1.isLeaves(state1, world, new BlockPos(x, trunkY, z)))
						{
							this.setBlockAndNotifyAdequately(world, new BlockPos(x, trunkY, z), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + 1, trunkY, z), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
							this.setBlockAndNotifyAdequately(world, new BlockPos(x, trunkY, z + 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
							this.setBlockAndNotifyAdequately(world, new BlockPos(x + 1, trunkY, z + 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
							i2 = trunkY;
						}
					}

					//Genthe root
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y, z), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y, z), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y, z + 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y, z + 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());

					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z - 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z - 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z + 2), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z + 2), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());

					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z +1 ), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z + 1), AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState());
					
					
					generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 1, 1);
					generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, -1, -1);
					generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, -1, 1);
					generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 1, -1);

					generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 1, 0);
					generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, -1, 0);
					generatePod(world, random, 6, x, random.nextInt(10) + y + treeHeight / 6, z + 1, 0, 1);
					generatePod(world, random, 6, x + 1, random.nextInt(10) + y + treeHeight / 6, z, 0, -1);

					generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z, 1, 1);
					generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z + 1, -1, -1);
					generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z + 1, -1, 1);
					generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z, 1, -1);

					generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight-(treeHeight / 3), z, 1, 0);
					generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight- (treeHeight / 3), z + 1, -1, 0);
					generatePod(world, random, 3, x, random.nextInt(5) + y + treeHeight - (treeHeight / 3), z + 1, 0, 1);
					generatePod(world, random, 3, x + 1, random.nextInt(5) + y + treeHeight - (treeHeight / 3), z, 0, -1);

					for (j2 = -3; j2 <= 3; ++j2)
					{
						for (trunkY = -3; trunkY <= 1; ++trunkY)
						{
							byte b1 = -1;

							for(int c = 0; c < treeHeight - 4; c++) {
								int radius = Math.abs(x + j2) + Math.abs(z + trunkY);
								if( (c < treeHeight/3 && radius < 3 ) || ((c >= treeHeight/3) && radius < 4)){
									this.replaceAirWithLeaves(world, x + j2, i2 + b1 - c, z + trunkY);
									this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b1 - c, z + trunkY);
									this.replaceAirWithLeaves(world, x + j2, i2 + b1 - c, 1 + z - trunkY);
									this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b1 - c, 1 + z - trunkY);
								}
							}


							if ((j2 > -2 || trunkY > -1) && (j2 != -1 || trunkY != -2))
							{
								byte b2 = 1;
								this.replaceAirWithLeaves(world, x + j2, i2 + b2, z + trunkY);
								this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b2, z + trunkY);
								this.replaceAirWithLeaves(world, x + j2, i2 + b2, 1 + z - trunkY);
								this.replaceAirWithLeaves(world, 1 + x - j2, i2 + b2, 1 + z - trunkY);
							}
						}
					}

					if (random.nextBoolean())
					{
						this.replaceAirWithLeaves(world, x, i2 + 2, z);
						this.replaceAirWithLeaves(world, x + 1, i2 + 2, z);
						this.replaceAirWithLeaves(world, x + 1, i2 + 2, z + 1);
						this.replaceAirWithLeaves(world, x, i2 + 2, z + 1);
					}

					for (j2 = -3; j2 <= 4; ++j2)
					{
						for (trunkY = -3; trunkY <= 4; ++trunkY)
						{
							if ((j2 != -3 || trunkY != -3) && (j2 != -3 || trunkY != 4) && (j2 != 4 || trunkY != -3) && (j2 != 4 || trunkY != 4) && (Math.abs(j2) < 3 || Math.abs(trunkY) < 3))
							{
								this.replaceAirWithLeaves(world, x + j2, i2, z + trunkY);
							}
						}
					}

					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
	}


	private void generatePod(World world, Random random, int intitalDist, int x, int y, int z, int dirX, int dirZ) {
		int branchLength = random.nextInt(5) + intitalDist;

		EnumFacing direction = EnumFacing.getFront((dirX != 0 && dirZ != 0) ? Math.abs(dirX)*4 : Math.abs(dirX)*4 + Math.abs(dirZ)*8);

		boolean flag = true;

		for(int l = 0; l < branchLength && flag; l++) {
			int newX = x + (dirX*l);
			int newY = l >= branchLength/2 ? y + 2 : y;
			int newZ = z + (dirZ*l);

			flag = this.replaceBlockWithWood(world, newX, newY, newZ, direction);
			flag = flag && this.replaceBlockWithWood(world, newX, newY - 1, newZ, direction);
			flag = flag && this.replaceBlockWithWood(world, newX + dirZ, newY, newZ + dirX, direction);
			flag = flag && this.replaceBlockWithWood(world, newX + dirZ, newY - 1, newZ + dirX, direction);
		}

		int radius = 4;

		for(int offX = -radius; offX < radius; offX++) {
			for(int offY = -radius; offY < radius; offY++) {
				for(int offZ = -radius; offZ < radius; offZ++) {
					if(offX*offX + offY*offY + offZ*offZ < radius*radius + 1)
						replaceAirWithLeaves(world, x + offX + (dirX*branchLength), y + offY + 1, z + offZ + (dirZ*branchLength));
				}
			}
		}

	}

	private boolean replaceBlockWithWood(World world, int x, int y, int z, EnumFacing direction) {
		BlockPos pos = new BlockPos(x,y,z);
		IBlockState state = world.getBlockState(pos);
		
		Block block = state.getBlock();

		if( block.isReplaceable(world, pos) ||  block.isLeaves(state, world, pos) || block == AdvancedRocketryBlocks.blockLightwoodWood || block == AdvancedRocketryBlocks.blockLightwoodSapling) {
			this.setBlockAndNotifyAdequately(world, pos, AdvancedRocketryBlocks.blockLightwoodWood.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(direction.getAxis())));
			return true;
		}
		else
			return false;
	}

	private void replaceAirWithLeaves(World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);

		if (world.isAirBlock(pos))
		{
			this.setBlockAndNotifyAdequately(world, pos, AdvancedRocketryBlocks.sblockLightwoodLeaves.getDefaultState());
		}
	}

	//Just a helper macro
	private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
	{
		BlockPos pos = new BlockPos(x,y,z);
		world.getBlockState(new BlockPos(x, y, z)).getBlock().onPlantGrow(world.getBlockState(pos), world, pos, new BlockPos(sourceX, sourceY, sourceZ));
	}

}
