package zmaster587.advancedRocketry.world.decoration;

<<<<<<< HEAD
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction.Axis;
=======
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
<<<<<<< HEAD
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
=======
import net.minecraft.world.gen.MapGenBase;
import zmaster587.advancedRocketry.world.biome.BiomeGenDeepSwamp;
>>>>>>> origin/feature/nuclearthermalrockets

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.serialization.Codec;

import java.util.Random;
import java.util.function.Function;

public class MapGenSwampTree extends  WorldCarver<ProbabilityConfig>  {

	Map<BlockPos, BlockState> cachedCanopy;
	Map<BlockPos, BlockState> cachedRoots;
	private final static double arcSize = 16.0;
	int chancePerChunk;

<<<<<<< HEAD
	public MapGenSwampTree(Codec<ProbabilityConfig> codec, int chancePerChunk) {
		super(codec, chancePerChunk);
		chancePerChunk= 10;
		cachedCanopy = new HashMap<BlockPos, BlockState>();
		cachedRoots = new HashMap<BlockPos, BlockState>();
=======
	public MapGenSwampTree(int chancePerChunk) {
		super();
		cachedCanopy = new HashMap<>();
		cachedRoots = new HashMap<>();
>>>>>>> origin/feature/nuclearthermalrockets
		this.chancePerChunk = chancePerChunk;
		buildCanopy();
		buildRoots();
	}

	/*@Override
	public boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config) {
		return false;
	}*/

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

				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset, yy - yOffset +2,  zz- zOffset)))
					cachedRoots.put(  new BlockPos(2 + xx - xOffset, yy - yOffset +2,  zz- zOffset), Blocks.OAK_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
				if(!cachedRoots.containsKey(new BlockPos(3 + xx - xOffset, yy - yOffset +2,  zz- zOffset)))
					cachedRoots.put(new BlockPos(3 + xx - xOffset, yy - yOffset +2,  zz- zOffset), Blocks.OAK_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset, yy - yOffset +2, 1 + zz- zOffset)))
					cachedRoots.put( new BlockPos(2 + xx - xOffset, yy - yOffset +2, 1 + zz- zOffset), Blocks.OAK_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset,  yy - yOffset +3, 1 + zz- zOffset)))
					cachedRoots.put( new BlockPos(2 + xx - xOffset,  yy - yOffset +3, 1 + zz- zOffset), Blocks.OAK_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
				if(!cachedRoots.containsKey(new BlockPos(1 + xx - xOffset  , yy - yOffset +2, zz- zOffset)))
					cachedRoots.put(new BlockPos(1 + xx - xOffset  , yy - yOffset +2, zz- zOffset), Blocks.OAK_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset, yy - yOffset +2, zz- zOffset - 1)))
					cachedRoots.put( new BlockPos(2 + xx - xOffset, yy - yOffset +2, zz- zOffset - 1), Blocks.OAK_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
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
					if(!cachedCanopy.containsKey(new BlockPos(2 + xx - xOffset, yyy + yy - yOffset +2, zz- zOffset)))
						cachedCanopy.put(new BlockPos(2 + xx - xOffset, yyy + yy - yOffset +2, zz- zOffset), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true));

				//world.setBlock( x + 2 + xx - xOffset - radius/2, treeHeight -3 + yy - yOffset +2, z + zz- zOffset, Blocks.vine, 0,2);
			}

		}
	}

	@Override
	public boolean carveRegion(IChunk world, Function<BlockPos, Biome> biomePos, Random rand,
			int p_225555_8_, int rangeX, int rangeZ, int chunkX, int chunkZ, BitSet carvingMask,
			ProbabilityConfig config) {

		int x = (rangeX - chunkX)*16;
		int z =  (rangeZ- chunkZ)*16;
		int y = 56;


		int treeHeight = rand.nextInt(10) + 40;
		int radius = 4;

		int edgeRadius = 1;
		int numDiag = edgeRadius + 1;

		int meta = 3;
		BlockState block = Blocks.OAK_LOG.getDefaultState();
		int currentEdgeRadius;

		final float SHAPE = 0.1f;

		currentEdgeRadius = (int)((SHAPE*(edgeRadius * treeHeight )) + ((1f-SHAPE)*edgeRadius));

		y++;


		for(int yOff = -20; yOff < treeHeight; yOff++) {

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * (treeHeight - yOff))) + ((1f-SHAPE)*edgeRadius));

			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					setBlock(new BlockPos(x + xOff, y + yOff, z + zOff), block, world);
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					setBlock(new BlockPos(x + xOff, y + yOff, z + zOff), block, world);
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					setBlock(new BlockPos(x + xOff, y + yOff, z + zOff), block, world);
				}
			}
		}

		//Canopy
		for(Entry<BlockPos, BlockState> entry : cachedCanopy.entrySet())
			setBlock( entry.getKey().add(x - radius/2, y + treeHeight, z), entry.getValue(), world);

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

				setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
				setBlock( new BlockPos(x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
				setBlock( new BlockPos(x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
				setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
				setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset + 1), Blocks.OAK_LOG.getDefaultState(), world);
				setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset - 1), Blocks.OAK_LOG.getDefaultState(), world);

			}

<<<<<<< HEAD
=======
					final int p_i46030_2_ = y + treeHeight + yy - yOffset + 2;
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 3 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 1 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset + 1), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset - 1), Blocks.LOG.getDefaultState(), blocks);
>>>>>>> origin/feature/nuclearthermalrockets

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
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), world);
				xx=xxx;
				zz=zzz;
				//Descending 
				for(int yyy = 0; yyy < 10; yyy++) {


<<<<<<< HEAD
					for(zz = -2; zz < 3; zz++)
						for(xx = -2; xx < 3; xx++)
							setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight - yyy + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), world);
					xx=xxx;
					zz=zzz;
