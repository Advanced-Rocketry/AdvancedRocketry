package zmaster587.advancedRocketry.stations;

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
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
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
import zmaster587.advancedRocketry.util.SpacePosition;
import zmaster587.advancedRocketry.util.StationLandingLocation;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;

public class SpaceStationObject implements ISpaceObject, IPlanetDefiner {
	private int launchPosX, launchPosZ, posX, posZ;
	private boolean created;
	private int altitude;
	private float orbitalDistance;
	private int destinationDimId;
	private int fuelAmount;
	private final int MAX_FUEL = 1000;
	private HashedBlockPosition spawnLocation;
	private List<StationLandingLocation> spawnLocations;
	private List<HashedBlockPosition> warpCoreLocation;
	private Set<Integer> knownPlanetList;
	private HashMap<HashedBlockPosition, String> dockingPoints;
	private long transitionEta;
	private EnumFacing direction;
	private boolean isAnchored = false;
	private double[] rotation;
	private double[] angularVelocity;
	private long lastTimeModification = 0;
	private DimensionProperties properties;
	public boolean hasWarpCores = false;

	public int targetOrbitalDistance;
	public int targetGravity;
	public int[] targetRotationsPerHour;

	public SpaceStationObject() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
		orbitalDistance = 4.0f;
		targetOrbitalDistance = 4;
		targetRotationsPerHour = new int[]{0, 0, 0};
		targetGravity = 10;
		spawnLocations = new LinkedList<>();
		warpCoreLocation = new LinkedList<>();
		dockingPoints = new HashMap<>();
		transitionEta = -1;
		destinationDimId = 0;
		created = false;
		knownPlanetList = new HashSet<>();
		angularVelocity = new double[3];
		rotation = new double[3];
	}

	public long getExpireTime() { 
		return Long.MAX_VALUE;
	}

	public void beginTransition(long time) {
		if(time > 0)
			transitionEta = time;
		
		//Hack because somehow created ends up being false
		created = true;
	}

	public boolean isWarping() {
		return getOrbitingPlanetId() == SpaceObjectManager.WARPDIMID;
	}

	public long getTransitionTime() {
		return transitionEta;
	}

	public void setTargetRotationsPerHour(int index, int rotations) {
		targetRotationsPerHour[index] = rotations;
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
	@Nonnull
	public DimensionProperties getProperties() {
		return properties;
	}

	/**
	 * @return the insolation relative to Earth ground of the station - returns 0 for warping!
	 */
	public double getInsolationMultiplier() {
		return (isWarping()) ? 0.0 : getOrbitingPlanet().getPeakInsolationMultiplierWithoutAtmosphere();
	}

	@SideOnly(Side.CLIENT)
	public void setProperties(@Nonnull IDimensionProperties properties) {
		this.properties = (DimensionProperties)properties;
	}

	/**
	 * @return the DIMID of the planet the object is currently orbiting, Constants.INVALID_PLANET if none
	 */
	@Override
	public int getOrbitingPlanetId() {
		return created ? properties.getParentPlanet() : Constants.INVALID_PLANET;
	}
	
	public DimensionProperties getOrbitingPlanet()
	{
		int planetId = getOrbitingPlanetId();
		if(planetId != Constants.INVALID_PLANET)
			return zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(planetId);
		return null;
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
			return EnumFacing.NORTH;
		return direction;
	}

	/**
	 * @return if the object is anchored in place by anything
	 */
	@Override
	public boolean isAnchored() {
		return isAnchored;}

	/**
	 * Sets if the object is anchored or not
	 */
	@Override
	public void setIsAnchored(boolean anchored) {isAnchored = anchored; }

	/**
	 * @return the altitude above the parent DIM the object currently is
	 */
	public int getAltitude() {
		return altitude;
	}

	/**
	 * @return rotation of the station in degrees
	 */
	public double getRotation(EnumFacing dir) {
		
		return (rotation[getIDFromDir(dir)] + getDeltaRotation(dir)*(getWorldTime() - lastTimeModification)) % (360D);
	}

	/**
	 * @return whether the bottom of the station is facing the planet or not, this is if a laser would hit the planet at all if shined straight down
	 */
	public boolean isStationFacingPlanet () {
		//They use 0 to 1.0 so we need to convert to that, and to to check angle <150 degrees
		return Math.abs(rotation[0] - (int)rotation[0] - 0.5) > 0.40 && Math.abs(rotation[2] - (int)rotation[2] - 0.5) > 0.40;
	}

	/**
	 * @return whether the station's current rotation would break the tether
	 */
	public boolean wouldStationBreakTether () {
		//0.47 here is approximately between 10 and 15 degrees from the horizontal
		return 0.47 > Math.abs(rotation[0] - (int)rotation[0] - 0.5) || 0.47 > Math.abs(rotation[2] - (int)rotation[2] - 0.5) || Math.abs(getDeltaRotation(EnumFacing.UP)) > 0 || Math.abs(getDeltaRotation(EnumFacing.NORTH)) > 0 || Math.abs(getDeltaRotation(EnumFacing.EAST)) > 0;
	}

	private int getIDFromDir(EnumFacing facing){
		if(facing == EnumFacing.EAST)
			return 0;
		else if(facing == EnumFacing.UP)
			return 1;
		else
			return 2;
	}
	
	/**
	 * @param rotation rotation of the station in degrees
	 */
	public void setRotation(double rotation, EnumFacing facing) {
			this.rotation[getIDFromDir(facing)] = rotation;
	}

	/**
	 * @return anglarVelocity of the station in degrees per tick
	 */
	public double getDeltaRotation(EnumFacing facing) {
		return this.angularVelocity[getIDFromDir(facing)];
	}

	/**
	 * @param rotation anglarVelocity of the station in degrees per tick
	 */
	public void setDeltaRotation(double rotation, EnumFacing facing) {
		if (!isAnchored()) {
			this.rotation[getIDFromDir(facing)] = getRotation(facing);
			this.lastTimeModification = getWorldTime();

			this.angularVelocity[getIDFromDir(facing)] = rotation;
		}
	}

	public double getMaxRotationalAcceleration() {
		return 0.02D;
	}

	private long getWorldTime() {
		return AdvancedRocketry.proxy.getWorldTimeUniversal(ARConfiguration.getCurrentConfig().spaceDimId);
	}


	/**
	 * @return the X location the station was launched from
	 */
	public int getLaunchPosX() {
		return launchPosX;
	}

	/**
	 * @return the Z location the station was launched from
	 */
	public int getLaunchPosZ() {
		return launchPosZ;
	}
	
	/**
	 * @return the X coordinate over the planet the station is orbiting
	 */
	public int getOrbitalPosX() {
		return posX;
	}

	/**
	 * @return the Z coordinate over the planet the station is orbiting
	 */
	public int getOrbitalPosZ() {
		return posZ;
	}
	
	/**
	 * @return orbital velocity in meter per second with respect to the surface
	 */
	public double getOrbitalVelocity() {
		return 0;
	}
	
	/**
	 * @return the spawn location of the object
	 */
	public HashedBlockPosition getSpawnLocation() {
		return spawnLocation;
	}

	public SpacePosition getSpacePosition()
	{
		List<ISpaceObject> stations = SpaceObjectManager.getSpaceManager().getSpaceStationsOrbitingPlanet(getOrbitingPlanetId());
		if(stations.size() == 0)
			return new SpacePosition();
		DimensionProperties properties = getOrbitingPlanet();
		int stationCount = stations.size();
		int myIndex = stations.indexOf(this);
		
		float theta = myIndex*(360f / stationCount);
		
		return new SpacePosition().getFromSpherical(properties.getRenderSizePlanetView()*2f, theta);
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
	
	public void setLandingPadAutoLandStatus(BlockPos pos, boolean status) {
		setLandingPadAutoLandStatus(pos.getX(), pos.getZ(), status);
	}
	
	public void setLandingPadAutoLandStatus(int x, int z, boolean status) {
		HashedBlockPosition pos = new HashedBlockPosition(x, 0, z);

		for (StationLandingLocation loc : spawnLocations) {
			if (loc.getPos().equals(pos))
				loc.setAllowedForAutoLand(status);
		}
	}

	public void addLandingPad(BlockPos pos, String name) {
		addLandingPad(pos.getX(), pos.getZ(), name);
	}

	/**
	 * Adds a landing pad to the station
	 * @param x
	 * @param z
	 */
	public void addLandingPad(int x, int z, String name) {
		StationLandingLocation pos = new StationLandingLocation(new HashedBlockPosition(x, 0, z), name);
		if(!spawnLocations.contains(pos)) {
			spawnLocations.add(pos);
			pos.setOccupied(false);
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

		spawnLocations.removeIf(loc -> loc.getPos().equals(pos));
		//spawnLocations.remove(pos);
	}

	/**
	 * Adds a docking location to the station
	 * @param pos
	 * @param str
	 */
	public void addDockingPosition(BlockPos pos, String str) {
		HashedBlockPosition pos2 = new HashedBlockPosition(pos);
		dockingPoints.put(pos2, str);
	}
	/**
	 * Removes a docking location from the station
	 * @param pos
	 */
	public void removeDockingPosition(BlockPos pos) {
		HashedBlockPosition pos2 = new HashedBlockPosition(pos);
		dockingPoints.remove(pos2);
	}

	/**
	 * @return next viable place to land
	 */
	public HashedBlockPosition getNextLandingPad(boolean commit) {
		for(StationLandingLocation pos : spawnLocations) {
			if(!pos.getOccupied() && pos.getAllowedForAutoLand()) {
				if(commit)
					pos.setOccupied(true);
				return pos.getPos();
			}
		}
		return null;
	}

	public List<StationLandingLocation> getLandingPads() {
		return spawnLocations;
	}
	
	/**
	 * @return true if there is an empty pad to land on
	 */
	public boolean hasFreeLandingPad() {
		for(StationLandingLocation pos : spawnLocations) {
			if(!pos.getOccupied()) {
				return true;
			}
		}
		return false;
	}

	public void setPadStatus(BlockPos pos, boolean full) {
		setPadStatus(pos.getX(), pos.getZ(), full);
	}

	public StationLandingLocation getPadAtLocation(HashedBlockPosition pos) {
		pos.y = 0;
		for(StationLandingLocation loc : spawnLocations) {
			if(loc.getPos().equals(pos))
				return loc;
		}
		return null;
	}
	
	public void setPadName(World worldObj, HashedBlockPosition pos, String name) {
		StationLandingLocation loc = getPadAtLocation(pos);
		if(loc != null)
			loc.setName(name);
		
		//Make sure our remote uses get the data
		if(!worldObj.isRemote)
			PacketHandler.sendToAll(new PacketSpaceStationInfo(getId(), this));
	}
	
	/**
	 * @param x
	 * @param z
	 * @param full true if the pad is avalible to use
	 */
	public void setPadStatus(int x, int z, boolean full) {
		StationLandingLocation pos = new StationLandingLocation(new HashedBlockPosition(x, 0, z));
		
		for(StationLandingLocation loc : spawnLocations) {
			if(loc.equals(pos))
				loc.setOccupied(full);
		}
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
		this.posZ = posY;
	}
	
	/**
	 * Sets the launch coordinates of the space object
	 * @param posX
	 * @param posY
	 */
	public void setLaunchPos(int posX, int posY) {
		this.launchPosX = posX;
		this.launchPosZ = posY;
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

		if(DimensionManager.isDimensionRegistered(ARConfiguration.getCurrentConfig().spaceDimId) &&  DimensionManager.getWorld(ARConfiguration.getCurrentConfig().spaceDimId) == null)
			DimensionManager.initDimension(ARConfiguration.getCurrentConfig().spaceDimId);
		World worldObj = DimensionManager.getWorld(ARConfiguration.getCurrentConfig().spaceDimId);
		if(!created) {
			chunk.pasteInWorld(worldObj, spawnLocation.x - chunk.getSizeX()/2, spawnLocation.y - chunk.getSizeY()/2, spawnLocation.z - chunk.getSizeZ()/2);

			created = true;
			setLaunchPos(posX, posZ);
			setPos(posX, posZ);
		}
		else {
			List<TileEntity> tiles = chunk.getTileEntityList();
			List<String> targetIds = new LinkedList<>();
			List<TileEntity> myPoss = new LinkedList<>();
			HashedBlockPosition pos;
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
				else if(cross.getOpposite() != moduleFacing)
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
		nbt.setInteger("launchposX", launchPosX);
		nbt.setInteger("launchposY", launchPosZ);
		nbt.setBoolean("isAnchored", isAnchored);
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posZ);
		nbt.setBoolean("created", created);
		nbt.setInteger("alitude", altitude);
		nbt.setInteger("spawnX", spawnLocation.x);
		nbt.setInteger("spawnY", spawnLocation.y);
		nbt.setInteger("spawnZ", spawnLocation.z);
		nbt.setInteger("destinationDimId", destinationDimId);
		nbt.setInteger("fuel", fuelAmount);
		nbt.setFloat("orbitalDistance", orbitalDistance);
		nbt.setInteger("targetOrbitalDistance", targetOrbitalDistance);
		nbt.setInteger("targetGravity", targetGravity);
		nbt.setInteger("targetRotationX", targetRotationsPerHour[0]);
		nbt.setInteger("targetRotationY", targetRotationsPerHour[1]);
		nbt.setInteger("targetRotationZ", targetRotationsPerHour[2]);
		nbt.setDouble("rotationX", rotation[0]);
		nbt.setDouble("rotationY", rotation[1]);
		nbt.setDouble("rotationZ", rotation[2]);
		nbt.setDouble("deltaRotationX", angularVelocity[0]);
		nbt.setDouble("deltaRotationY", angularVelocity[1]);
		nbt.setDouble("deltaRotationZ", angularVelocity[2]);

		//Set known planets
		int[] array = new int[knownPlanetList.size()];
		int j = 0;
		for(int i : knownPlanetList)
			array[j++] = i;
		nbt.setIntArray("knownPlanets", array);


		if(direction != null)
			nbt.setInteger("direction", direction.ordinal());

		if(transitionEta > -1)
			nbt.setLong("transitionEta", transitionEta);

		NBTTagList list = new NBTTagList();
		for(StationLandingLocation pos : this.spawnLocations) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("occupied", pos.getOccupied());
			tag.setBoolean("autoLand", pos.getAllowedForAutoLand());
			tag.setIntArray("pos", new int[] {pos.getPos().x, pos.getPos().z});
			//if(pos.getName() != null && !pos.getName().isEmpty())
				tag.setString("name", pos.getName());
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

		destinationDimId = nbt.getInteger("destinationDimId");
		isAnchored = nbt.getBoolean("isAnchored");
		launchPosX = nbt.getInteger("launchposX");
		launchPosZ = nbt.getInteger("launchposY");
		posX = nbt.getInteger("posX");
		posZ = nbt.getInteger("posY");
		created = nbt.getBoolean("created");
		altitude = nbt.getInteger("altitude");
		fuelAmount = nbt.getInteger("fuel");
		orbitalDistance = nbt.getFloat("orbitalDistance");
		targetOrbitalDistance = nbt.getInteger("targetOrbitalDistance");
		targetRotationsPerHour[0] = nbt.getInteger("targetRotationX");
		targetRotationsPerHour[1] = nbt.getInteger("targetRotationY");
		targetRotationsPerHour[2] = nbt.getInteger("targetRotationZ");
		targetGravity = nbt.getInteger("targetGravity");
		spawnLocation = new HashedBlockPosition(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		properties.setId(nbt.getInteger("id"));
		rotation[0] = nbt.getDouble("rotationX");
		rotation[1] = nbt.getDouble("rotationY");
		rotation[2] = nbt.getDouble("rotationZ");
		angularVelocity[0] = nbt.getDouble("deltaRotationX");
		angularVelocity[1] = nbt.getDouble("deltaRotationY");
		angularVelocity[2] = nbt.getDouble("deltaRotationZ");

		//get known planets

		int[] array = nbt.getIntArray("knownPlanets");
		int j = 0;
		for(int i : array)
			knownPlanetList.add(i);

		if(nbt.hasKey("direction"))
			direction = EnumFacing.values()[nbt.getInteger("direction")];

		if(nbt.hasKey("transitionEta"))
			transitionEta = nbt.getLong("transitionEta");

		NBTTagList list = nbt.getTagList("spawnPositions", NBT.TAG_COMPOUND);
		spawnLocations.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			HashedBlockPosition pos = new HashedBlockPosition(posInt[0], 0, posInt[1]);
			StationLandingLocation loc = new StationLandingLocation(pos, tag.getString("name"));
			spawnLocations.add(loc);
			loc.setOccupied(tag.getBoolean("occupied"));
			loc.setAllowedForAutoLand(!tag.hasKey("occupied") || tag.getBoolean("occupied"));
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
		if (!isAnchored()) {
			orbitalDistance = Math.max(4.0f, finalVel);
		}
	}

	@Override
	public boolean isPlanetKnown(IDimensionProperties properties) {
		return !ARConfiguration.getCurrentConfig().planetsMustBeDiscovered || knownPlanetList.contains(properties.getId()) || zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().knownPlanets.contains(properties.getId());
	}

	@Override
	public boolean isStarKnown(StellarBody body) {
		return true;
	}
}
