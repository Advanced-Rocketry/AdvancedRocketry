/* Temporarily stores tile/blocks to move a block of them
 * 
 * 
 */

package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.IStorageChunk;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteBay;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;

import com.mojang.serialization.Lifecycle;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class StorageChunk implements IWorld, IStorageChunk {

	private static final int CHUNK_SIZE = 16;
	private static final int CHUNK_HEIGHT = 256;
	int sizeX, sizeY, sizeZ;
	public Chunk chunk;


	private ArrayList<TileEntity> tileEntities;

	//To store inventories (All inventories)
	private ArrayList<TileEntity> inventoryTiles;
	private ArrayList<TileEntity> liquidTiles;

	public WorldDummy world;
	private Entity entity;
	public boolean finalized = false; // Make sure we are ready to render

	public StorageChunk() {
		sizeX = 0;
		sizeY = 0;
		sizeZ = 0;
		tileEntities = new ArrayList<>();
		inventoryTiles = new ArrayList<>();
		liquidTiles = new ArrayList<>();

		world = new WorldDummy(AdvancedRocketry.proxy.getProfiler(), this);
		SimpleRegistry<Biome> registry = new SimpleRegistry<>(Registry.BIOME_KEY, Lifecycle.stable());
		SimpleRegistry.register(registry, Biomes.OCEAN.getRegistryName(), AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getLocation()));
		this.chunk = new Chunk(world, new ChunkPos(0, 0), new BiomeContainer(registry, new ChunkPos(0, 0), new SingleBiomeProvider(AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getLocation()))));
		// Hacky, quick workaround, I need a break
		world.setChunk(chunk);
	}

	protected StorageChunk(int xSize, int ySize, int zSize) {

		sizeX = xSize;
		sizeY = ySize;
		sizeZ = zSize;

		tileEntities = new ArrayList<>();
		inventoryTiles = new ArrayList<>();
		liquidTiles = new ArrayList<>();

		world = new WorldDummy(AdvancedRocketry.proxy.getProfiler(), this);
		SimpleRegistry<Biome> registry = new SimpleRegistry<>(Registry.BIOME_KEY, Lifecycle.stable());
		SimpleRegistry.register(registry, Biomes.OCEAN.getRegistryName(), AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getLocation()));
		this.chunk = new Chunk(world, new ChunkPos(0, 0), new BiomeContainer(registry, new ChunkPos(0, 0), new SingleBiomeProvider(AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getLocation()))));
		// Hacky, quick workaround, I need a break
		world.setChunk(chunk);
	}

	public void setEntity(EntityRocketBase entity) {
		this.entity = entity;
	}

	public EntityRocketBase getEntity() {
		return (EntityRocketBase)entity;
	}

	@Override
	public int getSizeX() { return sizeX; }

	@Override
	public int getSizeY() { return sizeY; }

	@Override
	public int getSizeZ() { return sizeZ; }

	@Override
	public List<TileEntity> getTileEntityList() {
		return tileEntities;
	}

	/**
	 * @return list of fluid handing tiles on the rocket all also implement IFluidHandler
	 */
	public List<TileEntity> getFluidTiles() {
		return liquidTiles;
	}

	public List<TileEntity> getInventoryTiles() {
		return inventoryTiles;
	}

	public List<TileEntity> getGUITiles() {
		return new LinkedList<>(inventoryTiles);
	}

	@Nonnull
	@Override
	public BlockState getBlockState(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ || chunk == null)
			return Blocks.AIR.getDefaultState();
		return chunk.getBlockState(pos);
	}

	public void setBlockState(BlockPos pos, BlockState state) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		chunk.setBlockState(pos, state, false);
	}


	//TODO: optimize the F*** out of this
	public void writeToNBT(CompoundNBT nbt) {
		nbt.putInt("xSize", sizeX);
		nbt.putInt("ySize", sizeY);
		nbt.putInt("zSize", sizeZ);
		
		ChunkPos chunkpos = chunk.getPos();
		CompoundNBT compoundnbt1 = new CompoundNBT();

		ChunkSection[] achunksection = chunk.getSections();
		ListNBT listnbt = new ListNBT();
		WorldLightManager worldlightmanager = world.getChunkProvider().getLightManager();
		boolean flag = chunk.hasLight();

		for(int i = -1; i < 17; ++i) {
			int j = i;
			ChunkSection chunksection = Arrays.stream(achunksection).filter((p_222657_1_) -> p_222657_1_ != null && p_222657_1_.getYLocation() >> 4 == j).findFirst().orElse(Chunk.EMPTY_SECTION);
			NibbleArray nibblearray = worldlightmanager.getLightEngine(LightType.BLOCK).getData(SectionPos.from(chunkpos, j));
			NibbleArray nibblearray1 = worldlightmanager.getLightEngine(LightType.SKY).getData(SectionPos.from(chunkpos, j));
			if (chunksection != Chunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
				CompoundNBT compoundnbt2 = new CompoundNBT();
				compoundnbt2.putByte("Y", (byte)(j & 255));
				if (chunksection != Chunk.EMPTY_SECTION) {
					chunksection.getData().writeChunkPalette(compoundnbt2, "Palette", "BlockStates");
				}

				if (nibblearray != null && !nibblearray.isEmpty()) {
					compoundnbt2.putByteArray("BlockLight", nibblearray.getData());
				}

				if (nibblearray1 != null && !nibblearray1.isEmpty()) {
					compoundnbt2.putByteArray("SkyLight", nibblearray1.getData());
				}

				listnbt.add(compoundnbt2);
			}
		}

		compoundnbt1.put("Sections", listnbt);
		if (flag) {
			compoundnbt1.putBoolean("isLightOn", true);
		}

		BiomeContainer biomecontainer = chunk.getBiomes();
		if (biomecontainer != null) {
			compoundnbt1.putIntArray("Biomes", biomecontainer.getBiomeIds());
		}

		ListNBT listnbt1 = new ListNBT();

		for(TileEntity tile : tileEntities) {
			CompoundNBT compoundnbt4 = tile.write(new CompoundNBT());
			if (compoundnbt4 != null) {
				listnbt1.add(compoundnbt4);
			}
		}

		compoundnbt1.put("TileEntities", listnbt1);
		ListNBT listnbt2 = new ListNBT();
		chunk.setHasEntities(false);

		for(int k = 0; k < chunk.getEntityLists().length; ++k) {
			for(Entity entity : chunk.getEntityLists()[k]) {
				CompoundNBT compoundnbt3 = new CompoundNBT();
				try {
					if (entity.writeUnlessPassenger(compoundnbt3)) {
						chunk.setHasEntities(true);
						listnbt2.add(compoundnbt3);
					}
				} catch (Exception e) {
					LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), e);
				}
			}
		}
		try {
			final CompoundNBT capTag = chunk.writeCapsToNBT();
			if (capTag != null) compoundnbt1.put("ForgeCaps", capTag);
		} catch (Exception exception) {
			LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
		}

		compoundnbt1.put("Entities", listnbt2);


		CompoundNBT compoundnbt6 = new CompoundNBT();

		for(Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().getHeightMaps().contains(entry.getKey())) {
				compoundnbt6.put(entry.getKey().getId(), new LongArrayNBT(entry.getValue().getDataArray()));
			}
		}

		compoundnbt1.put("Heightmaps", compoundnbt6);

		nbt.merge(compoundnbt1);
	}

	public void rotateBy(Direction dir) {

		HashedBlockPosition newSizes = new HashedBlockPosition(getSizeX(), getSizeY(), getSizeZ());

		HashedBlockPosition newerSize = remapCoord(newSizes, dir);
		newSizes = remapCoord(newSizes, dir);

		BlockState[][][] blocks = new BlockState[newSizes.x][newSizes.y][newSizes.z];

		for(int y = 0; y < getSizeY(); y++) {
			for(int z = 0; z < getSizeZ(); z++) {
				for(int x = 0; x < getSizeX(); x++) {
					newSizes = getNewCoord(new HashedBlockPosition(x, y, z), dir);
					blocks[newSizes.x][newSizes.y][newSizes.z] = getBlockState(new BlockPos(x,y,z));
				}
			}
		}

		int oldX = sizeX;
		int oldY = sizeY;
		int oldZ = sizeZ;

		this.sizeX = newerSize.x;
		this.sizeY = newerSize.y;
		this.sizeZ = newerSize.z;

		for(int y = 0; y < getSizeY(); y++) {
			for(int z = 0; z < getSizeZ(); z++) {
				for(int x = 0; x < getSizeX(); x++) {
					chunk.setBlockState(new BlockPos(x,y,z), blocks[newSizes.x][newSizes.y][newSizes.z], false);
				}
			}
		}

		this.sizeX = oldX;
		this.sizeY = oldY;
		this.sizeZ = oldZ;

		for(TileEntity e : tileEntities) {
			newSizes = getNewCoord(new HashedBlockPosition(e.getPos()), dir);
			e.setPos(newSizes.getBlockPos());
		}

		this.sizeX = newerSize.x;
		this.sizeY = newerSize.y;
		this.sizeZ = newerSize.z;
	}

	private HashedBlockPosition remapCoord(HashedBlockPosition in, Direction dir) {

		HashedBlockPosition out = new HashedBlockPosition(0, 0, 0);

		switch(dir) {
			case DOWN:
			case UP:
				out.x = in.z;
				out.y = in.y;
				out.z = in.x;
				break;
			case NORTH:
			case SOUTH:
				out.x = in.y;
				out.y = (short)(in.x);
				out.z = in.z;
				break;
			case EAST:
			case WEST:
				out.x = in.x;
				out.y = (short)(in.z);
				out.z = in.y;
				break;
		}

		return out;
	}

	public HashedBlockPosition getNewCoord(HashedBlockPosition in, Direction dir) {

		HashedBlockPosition out = new HashedBlockPosition(0, 0, 0);

		switch(dir) {
		case DOWN:
			out.x = in.z;
			out.y = in.y;
			out.z = getSizeX()-in.x-1;
			break;
		case UP:
			out.x = getSizeZ()-in.z -1;
			out.y = in.y;
			out.z = in.x;
			break;
		case NORTH:
			out.x = in.y;
			out.y = (short)(getSizeX()-in.x-1);
			out.z = in.z;
			break;
		case SOUTH:
			out.x = getSizeY()-in.y-1;
			out.y = (short)in.x;
			out.z = in.z;
			break;
		case EAST:
			out.x = in.x;
			out.y = (short)(getSizeZ()-in.z-1);
			out.z = in.y;
			break;
		case WEST:
			out.x = in.x;
			out.y = (short)in.z;
			out.z = getSizeY()-in.y-1;
			break;
		}

		return out;
	}


	private static boolean isInventoryBlock(TileEntity tile) {
		return tile instanceof IInventory || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() && !(tile instanceof TileGuidanceComputer);
	}

	private static boolean isLiquidContainerBlock(TileEntity tile) {
		return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent();
	}

	public void readFromNBT(CompoundNBT nbt) {
		sizeX = nbt.getInt("xSize");
		sizeY = nbt.getInt("ySize");
		sizeZ = nbt.getInt("zSize");
		ChunkPos pos = new ChunkPos(0,0);
		inventoryTiles.clear();
		liquidTiles.clear();
		tileEntities.clear();


		boolean flag = nbt.getBoolean("isLightOn");
		ListNBT listnbt = nbt.getList("Sections", 10);
		ChunkSection[] achunksection = new ChunkSection[16];
		boolean flag1 = true; //worldIn.getDimensionType().hasSkyLight();
		AbstractChunkProvider abstractchunkprovider = this.world.getChunkProvider();
		WorldLightManager worldlightmanager = abstractchunkprovider.getLightManager();

		for(int j = 0; j < listnbt.size(); ++j) {
			CompoundNBT compoundnbt1 = listnbt.getCompound(j);
			int k = compoundnbt1.getByte("Y");
			if (compoundnbt1.contains("Palette", 9) && compoundnbt1.contains("BlockStates", 12)) {
				ChunkSection chunksection = new ChunkSection(k << 4);
				chunksection.getData().readChunkPalette(compoundnbt1.getList("Palette", 10), compoundnbt1.getLongArray("BlockStates"));
				chunksection.recalculateRefCounts();
				if (!chunksection.isEmpty()) {
					achunksection[k] = chunksection;
				}
			}

			if (flag) {
				if (compoundnbt1.contains("BlockLight", 7)) {
					worldlightmanager.setData(LightType.BLOCK, SectionPos.from(pos, k), new NibbleArray(compoundnbt1.getByteArray("BlockLight")), true);
				}

				if (flag1 && compoundnbt1.contains("SkyLight", 7)) {
					worldlightmanager.setData(LightType.SKY, SectionPos.from(pos, k), new NibbleArray(compoundnbt1.getByteArray("SkyLight")), true);
				}
			}
		}

		Chunk ichunk;
		ichunk = new Chunk(this.world, pos, new BiomeContainer(AdvancedRocketryBiomes.getBiomeRegistry(), pos, new SingleBiomeProvider(AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getLocation()))), UpgradeData.EMPTY, EmptyTickList.get(), EmptyTickList.get(), 0L, achunksection, null);
		if (nbt.contains("ForgeCaps")) ichunk.readCapsFromNBT(nbt.getCompound("ForgeCaps"));


		ichunk.setLight(flag);
		CompoundNBT compoundnbt3 = nbt.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumset = EnumSet.noneOf(Heightmap.Type.class);

		for(Heightmap.Type heightmap$type : ichunk.getStatus().getHeightMaps()) {
			String s = heightmap$type.getId();
			if (compoundnbt3.contains(s, 12)) {
				ichunk.setHeightmap(heightmap$type, compoundnbt3.getLongArray(s));
			} else {
				enumset.add(heightmap$type);
			}
		}

		Heightmap.updateChunkHeightmaps(ichunk, enumset);

		ListNBT listnbt3 = nbt.getList("PostProcessing", 9);

		for(int l1 = 0; l1 < listnbt3.size(); ++l1) {
			ListNBT listnbt1 = listnbt3.getList(l1);

			for(int l = 0; l < listnbt1.size(); ++l) {
				ichunk.addPackedPosition(listnbt1.getShort(l), l1);
			}
		}

		this.chunk = ichunk;


		ListNBT listnbt1 = nbt.getList("TileEntities", NBT.TAG_COMPOUND);

		for(int j = 0; j < listnbt1.size(); ++j) {
			CompoundNBT compoundnbt1 = listnbt1.getCompound(j);
			BlockPos blockpos = new BlockPos(compoundnbt1.getInt("x"), compoundnbt1.getInt("y"), compoundnbt1.getInt("z"));
			TileEntity tileentity = TileEntity.readTileEntity(chunk.getBlockState(blockpos), compoundnbt1);
			if (tileentity != null) {
				if(isInventoryBlock(tileentity)) {
					inventoryTiles.add(tileentity);
				}

				if(isLiquidContainerBlock(tileentity)) {
					liquidTiles.add(tileentity);
				}

				tileEntities.add(tileentity);
				tileentity.setWorldAndPos(world, blockpos);

				chunk.addTileEntity(tileentity);
			}
		}
	}

	public static StorageChunk copyWorldBB(World world, AxisAlignedBB bb) {
		int actualMinX = (int)bb.maxX,
				actualMinY = (int)bb.maxY,
				actualMinZ = (int)bb.maxZ,
				actualMaxX = (int)bb.minX,
				actualMaxY = (int)bb.minY,
				actualMaxZ = (int)bb.minZ;


		//Try to fit to smallest bounds
		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {
					BlockPos pos = new BlockPos(x,y,z);

					Block block = world.getBlockState(pos).getBlock();

					if(!block.isAir(world.getBlockState(pos) ,world, pos)) {
						if(x < actualMinX)
							actualMinX = x;
						if(y < actualMinY)
							actualMinY = y;
						if(z < actualMinZ)
							actualMinZ = z;
						if(x > actualMaxX)
							actualMaxX = x;
						if(y > actualMaxY)
							actualMaxY = y;
						if(z > actualMaxZ)
							actualMaxZ = z;
					}
				}
			}
		}

		StorageChunk ret = new StorageChunk((actualMaxX - actualMinX + 1), (actualMaxY - actualMinY + 1), (actualMaxZ - actualMinZ + 1));


		//Iterate though the bounds given storing blocks/meta/tiles
		for(int x = actualMinX; x <= actualMaxX; x++) {
			for(int z = actualMinZ; z <= actualMaxZ; z++) {
				for(int y = actualMinY; y<= actualMaxY; y++) {
					BlockPos pos = new BlockPos(x,y,z);
					BlockState state = world.getBlockState(pos);

					TileEntity entity = world.getTileEntity(pos);
					
					ret.setBlockState(new BlockPos(x - actualMinX, y - actualMinY, z - actualMinZ), state);
					
					if(entity != null) {
						CompoundNBT nbt = new CompoundNBT();
						entity.write(nbt);

						//Transform tileEntity coords
						nbt.putInt("x",nbt.getInt("x") - actualMinX);
						nbt.putInt("y",nbt.getInt("y") - actualMinY);
						nbt.putInt("z",nbt.getInt("z") - actualMinZ);




						TileEntity newTile = entity.getType().create();
						newTile.deserializeNBT(nbt);

						if(newTile != null) {
							newTile.setWorldAndPos(ret.world, pos.add(- actualMinX, - actualMinY, - actualMinZ));

							if(isInventoryBlock(newTile)) {
								ret.inventoryTiles.add(newTile);
							}

							if(isLiquidContainerBlock(newTile)) {
								ret.liquidTiles.add(newTile);
							}

							ret.tileEntities.add(newTile);
						}
						
						ret.chunk.addTileEntity(pos, newTile);
					}
				}
			}
		}

		return ret;
	}

	//pass the coords of the xmin, ymin, zmin as well as the world to move the rocket
	@Override
	public void pasteInWorld(World world, int xCoord, int yCoord ,int zCoord) {

		//Set all the blocks
		for(int x = 0; x < sizeX; x++) {
			for(int z = 0; z < sizeZ; z++) {
				for(int y = 0; y< sizeY; y++) {

					if(chunk != null && !chunk.getBlockState(new BlockPos(x, y, z)).isAir()) {
						world.setBlockState(new BlockPos(xCoord + x, yCoord + y, zCoord + z), chunk.getBlockState(new BlockPos(x, y, z)), 2);
					}
				}
			}
		}

		//Set tiles for each block
		for(TileEntity tile : tileEntities) {
			CompoundNBT nbt = new CompoundNBT();
			tile.write(nbt);
			int x = nbt.getInt("x");
			int y = nbt.getInt("y");
			int z = nbt.getInt("z");

			int tmpX = x + xCoord;
			int tmpY = y + yCoord;
			int tmpZ = z + zCoord;

			//Set blocks of tiles again to avoid weirdness caused by updates
			//world.setBlock(xCoord + x, yCoord + y, zCoord + z, blocks[x][y][z], metas[x][y][z], 2);


			nbt.putInt("x",tmpX);
			nbt.putInt("y",tmpY);
			nbt.putInt("z",tmpZ);

			TileEntity entity = world.getTileEntity(new BlockPos(tmpX, tmpY, tmpZ));

			if(entity != null)
				entity.deserializeNBT(nbt);
		}
	}


	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		for(TileEntity tileE : tileEntities) {
			if( tileE.getPos().compareTo(pos) == 0)
				return tileE;
		}
		return null;
	}


	@Override
	public boolean isAirBlock(BlockPos pos) {
		if(pos.getX() >= CHUNK_SIZE || pos.getY() >= CHUNK_HEIGHT || pos.getZ() >= CHUNK_SIZE)
			return true;
		return chunk.getBlockState(pos).isAir();
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public Biome getBiome(BlockPos pos) {
		return AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getRegistryName());
	}

	public static StorageChunk cutWorldBB(World worldObj, AxisAlignedBB bb) {
		StorageChunk chunk = StorageChunk.copyWorldBB(worldObj, bb);
		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {

					BlockPos pos = new BlockPos(x, y, z);
					//Workaround for dupe
					TileEntity tile = worldObj.getTileEntity(pos);
					if(tile instanceof IInventory) {
						IInventory inv = (IInventory) tile;
						for(int i = 0; i < inv.getSizeInventory(); i++) {
							inv.setInventorySlotContents(i, ItemStack.EMPTY);
						}
					}

					worldObj.setBlockState(pos, Blocks.AIR.getDefaultState(),2);
				}
			}
		}

		//Carpenter's block's dupe
		for(Entity entity : worldObj.getEntitiesWithinAABB(ItemEntity.class, bb.grow(5, 5, 5)) ) {
			entity.remove();
		}

		return chunk;
	}



	public List<TileSatelliteBay> getSatelliteHatches() {
		LinkedList<TileSatelliteBay> satelliteHatches = new LinkedList<>();
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileSatelliteBay) {
				satelliteHatches.add((TileSatelliteBay) tile);
			}
		}

		return satelliteHatches;
	}

	@Deprecated
	public List<SatelliteBase> getSatellites() {
		LinkedList<SatelliteBase> satellites = new LinkedList<>();
		LinkedList<TileSatelliteBay> satelliteHatches = new LinkedList<>();
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileSatelliteBay) {
				satelliteHatches.add((TileSatelliteBay) tile);
			}
		}


		for(TileSatelliteBay tile : satelliteHatches) {
			SatelliteBase satellite = tile.getSatellite();
			if(satellite != null)
				satellites.add(satellite);
		}
		return satellites;
	}

	public TileGuidanceComputer getGuidanceComputer() {
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileGuidanceComputer) {
				return (TileGuidanceComputer) tile;
			}
		}

		return null;
	}

	/**
	 * @return destination ID or Constants.INVALID_PLANET if none
	 */
	public ResourceLocation getDestinationDimId(ResourceLocation currentDimId, int x, int z) {
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer) tile).getDestinationDimId(currentDimId, new BlockPos(x, 0, z));
			}
		}

		return Constants.INVALID_PLANET;
	}

	public ResourceLocation getDestinationDimId(World world, int x, int z) {
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer) tile).getDestinationDimId(ZUtils.getDimensionIdentifier(world), new BlockPos(x, 0, z));
			}
		}

		return Constants.INVALID_PLANET;
	}

	public Vector3F<Float> getDestinationCoordinates(ResourceLocation destDimID, boolean commit) {
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer) tile).getLandingLocation(destDimID, commit);
			}
		}
		return null;
	}

	public String getDestinationName(ResourceLocation destDimID) {
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer) tile).getDestinationName(destDimID);
			}
		}
		return "";
	}

	public void setDestinationCoordinates(Vector3F<Float> vec, ResourceLocation dimid) {
		for (TileEntity tile : getTileEntityList()) {
			if (tile instanceof TileGuidanceComputer) {
				((TileGuidanceComputer) tile).setReturnPosition(vec, dimid);
			}
		}
	}

	public void writeToNetwork(PacketBuffer out) {


		CompoundNBT nbt = new CompoundNBT();
		this.writeToNBT(nbt);
		out.writeCompoundTag(nbt);
	}

	public void readFromNetwork(PacketBuffer in) {
		PacketBuffer buffer = new PacketBuffer(in);
		this.readFromNBT(in.readCompoundTag());
	}

	@Override
	@ParametersAreNonnullByDefault
	public int getStrongPower(BlockPos pos, Direction direction) {
		return 0;
	}

	@Override
	public DynamicRegistries func_241828_r() {
		return null;
	}

	@Nonnull
	@Override
	public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, @Nullable AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
		return new LinkedList<>();
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, Predicate<? super T> filter) {
		return new LinkedList<>();
	}

	@Nonnull
	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return new LinkedList<>();
	}

	@Override
	@ParametersAreNonnullByDefault
	public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
		return chunk;
	}

	@Override
	@ParametersAreNonnullByDefault
	public int getHeight(Type heightmapType, int x, int z) {
		return chunk.getHeightmap(heightmapType).getHeight(x, z);
	}

	@Override
	public int getSkylightSubtracted() {
		return 0;
	}

	@Override
	public BiomeManager getBiomeManager() {
		return null;
	}

	@Override
	public Biome getNoiseBiomeRaw(int x, int y, int z) {
		return AdvancedRocketryBiomes.getBiomeFromResourceLocation(Biomes.OCEAN.getRegistryName());
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public DimensionType getDimensionType() {
		return null;
	}

	@Override
	@ParametersAreNonnullByDefault
	public float func_230487_a_(Direction direction, boolean p_230487_2_) {
		return 0;
	}

	@Override
	public WorldLightManager getLightManager() {
		return chunk.getWorldLightManager();
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public FluidState getFluidState(BlockPos pos) {
		return chunk.getFluidState(pos);
	}

	@Nonnull
	@Override
	public WorldBorder getWorldBorder() {
		return new WorldBorder();
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean hasBlockState(BlockPos pos, Predicate<BlockState> predicate) {
		return false;
	}

	/*@Override
	public boolean func_241211_a_(BlockPos p_241211_1_, BlockState p_241211_2_, int p_241211_3_, int p_241211_4_) {
		return false;
	}*/

	@Override
	@ParametersAreNonnullByDefault
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int recursionLeft) {
		return false;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean removeBlock(BlockPos pos, boolean isMoving) {
		chunk.setBlockState(pos, Blocks.AIR.getDefaultState(), isMoving);
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
		return false;
	}

	/*@Override
	public boolean func_241212_a_(BlockPos p_241212_1_, boolean p_241212_2_, Entity p_241212_3_, int p_241212_4_) {
		return false;
	}*/

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
	public IWorldInfo getWorldInfo() {
		return world.getWorldInfo();
	}

	@Override
	@ParametersAreNonnullByDefault
	public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
		return null;
	}

	@Nonnull
	@Override
	public AbstractChunkProvider getChunkProvider() {
		return world.getChunkProvider();
	}

	@Override
	public Random getRandom() {
		return null;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {}

	@Override
	@ParametersAreNonnullByDefault
	public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {}

	@Override
	@ParametersAreNonnullByDefault
	public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {
	}
}
