package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.gen.WorldGenCharredTree;
import zmaster587.advancedRocketry.world.gen.WorldGenElectricMushroom;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeGenStormland extends Biome {

	WorldGenAbstractTree charTree = new WorldGenCharredTree(false, 6);
	
	public BiomeGenStormland(int biomeId, boolean register) {
		super(new BiomeProperties("Stormland").setBaseHeight(1f).setHeightVariation(0.1f).setRainfall(0.9f).setTemperature(0.9f));
		
		registerBiome(biomeId, "Stormland", this);
		
		spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityCreeper.class, 5, 1, 1));
		this.spawnableCreatureList.clear();
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=6;
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {
		// TODO Auto-generated method stub
		super.decorate(worldIn, rand, pos);
		
        int x = pos.getX() + rand.nextInt(16);
        int y = rand.nextInt(28) + 80;
        int z = pos.getZ() + rand.nextInt(16);
		(new WorldGenElectricMushroom(AdvancedRocketryBlocks.blockElectricMushroom)).generate(worldIn, rand, new BlockPos(x, y, z));
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
	public WorldGenAbstractTree genBigTreeChance(Random rand) {
		return charTree;
	}
	
	@Override
	public int getModdedBiomeGrassColor(int original) {
		return super.getModdedBiomeGrassColor(0x202020);
	}
}
