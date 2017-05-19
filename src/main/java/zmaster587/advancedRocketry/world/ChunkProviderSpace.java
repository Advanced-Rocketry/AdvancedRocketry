package zmaster587.advancedRocketry.world;

import java.util.Arrays;
import java.util.List;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

public class ChunkProviderSpace implements IChunkGenerator {

	World worldObj;
	
	public ChunkProviderSpace(World p_i2006_1_, long p_i2006_2_)
	{
		this.worldObj = p_i2006_1_;
	}

	@Override
	public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
		Block[] ablock = new Block[65536];
		byte[] abyte = new byte[65536];
		ChunkPrimer chunkprimer = new ChunkPrimer();
		
		//ChunkExtendedBiome
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, p_73154_1_, p_73154_2_);//new Chunk(this.worldObj, ablock, abyte, p_73154_1_, p_73154_2_);
		//TODO: convert back to int
		byte[] abyte1 = chunk.getBiomeArray();

		Arrays.fill(abyte1, (byte)Biome.getIdForBiome(AdvancedRocketryBiomes.spaceBiome));

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z) {
		
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(
			EnumCreatureType creatureType, BlockPos pos) {
		return null;
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName,
			BlockPos position, boolean bool) {
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		
	}

}
