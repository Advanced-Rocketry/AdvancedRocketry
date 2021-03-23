package zmaster587.advancedRocketry.world.biome;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.MapGenBase;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.decoration.MapGenVolcano;

public class BiomeGenBarrenVolcanic extends Biome {
	
	public static MapGenBase volcano;
	
	public BiomeGenBarrenVolcanic() {
		
		super(new BiomeProperties("VolcanicBarren").setRainDisabled().setBaseHeight(0f).setHeightVariation(0.9f).setRainfall(0).setTemperature(1.0f));
		
        this.setRegistryName(new ResourceLocation("advancedrocketry:VolcanicBarren"));
		
		spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityCreeper.class, 5, 1, 1));
		this.spawnableCreatureList.clear();
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=0;
		this.decorator.mushroomsPerChunk=0;
		this.fillerBlock = this.topBlock = AdvancedRocketryBlocks.blockBasalt.getDefaultState();
		volcano = new MapGenVolcano(800);
	}
	
	@Override
	public int getSkyColorByTemp(float p_76731_1_) {
		return 0x332428;
	}
	
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		return 0x132113;
	}

	
}
