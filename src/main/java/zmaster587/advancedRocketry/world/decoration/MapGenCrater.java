package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MapGenCrater extends MapGenBase {
    
	int chancePerChunk;
	Random rand;

	public MapGenCrater(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;
		this.rand = new Random();
	}
	
	
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {

		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());

		List<IBlockState> ores = props.craterOres.stream()
					   .map(s->OreDictionary.getOres(s).get(0))
					   .map(itemStack->Block.getBlockFromItem(itemStack.getItem())
											   .getDefaultState()
					   )
					   .collect(Collectors.toList());
		
		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk) {

			int radius = rand.nextInt(56) + 8; //64; 8 -> 64
			
			//TODO: make hemisphere from surface and line the side with ore of some kind



			int depth = radius*radius;
			
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
							int count = (depth - (((xCoord * 16) + x) * ((xCoord * 16) + x) + ((zCoord * 16) + z) * ((zCoord * 16) + z))) / (radius * 2);


							for (int dist = 0; dist < count; dist++) {
								if (y - dist > 2) {
									if (y-dist <= fluidMaxY) {
										chunkPrimerIn.setBlockState(x, y - dist, z, fillBlock);
									} else {
										chunkPrimerIn.setBlockState(x, y - dist, z, Blocks.AIR.getDefaultState());
									}
								}
							}

							int ridgeSize = 12;
							if (count <= 0 && count > -2 * ridgeSize) {
								for (int dist = 0; dist < ((ridgeSize * ridgeSize) - (count + ridgeSize) * (count + ridgeSize)) / (ridgeSize * 2); dist++) {
									if (y + dist < 255) {
										chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world,chunkX, chunkZ * 16,ores));
									}
								}
							}

							if (count > 1 && (y - count > 2))
								chunkPrimerIn.setBlockState(x, y - count, z, this.getBlockToPlace(world,chunkX, chunkZ * 16,ores));
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
}