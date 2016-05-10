package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.world.gen.WorldGenLargeCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenCrystal extends BiomeGenBase  {
	
	WorldGenerator crystalGenerator;
	
	public BiomeGenCrystal(int biomeId, boolean register) {
		super(biomeId, register);
		rootHeight=1f;
		heightVariation=0.1f;
		rainfall = 0.2f;
		temperature = 0.2f;
		topBlock = Blocks.snow;
		fillerBlock = Blocks.packed_ice;
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.theBiomeDecorator.generateLakes=false;
		this.theBiomeDecorator.flowersPerChunk=0;
		this.theBiomeDecorator.grassPerChunk=0;
		this.theBiomeDecorator.treesPerChunk=0;
		this.biomeName="CrystalChasms";
		
		crystalGenerator = new WorldGenLargeCrystal();
	}

	
	@Override
	public void decorate(World world, Random rand, int x,
			int z) {
		super.decorate(world, rand, x, z);
		
		if(rand.nextInt(100) == 0) {
			int xCoord = x;
			int zCoord = z;
			
			crystalGenerator.generate(world, rand, xCoord, world.getTopSolidOrLiquidBlock(xCoord, zCoord), zCoord);
		}
	}
}
