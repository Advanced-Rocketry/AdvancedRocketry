package zmaster587.advancedRocketry.world;

import java.util.Arrays;
import java.util.List;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderSpace implements IChunkProvider{

	World worldObj;
	
	public ChunkProviderSpace(World p_i2006_1_, long p_i2006_2_)
	{
		this.worldObj = p_i2006_1_;
	}
	
	@Override
	public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
		return false;
	}

	@Override
	public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
		Block[] ablock = new Block[65536];
		byte[] abyte = new byte[65536];

		//ChunkExtendedBiome
		Chunk chunk = new Chunk(this.worldObj, ablock, abyte, p_73154_1_, p_73154_2_);
		//TODO: convert back to int
		byte[] abyte1 = chunk.getBiomeArray();

		Arrays.fill(ablock,Blocks.air);
		Arrays.fill(abyte1, (byte)AdvancedRocketryBiomes.spaceBiome.biomeID);

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
		return this.provideChunk(p_73158_1_, p_73158_2_);
	}

	@Override
	public void populate(IChunkProvider provider, int x,
			int z) {
	}

	@Override
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
		return true;
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String makeString() {
		return "RandomLevelSource";
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType p_73155_1_,
			int p_73155_2_, int p_73155_3_, int p_73155_4_) {
		return null;
	}

	@Override
	public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_,
			int p_147416_3_, int p_147416_4_, int p_147416_5_) {
		return null;
	}

	@Override
	public int getLoadedChunkCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void recreateStructures(int p_82695_1_, int p_82695_2_) {
		
	}

	@Override
	public void saveExtraData() {
		
	}

}
