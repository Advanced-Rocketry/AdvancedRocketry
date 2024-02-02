package zmaster587.advancedRocketry.world.util;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import zmaster587.advancedRocketry.util.StorageChunk;

public class ChunkProviderDummy implements IChunkProvider {

    World world;
    StorageChunk storage;

    ChunkProviderDummy(World world, StorageChunk storage) {
        this.world = world;
        this.storage = storage;
    }

    @Override
    public Chunk getLoadedChunk(int x, int z) {
        return new Chunk(world, x, z);
    }

    @Override
    public Chunk provideChunk(int x, int z) {
        if (x == 0 && z == 0)
            return storage.chunk;
        return new Chunk(world, x, z);
    }

    @Override
    public String makeString() {
        return null;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public boolean isChunkGeneratedAt(int x, int z) {
        return false;
    }

}
