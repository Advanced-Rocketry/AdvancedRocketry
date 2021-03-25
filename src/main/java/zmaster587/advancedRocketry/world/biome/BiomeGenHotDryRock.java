package zmaster587.advancedRocketry.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class BiomeGenHotDryRock extends Biome {

	public BiomeGenHotDryRock(BiomeProperties properties) {
		super(properties);

		//hot and stinks
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=0;
		this.decorator.mushroomsPerChunk=0;
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
