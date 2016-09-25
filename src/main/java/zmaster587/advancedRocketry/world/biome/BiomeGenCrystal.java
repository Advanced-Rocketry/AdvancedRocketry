package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.world.gen.WorldGenLargeCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenCrystal extends Biome  {
	
	WorldGenerator crystalGenerator;
	
	public BiomeGenCrystal(int biomeId, boolean register) {
		super(new BiomeProperties("CrystalChasms").setHeightVariation(0.1f).setBaseHeight(1f).setRainfall(0.2f).setTemperature(0.1f));
		
		registerBiome(biomeId, "CrystalChasms", this);
		
		
		topBlock = Blocks.SNOW.getDefaultState();
		fillerBlock = Blocks.PACKED_ICE.getDefaultState();
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		
		crystalGenerator = new WorldGenLargeCrystal();
	}

	
	@Override
	public void decorate(World world, Random rand, BlockPos pos) {
		// TODO Auto-generated method stub
		super.decorate(world, rand, pos);
		
		if(rand.nextInt(100) == 0) {
			crystalGenerator.generate(world, rand, world.getTopSolidOrLiquidBlock(pos));
		}
	}
}
