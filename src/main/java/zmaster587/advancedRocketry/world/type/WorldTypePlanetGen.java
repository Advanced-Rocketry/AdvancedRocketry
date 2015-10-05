package zmaster587.advancedRocketry.world.type;

import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.ChunkProviderPlanet;
import zmaster587.advancedRocketry.world.GenLayerBiomePlanet;
import zmaster587.advancedRocketry.world.gen.GenLayerEdgeExtendedBiomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class WorldTypePlanetGen extends WorldType {

	public WorldTypePlanetGen(String name) {
		super("PlanetGen");
	}

	@Override
	public WorldChunkManager getChunkManager(World world)
	{
		return new ChunkManagerPlanet(world); //new WorldChunkManager(world);//
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderPlanet(world, world.getSeed(), false);
	}
	
	@Override
	public boolean getCanBeCreated() {
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
	public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer)
	{
		//return super.getBiomeLayer(worldSeed, parentLayer);
		GenLayer ret = new GenLayerBiomePlanet(200L, parentLayer, this);

		ret = GenLayerZoom.magnify(1000L, ret, 2);
		ret = new GenLayerEdgeExtendedBiomes(1000L, ret);
		return ret;
	}

}
