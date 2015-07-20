package zmaster587.advancedRocketry.world;

import java.util.ArrayList;
import java.util.HashMap;

import scala.actors.threadpool.Arrays;
import zmaster587.advancedRocketry.world.gen.GenLayerEdgeExtendedBiomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraftforge.common.BiomeDictionary;

public class WorldTypePlanetGen extends WorldType {

	public WorldTypePlanetGen(String name) {
		super("PlanetGen");
	}

	@Override
	public WorldChunkManager getChunkManager(World world)
	{
		return new ChunkManagerPlanet(world);
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
		ArrayList<BiomeGenBase> list = new ArrayList<BiomeGenBase>();

		list.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.DRY)));
		list.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.SNOWY)));

		//Neither are acceptable on planets
		list.remove(BiomeGenBase.hell);
		list.remove(BiomeGenBase.sky);

		GenLayer ret = new GenLayerBiomePlanet(200L, parentLayer, this);
		//GenLayer ret  = new GenLayerBiomePlanet(200L, parentLayer, this);

		ret = GenLayerZoom.magnify(1000L, ret, 2);
		ret = new GenLayerEdgeExtendedBiomes(1000L, ret);
		return ret;
	}

}
