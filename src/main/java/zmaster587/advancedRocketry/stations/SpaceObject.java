package zmaster587.advancedRocketry.stations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.IStorageChunk;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.IPlanetDefiner;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.network.PacketStationUpdate.Type;
import zmaster587.advancedRocketry.tile.station.TileDockingPort;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

public class SpaceObject implements ISpaceObject, IPlanetDefiner {
	private int posX, posY;
	private boolean created;
	private int altitude;
	private float orbitalDistance;
	private int destinationDimId;
	private int fuelAmount;
	private final int MAX_FUEL = 1000;
	private BlockPosition spawnLocation;
	private List<BlockPosition> spawnLocations;
	private List<BlockPosition> warpCoreLocation;
	private Set<Integer> knownPlanetList;
	private HashMap<BlockPosition, String> dockingPoints;
	private HashMap<BlockPosition,Boolean> occupiedLandingPads;
	private long transitionEta;
	private ForgeDirection direction;
	private double rotation;
	private double angularVelocity;
	private long lastTimeModification = 0;
	private DimensionProperties properties;
	public boolean hasWarpCores = false;

	public SpaceObject() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
		spawnLocations = new LinkedList<BlockPosition>();
		occupiedLandingPads = new HashMap<BlockPosition,Boolean>();
		warpCoreLocation = new LinkedList<BlockPosition>();
		dockingPoints = new HashMap<BlockPosition, String>();
		transitionEta = -1;
		destinationDimId = 0;
		created = false;
		knownPlanetList = new HashSet<Integer>();
		knownPlanetList.addAll(Configuration.initiallyKnownPlanets);
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

