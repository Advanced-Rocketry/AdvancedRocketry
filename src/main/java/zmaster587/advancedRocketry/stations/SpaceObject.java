package zmaster587.advancedRocketry.stations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.IStorageChunk;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

public class SpaceObject implements ISpaceObject {
	private int posX, posY;
	private int altitude;
	private int destinationDimId;
	private int fuelAmount;
	private final int MAX_FUEL = 1000;
	private BlockPosition spawnLocation;
	private List<BlockPosition> spawnLocations;
	private List<BlockPosition> warpCoreLocation;
	private HashMap<BlockPosition,Boolean> occupiedLandingPads;
	private long transitionEta;
	private ForgeDirection direction;
	DimensionProperties properties;

	public SpaceObject() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
		spawnLocations = new LinkedList<BlockPosition>();
		occupiedLandingPads = new HashMap<BlockPosition,Boolean>();
		warpCoreLocation = new LinkedList<BlockPosition>(); 
		transitionEta = -1;
		destinationDimId = -1;
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
	public void setProperties(DimensionProperties properties) {
		this.properties = properties;
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
			return ForgeDirection.UNKNOWN;
		return direction;
	}
	/**
	 * @return the altitude above the parent DIM the object currently is
	 */
	public int getAltitude() {
		return altitude;
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
	}
	public void removeWarpCore(BlockPosition position) {
		warpCoreLocation.remove(position);
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
	public BlockPosition getNextLandingPad() {
		for(BlockPosition pos : spawnLocations) {
			if(!occupiedLandingPads.get(pos)) {
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
		
		properties.setParentPlanet(id, false);
		if(id != -1)
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
	public void onFirstCreated(IStorageChunk chunk) {
		World worldObj = DimensionManager.getWorld(Configuration.spaceDimId);
		chunk.pasteInWorld(worldObj, spawnLocation.x - chunk.getSizeX()/2, spawnLocation.y - chunk.getSizeY()/2, spawnLocation.z - chunk.getSizeZ()/2);

	}

	@Override
	public void writeToNbt(NBTTagCompound nbt) {
		properties.writeToNBT(nbt);
		nbt.setInteger("id", getId());
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setInteger("alitude", altitude);
		nbt.setInteger("spawnX", spawnLocation.x);
		nbt.setInteger("spawnY", spawnLocation.y);
		nbt.setInteger("spawnZ", spawnLocation.z);
		nbt.setInteger("destinationDimId", destinationDimId);
		nbt.setInteger("fuel", fuelAmount);
		
		if(direction !=null)
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
	}

	@Override
	public void readFromNbt(NBTTagCompound nbt) {
		properties.readFromNBT(nbt);
		
		destinationDimId = nbt.getInteger("destinationDimId");
		posX = nbt.getInteger("posX");
		posY = nbt.getInteger("posY");
		altitude = nbt.getInteger("altitude");
		fuelAmount = nbt.getInteger("fuel");
		spawnLocation = new BlockPosition(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		properties.setId(nbt.getInteger("id"));
		
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
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int[] posInt = tag.getIntArray("pos");
			BlockPosition pos = new BlockPosition(posInt[0], posInt[1], posInt[2]);
			warpCoreLocation.add(pos);
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
}
