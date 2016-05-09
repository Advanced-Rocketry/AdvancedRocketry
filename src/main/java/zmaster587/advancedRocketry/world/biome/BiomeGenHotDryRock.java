package zmaster587.advancedRocketry.world.biome;

import net.minecraft.world.biome.BiomeGenBase;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class BiomeGenHotDryRock extends BiomeGenBase {

	public BiomeGenHotDryRock(int biomeId, boolean register) {
		super(biomeId, register);
		
		//cold and dry
		
		enableRain = false;
		enableSnow = false;
		rootHeight=1f;
		heightVariation=0.01f;
		rainfall = 0f;
		temperature = 0.9f;
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = AdvancedRocketryBlocks.blockHotTurf;
		this.biomeName="Hot Dry Rock";
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
