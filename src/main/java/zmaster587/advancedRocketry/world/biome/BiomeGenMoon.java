package zmaster587.advancedRocketry.world.biome;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenMoon extends BiomeGenBase {

	public BiomeGenMoon(int biomeId, boolean register) {
		super(biomeId, register);
		
		//cold and dry
		
		enableRain = false;
		enableSnow = false;
		rootHeight=1f;
		heightVariation=0.01f;
		rainfall = 0f;
		temperature = 0.3f;
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = AdvancedRocketryBlocks.blockMoonTurf;
		this.biomeName="Moon";
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
