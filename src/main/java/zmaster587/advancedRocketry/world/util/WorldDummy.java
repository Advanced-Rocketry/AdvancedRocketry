package zmaster587.advancedRocketry.world.util;

import zmaster587.advancedRocketry.network.PacketStorageTileUpdate;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldDummy extends World {

	private final static ProviderDummy dummyProvider = new ProviderDummy();

	StorageChunk storage;
	
	
	public int glListID = -1;

	public WorldDummy(Profiler p_i45368_5_, StorageChunk storage) {
		super(new DummySaveHandler(), "dummy", new WorldSettings(0, WorldSettings.GameType.SURVIVAL, false, false, WorldType.FLAT), dummyProvider, p_i45368_5_);
		this.storage = storage;
		this.chunkProvider = new ChunkProviderDummy(this);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return storage.getBlock(x, y, z);
	}

	@Override
	public void markBlockForUpdate(int x, int y,
			int z) {
		super.markBlockForUpdate(x, y, z);
		if(storage.getEntity() != null && !storage.getEntity().worldObj.isRemote) {
			if(getTileEntity(x, y, z) != null && getTileEntity(x, y, z).getDescriptionPacket() instanceof S35PacketUpdateTileEntity )
				PacketHandler.sendToPlayersTrackingEntity(new PacketStorageTileUpdate(storage.getEntity(), storage, getTileEntity(x, y, z)), storage.getEntity());
		}
	}

	@Override
	public int getBlockLightOpacity(int x, int y, int z) {
		return getBlock(x, y, z).getLightOpacity();
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return storage.getBlockMetadata(x, y, z);
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		return storage.getTileEntity(x, y, z);
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side,
			boolean _default) {
		return storage.isSideSolid(x, y, z, side, _default);
	}

	@Override
	public void notifyBlockOfNeighborChange(int p_147460_1_, int p_147460_2_, int p_147460_3_, final Block p_147460_4_) {

		//Dummy out

	}


	@Override
	protected boolean chunkExists(int x, int z) {
		return false;
	}

	@Override
	public boolean blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_) {
		return false;
	}

	@Override
	public int getBlockLightValue_do(int p_72849_1_, int p_72849_2_,
			int p_72849_3_, boolean p_72849_4_) {

		if (p_72849_4_ && this.getBlock(p_72849_1_, p_72849_2_, p_72849_3_).getUseNeighborBrightness())
		{
			return super.getBlockLightValue_do(p_72849_1_, p_72849_2_, p_72849_3_,
					p_72849_4_);
		}
		else 
			return 15;//TODO: make chunks
	}

	@Override
	protected void finishSetup() {
		//Dont care about villages or providers or registration here
		this.chunkProvider = this.createChunkProvider();
	}

	@Override
	protected void func_147467_a(int p_147467_1_, int p_147467_2_,
			Chunk p_147467_3_) {
		// Dummy out
	}

	@Override
	protected void setActivePlayerChunksAndCheckLight() {
		//dummy out
	}

	@Override
	public boolean setBlock(int p_147465_1_, int p_147465_2_, int p_147465_3_, Block p_147465_4_, int p_147465_5_, int p_147465_6_) {
		return false;
		//Dummy out
	}

	@Override
	public boolean setBlockMetadataWithNotify(int p_72921_1_, int p_72921_2_, int p_72921_3_, int p_72921_4_, int p_72921_5_) {
		return false;
		//Dummy it out
	}

	@Override
	public void updateEntities() {
		//Dummy out
	}

	@Override
	public void tick() {
		//Dont tick
	}

	@Override
	public boolean tickUpdates(boolean p_72955_1_) {
		//Dont tick
		return false;
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		if(this.isRemote)
			return new ChunkProviderClient(this);
		else 
			return null;
	}

	@Override
	protected int func_152379_p() {
		return 0;
	}

	//No entities exist
	@Override
	public Entity getEntityByID(int p_73045_1_) {
		return null;
	}

}
