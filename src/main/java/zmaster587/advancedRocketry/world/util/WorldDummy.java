package zmaster587.advancedRocketry.world.util;

import java.util.List;
import java.util.OptionalLong;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.util.LazyOptional;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.advancedRocketry.world.DummyChunkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class WorldDummy extends World  {

	StorageChunk storage;
	private CapabilityDispatcher capabilities;

	DummyChunkProvider cnkprovider;
	Chunk chunk;

	final static class DummyDimensionType extends DimensionType
	{
		public DummyDimensionType() {
			super(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 256, ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_ID, 0.0F);
		}
	}

	public WorldDummy(IProfiler p_i45368_5_, StorageChunk storage) {
		super(null, RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("advancedrocketry:dummyworld")), new DummyDimensionType(), AdvancedRocketry.proxy::getProfiler, false, false, 0);
		this.storage = storage;
		cnkprovider = new DummyChunkProvider(this);
	}
	
	public void setChunk(Chunk chunk)
	{
		this.chunk = chunk;
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		return capabilities == null ? LazyOptional.empty() : capabilities.getCapability(capability, facing);
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public BlockState getBlockState(BlockPos pos) {
		return storage.getBlockState(pos);
	}

	@Override
	@ParametersAreNonnullByDefault
	public TileEntity getTileEntity(BlockPos pos) {
		return storage.getTileEntity(pos);
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus) {
		return chunk;
	}


	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public Biome getBiome(BlockPos pos) {
		return AdvancedRocketryBiomes.spaceBiome;
	}

	//No entities exist
	@Override
	public Entity getEntityByID(int p_73045_1_) {
		return null;
	}

	@Override
	public ITickList<Block> getPendingBlockTicks() {
		return null;
	}

	@Override
	public ITickList<Fluid> getPendingFluidTicks() {
		return null;
	}

	@Nonnull
	@Override
	public AbstractChunkProvider getChunkProvider() {
		return cnkprovider;
	}

	@Override
	public void playEvent(PlayerEntity player, int type, BlockPos pos, int data) {
	}

	@Override
	public DynamicRegistries func_241828_r() {
		return null;
	}

	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return null;
	}

	@Override
	public Biome getNoiseBiomeRaw(int x, int y, int z) {
		return null;
	}

	@Override
	@ParametersAreNonnullByDefault
	public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
		return 0;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
	}

	@Override
	@ParametersAreNonnullByDefault
	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {}

	@Override
	@ParametersAreNonnullByDefault
	public void playMovingSound(@Nullable PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch) {}

	@Override
	@ParametersAreNonnullByDefault
	public MapData getMapData(String mapName) {
		return null;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void registerMapData(MapData mapDataIn) {}

	@Override
	public int getNextMapId() {
		return 0;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}

	@Nonnull
	@Override
	public Scoreboard getScoreboard() {
		return new Scoreboard();
	}

	@Override
	public RecipeManager getRecipeManager() {
		return null;
	}

	@Override
	public ITagCollectionSupplier getTags() {
		return null;
	}

}
