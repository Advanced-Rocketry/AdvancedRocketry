package zmaster587.advancedRocketry.world.decoration;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.fluids.IFluidBlock;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.util.ZUtils;

public class StructurePieceCrater extends ScatteredStructurePiece {

	int chancePerChunk;
	int radius;
	int xCenter, zCenter;

	public StructurePieceCrater(Random random, int x, int z) {
		super(AdvancedRocketryBiomes.STRUCTURE_PIECE_CRATER, random, x, 64, z, 128 + 32, 15, 128 + 32);
		this.setCoordBaseMode(null);
		radius = random.nextInt(56) + 8; //64; 8 -> 64
		xCenter = x;
		zCenter = z;
	}

    public StructurePieceCrater(TemplateManager mgr, CompoundNBT piece) {
        super(AdvancedRocketryBiomes.STRUCTURE_PIECE_CRATER, piece);
        radius = piece.getInt("Radius");
        xCenter = piece.getInt("xCenter");
        zCenter =  piece.getInt("zCenter");
     }
	

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readAdditional(CompoundNBT tagCompound) {
       super.readAdditional(tagCompound);
       tagCompound.putInt("Radius", radius);
       tagCompound.putInt("xCenter", xCenter);
       tagCompound.putInt("zCenter", zCenter);
    }

