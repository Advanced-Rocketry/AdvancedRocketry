package zmaster587.advancedRocketry.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;

import java.util.List;

public class GenLayerBiomePlanet implements IC0Transformer {

	private List<Biome> biomes;
	private static List<Biome> biomeEntries;

	int biomeLimiter = -1;

	public GenLayerBiomePlanet(List<Biome> biomes)
	{
		biomeEntries = biomes;

	}

	@Override
	public int apply(INoiseRandom context, int value) {
		if(biomeEntries.isEmpty())
			return AdvancedRocketryBiomes.getBiomeId(Biomes.OCEAN.getRegistryName());
		return AdvancedRocketryBiomes.getBiomeId(biomeEntries.get(context.random(biomeEntries.size())));
	}
}
