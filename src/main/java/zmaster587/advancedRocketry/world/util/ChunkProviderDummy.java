package zmaster587.advancedRocketry.world.util;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDummy implements IChunkProvider {

	World world;
	
	ChunkProviderDummy(World world){
		this.world = world;
	}
	
	
	@Override
	public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
		return new Chunk(world, p_73154_1_, p_73154_2_);
	}

	@Override
	public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
		return new Chunk(world, p_73158_1_, p_73158_2_);
	}

	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_,
			int p_73153_3_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unloadQueuedChunks() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String makeString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType p_73155_1_,
			int p_73155_2_, int p_73155_3_, int p_73155_4_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_,
			int p_147416_3_, int p_147416_4_, int p_147416_5_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLoadedChunkCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void recreateStructures(int p_82695_1_, int p_82695_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveExtraData() {
		// TODO Auto-generated method stub
		
	}

}
