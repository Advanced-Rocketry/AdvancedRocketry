package zmaster587.advancedRocketry.world.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.util.BlockPosition;

public class WorldGenSwampTree extends MapGenBase {

	Map<BlockPosition, BlockMeta> cachedCanopy;
	Map<BlockPosition, BlockMeta> cachedRoots;
	private final static double arcSize = 16.0;
	int chancePerChunk;

	public WorldGenSwampTree(int chancePerChunk) {
		super();
		chancePerChunk= 10;
		cachedCanopy = new HashMap<BlockPosition, BlockMeta>();
		cachedRoots = new HashMap<BlockPosition, BlockMeta>();
		this.chancePerChunk = chancePerChunk;
		buildCanopy();
		buildRoots();
	}

	private void buildRoots() {
		cachedRoots.clear();
		for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/3.0){
			// Yangle = 0.0;//Math.PI/4.0;
			int yOffset = (int)(1.25*arcSize*Math.sin(Math.PI)) + 1;
			int xOffset = (int)(1.1*arcSize*Math.cos(Math.PI)*Math.cos(Yangle));
			int zOffset = (int)(1.1*arcSize*Math.cos(Math.PI)*Math.sin(Yangle));


			for(double angle = Math.PI; angle > 0; angle -= Math.PI/40.0) {
				int yy = (int)(1.25*arcSize*Math.sin(angle));
				double xzRadius = (0.75*arcSize*Math.cos(angle));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));

