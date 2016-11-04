package zmaster587.advancedRocketry.stations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.IStorageChunk;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.network.PacketStationUpdate.Type;
import zmaster587.advancedRocketry.tile.station.TileDockingPort;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpaceObject implements ISpaceObject {
	private int posX, posY;
	private boolean created;
	private int altitude;
	private float orbitalDistance;
	private int destinationDimId;
	private int fuelAmount;
	private final int MAX_FUEL = 1000;
	private HashedBlockPosition spawnLocation;
	private List<HashedBlockPosition> spawnLocations;
	private List<HashedBlockPosition> warpCoreLocation;
	private HashMap<HashedBlockPosition, String> dockingPoints;
	private HashMap<HashedBlockPosition,Boolean> occupiedLandingPads;
	private long transitionEta;
	private EnumFacing direction;
	private double rotation;
	private double angularVelocity;
	private long lastTimeModification = 0;
	private DimensionProperties properties;
	public boolean hasWarpCores = false;

	public SpaceObject() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
		spawnLocations = new LinkedList<HashedBlockPosition>();
		occupiedLandingPads = new HashMap<HashedBlockPosition,Boolean>();
		warpCoreLocation = new LinkedList<HashedBlockPosition>(); 
		dockingPoints = new HashMap<HashedBlockPosition, String>();
		transitionEta = -1;
		destinationDimId = 0;
		created = false;
	}

	public long getExpireTime() { 
		return Long.MAX_VALUE;
	}

	public void beginTransition(long time) {
		if(time > 0)
			transitionEta = time;
	}

	public long getTransitionTime() {
		return transitionEta;
	}

	/**
	 * @return id of the space object (NOT the DIMID)
	 */
	@Override
	public int getId() {
		return properties.getId();
	}

	/**
	 * @return dimension properties of the object
	 */
	@Override
	public DimensionProperties getProperties() {
		return properties;
	}

	@SideOnly(Side.CLIENT)
	public void setProperties(IDimensionProperties properties) {
		this.properties = (DimensionProperties)properties;
	}

	/**
	 * @return the DIMID of the planet the object is currently orbiting, -1 if none
	 */
	@Override
	public int getOrbitingPlanetId() {
		return properties.getParentPlanet();
	}

	/**
	 * Sets the forward Facing direction of the object.  Mostly used for warpships
	 * @param direction
	 */
	public void setForwardDirection(EnumFacing direction) {
		this.direction = direction;
	}

	/**
	 * Gets the forward facing direction of the ship.  Direction is not garunteed to be set
	 * @return direction of the ship, or UNKNOWN if none exists
	 */
	public EnumFacing getForwardDirection() {
		if(direction == null)
			return EnumFacing.DOWN;
		return direction;
	}
	/**
	 * @return the altitude above the parent DIM the object currently is
	 */
	public int getAltitude() {
		return altitude;
	}

	/**
	 * @return rotation of the station in degrees
	 */
	public double getRotation() {
		return (rotation + getDeltaRotation()*(getWorldTime() - lastTimeModification)) % (360D);
	}

	/**
	 * @param rotation rotation of the station in degrees
	 */
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	/**
	 * @return anglarVelocity of the station in degrees per tick
	 */
	public double getDeltaRotation() {
		return angularVelocity;
	}

	/**
	 * @param rotation anglarVelocity of the station in degrees per tick
	 */
	public void setDeltaRotation(double rotation) {
		this.rotation = getRotation();
		this.lastTimeModification = getWorldTime();
		this.angularVelocity = rotation;
	}

	public double getMaxRotationalAcceleration() {
		return 0.00002D;
	}

	private long getWorldTime() {
		return AdvancedRocketry.proxy.getWorldTimeUniversal(Configuration.spaceDimId);
	}

	/**
	 * @return the X postion on the graph the object is stored in {@link SpaceObjectManager}
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return the Y postion on the graph the object is stored in {@link SpaceObjectManager}
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @return the spawn location of the object
	 */
	public HashedBlockPosition getSpawnLocation() {
		return spawnLocation;
	}

	public void addWarpCore(HashedBlockPosition position) {
		warpCoreLocation.add(position);
		hasWarpCores = true;
	}
	public void removeWarpCore(HashedBlockPosition position) {
		warpCoreLocation.remove(position);
		if(warpCoreLocation.size() == 0 )
			hasWarpCores = false;
	}

	public List<HashedBlockPosition> getWarpCoreLocations() {
		return warpCoreLocation;
	}

	public boolean hasUsableWarpCore() {
		return hasWarpCores && properties.getParentPlanet() != SpaceObjectManager.WARPDIMID && getDestOrbitingBody() != getOrbitingPlanetId();
	}

	public int getFuelAmount() {
		return fuelAmount;
	}

	public int getMaxFuelAmount() {
		return MAX_FUEL;
	}

	public void setFuelAmount(int amt) {
		fuelAmount = amt;
	}

	/**
	 * Adds the passed amount of fuel to the space station
	 * @param amt
	 * @return amount of fuel used
	 */
	public int addFuel(int amt) {
		if(amt < 0)
			return amt;

		int oldFuelAmt = fuelAmount;
		fuelAmount = Math.min(fuelAmount + amt, MAX_FUEL);

		amt = fuelAmount - oldFuelAmt;

		if(FMLCommonHandler.instance().getSide().isServer())
			PacketHandler.sendToAll(new PacketStationUpdate(this, Type.FUEL_UPDATE));
		return amt;
	}

	/**
	 * Used the amount of fuel passed
	 * @param amt
	 * @return amount of fuel consumed
	 */
	public int useFuel(int amt) {
		if(amt > getFuelAmount())
			return 0;

		fuelAmount -= amt;

		if(FMLCommonHandler.instance().getSide().isServer())
			PacketHandler.sendToAll(new PacketStationUpdate(this, Type.FUEL_UPDATE));
		return amt;
	}

	public void addLandingPad(BlockPos pos) {
		addLandingPad(pos.getX(), pos.getZ());
	}

	/**
	 * Adds a landing pad to the station
	 * @param x
	 * @param z
	 */
	public void addLandingPad(int x, int z) {
		HashedBlockPosition pos = new HashedBlockPosition(x, 0, z);
		if(!spawnLocations.contains(pos)) {
			spawnLocations.add(pos);
			occupiedLandingPads.put(pos, false);
		}
	}

	public void removeLandingPad(BlockPos pos) {
		removeLandingPad(pos.getX(), pos.getZ());
	}

	/**
	 * Removes an existing landing pad from the station
	 * @param x
	 * @param z
	 */
	public void removeLandingPad(int x, int z) {
		HashedBlockPosition pos = new HashedBlockPosition(x, 0, z);
		spawnLocations.remove(pos);
		occupiedLandingPads.remove(pos);
	}

	/**
	 * Adds a docking location to the station
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addDockingPosition(BlockPos pos, String str) {
		HashedBlockPosition pos2 = new HashedBlockPosition(pos);
		dockingPoints.put(pos2, str);
	}
	/**
	 * Removes a docking location from the station
	 * @param x
	 * @param y
	 * @param z
	 */
	public void removeDockingPosition(BlockPos pos) {
		HashedBlockPosition pos2 = new HashedBlockPosition(pos);
		dockingPoints.remove(pos2);
	}

	/**
	 * @return next viable place to land
	 */
	public HashedBlockPosition getNextLandingPad(boolean commit) {
		for(HashedBlockPosition pos : spawnLocations) {
			if(!occupiedLandingPads.get(pos)) {
				if(commit)
					occupiedLandingPads.put(pos, true);
				return pos;
			}
		}
		return null;
	}

	/**
	 * @return true if there is an empty pad to land on
	 */
	public boolean hasFreeLandingPad() {
		for(HashedBlockPosition pos : spawnLocations) {
			if(!occupiedLandingPads.get(pos)) {
				return true;
			}
		}
		return false;
	}

	public void setPadStatus(BlockPos pos, boolean full) {
		setPadStatus(pos.getX(), pos.getZ(), full);
	}

	/**
	 * @param x
	 * @param z
	 * @param full true if the pad is avalible to use
	 */
	public void setPadStatus(int x, int z, boolean full) {
		HashedBlockPosition pos = new HashedBlockPosition(x, 0, z);
		if(occupiedLandingPads.containsKey(pos))
			occupiedLandingPads.put(pos, full);
	}

	/**
	 * @param id the space object id of this object (NOT DIMID)
	 */
	@Override
	public void setId(int id) {
		properties.setId(id);
	}

	/**
	 * Sets the coords of the space object on the graph
	 * @param posX
	 * @param posY
	 */
	@Override
	public void setPos(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	/**
	 * Sets the spawn location for the space object
	 * @param x
	 * @param y
	 * @param z
	 */
	@Override
	public void setSpawnLocation(int x, int y, int z) {
		spawnLocation = new HashedBlockPosition(x,y,z);
	}

	/**
	 * Sets the orbiting planet for the space object but does NOT register it with the planet
	 * @param id
	 */
	@Override
	public void setOrbitingBody(int id) {
		if(id == this.getOrbitingPlanetId())
			return;

		properties.setParentPlanet(zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(id), false);
		if(id != SpaceObjectManager.WARPDIMID)
			destinationDimId = id;
	}

	@Override
	public void setDestOrbitingBody(int id) {
		destinationDimId = id;
		if(FMLCommonHandler.instance().getSide().isServer()) {
			PacketHandler.sendToAll(new PacketStationUpdate(this, PacketStationUpdate.Type.DEST_ORBIT_UPDATE));
		}
	}

	@Override
	public int getDestOrbitingBody() {
		return destinationDimId;
	}

	/**
	 * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
	 * @param chunk
	 */
	public void onModuleUnpack(IStorageChunk chunk) {

		if(DimensionManager.isDimensionRegistered(Configuration.spaceDimId) &&  DimensionManager.getWorld(Configuration.spaceDimId) == null)
			DimensionManager.initDimension(Configuration.spaceDimId);
		World worldObj = DimensionManager.getWorld(Configuration.spaceDimId);
		if(!created) {
			chunk.pasteInWorld(worldObj, spawnLocation.x - chunk.getSizeX()/2, spawnLocation.y - chunk.getSizeY()/2, spawnLocation.z - chunk.getSizeZ()/2);
			created = true;
		}
		else {
			List<TileEntity> tiles = chunk.getTileEntityList();
			List<String> targetIds = new LinkedList<String>();
			List<TileEntity> myPoss = new LinkedList<TileEntity>();
			HashedBlockPosition pos = null;
			TileDockingPort destTile = null;
			TileDockingPort srcTile = null;

			//Iterate though all docking ports on the module in the chunk being launched
			for(TileEntity tile : tiles) {
				if(tile instanceof TileDockingPort) {
					targetIds.add(((TileDockingPort)tile).getTargetId());
					myPoss.add(tile);
				}
			}

			//Find the first docking port on the station that matches the id in the new chunk
			for(Entry<HashedBlockPosition, String> map : dockingPoints.entrySet()) {
				if(targetIds.contains(map.getValue())) {
					int loc = targetIds.indexOf(map.getValue());
					pos = map.getKey();
					TileEntity tile;
					if((tile = worldObj.getTileEntity(pos.getBlockPos())) instanceof TileDockingPort) {
						destTile = (TileDockingPort)tile;
						srcTile = (TileDockingPort) myPoss.get(loc);
						break;
					}
				}
			}

			if(destTile != null) {
				EnumFacing stationFacing = destTile.getBlockType().getStateFromMeta(destTile.getBlockMetadata()).getValue(BlockFullyRotatable.FACING);
				EnumFacing moduleFacing = srcTile.getBlockType().getStateFromMeta(srcTile.getBlockMetadata()).getValue(BlockFullyRotatable.FACING);


				EnumFacing cross = moduleFacing.rotateAround(stationFacing.getAxis());

				if(stationFacing.getAxisDirection() == AxisDirection.NEGATIVE)
					cross = cross.getOpposite();

				if(cross == moduleFacing) {
					if(moduleFacing == stationFacing) {
						if(cross == EnumFacing.DOWN || cross == EnumFacing.UP) {
							chunk.rotateBy(EnumFacing.NORTH);
							chunk.rotateBy(EnumFacing.NORTH);
						}
						else {
							chunk.rotateBy(EnumFacing.UP);
							chunk.rotateBy(EnumFacing.UP);
						}
					}
				}
				else
					chunk.rotateBy(stationFacing.getFrontOffsetY() == 0 ? cross : cross.getOpposite());

				int xCoord = (stationFacing.getFrontOffsetX() == 0 ? -srcTile.getPos().getX() : srcTile.getPos().getX()*stationFacing.getFrontOffsetX()) + stationFacing.getFrontOffsetX() + destTile.getPos().getX();
				int yCoord = (stationFacing.getFrontOffsetY() == 0 ? -srcTile.getPos().getY() : srcTile.getPos().getY()*stationFacing.getFrontOffsetY()) + stationFacing.getFrontOffsetY() + destTile.getPos().getY();
				int zCoord = (stationFacing.getFrontOffsetZ() == 0 ? -srcTile.getPos().getZ() : srcTile.getPos().getZ()*stationFacing.getFrontOffsetZ()) + stationFacing.getFrontOffsetZ() + destTile.getPos().getZ();
				chunk.pasteInWorld(worldObj, xCoord, yCoord, zCoord);
				worldObj.setBlockToAir(destTile.getPos().offset(stationFacing));
				worldObj.setBlockToAir(destTile.getPos());
			}
		}
	}

	@Override
	public void writeToNbt(NBTTagCompound nbt) {
		properties.writeToNBT(nbt);
		nbt.setInteger("id", getId());
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setBoolean("created", created);
		nbt.setInteger("alitude", altitude);
		nbt.setInteger("spawnX", spawnLocation.x);
		nbt.setInteger("spawnY", spawnLocation.y);
		nbt.setInteger("spawnZ", spawnLocation.z);
		nbt.setInteger("destinationDimId", destinationDimId);
		nbt.setInteger("fuel", fuelAmount);
		nbt.setDouble("rotation", rotation);
		nbt.setDouble("deltaRotation", angularVelocity);


		if(direction != null)
			nbt.setInteger("direction", direction.ordinal());

		if(transitionEta > -1)
			nbt.setLong("transitionEta", transitionEta);

		NBTTagList list = new NBTTagList();
		for(HashedBlockPosition pos : this.spawnLocations) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("occupied", occupiedLandingPads.get(pos));
			tag.setIntArray("pos", new int[] {pos.x, pos.z});
			list.appendTag(tag);
		}
		nbt.setTag("spawnPositions", list);

		list = new NBTTagList();
		for(HashedBlockPosition pos : this.warpCoreLocation) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setIntArray("pos", new int[] {pos.x, pos.y, pos.z});
			list.appendTag(tag);
		}
		nbt.setTag("warpCorePositions", list);

		list = new NBTTagList();
		for(Entry<HashedBlockPosition, String> obj : this.dockingPoints.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			HashedBlockPosition pos = obj.getKey();
			String str = obj.getValue();
			tag.setIntArray("pos", new int[] {pos.x, pos.y, pos.z});
			tag.setString("id", str);
			list.appendTag(tag);
		}
		nbt.setTag("dockingPositons", list);
	}

	@Override
	public void readFromNbt(NBTTagCompound nbt) {
		properties.readFromNBT(nbt);

		if((int)orbitalDistance != properties.getParentOrbitalDistance())
			orbitalDistance = properties.getParentOrbitalDistance();

		destinationDimId = nbt.getInteger("destinationDimId");
		posX = nbt.getInteger("posX");
		posY = nbt.getInteger("posY");
		created = nbt.getBoolean("created");
		altitude = nbt.getInteger("altitude");
		fuelAmount = nbt.getInteger("fuel");
		spawnLocation = new HashedBlockPosition(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		properties.setId(nbt.getInteger("id"));
		rotation = nbt.getDouble("rotation");
		angularVelocity = nbt.getDouble("deltaRotation");

		if(nbt.hasKey("direction"))
			direction = EnumFacing.values()[nbt.getInteger("direction")];

		if(nbt.hasKey("transitionEta"))
			transitionEta = nbt.getLong("transitionEta");

		NBTTagList list = nbt.getTagList("spawnPositions", NBT.TAG_COMPOUND);
		spawnLocations.clear();
		occupiedLandingPads.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			HashedBlockPosition pos = new HashedBlockPosition(posInt[0], 0, posInt[1]);
			spawnLocations.add(pos);
			occupiedLandingPads.put(pos, tag.getBoolean("occupied"));
		}

		list = nbt.getTagList("warpCorePositions", NBT.TAG_COMPOUND);
		hasWarpCores = false;
		warpCoreLocation.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			HashedBlockPosition pos = new HashedBlockPosition(posInt[0], posInt[1], posInt[2]);
			warpCoreLocation.add(pos);
			hasWarpCores = true;
		}

		list = nbt.getTagList("dockingPositons", NBT.TAG_COMPOUND);
		dockingPoints.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			HashedBlockPosition pos = new HashedBlockPosition(posInt[0], posInt[1], posInt[2]);
			String str = tag.getString("id");
			dockingPoints.put(pos, str);
		}
	}

	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	@Override
	public boolean hasCustomSpawnLocation() {
		return false;
	}

	@Override
	public float getOrbitalDistance() {
		return orbitalDistance;
	}

	@Override
	public void setOrbitalDistance(float finalVel) {
		if((int)orbitalDistance != properties.getParentOrbitalDistance())
			properties.setParentOrbitalDistance((int)orbitalDistance);
		orbitalDistance = finalVel;
	}
}
