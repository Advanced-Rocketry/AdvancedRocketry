package zmaster587.advancedRocketry.world.biome;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeGenMoon extends Biome {

	public BiomeGenMoon(int biomeId, boolean register) {
		super(new BiomeProperties("Moon").setRainDisabled().setBaseHeight(1f).setHeightVariation(0.2f).setRainfall(0).setTemperature(0.3f));

		registerBiome(biomeId, "Moon", this);
		
		//cold and dry
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = AdvancedRocketryBlocks.blockMoonTurf.getDefaultState();
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
