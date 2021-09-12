package zmaster587.advancedRocketry.world.biome;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.gen.WorldGenCharredTree;
import zmaster587.advancedRocketry.world.gen.WorldGenElectricMushroom;

import java.util.Random;

public class BiomeGenStormland extends Biome {

	WorldGenAbstractTree charTree = new WorldGenCharredTree(false, 6);
	
	public BiomeGenStormland(BiomeProperties properties) {
		super(properties);
		
		spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityCreeper.class, 5, 1, 1));
		this.spawnableCreatureList.clear();
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=6;
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {
		super.decorate(worldIn, rand, pos);
		
        int x = pos.getX() + rand.nextInt(16);
        int y = rand.nextInt(28) + 80;
        int z = pos.getZ() + rand.nextInt(16);
		(new WorldGenElectricMushroom(AdvancedRocketryBlocks.blockElectricMushroom)).generate(worldIn, rand, new BlockPos(x, y, z));
	}
	
	
	
	@Override
	public float getSpawningChance() {
		return 0f; //Nothing spawns
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float p_76731_1_) {
		return 0x202020;
	}
	
	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
		return charTree;
	}
	
	@Override
	public int getModdedBiomeGrassColor(int original) {
		return super.getModdedBiomeGrassColor(0x202020);
	}
}
