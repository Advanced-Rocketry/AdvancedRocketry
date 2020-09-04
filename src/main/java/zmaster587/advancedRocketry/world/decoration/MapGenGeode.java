package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.libVulpes.block.BlockMeta;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

public class MapGenGeode extends WorldCarver<ProbabilityConfig> {
	int chancePerChunk;

	private static  List<BlockMeta> ores; // = {new BlockMeta(Blocks.IRON_ORE), new BlockMeta(Blocks.GOLD_ORE), new BlockMeta(Blocks.REDSTONE_ORE), new BlockMeta(Blocks.LAPIS_ORE)};

	public MapGenGeode(Codec<ProbabilityConfig> codec, int chancePerChunk) {
		super(codec, chancePerChunk);
		this.chancePerChunk = chancePerChunk;

		if(ores == null) {
			ores = new LinkedList<BlockMeta>();
			for(int i = 0; i < ARConfiguration.getCurrentConfig().standardGeodeOres.size(); i++) {
				ResourceLocation oreDictName = ARConfiguration.getCurrentConfig().standardGeodeOres.get(i);

				for(Block blk : BlockTags.getCollection().get(oreDictName).func_230236_b_())
					ores.add(new BlockMeta(blk));
			}
		}
	}

	@Override
	public boolean func_225555_a_(IChunk chunkPrimerIn, Function func, Random rand, int p_225555_4_,
			int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, BitSet p_225555_9_,
			ProbabilityConfig p_225555_10_) {


		int radius = rand.nextInt(ARConfiguration.getCurrentConfig().geodeVariation.get()) + ARConfiguration.getCurrentConfig().geodeBaseSize.get() - (ARConfiguration.getCurrentConfig().geodeVariation.get()/2); //24; 24 -> 48

		//TODO: make hemisphere from surface and line the side with ore of some kind

		int depth = radius*radius;

		int xCoord = -chunkX + p_180701_4_;
		int zCoord =  -chunkZ + p_180701_5_;

		BlockPos pos = new BlockPos(chunkX*16, 0, chunkZ*16);

		int avgY = (int) 64;

		for(int x = 15; x >= 0; x--) {
			for(int z = 15; z >= 0; z--) {
				int index;

				index =  (x * 16 + z) * 256 + chunkPrimerIn.getHeightmap(Type.WORLD_SURFACE_WG).getHeight(x,z);

				int count = ( depth - ( ((xCoord*16)+x)*((xCoord*16)+x) + ((zCoord*16)+z)*((zCoord*16)+z) ) )/(radius*2);

				//Check for IOB exceptions early, in case it generates near bedrock or something
				if(avgY-count < 1 || avgY+count > 255)
					continue;

				//Clears air for the ceiling
				for(int dist = -count; dist < Math.min(count,3); dist++) {
					index = (x * 16 + z) * 256 + avgY -dist;
					chunkPrimerIn.setBlockState(new BlockPos(x, avgY - dist, z), Blocks.AIR.getDefaultState(), false);
				}

				if(count >= 0) {

					if(count > 4) {
						int size = rand.nextInt(4) + 4;

						//Generates ore hanging from the ceiling
						if( x % 4 > 0 && z % 4 > 0) {
							for(int i = 1; i < size; i++)
								chunkPrimerIn.setBlockState(new BlockPos(x, avgY + count - i, z), ores.get((x/4 + z/4) % ores.size()).getBlockState(), false);
						}
						else {
							size -=2;
							for(int i = 1; i < size; i++) {
								chunkPrimerIn.setBlockState(new BlockPos(x, avgY + count - i, z), Blocks.STONE.getDefaultState(), false);
							}
						}

						//Generates ore in the floor
						if( (x+2) % 4 > 0 && (z+2) % 4 > 0) {
							for(int i = 1; i < size; i++)
								chunkPrimerIn.setBlockState(new BlockPos(x, avgY - count + i, z), ores.get((x/4 + z/4) % ores.size()).getBlockState(), false);
						}

					}

					chunkPrimerIn.setBlockState(new BlockPos(x, avgY - count, z), AdvancedRocketryBlocks.blocksGeode.getDefaultState(), false);
					chunkPrimerIn.setBlockState(new BlockPos(x, avgY + count, z), AdvancedRocketryBlocks.blocksGeode.getDefaultState(), false);
				}
			}
		}
		return true;
	}

	@Override
	public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
		return rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk;
	}


	@Override
	protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
		return false;
	}
}
