package zmaster587.advancedRocketry.world.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.util.StorageChunk;

import javax.annotation.Nullable;

public class WorldDummy extends World  {

	private final static ProviderDummy dummyProvider = new ProviderDummy();

	StorageChunk storage;
	public int displayListIndex = -1;
	private CapabilityDispatcher capabilities;
	
	public WorldDummy(Profiler p_i45368_5_, StorageChunk storage) {
		super(new DummySaveHandler(), new WorldInfo(new NBTTagCompound()), dummyProvider, p_i45368_5_, false);
		dummyProvider.setWorld(this);
		this.storage = storage;
		this.chunkProvider = new ChunkProviderDummy(this, storage);
		
	}
	
	@Override
	public World init() {
		this.mapStorage = new MapStorageDummy(this.saveHandler);
		this.capabilities = ForgeEventFactory.gatherCapabilities(this, null);
		
		return super.init();
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable EnumFacing facing) {
		return capabilities != null && capabilities.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing) {
		return capabilities == null ? null : capabilities.getCapability(capability, facing);
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return storage.getBlockState(pos);
	}
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return storage.getTileEntity(pos);
	}

    @SideOnly(Side.CLIENT)
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos)
    {
    	if(type == EnumSkyBlock.SKY)
    		return 15;
    	return super.getLightFromNeighborsFor(type, pos);
    }
	
	@Override
	public long getWorldTime() {
		return 0;
	}
	
	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean bool) {
		return storage.isSideSolid(pos, side, bool);
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
	public Biome getBiomeForCoordsBody(BlockPos pos) {
		return AdvancedRocketryBiomes.spaceBiome;
	}
	
	@Override
	public Biome getBiome(BlockPos pos) {
		return AdvancedRocketryBiomes.spaceBiome;
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		if(this.isRemote)
			return new ChunkProviderClient(this);
		else 
			return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float partialTicks) {
		return 0;
	}

	@Override
	public int getLight(BlockPos pos, boolean checkNeighbors) {
		return 15;
	}
	
	//No entities exist
	@Override
	public Entity getEntityByID(int p_73045_1_) {
		return null;
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		//Dummy out
		return false;
	}

}
