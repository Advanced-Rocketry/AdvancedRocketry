package zmaster587.advancedRocketry.world.type;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerZoom;
import zmaster587.advancedRocketry.world.ChunkProviderPlanet;
import zmaster587.advancedRocketry.world.GenLayerBiomePlanet;

public class WorldTypePlanetGen extends WorldType {

	public WorldTypePlanetGen(String name) {
		super("PlanetGen");
	}

	@Override
	public BiomeProvider getBiomeProvider(World world)
	{
		return null;//new ChunkManagerPlanet(world); //new WorldChunkManager(world);//
	}

	@Override
	public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderPlanet(world, world.getSeed(), false, generatorOptions);
	}
	
	
	
	@Override
	public boolean canBeCreated() {
		return false;
	}
	
	/**
	 * Creates the GenLayerBiome used for generating the world
	 *
	 * @param worldSeed The world seed
	 * @param parentLayer The parent layer to feed into any layer you return
	 * @return A GenLayer that will return ints representing the Biomes to be generated, see GenLayerBiome
	 */
	@Override
	public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer, ChunkGeneratorSettings chunkProviderSettings)
	{
		//return super.getBiomeLayer(worldSeed, parentLayer);
		GenLayer ret = new GenLayerBiomePlanet(200L, parentLayer, this);

		ret = GenLayerZoom.magnify(1000L, ret, 2);
		//REKT with random ocean
		//ret = new GenLayerEdge(1000L, ret, Mode.SPECIAL);
		return ret;
	}

}
