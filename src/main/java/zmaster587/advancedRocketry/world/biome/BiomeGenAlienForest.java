package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;
import zmaster587.advancedRocketry.world.gen.WorldGenNoTree;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenAlienForest extends BiomeGenBase {

	public final static WorldGenAbstractTree alienTree = new WorldGenAlienTree(false);
	private final static WorldGenNoTree noTree = new WorldGenNoTree(false);;

	public BiomeGenAlienForest(int biomeId, boolean register) {
		super(biomeId, register);

		this.fillerBlock = Blocks.grass;
		this.waterColorMultiplier = 0x8888FF;
		this.biomeName="Alien Forest";
		this.theBiomeDecorator.grassPerChunk = 50;
		this.theBiomeDecorator.flowersPerChunk = 0;
	}

	@Override
	public void decorate(World p_76728_1_, Random random, int chunkX,
			int chunkZ) {

		//int xCoord = (chunkX << 4) + 8;
		//int zCoord = (chunkZ << 4) + 8;

		if(random.nextInt(20) == 0) {

			int yCoord =  p_76728_1_.getHeightValue(chunkX, chunkZ);

			alienTree.generate(p_76728_1_, random, chunkX, yCoord, chunkZ);
		}

		super.decorate(p_76728_1_, random, chunkX, chunkZ);
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
	{
		return noTree;//alienTree;
	}

	@Override
	public int getBiomeFoliageColor(int p_150571_1_, int p_150571_2_,
			int p_150571_3_) {
		int color = 0x55ffe1;
		return getModdedBiomeFoliageColor(color);
	}

	@Override
	public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_,
			int p_150558_3_) {
		waterColorMultiplier = 0xff1144;
		int color = 0x7777ff;
		return getModdedBiomeFoliageColor(color);
	}
}
