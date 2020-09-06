package zmaster587.advancedRocketry.world.decoration;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class MapGenVolcano extends WorldCarver<ProbabilityConfig> {

	int chancePerChunk;
	private static final Supplier<? extends Block> blockEnrichedLava = AdvancedRocketryBlocks.blockEnrichedLavaFluid;
	private static final Block blockCasing = AdvancedRocketryBlocks.blockBasalt;

	public MapGenVolcano(Codec<ProbabilityConfig> codec, int chancePerChunk) {
		super(codec, chancePerChunk);
		this.chancePerChunk = chancePerChunk;
	}


	@Override
	public boolean func_225555_a_(IChunk chunkPrimerIn, Function func3, Random rand, int p_225555_4_,
			int chunkX, int chunkZ, int rangeX, int rangeZ, BitSet p_225555_9_,
			ProbabilityConfig p_225555_10_) {

		chancePerChunk = 15;

		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk) {

			int size = 64;
			int sizeDeviation = rand.nextInt(8); // 0 -> 8
			int baseHeight = 40;
			int lavaNodeHeight = 25;

			int xCoord = -chunkX + rangeX;
			int zCoord =  -chunkZ + rangeZ;
			int crackle;

			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {

					//Do some roughness
					crackle = rand.nextInt(2);

					int xCoordNew = (xCoord*16)+x;
					int zCoordNew = (zCoord*16)+z;

					int x2 = xCoordNew*xCoordNew;
					int z2 = zCoordNew*zCoordNew;
					double radius = Math.sqrt(x2 + z2);
					double func = 1/(Math.pow(1.028, radius-(size-sizeDeviation)*3)) + 
							+ baseHeight
							- 8/(Math.pow(1.09, radius-((size-sizeDeviation)/2.6)))
							-Math.pow(1.7, radius - size*.9);
					
					for(int y = 254; y >= 1; y--) {
						boolean underSurface = func >= y+crackle;
						boolean innerLayer = func >= y+crackle && radius < 5 && y > lavaNodeHeight;

						if(innerLayer)
							chunkPrimerIn.setBlockState(new BlockPos(x, y, z), blockEnrichedLava.get().getDefaultState(), false);
						else if(underSurface)
							chunkPrimerIn.setBlockState(new BlockPos(x, y, z), blockCasing.getDefaultState(), false);
						
						
						double sphereradius = x2+z2+(y - lavaNodeHeight)*(y - lavaNodeHeight);
						if(sphereradius < 23*23)
							chunkPrimerIn.setBlockState(new BlockPos(x, y, z), blockEnrichedLava.get().getDefaultState(), false);
						else if(sphereradius < 25*25)
							chunkPrimerIn.setBlockState(new BlockPos(x, y, z), blockCasing.getDefaultState(), false);
						
						//For the top of the bulb, im lazy
						if(innerLayer)
							chunkPrimerIn.setBlockState(new BlockPos(x, y, z), blockEnrichedLava.get().getDefaultState(), false);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
		chancePerChunk = 15;
		return rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk;
	}


	@Override
	protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
		return false;
	}
}