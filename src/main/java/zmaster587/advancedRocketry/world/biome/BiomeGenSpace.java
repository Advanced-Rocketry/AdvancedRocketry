package zmaster587.advancedRocketry.world.biome;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class BiomeGenSpace extends Biome {
	public BiomeGenSpace(int biomeId, boolean register) {
		super(new BiomeProperties("Space").setRainDisabled().setBaseHeight(-2f).setHeightVariation(0f).setTemperature(1f));
		
        this.setRegistryName(new ResourceLocation("advancedrocketry:Space"));
		
		//cold and dry
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=0;
		this.decorator.mushroomsPerChunk=0;
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