	public boolean func_230383_a_(ISeedReader world, StructureManager structureMgr, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos)
	{

		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world.getWorld()));

		List<BlockState> ores = props.craterOres.stream()
				.map(s-> Block.getBlockFromItem(s.getItem()).getDefaultState())
				.collect(Collectors.toList());

		//Random coefficients for the sin functions
		int[] sinCoefficients = {rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1};
		//Radius determination, with heavy weight towards smaller craters
		int baseRadius = getBaseRadius(rand.nextInt(500), rand);
		boolean large = baseRadius > 32;
		//Perturbation # calculation
		int numBulges = rand.nextInt(large ? 5 : 4) + 1;

		//Turn the coordinates from chunk stuff into their actual values
		int xCoord = chunkPos.x << 4;
		int zCoord = chunkPos.z << 4;

		//Set up fluid fill, if needed
		BlockState fillBlock = Blocks.AIR.getDefaultState();
		int fluidMaxY = 0;
		for(int relx = 15; relx >= 0; relx--) {
			for (int relz = 15; relz >= 0; relz--) {
				for (int y = 254; y >= 0; y--) {

					int x = relx + xCoord;
					int z = relz + zCoord;

					if (getBlockStateFromPos(world.getWorld(), x, y, z, bb).getBlock() instanceof FlowingFluidBlock || getBlockStateFromPos(world.getWorld(), x, y, z, bb).getBlock() instanceof IFluidBlock) {
						if (y > fluidMaxY) {
							fillBlock = getBlockStateFromPos(world.getWorld(), x, y, z, bb);
							fluidMaxY = y;
						}
					} else if (getBlockStateFromPos(world.getWorld(), x, y, z, bb).getBlock() != Blocks.AIR)
						break;
				}
			}
		}

		//Actually generate the crater
		for(int relx = 15; relx >= 0; relx--) {
			for(int relz = 15; relz >= 0; relz--) {
				for (int y = 254; y >= 0; y--) {

					int x = relx + xCoord;
					int z = relz + zCoord;

					if (y <= fluidMaxY && fillBlock.getBlock() != Blocks.AIR && getBlockStateFromPos(world.getWorld(), x, y, z, bb).getBlock() == Blocks.AIR) {
						this.setBlockState(world, fillBlock, x, y, z, bb);
					}
					if (!isCraterIgnoredBlock(getBlockStateFromPos(world.getWorld(), x, y, z, bb).getBlock())) {
						//Get us some funky radii up in here
						int radius = getRadius(baseRadius, x, z, numBulges, sinCoefficients);

						//Standard inverseHalfRadius stuff
						int distancesSquared = (x) * (x) + (z) * (z);
						int blockRadius = (int)Math.sqrt(distancesSquared);
						int inversePartialSquareRadius = (radius*radius - distancesSquared) / (radius * 2);
						int inverseRadius = radius - blockRadius;

						//Places filler blocks to excavate the crater
						for (int dist = 0; dist < inversePartialSquareRadius; dist++) {
							if (y - dist > 2) {
								this.setBlockState(world, (y-dist <= fluidMaxY) ? fillBlock : Blocks.AIR.getDefaultState(), x, y-dist, z, bb);
							}
						}

						//Places blocks to form the ridges
						double ridgeSize = Math.max(1, (12 * (radius)/64.0));
						if (inverseRadius <= radius/4 && inverseRadius > -2 * radius) {
							//The graph of this function and the old one can be found here https://www.desmos.com/calculator/x02rgy2wlf
							for (int dist = -1; dist < 9 * ridgeSize * ((1 - inverseRadius)/(0.8 * radius + (inverseRadius - 1) * (inverseRadius - 1))) - 1.06; dist++) {
								//Place the bank thrown up by the impact, and have some of the farthest be dispersed
								if (y + dist < 255 && inverseRadius > -0.5 * radius)
									this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y + dist, z, bb);
								else if (y + dist < 255 && inverseRadius >= -0.625 * radius)
									this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y + dist, z, bb);
								else if (y + dist < 255 && inverseRadius < -0.625 * radius  && rand.nextInt(Math.abs(inverseRadius + (int)(radius * 0.625)) + 1) == 0)
									this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y + dist, z, bb);

								//Ejecta blocks on top, then ejecta blocks below farther out
								if (rand.nextInt(Math.abs(inverseRadius) + 1) == 0 && baseRadius > 40) {
									double ejectaRadius = -(1.0 + Math.max((baseRadius - 20)/20f, 0.5));
									if (inverseRadius < -0.375 * radius && inverseRadius >= ejectaRadius * radius)
										this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y + dist + 1, z, bb);
									else if (inverseRadius < ejectaRadius * radius)
										this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y + dist + 1 + rand.nextInt(2), z, bb);
								}
							}
						}

						//Places blocks to form the surface of the bowl
						if (inversePartialSquareRadius >= 0 && (y - inversePartialSquareRadius > 0)) {
							//Two blocks to remove weird stone
							this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y - Math.min(large ? 16 : 12, inversePartialSquareRadius), z, bb);
							this.setBlockState(world, this.getBlockToPlace(world.getWorld(), x, z, ores, rand), x, y - 1 - Math.min(large ? 16 : 12, inversePartialSquareRadius), z, bb);
						}

						break;
					}
				}
			}
		}
		return true;
	}
	
   protected void setBlockState(ISeedReader worldIn, BlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
	   //super.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
	   worldIn.setBlockState(new BlockPos(x,y,z), blockstateIn, 2);
   }

	//Ignore liquids, and ignore air. Everything else is fair game
	private static boolean isCraterIgnoredBlock(Block block) {
		return block instanceof FlowingFluidBlock || block instanceof IFluidBlock || block == Blocks.AIR || block == Blocks.ICE;
	}

	//Place some ores but not a lot, if ore list exists
	private BlockState getBlockToPlace(World world, int x, int z, List<BlockState> ores, Random rand) {
		if(rand.nextInt(24) == 0 && !ores.isEmpty()) {
			return ores.get(rand.nextInt(ores.size()));
		} else {
			return world.getBiome(new BlockPos(x, 64, z)).getGenerationSettings().getSurfaceBuilderConfig().getTop();
		}
	}

	//Random radius determination
	private int getBaseRadius(int random, Random rand) {
		int radius = 8;
		if (random < 440)
			radius += rand.nextInt(16);
		else if (random < 485)
			radius += 24 + rand.nextInt(16);
		return radius;
	}

	//Very fun function for fancy radius
	//Int[] MUST be the same size as max bumps or larger!
	private int getRadius(int base, int x, int z, int bumps, int[] random) {
		//We need to start this out with polar coordinates
		double radians = Math.atan2(x, z);

		//Then we want to add some sin-function bumps to it, as determined by the bumps
		//They increase theta each time because then we can get different-placed perturbations
		//An example graph for this is here: https://www.desmos.com/calculator/5ojoqscuxv
		int extras = 0;
		for (int i = 2; i < Math.min(5, bumps) + 2; i++){
			extras += random[i-2] * base * Math.sin(i * radians) * 0.0075;
		}

		return base + extras;
	}
	
}