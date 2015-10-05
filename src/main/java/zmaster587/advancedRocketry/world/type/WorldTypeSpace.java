package zmaster587.advancedRocketry.world.type;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.ChunkProviderSpace;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldTypeSpace extends WorldType {

	public WorldTypeSpace(String string) {
		super(string);
	}

	@Override
	public WorldChunkManager getChunkManager(World world)
	{
		return new WorldChunkManagerHell(AdvancedRocketryBiomes.spaceBiome, 0.5F);
	}

	
	@Override
	public boolean getCanBeCreated() {
		return false;
	}
}
