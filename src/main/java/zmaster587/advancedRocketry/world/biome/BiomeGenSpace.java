package zmaster587.advancedRocketry.world.biome;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeGenSpace extends Biome {
	public BiomeGenSpace(int biomeId, boolean register) {
		super(new BiomeProperties("Space").setRainDisabled().setBaseHeight(-2f).setHeightVariation(0f).setTemperature(1f));
		

		registerBiome(biomeId, "Space", this);
		
		//cold and dry
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = Blocks.AIR.getDefaultState();
	}
	
	@Override
	public List getSpawnableList(EnumCreatureType p_76747_1_) {
		return new LinkedList<>();
	}
	
	@Override
	public float getSpawningChance() {
		return 0f; //Nothing spawns
	}
}
