package zmaster587.advancedRocketry.world.util;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDummy implements IChunkProvider {

	World world;
	
	ChunkProviderDummy(World world){
		this.world = world;
	}
	
	@Override
	public Chunk getLoadedChunk(int x, int z) {
		return new Chunk(world, x, z);
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		return new Chunk(world, x, z);
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public String makeString() {
		return null;
	}

}
