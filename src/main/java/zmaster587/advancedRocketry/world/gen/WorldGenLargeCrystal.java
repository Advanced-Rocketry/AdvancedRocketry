package zmaster587.advancedRocketry.world.gen;

import java.util.Random;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockCrystal;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenLargeCrystal extends WorldGenerator {

	Block block;
	int validMetas;
	public WorldGenLargeCrystal() {
		this.block = AdvancedRocketryBlocks.blockCrystal;
		validMetas = BlockCrystal.numMetas;
	}

	@Override
	public boolean generate(World world, Random rand,
			int x, int y, int z) {
		Block fillerBlock = world.getBiomeGenForCoords(x, z).fillerBlock;

		int height = rand.nextInt(40) + 10;
		int edgeRadius = rand.nextInt(4) + 2;
		int numDiag = edgeRadius + 1;
		int xShear = 1 - (rand.nextInt(6) + 3) / 4; //1/6 lean right, 1/6 lean left, 4/6 no lean
		int zShear = 1 - (rand.nextInt(6) + 3) / 4; //1/6 lean right, 1/6 lean left, 4/6 no lean
		
		int meta = rand.nextInt(validMetas);
		int currentEdgeRadius;

		final float SHAPE = 0.01f + rand.nextFloat()*0.2f;

		y-=2;

		currentEdgeRadius = (int)((SHAPE*(edgeRadius * height )) + ((1f-SHAPE)*edgeRadius));

		//Make the base of the crystal
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				
				for(int yOff = world.getHeightValue(x + xOff, z + zOff); yOff < y; yOff++) //Fills the gaps under the crystal
					world.setBlock(x + xOff, yOff, z + zOff, fillerBlock);
				world.setBlock(x + xOff, y, z + zOff, fillerBlock);
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				
				for(int yOff = world.getHeightValue(x + xOff, z + zOff); yOff < y; yOff++) //Fills the gaps under the crystal
					world.setBlock(x + xOff, yOff, z + zOff, fillerBlock);
				world.setBlock(x + xOff, y, z + zOff, fillerBlock);
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				for(int yOff = world.getHeightValue(x + xOff, z + zOff); yOff < y; yOff++) //Fills the gaps under the crystal
					world.setBlock(x + xOff, yOff, z + zOff, fillerBlock);
				world.setBlock(x + xOff, y, z + zOff, fillerBlock);
			}
		}

		y++;


		for(int yOff = 0; yOff < height; yOff++) {

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * (height - yOff))) + ((1f-SHAPE)*edgeRadius));

			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlock(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff, block, meta, 3);
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlock(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff, block, meta, 3);
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlock(x + xOff + xShear*yOff, y + yOff, z + zOff + zShear*yOff, block, meta, 3);
				}
			}
		}

		
		currentEdgeRadius = (int)((SHAPE*(edgeRadius * height )) + ((1f-SHAPE)*edgeRadius));
		//Make some rand noise in the base
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
				if(rand.nextInt(3)  < 1)
					world.setBlock(x + xOff, y, z + zOff, fillerBlock);
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
				if(rand.nextInt(3)  < 1)
					world.setBlock(x + xOff, y, z + zOff, fillerBlock);
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -currentEdgeRadius/2; xOff <= currentEdgeRadius/2; xOff++) {
				if(rand.nextInt(3)  < 1)
					world.setBlock(x + xOff, y, z + zOff, fillerBlock);
			}
		}

		return true;
	}



}
