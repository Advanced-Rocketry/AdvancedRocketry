package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import java.util.Random;

import com.mojang.serialization.Codec;

public class WorldGenLargeCrystal extends Feature<NoFeatureConfig> {

	BlockState block;
	public WorldGenLargeCrystal(Codec<NoFeatureConfig> codec) {
		super(codec);
		this.block = AdvancedRocketryBlocks.blockCrystal.getDefaultState();
	}

	@Override
	public boolean generate(ISeedReader world, ChunkGenerator chunkGen, Random rand,
			BlockPos pos, NoFeatureConfig config) {
		
		if(rand.nextInt() % 18 != 0)
			return false;

		BlockState state = world.getBiome(pos).getGenerationSettings().getSurfaceBuilderConfig().getUnder();
		Block fillerBlock = state.getBlock();

		int height = rand.nextInt(40) + 10;
		int edgeRadius = rand.nextInt(4) + 2;
		int numDiag = edgeRadius + 1;
		int xShear = 1 - (rand.nextInt(6) + 3) / 4; //1/6 lean right, 1/6 lean left, 4/6 no lean
		int zShear = 1 - (rand.nextInt(6) + 3) / 4; //1/6 lean right, 1/6 lean left, 4/6 no lean
		
		BlockState usedState = AdvancedRocketryBlocks.crystalBlocks[rand.nextInt(AdvancedRocketryBlocks.crystalBlocks.length)].getDefaultState();
		
		int currentEdgeRadius;

		final float SHAPE = 0.01f + rand.nextFloat()*0.2f;

		int x = pos.getX();
		int z = pos.getZ();
		int y = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x, 0, z)).getY() - 2;


		final int startingCurrentEdgeRadius = (int)((SHAPE*(edgeRadius * height )) + ((1f-SHAPE)*edgeRadius));
		currentEdgeRadius = startingCurrentEdgeRadius;

		//Make the base of the crystal
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(BlockPos yOff = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x + xOff, y, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					setBlockState(world,yOff, fillerBlock.getDefaultState());
				setBlockState(world,new BlockPos(x + xOff, y, z + zOff), fillerBlock.getDefaultState());
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				
				for(BlockPos yOff = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x + xOff, y,z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					setBlockState(world, yOff, fillerBlock.getDefaultState());
				setBlockState(world, new BlockPos(x + xOff, y, z + zOff), fillerBlock.getDefaultState());
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				for(BlockPos yOff = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x + xOff, y, z + zOff)); yOff.getY() < y;  yOff = yOff.up()) //Fills the gaps under the crystal
					setBlockState(world,yOff, fillerBlock.getDefaultState());
				setBlockState(world,new BlockPos(x + xOff, y, z + zOff), fillerBlock.getDefaultState());
			}
		}

		y++;


		for(int yOff = 0; yOff < height; yOff++) {

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * (height - yOff))) + ((1f-SHAPE)*edgeRadius));

			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					setBlockState(world, new BlockPos(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff), usedState);
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					setBlockState(world,new BlockPos(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff), usedState);
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					setBlockState(world, new BlockPos(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff), usedState);
				}
			}
		}

		
		currentEdgeRadius = startingCurrentEdgeRadius;
		//Make some rand noise in the base
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
				if(rand.nextInt(3)  < 1)
					setBlockState(world, new BlockPos(x + xOff, y, z + zOff), state);
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
				if(rand.nextInt(3)  < 1)
					setBlockState(world, new BlockPos(x + xOff, y, z + zOff), state);
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
				if(rand.nextInt(3)  < 1)
					setBlockState(world, new BlockPos(x + xOff, y, z + zOff), state);
			}
		}

		return true;
	}
	
	private void setBlockState(ISeedReader world, BlockPos pos, BlockState state)
	{
		world.setBlockState(pos, state, 2);
	}
}
