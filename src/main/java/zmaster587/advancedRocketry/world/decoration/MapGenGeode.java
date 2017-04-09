package zmaster587.advancedRocketry.world.decoration;

import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.libVulpes.block.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.oredict.OreDictionary;

public class MapGenGeode extends MapGenBase {
	int chancePerChunk;

	private static  List<BlockMeta> ores; // = {new BlockMeta(Blocks.IRON_ORE), new BlockMeta(Blocks.GOLD_ORE), new BlockMeta(Blocks.REDSTONE_ORE), new BlockMeta(Blocks.LAPIS_ORE)};

	public MapGenGeode(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;

		if(ores == null) {
			ores = new LinkedList<BlockMeta>();
			for(int i = 0; i < Configuration.standardGeodeOres.size(); i++) {
				String oreDictName = Configuration.standardGeodeOres.get(i);
				List<ItemStack> ores2 = OreDictionary.getOres(oreDictName);

				if(ores2 != null && !ores2.isEmpty()) {
					Block block = Block.getBlockFromItem(ores2.get(0).getItem());
					if(block != null)
						ores.add(new BlockMeta(block, ores2.get(0).getItemDamage()));
				}
			}
		}
	}

	@Override
	protected void recursiveGenerate(World world, int chunkX,
			int chunkZ, int p_180701_4_, int p_180701_5_,
			ChunkPrimer chunkPrimerIn) {

		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk) {

			int radius = rand.nextInt(Configuration.geodeVariation) + Configuration.geodeBaseSize - (Configuration.geodeVariation/2); //24; 24 -> 48

			//TODO: make hemisphere from surface and line the side with ore of some kind

			int depth = radius*radius;

			int xCoord = -chunkX + p_180701_4_;
			int zCoord =  -chunkZ + p_180701_5_;

			BlockPos pos = new BlockPos(chunkX*16, 0, chunkZ*16);

			int avgY = (int) ((world.getBiomeGenForCoords(pos).getBaseHeight() + 2) *32) - 3*radius/4;

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
}
