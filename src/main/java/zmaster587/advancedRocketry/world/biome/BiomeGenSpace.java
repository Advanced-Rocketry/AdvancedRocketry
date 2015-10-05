package zmaster587.advancedRocketry.world.biome;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenSpace extends BiomeGenBase {
	public BiomeGenSpace(int biomeId, boolean register) {
		super(biomeId, register);
		
		//cold and dry
		enableRain = false;
		enableSnow = false;
		rootHeight=-2f;
		heightVariation=0.00f;
		rainfall = 0f;
		temperature = 0.0f;
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = this.topBlock = Blocks.air;
		this.biomeName="Space";
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
