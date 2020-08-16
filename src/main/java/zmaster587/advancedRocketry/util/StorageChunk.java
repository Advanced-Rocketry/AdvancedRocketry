/* Temporarily stores tile/blocks to move a block of them
 * 
 * 
 */

package zmaster587.advancedRocketry.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.IStorageChunk;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StorageChunk implements IWorld, IStorageChunk {

	BlockState blocks[][][];
	int sizeX, sizeY, sizeZ;
	public Chunk chunk;


	ArrayList<TileEntity> tileEntities;

	//To store inventories (All inventories)
	ArrayList<TileEntity> inventoryTiles;
	ArrayList<TileEntity> liquidTiles;

	public WorldDummy world;
	private Entity entity;
	public boolean finalized = false; // Mkae sure we are ready to render

	public StorageChunk() {
		sizeX = 0;
		sizeY = 0;
		sizeZ = 0;
		tileEntities = new ArrayList<TileEntity>();
		inventoryTiles = new ArrayList<TileEntity>();
		liquidTiles = new ArrayList<TileEntity>();

		world = new WorldDummy(AdvancedRocketry.proxy.getProfiler(), this);
		world.init();
		this.chunk = new Chunk(world, new ChunkPos(0, 0), new BiomeContainer(p_i241971_1_, p_i241971_2_));
	}

	protected StorageChunk(int xSize, int ySize, int zSize) {
		blocks = new Block[xSize][ySize][zSize];
		metas = new int[xSize][ySize][zSize];

		sizeX = xSize;
		sizeY = ySize;
		sizeZ = zSize;

		tileEntities = new ArrayList<TileEntity>();
		inventoryTiles = new ArrayList<TileEntity>();
		liquidTiles = new ArrayList<TileEntity>();

		world = new WorldDummy(AdvancedRocketry.proxy.getProfiler(), this);
		world.init();
		this.chunk = new Chunk(world, 0, 0);
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

	public List<TileEntity> getGUItiles() {
		List<TileEntity> list = new LinkedList<TileEntity>(inventoryTiles);

		/*TileEntity guidanceComputer = getGuidanceComputer();
		if(guidanceComputer != null)
			list.add(getGuidanceComputer());*/
		return list;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ || blocks[x][y][z] == null)
			return Blocks.AIR.getDefaultState();
		return blocks[x][y][z];
	}

	public void setBlockState(BlockPos pos, BlockState state) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		blocks[x][y][z] = state;
	}


	//TODO: optimize the F*** out of this
	public void writeToNBT(CompoundNBT nbt) {
		nbt.putInt("xSize", sizeX);
		nbt.putInt("ySize", sizeY);
		nbt.putInt("zSize", sizeZ);


		Iterator<TileEntity> tileEntityIterator = tileEntities.iterator();
		ListNBT tileList = new ListNBT();
		while(tileEntityIterator.hasNext()) {
			TileEntity tile = tileEntityIterator.next();
			try {
				CompoundNBT tileNbt = new CompoundNBT();
				tile.write(tileNbt);
				tileList.add(tileNbt);
			} catch(RuntimeException e) {
				AdvancedRocketry.logger.warn("A tile entity has thrown an error: " + tile.getClass().getCanonicalName());
				blocks[tile.getPos().getX()][tile.getPos().getY()][tile.getPos().getZ()] = Blocks.AIR.getDefaultState();
				tileEntityIterator.remove();
			}
		}

		int[] blockId = new int[sizeX*sizeY*sizeZ];
		int[] metasId = new int[sizeX*sizeY*sizeZ];
		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {
					blockId[z + (sizeZ*y) + (sizeZ*sizeY*x)] = Block.getStateId(blocks[x][y][z]);
				}
			}
		}

		IntArrayNBT idList = new IntArrayNBT(blockId);
		IntArrayNBT metaList = new IntArrayNBT(metasId);

		nbt.put("idList", idList);
		nbt.put("metaList", metaList);
		nbt.put("tiles", tileList);


		/*for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {

					idList.add(new NBTTagInt(Block.getIdFromBlock(blocks[x][y][z])));
					metaList.add(new NBTTagInt(metas[x][y][z]));

					//CompoundNBT tag = new CompoundNBT();
					tag.putInt("block", Block.getIdFromBlock(blocks[x][y][z]));
					tag.putShort("meta", metas[x][y][z]);

					CompoundNBT tileNbtData = null;

					for(TileEntity tile : tileEntities) {
						CompoundNBT tileNbt = new CompoundNBT();

						tile.writeToNBT(tileNbt);

						if(tileNbt.getInt("x") == x && tileNbt.getInt("y") == y && tileNbt.getInt("z") == z){
							tileNbtData = tileNbt;
							break;
						}
					}

					if(tileNbtData != null)
						tag.put("tile", tileNbtData);

					nbt.put(String.format("%d.%d.%d", x,y,z), tag);
				}

			}
		}*/
	}

	public void rotateBy(Direction dir) {

		HashedBlockPosition newSizes = new HashedBlockPosition(getSizeX(), getSizeY(), getSizeZ());

		HashedBlockPosition newerSize = remapCoord(newSizes, dir);
		newSizes = remapCoord(newSizes, dir);

		BlockState blocks[][][] = new Block[newSizes.x][newSizes.y][newSizes.z];

		for(int y = 0; y < getSizeY(); y++) {
			for(int z = 0; z < getSizeZ(); z++) {
				for(int x = 0; x < getSizeX(); x++) {
					newSizes = getNewCoord(new HashedBlockPosition(x, y, z), dir);
					blocks[newSizes.x][newSizes.y][newSizes.z] = this.blocks[x][y][z];
				}
			}
		}
		this.blocks = blocks;


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
			out.x = in.z;
			out.y = in.y;
			out.z = in.x;
			break;
		case UP:
			out.x = in.z;
			out.y = in.y;
			out.z = in.x;
			break;
		case NORTH:
			out.x = in.y;
			out.y = (short)(in.x);
			out.z = in.z;
			break;
		case SOUTH:
			out.x = in.y;
			out.y = (short)in.x;
			out.z = in.z;
			break;
		case EAST:
			out.x = in.x;
			out.y = (short)(in.z);
			out.z = in.y;
			break;
		case WEST:
			out.x = in.x;
			out.y = (short)in.z;
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
		return tile instanceof IInventory || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP) && !(tile instanceof TileGuidanceComputer);
	}

	private static boolean isLiquidContainerBlock(TileEntity tile) {
		return tile instanceof IFluidHandler;
	}

	public void readFromNBT(CompoundNBT nbt) {
		
		ChunkSerializer.read(this.world, null, new PointOfInterestManager(null, null, finalized), new ChunkPos(0,0), nbt);
		
		sizeX = nbt.getInt("xSize");
		sizeY = nbt.getInt("ySize");
		sizeZ = nbt.getInt("zSize");

		blocks = new BlockState[sizeX][sizeY][sizeZ];

		tileEntities.clear();
		inventoryTiles.clear();
		liquidTiles.clear();
		chunk = new Chunk(world, 0, 0);

		int[] blockId = nbt.getIntArray("idList");
		int[] metasId = nbt.getIntArray("metaList");

		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {
					blocks[x][y][z] = Block.getStateById(blockId[z + (sizeZ*y) + (sizeZ*sizeY*x)]);

					chunk.setBlockState(new BlockPos(x, y, z), this.blocks[x][y][z], false);
				}
			}
		}

		ListNBT tileList = nbt.getList("tiles", NBT.TAG_COMPOUND);

		for(int i = 0; i < tileList.size(); i++) {

			try {
				TileEntity tile = TileEntity.func_235657_b_(p_235657_0_, p_235657_1_);
				tile.setWorld(world);

				if(isInventoryBlock(tile)) {
					inventoryTiles.add(tile);
				}

				if(isLiquidContainerBlock(tile)) {
					liquidTiles.add(tile);
				}

				tileEntities.add(tile);
				tile.setWorld(world);

				chunk.addTileEntity(tile);

			} catch (Exception e) {
				AdvancedRocketry.logger.warn("Rocket missing Tile (was a mod removed?)");
			}

		}

		/*for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {



					CompoundNBT tag = (CompoundNBT)nbt.getTag(String.format("%d.%d.%d", x,y,z));

					if(!tag.contains("block"))
						continue;
					int blockId = tag.getInt("block"); 
					blocks[x][y][z] = Block.getBlockById(blockId);
					metas[x][y][z] = tag.getShort("meta");


					if(blockId != 0 && blocks[x][y][z] == Blocks.air) {
						AdvancedRocketry.logger.warn("Removed pre-existing block with id " + blockId + " from a rocket (Was a mod removed?)");
					}
					else if(tag.contains("tile")) {

						if(blocks[x][y][z].hasTileEntity(metas[x][y][z])) {
							TileEntity tile = TileEntity.createAndLoadEntity(tag.getCompound("tile"));
							tile.setWorldObj(world);

							tileEntities.add(tile);

							//Machines would throw a wrench in the works
							if(isUsableBlock(tile)) {
								inventories.add((IInventory)tile);
								usableTiles.add(tile);
							}
						}
					}
				}
			}
		}*/
		this.chunk.generateSkylightMap();
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


		bb = new AxisAlignedBB(actualMinX, actualMinY, actualMinZ, actualMaxX, actualMaxY, actualMaxZ);

		StorageChunk ret = new StorageChunk((actualMaxX - actualMinX + 1), (actualMaxY - actualMinY + 1), (actualMaxZ - actualMinZ + 1));


		//Iterate though the bounds given storing blocks/meta/tiles
		for(int x = actualMinX; x <= actualMaxX; x++) {
			for(int z = actualMinZ; z <= actualMaxZ; z++) {
				for(int y = actualMinY; y<= actualMaxY; y++) {
					BlockPos pos = new BlockPos(x,y,z);
					BlockState state = world.getBlockState(pos);
					ret.blocks[x - actualMinX][y - actualMinY][z - actualMinZ] = state.getBlock();
					ret.metas[x - actualMinX][y - actualMinY][z - actualMinZ] = (short)state.getBlock().getMetaFromState(state);

					TileEntity entity = world.getTileEntity(pos);
					if(entity != null) {
						CompoundNBT nbt = new CompoundNBT();
						entity.write(nbt);

						//Transform tileEntity coords
						nbt.putInt("x",nbt.getInt("x") - actualMinX);
						nbt.putInt("y",nbt.getInt("y") - actualMinY);
						nbt.putInt("z",nbt.getInt("z") - actualMinZ);

						//XXX: Hack to make chisels & bits renderable
						if (nbt.getString("id").equals("minecraft:mod.chiselsandbits.tileentitychiseled"))
							nbt.putString("id", "minecraft:mod.chiselsandbits.tileentitychiseled.tesr");

						TileEntity newTile = ZUtils.createTile(nbt);
						if(newTile != null) {
							newTile.setWorld(ret.world);

							if(isInventoryBlock(newTile)) {
								ret.inventoryTiles.add(newTile);
							}

							if(isLiquidContainerBlock(newTile)) {
								ret.liquidTiles.add(newTile);
							}

							ret.tileEntities.add(newTile);
						}
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

					if(blocks[x][y][z] != Blocks.AIR) {
						world.setBlockState(new BlockPos(xCoord + x, yCoord + y, zCoord + z), blocks[x][y][z].getStateById(metas[x][y][z]), 2);
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
				entity.readFromNBT(nbt);
		}
	}


	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		for(TileEntity tileE : tileEntities) {
			if( tileE.getPos().compareTo(pos) == 0)
				return tileE;
		}
		return null;
	}


	@Override
	public boolean isAirBlock(BlockPos pos) {
		if(pos.getX() >= blocks.length || pos.getY() >= blocks[0].length || pos.getZ() >= blocks[0][0].length)
			return true;
		return blocks[pos.getX()][pos.getY()][pos.getZ()] == Blocks.AIR;
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		//Don't care, gen ocean
		return Biome.getBiome(0);
	}

	@Override
	public boolean isSideSolid(BlockPos pos, Direction side, boolean _default) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ  || x + side.getXOffset() < 0 
				|| x + side.getXOffset() >= sizeX || y + side.getYOffset() < 0 || y + side.getYOffset() >= sizeY 
				|| z + side.getZOffset() < 0 || z + side.getZOffset() >= sizeZ)
			return false;

		return blocks[x + side.getXOffset()][y + side.getYOffset()][z + side.getZOffset()].isSideSolid(blocks[x + side.getXOffset()][y + side.getYOffset()][z + side.getZOffset()].getStateById(metas[x + side.getXOffset()][y + side.getYOffset()][z + side.getZOffset()]), this, pos.offset(side), side.getOpposite());

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
		for(Object entity : worldObj.getEntitiesWithinAABB(ItemEntity.class, bb.grow(5, 5, 5)) ) {
			((Entity)entity).remove();
		}

		return chunk;
	}



	public List<TileSatelliteHatch> getSatelliteHatches() {
		LinkedList<TileSatelliteHatch> satelliteHatches = new LinkedList<TileSatelliteHatch>();
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();

			if(tile instanceof TileSatelliteHatch) {
				satelliteHatches.add((TileSatelliteHatch) tile);
			}
		}

		return satelliteHatches;
	}

	@Deprecated
	public List<SatelliteBase> getSatellites() {
		LinkedList<SatelliteBase> satellites = new LinkedList<SatelliteBase>();
		LinkedList<TileSatelliteHatch> satelliteHatches = new LinkedList<TileSatelliteHatch>();
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();

			if(tile instanceof TileSatelliteHatch) {
				satelliteHatches.add((TileSatelliteHatch) tile);
			}
		}


		for(TileSatelliteHatch tile : satelliteHatches) {
			SatelliteBase satellite = tile.getSatellite();
			if(satellite != null)
				satellites.add(satellite);
		}
		return satellites;
	}

	@Deprecated
	public TileGuidanceComputer getGuidanceComputer() {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();

			if(tile instanceof TileGuidanceComputer) {
				return (TileGuidanceComputer)tile;
			}
		}

		return null;
	}

	public boolean hasWarpCore() {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();

			if(tile instanceof TileWarpCore) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return destination ID or Constants.INVALID_PLANET if none
	 */
	public ResourceLocation getDestinationDimId(ResourceLocation currentDimId, int x, int z) {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();

			if(tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer)tile).getDestinationDimId(currentDimId, new BlockPos(x,0,z));
			}
		}

		return Constants.INVALID_PLANET;
	}
	
	public ResourceLocation getDestinationDimId(World world, int x, int z) {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();

			if(tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer)tile).getDestinationDimId(ZUtils.getDimensionIdentifier(world), new BlockPos(x,0,z));
			}
		}

		return Constants.INVALID_PLANET;
	}

	public Vector3F<Float> getDestinationCoordinates(ResourceLocation destDimID, boolean commit) {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();
			if(tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer)tile).getLandingLocation(destDimID, commit);
			}
		}
		return null;
	}
	
	public String getDestinationName(ResourceLocation destDimID) {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();
			if(tile instanceof TileGuidanceComputer) {
				return ((TileGuidanceComputer)tile).getDestinationName(destDimID);
			}
		}
		return "";
	}

	public void setDestinationCoordinates(Vector3F<Float> vec, ResourceLocation dimid) {
		Iterator<TileEntity> iterator = getTileEntityList().iterator();
		while(iterator.hasNext()) {
			TileEntity tile = iterator.next();
			if(tile instanceof TileGuidanceComputer) {
				((TileGuidanceComputer)tile).setReturnPosition(vec, dimid);
			}
		}
	}

	public void writeToNetwork(ByteBuf out) {
		PacketBuffer buffer = new PacketBuffer(out);

		buffer.writeByte(this.sizeX);
		buffer.writeByte(this.sizeY);
		buffer.writeByte(this.sizeZ);
		buffer.writeShort(tileEntities.size());

		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {
					buffer.writeInt(Block.getIdFromBlock(this.blocks[x][y][z]));
					buffer.writeShort(this.metas[x][y][z]);
				}
			}
		}

		Iterator<TileEntity> tileIterator = tileEntities.iterator();

		while(tileIterator.hasNext()) {
			TileEntity tile = tileIterator.next();

			CompoundNBT nbt = new CompoundNBT();

			try {
				tile.write(nbt);

				try {
					buffer.writeCompoundTag(nbt);
				} catch(Exception e) {
					e.printStackTrace();
				}

			} catch(RuntimeException e) {
				AdvancedRocketry.logger.warn("A tile entity has thrown an error while writing to network: " + tile.getClass().getCanonicalName());
				tileIterator.remove();
			}
		}
	}

	public void readFromNetwork(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);

		this.sizeX = buffer.readByte();
		this.sizeY = buffer.readByte();
		this.sizeZ = buffer.readByte();
		short numTiles = buffer.readShort();

		this.blocks = new Block[sizeX][sizeY][sizeZ];
		this.metas = new short[sizeX][sizeY][sizeZ];
		chunk = new Chunk(world, 0, 0);

		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {

					this.blocks[x][y][z] = Block.getBlockById(buffer.readInt());
					this.metas[x][y][z] = buffer.readShort();

					chunk.setBlockState(new BlockPos(x, y, z), this.blocks[x][y][z].getStateById(this.metas[x][y][z]));
				}
			}
		}

		for(short i = 0; i < numTiles; i++) {
			try {
				CompoundNBT nbt = buffer.readCompoundTag();

				TileEntity tile = ZUtils.createTile(nbt);
				tile.setWorld(world);
				tileEntities.add(tile);

				if(isInventoryBlock(tile)) {
					inventoryTiles.add(tile);
				}

				if(isLiquidContainerBlock(tile))
					liquidTiles.add(tile);
				tile.setWorld(world);

				chunk.addTileEntity(tile);

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		//We are now ready to render
		this.chunk.generateSkylightMap();
		finalized = true;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return lightValue;
	}

	@Override
	public int getStrongPower(BlockPos pos, Direction direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return WorldType.CUSTOMIZED;
	}
}
