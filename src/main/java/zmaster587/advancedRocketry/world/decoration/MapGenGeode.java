package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.block.BlockMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapGenGeode extends MapGenBase {
	int chancePerChunk;

	private List<BlockMeta> ores; // = {new BlockMeta(Blocks.IRON_ORE), new BlockMeta(Blocks.GOLD_ORE), new BlockMeta(Blocks.REDSTONE_ORE), new BlockMeta(Blocks.LAPIS_ORE)};

	public MapGenGeode(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;

		ores = new ArrayList<>();
		for(int i = 0; i < ARConfiguration.getCurrentConfig().standardGeodeOres.size(); i++) {
			String oreDictName = ARConfiguration.getCurrentConfig().standardGeodeOres.get(i);
			List<ItemStack> ores2 = OreDictionary.getOres(oreDictName);

			if(ores2 != null && !ores2.isEmpty()) {
				Block block = Block.getBlockFromItem(ores2.get(0).getItem());
				ores.add(new BlockMeta(block, ores2.get(0).getItemDamage()));
			}
		}
	}

	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {

		int dimid = world.provider.getDimension();
		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(dimid);
		ores.addAll(
				props.geodeOres.stream()
						.filter(OreDictionary::doesOreNameExist)
						.map(s->OreDictionary.getOres(s).get(0))
						.map(itemStack-> new BlockMeta(Block.getBlockFromItem(itemStack.getItem()),itemStack.getItemDamage()))
						.filter(block -> !ores.contains(block))
						.collect(Collectors.toSet())
		);

		if((rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk) && canGeodeGenerate(world, chunkX * 16, chunkZ * 16)) {

			int radius = rand.nextInt(ARConfiguration.getCurrentConfig().geodeVariation) + ARConfiguration.getCurrentConfig().geodeBaseSize - (ARConfiguration.getCurrentConfig().geodeVariation/2); //24; 24 -> 48

			//TODO: make hemisphere from surface and line the side with ore of some kind

			int depth = radius*radius;

			int xCoord = -chunkX + p_180701_4_;
			int zCoord =  -chunkZ + p_180701_5_;

			BlockPos pos = new BlockPos(chunkX*16, 0, chunkZ*16);

			int avgY = 64;

			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {
					int index;

					for(int y = 255; y >= 0; y--) {
						index = (x * 16 + z) * 256 + y;
						if(chunkPrimerIn.getBlockState(x, y, z) != Blocks.AIR.getDefaultState())
							break;
					}

					int count = ( depth - ( ((xCoord*16)+x)*((xCoord*16)+x) + ((zCoord*16)+z)*((zCoord*16)+z) ) )/(radius*2);

					//Check for IOB exceptions early, in case it generates near bedrock or something
					if(avgY-count < 1 || avgY+count > 255)
						continue;

					//Clears air for the ceiling
					for(int dist = -count; dist < Math.min(count,3); dist++) {
						index = (x * 16 + z) * 256 + avgY -dist;
						chunkPrimerIn.setBlockState(x, avgY - dist, z, Blocks.AIR.getDefaultState());
					}

					if(count >= 0) {

						if(count > 4) {
							int size = rand.nextInt(4) + 4;

							//Generates ore hanging from the ceiling
							if( x % 4 > 0 && z % 4 > 0) {
								for(int i = 1; i < size; i++)
									chunkPrimerIn.setBlockState(x, avgY + count - i, z, ores.get((x/4 + z/4) % ores.size()).getBlockState());
							}
							else {
								size -=2;
								for(int i = 1; i < size; i++) {
									chunkPrimerIn.setBlockState(x, avgY + count - i, z, Blocks.STONE.getDefaultState());
								}
							}

							//Generates ore in the floor
							if( (x+2) % 4 > 0 && (z+2) % 4 > 0) {
								for(int i = 1; i < size; i++)
									chunkPrimerIn.setBlockState(x, avgY - count + i, z, ores.get((x/4 + z/4) % ores.size()).getBlockState());
							}

						}

						chunkPrimerIn.setBlockState(x, avgY - count, z, AdvancedRocketryBlocks.blocksGeode.getDefaultState());
						chunkPrimerIn.setBlockState(x, avgY + count, z, AdvancedRocketryBlocks.blocksGeode.getDefaultState());
					}
				}
			}
		}
	}

	//Geodes should absolutely not be generating on top of liquid. That they could before was a huge oversight
	private static boolean canGeodeGenerate(World world, int x, int z) {
		return !BiomeDictionary.hasType(world.getBiome(new BlockPos(x, 0, z)), BiomeDictionary.Type.OCEAN) && !BiomeDictionary.hasType(world.getBiome(new BlockPos(x, 0, z)), BiomeDictionary.Type.RIVER) && !BiomeDictionary.hasType(world.getBiome(new BlockPos(x, 0, z)), BiomeDictionary.Type.BEACH);
	}
}
