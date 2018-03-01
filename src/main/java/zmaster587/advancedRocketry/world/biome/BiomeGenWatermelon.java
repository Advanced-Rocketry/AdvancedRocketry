package zmaster587.advancedRocketry.world.biome;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeGenWatermelon extends Biome {
	
	public BiomeGenWatermelon(int biomeId, boolean register) {
		super(new BiomeProperties("Watermelon").setBaseHeight(1f).setHeightVariation(0.1f).setTemperature(0.9f).setRainDisabled());
		
		//cold and dry
		
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = Blocks.MELON_BLOCK.getDefaultState();
		
		this.spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEnderman.class, 10, 1, 10));
	}
	
	
	@Override
	public float getSpawningChance() {
		return 1f; //Nothing spawns
	}
}
