package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.state.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
//TODO: finish
public class MapGenInvertedPillar extends MapGenBase {

	int chancePerChunk;
	BlockState block;
	BlockState topBlock;
	BlockState bottomBlock;

	public MapGenInvertedPillar(int chancePerChunk, BlockState bottom, BlockState blockType, BlockState blockTop) {
		super();
		this.chancePerChunk = chancePerChunk;
		block = blockType;
		topBlock = blockTop;
		bottomBlock = bottom;
	}

	@Override
	protected void recursiveGenerate(World worldIn, int rangeX,
			int rangeZ, int chunkX, int chunkZ, ChunkPrimer chunkPrimerIn) {
		if(rand.nextInt(chancePerChunk) == Math.abs(rangeX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(rangeZ) % chancePerChunk) {

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
		}
	}
	
	protected BlockState getBlockAtPercentHeight(float percent) {
		return percent > 0.95f && topBlock == Blocks.DIRT.getDefaultState() ? Blocks.GRASS.getDefaultState() : percent > 0.66f ? topBlock : percent < 0.33f ? bottomBlock : block;
	}

	private void setBlock(int x, int y, int z , BlockState block, ChunkPrimer primer) {

		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;

		primer.setBlockState(x, y, z, block);
	}
}
