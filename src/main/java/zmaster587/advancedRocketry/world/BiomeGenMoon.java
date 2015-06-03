package zmaster587.advancedRocketry.world;

import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
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
		this.fillerBlock = this.topBlock = AdvancedRocketry.blockMoonTurf;
		this.biomeName="Moon";
	}

	@Override
	public void decorate(World p_76728_1_, Random p_76728_2_, int p_76728_3_,
			int p_76728_4_) {
		super.decorate(p_76728_1_, p_76728_2_, p_76728_3_, p_76728_4_);
	}
	
	
	
	@Override
	public float getSpawningChance() {
		return 0f; //Nothing spawns
	}
	
	
	
	@Override
	public int getSkyColorByTemp(float p_76731_1_) {
		return 0x0;
	}
}