=======
					int yy = (int)(arcSize*Math.sin(Math.PI/3.0));
					double xzRadius = (1.25*arcSize*Math.cos(Math.PI/2.0));
					int xx = (int) (xzRadius*Math.cos(Yangle));
					int zz = (int) (xzRadius*Math.sin(Yangle));
					int xxx = xx;
					int zzz = zz;
					//Leaf caps on bottom
					for(zz = -1; zz < 2; zz++)
						for(xx = -1; xx < 2; xx++)
							setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false), blocks);
					//Descending 
					for(int yyy = 0; yyy < 10; yyy++) {


						for(zz = -2; zz < 3; zz++)
							for(xx = -2; xx < 3; xx++)
								setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight - yyy + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false), blocks);
						xx=xxx;
						zz=zzz;

						final int p_i46030_2_ = y + treeHeight + yy - yyy - yOffset + 2;
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 3 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 1 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +3, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset + 1), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, p_i46030_2_, z + zz- zOffset - 1), Blocks.LOG.getDefaultState(), blocks);
					}
				}
>>>>>>> origin/feature/nuclearthermalrockets

					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
					setBlock( new BlockPos(x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
					setBlock( new BlockPos(x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +3, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), world);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset + 1), Blocks.OAK_LOG.getDefaultState(), world);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset - 1), Blocks.OAK_LOG.getDefaultState(), world);
				}
			}

		}


		//roots
		for(Entry<BlockPos, BlockState> entry : cachedRoots.entrySet())
			setBlock( entry.getKey().add( + x - radius/2, y, z), entry.getValue(), world);
		
		return true;
	}

	protected void func_151538_a(World world2, int rangeX,
			int rangeZ, int chunkX, int chunkZ,
			Block[] blocks) {


	}

	private void setBlock(BlockPos pos, BlockState block, IChunk world) {

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;

		world.setBlockState(new BlockPos(x, y, z), block, false);
	}

	private BlockState getBlock(BlockPos pos, Block block, ChunkPrimer blocks) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return Blocks.AIR.getDefaultState();

		return blocks.getBlockState(pos);
	}

	public boolean generate(World world, Random rand, int x, int y, int z)
	{

		int treeHeight = rand.nextInt(10) + 40;
		int radius = 4;
		boolean flag = true;

		int edgeRadius = 1;
		int numDiag = edgeRadius + 1;

		int meta = 3;
		BlockState block = Blocks.OAK_LOG.getDefaultState();
		int currentEdgeRadius;

		final float SHAPE = 0.1f;

		currentEdgeRadius = (int)((SHAPE*(edgeRadius * treeHeight )) + ((1f-SHAPE)*edgeRadius));

		//Make the base of the crystal
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(BlockPos yOff = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x + xOff, 0, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					world.setBlockState(yOff, block);
				world.setBlockState(new BlockPos(x + xOff, y, z + zOff), block);
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(BlockPos yOff = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x + xOff, 0, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					world.setBlockState(yOff, block);
				world.setBlockState(new BlockPos(x + xOff, y, z + zOff), block);
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				for(BlockPos yOff = world.getHeight(Type.WORLD_SURFACE, new BlockPos(x + xOff, 0, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					world.setBlockState(yOff, block);
				world.setBlockState(new BlockPos(x + xOff, y, z + zOff), block);
			}
		}

		y++;


		for(int yOff = 0; yOff < treeHeight; yOff++) {

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * (treeHeight - yOff))) + ((1f-SHAPE)*edgeRadius));

			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), block);  //meta?
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), block);  //meta?
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), block);  //meta?
				}
			}
		}

		//Canopy
		for(Entry<BlockPos, BlockState> entry : cachedCanopy.entrySet())
			world.setBlockState(entry.getKey().add(x - radius/2, y + treeHeight, z), entry.getValue(), 2);

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

				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset + 1), Blocks.OAK_LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset - 1), Blocks.OAK_LOG.getDefaultState(), 5); //meta

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
<<<<<<< HEAD
						world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2,y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), 5);
				xx=xxx;
				zz=zzz;
=======
						world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2,y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false), 5);
>>>>>>> origin/feature/nuclearthermalrockets
				//Descending 
				for(int yyy = 0; yyy < 10; yyy++) {


					for(zz = -2; zz < 3; zz++)
						for(xx = -2; xx < 3; xx++)
							world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight - yyy + yy - yOffset +2, z + zz- zOffset), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), 5);
					xx=xxx;
					zz=zzz;

					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 3 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 1 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +3, z + zz- zOffset), Blocks.OAK_LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset + 1), Blocks.OAK_LOG.getDefaultState(),5);
					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset - 1), Blocks.OAK_LOG.getDefaultState(), 5);
				}
			}

		}


		//roots

		for(Entry<BlockPos, BlockState> entry : cachedRoots.entrySet())
			world.setBlockState( entry.getKey().add( x - radius/2, y , z), entry.getValue(),2);

		return true;
	}


	//Just a helper macro
	private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
	{

		world.getBlockState(new BlockPos(x,y,z)).getBlock().onPlantGrow(world.getBlockState(new BlockPos(x,y,z)), world, new BlockPos(x, y, z), new BlockPos(sourceX, sourceY, sourceZ));
	}

	@Override
	public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
		return rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk;
	}

	@Override
	protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
		// TODO Auto-generated method stub
		return false;
	}
}
