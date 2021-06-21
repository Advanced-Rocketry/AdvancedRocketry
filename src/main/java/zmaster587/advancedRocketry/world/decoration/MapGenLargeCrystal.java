package zmaster587.advancedRocketry.world.decoration;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class MapGenLargeCrystal extends WorldCarver<ProbabilityConfig> {

	BlockState fillerBlock;
	BlockState crystalBlock;

	public MapGenLargeCrystal(Codec<ProbabilityConfig> codec, int chancePerChunk, BlockState fillerBlock, BlockState blockCrystal) {
		super(codec, chancePerChunk);
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
	public boolean carveRegion(IChunk blocks, Function func, Random rand, int p_225555_4_,
			int chunkX, int chunkZ, int rangeX, int rangeZ, BitSet carvingMask,
			ProbabilityConfig config) {

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

		BlockState usedState = AdvancedRocketryBlocks.crystalBlocks[ rand.nextInt(AdvancedRocketryBlocks.crystalBlocks.length) ].getDefaultState();

		int currentEdgeRadius;

		final float SHAPE = 0.01f + rand.nextFloat()*0.2f;

<<<<<<< HEAD
		y = 80;//getHeightValue(x, z, blocks) - 2;
=======
			final int startingCurrentEdgeRadius = (int)((SHAPE*(edgeRadius * height )) + ((1f-SHAPE)*edgeRadius));
			currentEdgeRadius = startingCurrentEdgeRadius;
>>>>>>> origin/feature/nuclearthermalrockets

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

<<<<<<< HEAD
=======
			currentEdgeRadius = startingCurrentEdgeRadius;
			//Make some rand noise in the base
>>>>>>> origin/feature/nuclearthermalrockets
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
		return true;
	}

	/*@Override
	public boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config) {
		return false;
	}*/

	@Override
	public boolean shouldCarve(Random rand, int rangeX, int rangeZ, ProbabilityConfig config) {
		int chancePerChunk = 6;
		return rand.nextInt(chancePerChunk) == Math.abs(rangeX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(rangeZ) % chancePerChunk;
	}

	private void setBlock(int x, int y, int z , BlockState block, IChunk blocks) {

		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;
		blocks.setBlockState(new BlockPos(x, y, z), block, false);
	}

	private int getHeightValue(int x, int z, IChunk blocks) {
		int y;
		if(x > 15 || x < 0 || z > 15 || z < 0)
			return 0;
<<<<<<< HEAD

		return blocks.getHeightmap(Type.WORLD_SURFACE_WG).getHeight(x, z);
	}


	@Override
	protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
		return false;
=======
		for(y = 255; blocks.getBlockState(x, y, z).getBlock() == Blocks.AIR && y > 0; y--) {
			//System.out.println(y);
		}
		return y;
>>>>>>> origin/feature/nuclearthermalrockets
	}
}