	public void discoverPlanet(int pid) {
		knownPlanetList.add(pid);
		PacketHandler.sendToAll(new PacketSpaceStationInfo(getId(), this));
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
	public void setForwardDirection(ForgeDirection direction) {
		this.direction = direction;
	}

	/**
	 * Gets the forward facing direction of the ship.  Direction is not garunteed to be set
	 * @return direction of the ship, or UNKNOWN if none exists
	 */
	public ForgeDirection getForwardDirection() {
		if(direction == null)
			return ForgeDirection.NORTH;
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
	public BlockPosition getSpawnLocation() {
		return spawnLocation;
	}

	public void addWarpCore(BlockPosition position) {
		warpCoreLocation.add(position);
		hasWarpCores = true;
	}
	public void removeWarpCore(BlockPosition position) {
		warpCoreLocation.remove(position);
		
		if(warpCoreLocation.isEmpty())
			hasWarpCores = false;
	}

	public List<BlockPosition> getWarpCoreLocations() {
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

	/**
	 * Adds a landing pad to the station
	 * @param x
	 * @param z
	 */
	public void addLandingPad(int x, int z) {
		BlockPosition pos = new BlockPosition(x, 0, z);
		if(!spawnLocations.contains(pos)) {
			spawnLocations.add(pos);
			occupiedLandingPads.put(pos, false);
		}
	}

	/**
	 * Adds a docking location to the station
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addDockingPosition(int x, int y, int z, String str) {
		BlockPosition pos = new BlockPosition(x, y, z);
		dockingPoints.put(pos, str);
	}
	/**
	 * Removes a docking location from the station
	 * @param x
	 * @param y
	 * @param z
	 */
	public void removeDockingPosition(int x, int y, int z) {
		BlockPosition pos = new BlockPosition(x, y, z);
		dockingPoints.remove(pos);
	}

	/**
	 * Removes an existing landing pad from the station
	 * @param x
	 * @param z
	 */
	public void removeLandingPad(int x, int z) {
		BlockPosition pos = new BlockPosition(x, 0, z);
		spawnLocations.remove(pos);
		occupiedLandingPads.remove(pos);
	}

	/**
	 * @return next viable place to land
	 */
	public BlockPosition getNextLandingPad(boolean commit) {
		for(BlockPosition pos : spawnLocations) {
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
		for(BlockPosition pos : spawnLocations) {
			if(!occupiedLandingPads.get(pos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param x
	 * @param z
	 * @param full true if the pad is avalible to use
	 */
	public void setPadStatus(int x, int z, boolean full) {
		BlockPosition pos = new BlockPosition(x, 0, z);
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
		spawnLocation = new BlockPosition(x,y,z);
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
	 * Can also be called when a module is shipped
	 * @param chunk
	 */
	@Override
	public void onModuleUnpack(IStorageChunk chunk) {

		if(DimensionManager.isDimensionRegistered(Configuration.spaceDimId) &&  DimensionManager.getWorld(Configuration.spaceDimId) == null)
			DimensionManager.initDimension(Configuration.spaceDimId);
		World worldObj = DimensionManager.getWorld(Configuration.spaceDimId);

		//If this is the first module sent up
		if(!created) {
			chunk.pasteInWorld(worldObj, spawnLocation.x - chunk.getSizeX()/2, spawnLocation.y - chunk.getSizeY()/2, spawnLocation.z - chunk.getSizeZ()/2);
			created = true;
		}
		else {
			List<TileEntity> tiles = chunk.getTileEntityList();
			List<String> targetIds = new LinkedList<String>();
			List<TileEntity> myPoss = new LinkedList<TileEntity>();
			BlockPosition pos = null;
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
			for(Entry<BlockPosition, String> map : dockingPoints.entrySet()) {
				if(targetIds.contains(map.getValue())) {
					int loc = targetIds.indexOf(map.getValue());
					pos = map.getKey();
					TileEntity tile;
					if((tile = worldObj.getTileEntity(pos.x, pos.y, pos.z)) instanceof TileDockingPort) {
						destTile = (TileDockingPort)tile;
						srcTile = (TileDockingPort) myPoss.get(loc);
						break;
					}
				}
			}

			if(destTile != null) {
				ForgeDirection stationFacing = BlockFullyRotatable.getFront(destTile.getBlockMetadata());
				ForgeDirection moduleFacing = BlockFullyRotatable.getFront(srcTile.getBlockMetadata());

				ForgeDirection cross = moduleFacing.getRotation(stationFacing);
				if(cross == moduleFacing) {
					if(moduleFacing == stationFacing) {
						if(cross == ForgeDirection.DOWN || cross == ForgeDirection.UP) {
							chunk.rotateBy(ForgeDirection.NORTH);
							chunk.rotateBy(ForgeDirection.NORTH);
						}
						else {
							chunk.rotateBy(ForgeDirection.UP);
							chunk.rotateBy(ForgeDirection.UP);
						}
					}
				}
				else if(cross.getOpposite() != moduleFacing)
					chunk.rotateBy(stationFacing.offsetY == 0 ? cross :  cross.getOpposite());

				int xCoord = (stationFacing.offsetX == 0 ? -srcTile.xCoord : srcTile.xCoord*stationFacing.offsetX) + stationFacing.offsetX + destTile.xCoord;
				int yCoord = (stationFacing.offsetY == 0 ? -srcTile.yCoord : srcTile.yCoord*stationFacing.offsetY) + stationFacing.offsetY + destTile.yCoord;
				int zCoord = (stationFacing.offsetZ == 0 ? -srcTile.zCoord : srcTile.zCoord*stationFacing.offsetZ) + stationFacing.offsetZ + destTile.zCoord;
				chunk.pasteInWorld(worldObj, xCoord, yCoord, zCoord);
				worldObj.setBlockToAir(destTile.xCoord + stationFacing.offsetX, destTile.yCoord + stationFacing.offsetY, destTile.zCoord + stationFacing.offsetZ);
				worldObj.setBlockToAir(destTile.xCoord, destTile.yCoord, destTile.zCoord);
			}
		}
	}

	@Override
	public void writeToNbt(NBTTagCompound nbt) {
		properties.writeToNBT(nbt);
		nbt.setBoolean("created", created);
		nbt.setInteger("id", getId());
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setInteger("alitude", altitude);
		nbt.setInteger("spawnX", spawnLocation.x);
		nbt.setInteger("spawnY", spawnLocation.y);
		nbt.setInteger("spawnZ", spawnLocation.z);
		nbt.setInteger("destinationDimId", destinationDimId);
		nbt.setInteger("fuel", fuelAmount);
		nbt.setDouble("rotation", rotation);
		nbt.setDouble("deltaRotation", angularVelocity);
		
		//Set known planets
		int array[] = new int[knownPlanetList.size()];
		int j = 0;
		for(int i : knownPlanetList)
			array[j++] = i;
		nbt.setIntArray("knownPlanets", array);


		if(direction != null)
			nbt.setInteger("direction", direction.ordinal());

		if(transitionEta > -1)
			nbt.setLong("transitionEta", transitionEta);

		NBTTagList list = new NBTTagList();
		for(BlockPosition pos : this.spawnLocations) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("occupied", occupiedLandingPads.get(pos));
			tag.setIntArray("pos", new int[] {pos.x, pos.z});
			list.appendTag(tag);
		}
		nbt.setTag("spawnPositions", list);

		list = new NBTTagList();
		for(BlockPosition pos : this.warpCoreLocation) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setIntArray("pos", new int[] {pos.x, pos.y, pos.z});
			list.appendTag(tag);
		}
		nbt.setTag("warpCorePositions", list);

		list = new NBTTagList();
		for(Entry<BlockPosition, String> obj : this.dockingPoints.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			BlockPosition pos = obj.getKey();
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

		created = nbt.getBoolean("created");
		destinationDimId = nbt.getInteger("destinationDimId");
		posX = nbt.getInteger("posX");
		posY = nbt.getInteger("posY");
		altitude = nbt.getInteger("altitude");
		fuelAmount = nbt.getInteger("fuel");
		spawnLocation = new BlockPosition(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		properties.setId(nbt.getInteger("id"));
		rotation = nbt.getDouble("rotation");
		angularVelocity = nbt.getDouble("deltaRotation");
		
		
		//get known planets
		
		int array[] = nbt.getIntArray("knownPlanets");
		int j = 0;
		for(int i : array)
			knownPlanetList.add(i);

		if(nbt.hasKey("direction"))
			direction = ForgeDirection.getOrientation(nbt.getInteger("direction"));

		if(nbt.hasKey("transitionEta"))
			transitionEta = nbt.getLong("transitionEta");

		NBTTagList list = nbt.getTagList("spawnPositions", NBT.TAG_COMPOUND);
		spawnLocations.clear();
		occupiedLandingPads.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			BlockPosition pos = new BlockPosition(posInt[0], 0, posInt[1]);
			spawnLocations.add(pos);
			occupiedLandingPads.put(pos, tag.getBoolean("occupied"));
		}

		list = nbt.getTagList("warpCorePositions", NBT.TAG_COMPOUND);
		warpCoreLocation.clear();
		hasWarpCores = false;
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			BlockPosition pos = new BlockPosition(posInt[0], posInt[1], posInt[2]);
			warpCoreLocation.add(pos);
			hasWarpCores = true;
		}

		list = nbt.getTagList("dockingPositons", NBT.TAG_COMPOUND);
		dockingPoints.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			BlockPosition pos = new BlockPosition(posInt[0], posInt[1], posInt[2]);
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

	@Override
	public boolean isPlanetKnown(IDimensionProperties properties) {
		return !Configuration.planetsMustBeDiscovered || knownPlanetList.contains(properties.getId());
	}

	@Override
	public boolean isStarKnown(StellarBody body) {
		return true;
	}
}
