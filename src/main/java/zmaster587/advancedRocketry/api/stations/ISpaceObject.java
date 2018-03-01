package zmaster587.advancedRocketry.api.stations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.libVulpes.util.HashedBlockPosition;

public interface ISpaceObject {
	
	/**
	 * @return id of the space object (NOT the DIMID)
	 */
	public int getId();
	
	public float getOrbitalDistance();
	
	public void setOrbitalDistance(float finalVel);
	
	/**
	 * @return dimension properties of the object
	 */
	public IDimensionProperties getProperties();
	
	/**
	 * @return the DIMID of the planet the object is currently orbiting, -1 if none
	 */
	public int getOrbitingPlanetId();
	
	/**
	 * @param id the space object id of this object (NOT DIMID)
	 */
	public void setId(int id);
	
	/**
	 * Sets the coords of the space object on the graph
	 * @param posX
	 * @param posY
	 */
	public void setPos(int posX, int posY);
	
	/**
	 * Sets the spawn location for the space object
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setSpawnLocation(int x, int y, int z);
	
	/**
	 * Sets the orbiting planet for the space object but does NOT register it with the planet
	 * @param id
	 */
	public void setOrbitingBody(int id);
	
	/**
	 * @return the spawn location of the object
	 */
	public HashedBlockPosition getSpawnLocation();
	
	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	public boolean hasCustomSpawnLocation();
	
	/**
	 * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
	 * @param chunk
	 */
	public void onModuleUnpack(IStorageChunk chunk);
	
	public void writeToNbt(NBTTagCompound nbt);
	
	public void readFromNbt(NBTTagCompound nbt);
	
	public double getRotation(EnumFacing dir);
	public double getDeltaRotation(EnumFacing dir);
	
	public void setRotation(double rotation, EnumFacing dir);
	
	public double getMaxRotationalAcceleration();
	
	public void setDeltaRotation(double rotation, EnumFacing dir);
	
	/**
	 * @return true if there is an empty pad to land on
	 */
	public boolean hasFreeLandingPad();
	
	/**
	 * @return next viable place to land
	 */
	public HashedBlockPosition getNextLandingPad(boolean commit);
	
	/**
	 * Adds a landing pad to the station
	 * @param x
	 * @param z
	 * @param name the name of the landing pad
	 */
	public void addLandingPad(int x, int z, String name);
	
	/**
	 * Removes an existing landing pad from the station
	 * @param x
	 * @param z
	 */
	public void removeLandingPad(int x, int z);

	/**
	 * @param x
	 * @param z
	 * @param full true if the pad is avalible to use
	 */
	public void setPadStatus(int posX, int posZ, boolean full);
	
	/**
	 * Called when a time is given between dim transitions (warpships mostly)
	 * @param time time in ticks
	 */
	public void beginTransition(long time);
	
	/**
	 * Returns total world time for when the transition is due to complete
	 * @return
	 */
	public long getTransitionTime();

	/**
	 * Set the destination dim id if a jump were to be made
	 * @param id
	 */
	void setDestOrbitingBody(int id);

	/**
	 * Get the destination dimid of this object
	 * @return
	 */
	int getDestOrbitingBody();

	/**
	 * Set the properties of the dimension
	 * @param properties
	 */
	public void setProperties(IDimensionProperties properties);
	
	/**
	 * Called when a check for a cleanup is performed on objects registered as temporary
	 * @return worldtime expiration is to occur
	 */
	public long getExpireTime();

	/**
	 * @return
	 */
	public EnumFacing getForwardDirection();
}
