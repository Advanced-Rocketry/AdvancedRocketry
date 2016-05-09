package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.gen.WorldGenCharredTree;
import zmaster587.advancedRocketry.world.gen.WorldGenFlowerLike;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenStormland extends BiomeGenBase {

	WorldGenAbstractTree charTree = new WorldGenCharredTree(false, 6);
	
	public BiomeGenStormland(int biomeId, boolean register) {
		super(biomeId, register);
		rootHeight=1f;
		heightVariation=0.1f;
		rainfall = 0.9f;
		temperature = 0.9f;
		spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 5, 1, 1));
		this.spawnableCreatureList.clear();
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=6;
		this.biomeName="Stormland";
	}

	@Override
	public void decorate(World world, Random rand, int chunkX,
			int chunkZ) {
		super.decorate(world, rand, chunkX, chunkZ);
		
        int x = chunkX + rand.nextInt(16);
        int y = rand.nextInt(28) + 80;
        int z = chunkZ + rand.nextInt(16);
		(new WorldGenFlowerLike(AdvancedRocketryBlocks.blockElectricMushroom)).generate(world, rand, x, y, z);
	}
	
	
	@Override
	public float getSpawningChance() {
		return 1.0f;
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float p_76731_1_) {
		return 0x202020;
	}
	
	@Override
	public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
	{
		return charTree;
	}
	
	
	@Override
	public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_,
			int p_150558_3_) {
		return 0x202020;
	}
}
