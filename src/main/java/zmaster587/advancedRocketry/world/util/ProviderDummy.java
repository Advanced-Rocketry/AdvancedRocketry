package zmaster587.advancedRocketry.world.util;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;

public class ProviderDummy extends WorldProvider {

	@Override
	public String getDimensionName() {
		return null;
	}
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return AdvancedRocketryBiomes.spaceBiome;
	}

}
