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

public class MapGenCrater extends MapGenBase {
    
	int chancePerChunk;
	int radiusModifier;

	public MapGenCrater(int chancePerChunk, int radiusModifier) {
		this.chancePerChunk = chancePerChunk;
		this.radiusModifier = radiusModifier;
	}
	
	
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {

		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());

		List<IBlockState> ores = props.craterOres.stream()
				.filter(OreDictionary::doesOreNameExist)
				.map(s->OreDictionary.getOres(s).get(0))
				.map(itemStack-> new BlockMeta(Block.getBlockFromItem(itemStack.getItem()),itemStack.getItemDamage()).getBlockState())
				.collect(Collectors.toList());
		
		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk && shouldCraterSpawn(DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()), world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16)))) {

			//Random stuff
			int baseRadius = rand.nextInt((int)(1.125 * radiusModifier)) + radiusModifier + 4;
			int[] sinCoefficients = {rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1};

			int depth = baseRadius*baseRadius;
			
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
							int radius = getRadius(baseRadius, (xCoord * 16) + x, (zCoord * 16) + z, 1+ (baseRadius % 5), sinCoefficients);

							//Standard blockRadius stuff
							int blockRadius = (depth - (((xCoord * 16) + x) * ((xCoord * 16) + x) + ((zCoord * 16) + z) * ((zCoord * 16) + z))) / (radius * 2);

							//Places filler blocks to excavate the crater
							for (int dist = 0; dist < blockRadius; dist++) {
								if (y - dist > 2) {
									if (y-dist <= fluidMaxY) {
										chunkPrimerIn.setBlockState(x, y - Math.min(15, dist), z, fillBlock);
									} else {
										chunkPrimerIn.setBlockState(x, y - Math.min(15, dist), z, Blocks.AIR.getDefaultState());
									}
								}
							}

							//Places blocks to form the ridges
							double ridgeSize = Math.max(1, (12 * (radius)/64.0));
							if (blockRadius <= radius/4 && blockRadius > -3 * radius) {
								//The graph of this function and the old one can be found here https://www.desmos.com/calculator/x02rgy2wlf
								for (int dist = -1; dist < 9 * ridgeSize * ((1 - blockRadius)/(0.8 * radius + (blockRadius - 1) * (blockRadius - 1))) - 1.06; dist++) {
									//Place the bank thrown up by the impact, and have some of the farthest be dispersed
									if (y + dist < 255 && blockRadius > -0.875 * radius)
										chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
									else if (y + dist < 255 && blockRadius > -1.125 * radius && rand.nextInt(Math.abs(blockRadius/(radius > 48 ? 4 : 2)) + 1) == 0)
										chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));

									//Ejecta blocks on top, then ejecta blocks below farther out
									if (rand.nextInt(Math.abs(blockRadius) + 1) == 0) {
										if (blockRadius < -0.375 * radius && blockRadius > -1.5 * radius)
											chunkPrimerIn.setBlockState(x, y + dist + 1, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
										else if (blockRadius < -1.5 * radius)
											chunkPrimerIn.setBlockState(x, y + dist + 1+ rand.nextInt(2), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
									}
								}
							}

							//Places blocks to form the surface of the bowl
							if (blockRadius >= 0 && (y - blockRadius > 0)) {
								//Two blocks to remove wierd stone
								chunkPrimerIn.setBlockState(x, y - Math.min(16, blockRadius), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
								chunkPrimerIn.setBlockState(x, y - 1 - Math.min(16, blockRadius), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
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

	private IBlockState getBlockToPlace(World world, int chunkX, int chunkZ, List<IBlockState> ores) {
		if(rand.nextInt(24) == 0 && !ores.isEmpty()) {
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

	//Very fun function for fancy radius
	//Int[] MUST be the same size as max bumps!
	private int getRadius(int base, int x, int z, int bumps, int[] random) {
		//We need to start this out with polar coordinates
		double radians = Math.atan2(x, z);

		//Then we want to add some sin-function bumps to it, as determined by the bumps
		//They increase theta each time because then we can get different-placed perturbations
		//An example graph for this is here: https://www.desmos.com/calculator/2dqaekywth
		int extras = 0;
		for (int i = 2; i < Math.min(5, bumps) + 2; i++){
			extras += random[i-2] * base * Math.sin(i * radians) * 0.02;
		}

		return base + extras;
	}
}