				if(!cachedRoots.containsKey(new BlockPosition(2 + xx - xOffset, yy - yOffset +2,  zz- zOffset)))
					cachedRoots.put(  new BlockPosition(2 + xx - xOffset, yy - yOffset +2,  zz- zOffset), new BlockMeta(Blocks.log, 3));
				if(!cachedRoots.containsKey(new BlockPosition(3 + xx - xOffset, yy - yOffset +2,  zz- zOffset)))
					cachedRoots.put(new BlockPosition(3 + xx - xOffset, yy - yOffset +2,  zz- zOffset), new BlockMeta(Blocks.log, 3));
				if(!cachedRoots.containsKey(new BlockPosition(2 + xx - xOffset, yy - yOffset +2, 1 + zz- zOffset)))
					cachedRoots.put( new BlockPosition(2 + xx - xOffset, yy - yOffset +2, 1 + zz- zOffset), new BlockMeta(Blocks.log, 3));
				if(!cachedRoots.containsKey(new BlockPosition(2 + xx - xOffset,  yy - yOffset +3, 1 + zz- zOffset)))
					cachedRoots.put( new BlockPosition(2 + xx - xOffset,  yy - yOffset +3, 1 + zz- zOffset), new BlockMeta(Blocks.log, 3));
				if(!cachedRoots.containsKey(new BlockPosition(1 + xx - xOffset  , yy - yOffset +2, zz- zOffset)))
				cachedRoots.put(new BlockPosition(1 + xx - xOffset  , yy - yOffset +2, zz- zOffset), new BlockMeta(Blocks.log, 3));
				if(!cachedRoots.containsKey(new BlockPosition(2 + xx - xOffset, yy - yOffset +2, zz- zOffset - 1)))
					cachedRoots.put( new BlockPosition(2 + xx - xOffset, yy - yOffset +2, zz- zOffset - 1), new BlockMeta(Blocks.log, 3));
			}
		}
	}

	private void buildCanopy() {
		cachedCanopy.clear();
		//Gen the canopy
		for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/512.0){
			// Yangle = 0.0;//Math.PI/4.0;
			int yOffset = (int)(arcSize*Math.sin(1.5*Math.PI/2.0));
			int xOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.cos(Yangle));
			int zOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.sin(Yangle));


			for(double angle = 1.5*Math.PI/2.0; angle > -Math.PI/6.0; angle -= Math.PI/128.0) {
				int yy = (int)(arcSize*Math.sin(angle));
				double xzRadius = (1.3*arcSize*Math.cos(angle));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));

				for(int yyy = -2 ; yyy < 4; yyy++)
					if(!cachedCanopy.containsKey(new BlockPosition(2 + xx - xOffset, yyy + yy - yOffset +2, zz- zOffset)))
					cachedCanopy.put(new BlockPosition(2 + xx - xOffset, yyy + yy - yOffset +2, zz- zOffset), new BlockMeta(Blocks.leaves, 3));
				//world.setBlock( x + 2 + xx - xOffset - radius/2, treeHeight -3 + yy - yOffset +2, z + zz- zOffset, Blocks.vine, 0,2);
			}

		}
	}


	
	protected void func_151538_a(World world2, int rangeX,
			int rangeZ, int chunkX, int chunkZ,
			Block[] blocks) {
		if(rand.nextInt(chancePerChunk) == Math.abs(rangeX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(rangeZ) % chancePerChunk) {

			int x = (rangeX - chunkX)*16;
			int z =  (rangeZ- chunkZ)*16;
			int y = 56;
			
			
			int treeHeight = rand.nextInt(10) + 40;
			int radius = 4;

			int edgeRadius = 1;
			int numDiag = edgeRadius + 1;

			int meta = 3;
			Block block = Blocks.log;
			int currentEdgeRadius;

			final float SHAPE = 0.1f;

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * treeHeight )) + ((1f-SHAPE)*edgeRadius));

			y++;


			for(int yOff = -20; yOff < treeHeight; yOff++) {

				currentEdgeRadius = (int)((SHAPE*(edgeRadius * (treeHeight - yOff))) + ((1f-SHAPE)*edgeRadius));

				//Generate the top trapezoid
				for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(x + xOff, y + yOff, z + zOff, block, blocks);
					}
					currentEdgeRadius++;
				}

				//Generate square segment
				for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(x + xOff, y + yOff, z + zOff, block, blocks);
					}
				}

				//Generate the bottom trapezoid
				for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
					currentEdgeRadius--;
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(x + xOff, y + yOff, z + zOff, block, blocks);
					}
				}
			}

			//Canopy
			for(Entry<BlockPosition, BlockMeta> entry : cachedCanopy.entrySet())
				setBlock( entry.getKey().x + x - radius/2, y + treeHeight + entry.getKey().y, z + entry.getKey().z, entry.getValue().getBlock(), blocks);

			//Generate Logs
			for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/8.0){
				// Yangle = 0.0;//Math.PI/4.0;
				int yOffset = (int)(arcSize*Math.sin(1.5*Math.PI/2.0));
				int xOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.cos(Yangle));
				int zOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.sin(Yangle));


				for(double angle = 1.5*Math.PI/2.0; angle > -Math.PI/6.0; angle -= Math.PI/40.0) {
					int yy = (int)(arcSize*Math.sin(angle));
					double xzRadius = (1.25*arcSize*Math.cos(angle));
					int xx = (int) (xzRadius*Math.cos(Yangle));
					int zz = (int) (xzRadius*Math.sin(Yangle));

					setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset, Blocks.log, blocks);
					setBlock( x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset, Blocks.log, blocks);
					setBlock( x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset, Blocks.log, blocks);
					setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset, Blocks.log, blocks);
					setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset + 1, Blocks.log, blocks);
					setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset - 1, Blocks.log, blocks);

				}


				//Generate the hangy things
				if(rand.nextInt(4) == 0) {

					int yy = (int)(arcSize*Math.sin(Math.PI/3.0));
					double xzRadius = (1.25*arcSize*Math.cos(Math.PI/2.0));
					int xx = (int) (xzRadius*Math.cos(Yangle));
					int zz = (int) (xzRadius*Math.sin(Yangle));
					int xxx = xx;
					int zzz = zz;
					//Leaf caps on bottom
					for(zz = -1; zz < 2; zz++)
						for(xx = -1; xx < 2; xx++)
							setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset, Blocks.leaves, blocks);
					xx=xxx;
					zz=zzz;
					//Descending 
					for(int yyy = 0; yyy < 10; yyy++) {


						for(zz = -2; zz < 3; zz++)
							for(xx = -2; xx < 3; xx++)
								setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight - yyy + yy - yOffset +2, z + zz- zOffset, Blocks.leaves, blocks);
						xx=xxx;
						zz=zzz;

						setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset, Blocks.log, blocks);
						setBlock( x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset, Blocks.log, blocks);
						setBlock( x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset, Blocks.log, blocks);
						setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +3, z + zz- zOffset, Blocks.log, blocks);
						setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset + 1, Blocks.log, blocks);
						setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset - 1, Blocks.log, blocks);
					}
				}

			}


			//roots

			for(Entry<BlockPosition, BlockMeta> entry : cachedRoots.entrySet())
				setBlock( entry.getKey().x + x - radius/2, y + entry.getKey().y, z + entry.getKey().z, entry.getValue().getBlock(), blocks);


		}

	}

	private void setBlock(int x, int y, int z , Block block, Block[] blocks) {
		
		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;
		
		int index = (x * 16 + z) * 256 + y;
		blocks[index] = block;
	}
	
	private Block getBlock(int x, int y, int z , Block block, Block[] blocks) {
		
		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return Blocks.air;
		
		int index = (x * 16 + z) * 256 + y;
		return blocks[index];
	}
	
	
	public boolean generate(World world, Random rand, int x, int y, int z)
	{

		int treeHeight = rand.nextInt(10) + 40;
		int radius = 4;
		boolean flag = true;

		Block block2 = world.getBlock(x, y - 1, z);

		int edgeRadius = 1;
		int numDiag = edgeRadius + 1;

		int meta = 3;
		Block block = Blocks.log;
		int currentEdgeRadius;

		final float SHAPE = 0.1f;

		currentEdgeRadius = (int)((SHAPE*(edgeRadius * treeHeight )) + ((1f-SHAPE)*edgeRadius));

		//Make the base of the crystal
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(int yOff = world.getHeightValue(x + xOff, z + zOff); yOff < y; yOff++) //Fills the gaps under the crystal
					world.setBlock(x + xOff, yOff, z + zOff, block);
				world.setBlock(x + xOff, y, z + zOff, block);
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(int yOff = world.getHeightValue(x + xOff, z + zOff); yOff < y; yOff++) //Fills the gaps under the crystal
					world.setBlock(x + xOff, yOff, z + zOff, block);
				world.setBlock(x + xOff, y, z + zOff, block);
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				for(int yOff = world.getHeightValue(x + xOff, z + zOff); yOff < y; yOff++) //Fills the gaps under the crystal
					world.setBlock(x + xOff, yOff, z + zOff, block);
				world.setBlock(x + xOff, y, z + zOff, block);
			}
		}

		y++;


		for(int yOff = 0; yOff < treeHeight; yOff++) {

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * (treeHeight - yOff))) + ((1f-SHAPE)*edgeRadius));

			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlock(x + xOff, y + yOff, z + zOff, block, meta, 3);
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlock(x + xOff, y + yOff, z + zOff, block, meta, 3);
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlock(x + xOff, y + yOff, z + zOff, block, meta, 3);
				}
			}
		}

		//Canopy
		for(Entry<BlockPosition, BlockMeta> entry : cachedCanopy.entrySet())
			world.setBlock( entry.getKey().x + x - radius/2, y + treeHeight + entry.getKey().y, z + entry.getKey().z, entry.getValue().getBlock(), entry.getValue().getMeta(),2);

		//Generate Logs
		for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/8.0){
			// Yangle = 0.0;//Math.PI/4.0;
			int yOffset = (int)(arcSize*Math.sin(1.5*Math.PI/2.0));
			int xOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.cos(Yangle));
			int zOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.sin(Yangle));


			for(double angle = 1.5*Math.PI/2.0; angle > -Math.PI/6.0; angle -= Math.PI/40.0) {
				int yy = (int)(arcSize*Math.sin(angle));
				double xzRadius = (1.25*arcSize*Math.cos(angle));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));

				world.setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset, Blocks.log, 3,5);
				world.setBlock( x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset, Blocks.log, 3,5);
				world.setBlock( x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset, Blocks.log, 3,5);
				world.setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset, Blocks.log, 3,5);
				world.setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset + 1, Blocks.log, 3,5);
				world.setBlock( x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset - 1, Blocks.log, 3,5);

			}


			//Generate the hangy things
			if(rand.nextInt(4) == 0) {

				int yy = (int)(arcSize*Math.sin(Math.PI/3.0));
				double xzRadius = (1.25*arcSize*Math.cos(Math.PI/2.0));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));
				int xxx = xx;
				int zzz = zz;
				//Leaf caps on bottom
				for(zz = -1; zz < 2; zz++)
					for(xx = -1; xx < 2; xx++)
						world.setBlock( x + 2 + xx - xOffset - radius/2,y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset, Blocks.leaves, 3,5);
				xx=xxx;
				zz=zzz;
				//Descending 
				for(int yyy = 0; yyy < 10; yyy++) {


					for(zz = -2; zz < 3; zz++)
						for(xx = -2; xx < 3; xx++)
							world.setBlock( x + 2 + xx - xOffset - radius/2, y +treeHeight - yyy + yy - yOffset +2, z + zz- zOffset, Blocks.leaves, 3,5);
					xx=xxx;
					zz=zzz;

					world.setBlock( x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset, Blocks.log, 3,5);
					world.setBlock( x + 3 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset, Blocks.log, 3,5);
					world.setBlock( x + 1 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset, Blocks.log, 3,5);
					world.setBlock( x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +3, z + zz- zOffset, Blocks.log, 3,5);
					world.setBlock( x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset + 1, Blocks.log, 3,5);
					world.setBlock( x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset - 1, Blocks.log, 3,5);
				}
			}

		}


		//roots

		for(Entry<BlockPosition, BlockMeta> entry : cachedRoots.entrySet())
			world.setBlock( entry.getKey().x + x - radius/2, y + entry.getKey().y, z + entry.getKey().z, entry.getValue().getBlock(), entry.getValue().getMeta(),2);

		return true;
	}



	//Just a helper macro
	private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
	{
		world.getBlock(x, y, z).onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
	}
}
