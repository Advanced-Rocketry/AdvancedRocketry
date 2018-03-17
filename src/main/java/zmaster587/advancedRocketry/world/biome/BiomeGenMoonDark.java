package zmaster587.advancedRocketry.world.biome;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import java.util.LinkedList;
import java.util.List;

public class BiomeGenMoonDark extends Biome {

	public BiomeGenMoonDark(int biomeId, boolean register) {
		super(new BiomeProperties("MoonDark").setRainDisabled().setBaseHeight(0.5f).setHeightVariation(0.01f).setRainfall(0).setTemperature(0.3f));

		this.setRegistryName(new ResourceLocation("advancedrocketry:MoonDark"));
//		registerBiome(biomeId, "Moon", this);
		
		//cold and dry
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.theBiomeDecorator.mushroomsPerChunk=0;
		this.fillerBlock = this.topBlock = AdvancedRocketryBlocks.blockMoonTurfDark.getDefaultState();
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
