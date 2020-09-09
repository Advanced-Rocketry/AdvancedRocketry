package zmaster587.advancedRocketry.world;

import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.lighting.WorldLightManager;

public class DummyChunkProvider extends AbstractChunkProvider {

	World world;
	WorldLightManager lightmgr;
	
	public DummyChunkProvider(World world)
	{
		this.world = world;
		lightmgr = new WorldLightManager(this, true, true);
	}
	
	@Override
	public IBlockReader getWorld() {
		return world;
	}

	@Override
	public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
		return null;
	}

	@Override
	public String makeString() {
		return null;
	}

	@Override
	public WorldLightManager getLightManager() {
		return lightmgr;
	}

}
