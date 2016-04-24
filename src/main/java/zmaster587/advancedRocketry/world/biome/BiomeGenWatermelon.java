package zmaster587.advancedRocketry.world.biome;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class BiomeGenWatermelon extends BiomeGenBase {
	
	public BiomeGenWatermelon(int biomeId, boolean register) {
		super(biomeId, register);
		
		//cold and dry
		
		enableRain = true;
		enableSnow = false;
		rootHeight=1f;
		heightVariation=0.1f;
		rainfall = 0f;
		temperature = 0.9f;
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = Blocks.melon_block;
		this.biomeName="Hot Dry Rock";
		
		this.spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 1, 10));
	}
	
	
	@Override
	public float getSpawningChance() {
		return 1f; //Nothing spawns
	}
}
