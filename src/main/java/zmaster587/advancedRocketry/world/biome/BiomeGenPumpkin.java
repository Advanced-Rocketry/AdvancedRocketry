package zmaster587.advancedRocketry.world.biome;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BiomeGenPumpkin extends Biome {
	
	public BiomeGenPumpkin(int biomeId, boolean register) {
		super(new BiomeProperties("Pumpkin").setBaseHeight(1f).setHeightVariation(0.1f).setTemperature(0.9f).setRainDisabled());
		
		//cold and dry
		registerBiome(biomeId, "Pumpkin", this);
		
		
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=5;
		this.theBiomeDecorator.treesPerChunk=0;
		this.fillerBlock = Blocks.DIRT.getDefaultState();
		this.topBlock = Blocks.PUMPKIN.getDefaultState();
		
		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySkeleton.class, 10, 1, 10));
	}
	
	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		int color = 0x953929;
		return getModdedBiomeFoliageColor(color);
	}
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		int color = 0x953929;
		return getModdedBiomeFoliageColor(color);
	}
	
	@Override
	public float getSpawningChance() {
		return 0.7f; //Nothing spawns
	}
}
