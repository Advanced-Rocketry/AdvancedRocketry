package zmaster587.advancedRocketry.world.decoration;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class MapGenCrater extends WorldCarver<ProbabilityConfig> {

	int chancePerChunk;

	public MapGenCrater(Codec<ProbabilityConfig> codec, int chancePerChunk) {
		super(codec, chancePerChunk);
		this.chancePerChunk = chancePerChunk;
	}


	@Override
	public boolean func_225555_a_(IChunk chunkPrimerIn, Function func, Random rand, int p_225555_4_,
			int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, BitSet p_225555_9_,
			ProbabilityConfig p_225555_10_) {

 		int radius = rand.nextInt(56) + 8; //64; 8 -> 64
		IWorld world = chunkPrimerIn.getWorldForge();

		//TODO: make hemisphere from surface and line the side with ore of some kind

		int depth = radius*radius;

		int xCoord = -chunkX + p_180701_4_;
		int zCoord =  -chunkZ + p_180701_5_;


		for(int x = 15; x >= 0; x--) {
			for(int z = 15; z >= 0; z--) {
				for(int y = 254; y >= 0; y--) { //&& chunkPrimerIn.getBlockState(x, y, z).isOpaqueCube()) {
					
					if(chunkPrimerIn.getBlockState(new BlockPos(x, y, z)).isAir())
						continue;
					
					int count = ( depth - ( ((xCoord*16)+x)*((xCoord*16)+x) + ((zCoord*16)+z)*((zCoord*16)+z) ) )/(radius*2);

					for(int dist = 0; dist < count; dist++) {
						if(y-dist > 2)
							chunkPrimerIn.setBlockState(new BlockPos(x, y-dist, z), Blocks.AIR.getDefaultState(), false);
					}

					int ridgeSize = 12;

					if(count <= 0 && count > -2*ridgeSize) {


						for(int dist = 0; dist < ((ridgeSize*ridgeSize) - (count+ridgeSize)*(count+ridgeSize))/(ridgeSize*2); dist++) {
							if(y + dist < 255)
								chunkPrimerIn.setBlockState(new BlockPos(x, y + dist, z), chunkPrimerIn.getBiomes().getNoiseBiome(x, y + dist, z).func_242440_e().func_242502_e().getTop(), false);
						}
					}

					if(count > 1 && (y-count > 2))
						chunkPrimerIn.setBlockState(new BlockPos(x, y - count, z), chunkPrimerIn.getBiomes().getNoiseBiome(x, y -count, z).func_242440_e().func_242502_e().getTop(), false);
					break;
				}
			}
		}
		return true;
	}

	@Override
	public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
		return rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk;
	}


	@Override
	protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
		return false;
	}
}