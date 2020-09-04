package zmaster587.advancedRocketry.world.decoration;


import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
//TODO: finish
public class MapGenInvertedPillar extends WorldCarver<ProbabilityConfig>  {

	int chancePerChunk;
	BlockState block;
	BlockState topBlock;
	BlockState bottomBlock;

	public MapGenInvertedPillar(Codec<ProbabilityConfig> codec, int chancePerChunk, BlockState bottom, BlockState blockType, BlockState blockTop) {
		super(codec, chancePerChunk);
		this.chancePerChunk = chancePerChunk;
		block = blockType;
		topBlock = blockTop;
		bottomBlock = bottom;
	}

	@Override
	public boolean func_225555_a_(IChunk chunkPrimerIn, Function func, Random rand, int p_225555_4_,
			int chunkX, int chunkZ, int rangeX, int rangeZ, BitSet p_225555_9_,
			ProbabilityConfig p_225555_10_) {

			int x = (rangeX - chunkX)*16 + rand.nextInt(15);
			int z =  (rangeZ- chunkZ)*16 + rand.nextInt(15);
			int y = 56;

			int treeHeight = rand.nextInt(10) + 20;
			int radius = 5;

			int edgeRadius = 2;
			int numDiag = edgeRadius + 1;

			int currentEdgeRadius;

			final float SHAPE = -0.005f;

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * Math.pow(treeHeight,2) )) + ((1f-SHAPE)*edgeRadius));

			y++;

			for(int yOff = -20; yOff < treeHeight; yOff++) {
				BlockState actualBlock;// = yOff > (2*(treeHeight+rand.nextInt(4))/3f) ? topBlock : block;
				currentEdgeRadius = (int)((SHAPE*(edgeRadius * Math.pow(treeHeight - yOff, 2))) + ((1f-SHAPE)*edgeRadius));

				//Generate the top trapezoid
				for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						actualBlock = getBlockAtPercentHeight(yOff/(float)(treeHeight+rand.nextInt(4)));
						setBlock(x + xOff, y + yOff, z + zOff, actualBlock, chunkPrimerIn);
					}
					currentEdgeRadius++;
				}

				//Generate square segment
				for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						actualBlock = getBlockAtPercentHeight(yOff/(float)(treeHeight+rand.nextInt(4)));
						setBlock(x + xOff, y + yOff, z + zOff, actualBlock, chunkPrimerIn);
					}
				}

				//Generate the bottom trapezoid
				for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
					currentEdgeRadius--;
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						
						actualBlock = getBlockAtPercentHeight(yOff/(float)(treeHeight+rand.nextInt(4)));
						setBlock(x + xOff, y + yOff, z + zOff, actualBlock, chunkPrimerIn);
					}
				}
		}
		return true;
	}
	

	@Override
	public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
		return rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk;
	}


	@Override
	protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
		return false;
	}
	
	protected BlockState getBlockAtPercentHeight(float percent) {
		return percent > 0.95f && topBlock == Blocks.DIRT.getDefaultState() ? Blocks.GRASS.getDefaultState() : percent > 0.66f ? topBlock : percent < 0.33f ? bottomBlock : block;
	}

	private void setBlock(int x, int y, int z , BlockState block, IChunk primer) {

		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;

		primer.setBlockState(new BlockPos(x, y, z), block, false);
	}
}
