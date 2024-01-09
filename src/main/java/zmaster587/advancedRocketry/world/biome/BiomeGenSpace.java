package zmaster587.advancedRocketry.world.biome;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

public class BiomeGenSpace extends Biome {
	public BiomeGenSpace(BiomeProperties properties) {
		super(properties);
		
		//cold and dry
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=0;
		this.decorator.mushroomsPerChunk=0;
		this.fillerBlock = this.topBlock = Blocks.AIR.getDefaultState();
	}
	
	@Override
	@NotNull
	public List<Biome.SpawnListEntry> getSpawnableList(EnumCreatureType p_76747_1_) {
		return new LinkedList<>();
	}
	
	@Override
	public float getSpawningChance() {
		return 0f; //Nothing spawns
	}
}
