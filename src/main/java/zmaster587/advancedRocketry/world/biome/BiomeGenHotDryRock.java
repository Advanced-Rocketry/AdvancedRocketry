package zmaster587.advancedRocketry.world.biome;

import net.minecraft.world.biome.Biome;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class BiomeGenHotDryRock extends Biome {

	public BiomeGenHotDryRock(int biomeId, boolean register) {
		super(new BiomeProperties("Hot Dry Rock").setRainDisabled().setBaseHeight(1f).setHeightVariation(0.01f).setRainfall(0).setTemperature(0.9f));
		

		registerBiome(biomeId, "Hot Dry Rock", this);
		
		//hot and stinks
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = AdvancedRocketryBlocks.blockHotTurf.getDefaultState();
	}
	
	@Override
	public float getSpawningChance() {
		return 0f; //Nothing spawns
	}
	
	@Override
	public int getSkyColorByTemp(float p_76731_1_) {
		return 0x664444;
	}
}
