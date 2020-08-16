package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import zmaster587.advancedRocketry.block.BlockCrystal;

public class MapGenLargeCrystal extends MapGenBase {

	BlockState fillerBlock;
	BlockState crystalBlock;

	public MapGenLargeCrystal(BlockState fillerBlock, BlockState blockCrystal) {
		this.fillerBlock = fillerBlock;
		this.crystalBlock = blockCrystal;
	}

	public void setFillerBlock(BlockState fillerBlock) {
		this.fillerBlock = fillerBlock;
	}

	public void setCrystalBlock(BlockState crystalBlock) {
		this.crystalBlock = crystalBlock;
	}

	@Override
	protected void recursiveGenerate(World worldIn, int rangeX,
			int rangeZ, int chunkX, int chunkZ, ChunkPrimer blocks) {
		int chancePerChunk = 6;
		if(rand.nextInt(chancePerChunk) == Math.abs(rangeX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(rangeZ) % chancePerChunk) { 
			int x = 16*(-chunkX + rangeX);
			int z = 16*(-chunkZ + rangeZ);
			int y;

			BlockState state = fillerBlock;
			Block fillerBlock = state.getBlock();

			int height = rand.nextInt(40) + 10;
			int edgeRadius = rand.nextInt(4) + 2;
			int numDiag = edgeRadius + 1;
			int xShear = 1 - (rand.nextInt(6) + 3) / 4; //1/6 lean right, 1/6 lean left, 4/6 no lean
			int zShear = 1 - (rand.nextInt(6) + 3) / 4; //1/6 lean right, 1/6 lean left, 4/6 no lean

			BlockState usedState = crystalBlock.with(BlockCrystal.CRYSTALPROPERTY, BlockCrystal.EnumCrystal.values()[rand.nextInt(BlockCrystal.EnumCrystal.values().length)]);

			int currentEdgeRadius;

			final float SHAPE = 0.01f + rand.nextFloat()*0.2f;

			y = 80;//getHeightValue(x, z, blocks) - 2;

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * height )) + ((1f-SHAPE)*edgeRadius));

			//Make the base of the crystal
			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

					for(int yOff = getHeightValue(x + xOff, z + zOff, blocks); yOff < y; yOff++) //Fills the gaps under the crystal
						setBlock(x + xOff, yOff, z + zOff, usedState, blocks);
					setBlock(x + xOff, y, z + zOff, fillerBlock.getDefaultState(), blocks);
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

					for(int yOff = getHeightValue(x + xOff, z + zOff, blocks); yOff < y; yOff++) //Fills the gaps under the crystal
						setBlock(x + xOff, yOff, z + zOff, fillerBlock.getDefaultState(), blocks);
					setBlock(x + xOff, y, z + zOff, fillerBlock.getDefaultState(), blocks);
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					for(int yOff = getHeightValue(x + xOff, z + zOff, blocks); yOff < y; yOff++) //Fills the gaps under the crystal
						setBlock(x + xOff, yOff, z + zOff, fillerBlock.getDefaultState(), blocks);
					setBlock(x + xOff, y, z + zOff, fillerBlock.getDefaultState(), blocks);
				}
			}

			y++;


			for(int yOff = 0; yOff < height; yOff++) {

				currentEdgeRadius = (int)((SHAPE*(edgeRadius * (height - yOff))) + ((1f-SHAPE)*edgeRadius));

				//Generate the top trapezoid
				for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff, usedState, blocks);
					}
					currentEdgeRadius++;
				}

				//Generate square segment
				for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff, usedState, blocks);
					}
				}

				//Generate the bottom trapezoid
				for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
					currentEdgeRadius--;
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff, usedState, blocks);
					}
				}
			}


			currentEdgeRadius = (int)((SHAPE*(edgeRadius * height )) + ((1f-SHAPE)*edgeRadius));
			//Make some rand noise in the base
			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
					if(rand.nextInt(3)  < 1)
						setBlock(x + xOff, y, z + zOff, state, blocks);
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
					if(rand.nextInt(3)  < 1)
						setBlock(x + xOff, y, z + zOff, state, blocks);
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
					if(rand.nextInt(3)  < 1)
						setBlock(x + xOff, y, z + zOff, state, blocks);
				}
			}
		}
	}

	private void setBlock(int x, int y, int z , BlockState block, ChunkPrimer blocks) {

		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;
		blocks.setBlockState(x, y, z, block);
	}

	private int getHeightValue(int x, int z, ChunkPrimer blocks) {
		int y;
		if(x > 15 || x < 0 || z > 15 || z < 0)
			return 0;
		for(y = 255; blocks.getBlockState(x, y, z).getBlock() == Blocks.AIR && y > 0; y--)
		{
			//System.out.println(y);
		}
		return y;
	}
}
