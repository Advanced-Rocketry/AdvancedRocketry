package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.block.BlockMeta;

import java.util.List;
import java.util.stream.Collectors;

public class MapGenCraterHuge extends MapGenBase {

	int chancePerChunk;

	public MapGenCraterHuge(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;
		//We want these to be significantly larger to fit enormous craters
		this.range = 52;
	}

	@Override
	public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
		int i = this.range;
		this.world = worldIn;
		this.rand.setSeed(worldIn.getSeed());
		long j = this.rand.nextLong();
		long k = this.rand.nextLong();

		for (int l = x - i; l <= x + i; ++l) {
			for (int i1 = z - i; i1 <= z + i; ++i1) {
				long j1 = (long)l * j;
				long k1 = (long)i1 * k;
				this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
				this.recursiveGenerate(worldIn, l, i1, x, z, primer);
			}
		}
	}

	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {

		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());

		List<IBlockState> ores = props.craterOres.stream()
				.filter(OreDictionary::doesOreNameExist)
				.map(s->OreDictionary.getOres(s).get(0))
				.map(itemStack-> new BlockMeta(Block.getBlockFromItem(itemStack.getItem()),itemStack.getItemDamage()).getBlockState())
				.collect(Collectors.toList());

		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk && shouldCraterSpawn(DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()), world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16)))) {

			//Random coefficients for the sin functions
			int[] sinCoefficients = {rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1};
			//Radius determination, with heavy weight towards smaller craters
			int baseRadius = getBaseRadius(rand.nextInt(400));
			//Perturbation # calculation && center spire boolean
			int numBulges = rand.nextInt(5) + 1;
			boolean spire = rand.nextInt(4) == 0;

			//Turn the coordinates from chunk stuff into their actual values
			int xCoord = -chunkX + p_180701_4_;
			int zCoord =  -chunkZ + p_180701_5_;


			//Set up fluid fill, if needed
			IBlockState fillBlock = Blocks.AIR.getDefaultState();
			int fluidMaxY = 0;
			for(int x = 15; x >= 0; x--) {
				for (int z = 15; z >= 0; z--) {
					for (int y = 254; y >= 0; y--) {
						if (chunkPrimerIn.getBlockState(x, y, z).getBlock() instanceof BlockLiquid || chunkPrimerIn.getBlockState(x, y, z).getBlock() instanceof IFluidBlock) {
							if (y > fluidMaxY) {
								fillBlock = chunkPrimerIn.getBlockState(x, y, z);
								fluidMaxY = y;
							}
						} else if (chunkPrimerIn.getBlockState(x, y, z).getBlock() != Blocks.AIR)
							break;
					}
				}
			}

			//Actually generate the crater
			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {
					for (int y = 254; y >= 0; y--) {
						if (y <= fluidMaxY && fillBlock != Blocks.AIR && chunkPrimerIn.getBlockState(x, y, z).getBlock() == Blocks.AIR) {
							chunkPrimerIn.setBlockState(x, y, z, fillBlock);
						}
						if (!isCraterIgnoredBlock(chunkPrimerIn.getBlockState(x, y, z).getBlock())) {
							//Get us some funky radii up in here
							int radius = getRadius(baseRadius, (xCoord * 16) + x, (zCoord * 16) + z, numBulges, sinCoefficients);

							//Standard inversePartialSquareRadius & blockRadius stuff
							int distancesSquared = ((xCoord * 16) + x) * ((xCoord * 16) + x) + ((zCoord * 16) + z) * ((zCoord * 16) + z);
							int blockRadius = (int)Math.sqrt(distancesSquared);
							int inversePartialSquareRadius = (int)((radius*radius - distancesSquared) / (baseRadius * ((baseRadius > 256) ? 4 : (baseRadius > 128) ? 3 : 2.25)));
							int inverseRadius = radius - blockRadius;

							//Places filler blocks to excavate the crater
							for (int dist = 0; dist < inversePartialSquareRadius; dist++) {
								if (y - dist > 2) {
									chunkPrimerIn.setBlockState(x, y - Math.min(27, dist), z, (y-dist <= fluidMaxY) ? fillBlock : Blocks.AIR.getDefaultState());
								}
							}

							//Places blocks to form the ridges
							double ridgeSize = Math.max(1, (12 * (radius)/64.0));
							if (inverseRadius <= radius/4 && inverseRadius > -3 * radius) {
								//The graph of this function and the old one can be found here https://www.desmos.com/calculator/x02rgy2wlf
								for (int dist = -1; dist < 9 * ridgeSize * ((1 - inverseRadius)/(0.8 * radius + (inverseRadius - 1) * (inverseRadius - 1))) - 1.06; dist++) {
									//Place the bank thrown up by the impact, and have some of the farthest be dispersed
									if (y + dist < 255 && inverseRadius > -0.75 * radius)
										chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
									else if (y + dist < 255 && inverseRadius >= -0.875 * radius  && !(rand.nextInt(inverseRadius + (int)(radius * 0.875) + 1) == 0))
										chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
									else if (y + dist < 255 && inverseRadius < -0.875 * radius  && rand.nextInt(Math.abs(inverseRadius + (int)(radius * 0.875)) + 1) == 0)
										chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));

									//Ejecta blocks on top, then ejecta blocks below farther out
									if (rand.nextInt(Math.abs(inverseRadius) + 1) == 0) {
										if (inverseRadius < -0.375 * radius && inverseRadius > -1.5 * radius)
											chunkPrimerIn.setBlockState(x, y + dist + 1, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
										else if (inverseRadius < -1.5 * radius)
											chunkPrimerIn.setBlockState(x, y + dist + 1+ rand.nextInt(2), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
									}
								}
							}

							//Places blocks to form the surface of the bowl
							if (inversePartialSquareRadius >= 0) {
								//Two blocks to remove weird stone
								chunkPrimerIn.setBlockState(x, y - Math.min(28, inversePartialSquareRadius), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
								chunkPrimerIn.setBlockState(x, y - 1 - Math.min(28, inversePartialSquareRadius), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
							}

							//Place spire in the center of the bowl
							//An example of this graph is https://www.desmos.com/calculator/nn5xmzyu6i
							if (blockRadius < 0.25 * radius && spire) {
								for (int dist = 0; dist < Math.pow(Math.abs(-(radius/16.0) + blockRadius/4.0), 1.25); dist++) {
									chunkPrimerIn.setBlockState(x, y + Math.min(dist, 16) - 27, z, this.getBlockToPlaceRich(world, chunkX, chunkZ, ores));
								}

							}

							break;
						}
					}
				}
			}
		}
	}

	//Ignore liquids, and ignore air. Everything else is fair game
	private static boolean isCraterIgnoredBlock(Block block) {
		return block instanceof BlockLiquid || block instanceof IFluidBlock || block == Blocks.AIR || block == Blocks.ICE;
	}

	//Place some ores but not a lot, if ore list exists
	private IBlockState getBlockToPlace(World world, int chunkX, int chunkZ, List<IBlockState> ores) {
		if(rand.nextInt(24) == 0 && !ores.isEmpty()) {
			return ores.get(rand.nextInt(ores.size()));
		} else {
			return world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16)).topBlock;
		}
	}

	//Place a fair few ores, if ore list exists
	private IBlockState getBlockToPlaceRich(World world, int chunkX, int chunkZ, List<IBlockState> ores) {
		if(rand.nextInt(4) == 0 && !ores.isEmpty()) {
			return ores.get(rand.nextInt(ores.size()));
		} else {
			return world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16)).topBlock;
		}
	}

	//Check biome frequency
	private boolean shouldCraterSpawn(DimensionProperties properties, Biome biome) {
		if (properties.getCraterBiomeWeights().isEmpty()) return true;
		for (BiomeManager.BiomeEntry biomeEntry : properties.getCraterBiomeWeights()) {
			if (biomeEntry.biome.equals(biome) && biomeEntry.itemWeight > rand.nextInt(99))
				return true;
		}
		return false;
	}

	//Random radius determination
	private int getBaseRadius(int random) {
		int radius = 84;
		if (random < 200)
			radius += rand.nextInt(75);
		else if (random < 325)
			radius += 24 + rand.nextInt(75);
		else if (random < 375)
			radius += 40 + rand.nextInt(75);
		else if (random < 400)
			radius += 56 + rand.nextInt(75);
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