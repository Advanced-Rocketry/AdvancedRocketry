package zmaster587.advancedRocketry.api.stations;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.libVulpes.util.HashedBlockPosition;

public interface ISpaceObject {
	
	/**
	 * @return id of the space object (NOT the DIMID)
	 */
<<<<<<< HEAD
	public ResourceLocation getId();
=======
	int getId();
>>>>>>> origin/feature/nuclearthermalrockets
	
	float getOrbitalDistance();
	
	void setOrbitalDistance(float finalVel);
	
	/**
	 * @return dimension properties of the object
	 */
	IDimensionProperties getProperties();
	
	/**
	 * @return the DIMID of the planet the object is currently orbiting, Constants.INVALID_PLANET if none
	 */
<<<<<<< HEAD
	public ResourceLocation getOrbitingPlanetId();
=======
	int getOrbitingPlanetId();
>>>>>>> origin/feature/nuclearthermalrockets

	/**
	 * @return if the object is anchored in place by anything
	 */
	boolean isAnchored();

	/**
	 * Sets if the object is anchored or not
	 */
	void setIsAnchored(boolean anchored);
	
	/**
	 * @param id the space object id of this object (NOT DIMID)
	 */
<<<<<<< HEAD
	public void setId(ResourceLocation id);
=======
	void setId(int id);
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * Sets the coords of the space object on the graph
	 * @param posX
	 * @param posY
	 */
	void setPos(int posX, int posY);
	
	/**
	 * Sets the spawn location for the space object
	 * @param x
	 * @param y
	 * @param z
	 */
	void setSpawnLocation(int x, int y, int z);
	
	/**
	 * Sets the orbiting planet for the space object but does NOT register it with the planet
	 * @param id
	 */
<<<<<<< HEAD
	public void setOrbitingBody(ResourceLocation id);
=======
	void setOrbitingBody(int id);
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * @return the spawn location of the object
	 */
	HashedBlockPosition getSpawnLocation();
	
	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	boolean hasCustomSpawnLocation();
	
	/**
	 * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
	 * @param chunk
	 */
	void onModuleUnpack(IStorageChunk chunk);
	
<<<<<<< HEAD
	public void writeToNbt(CompoundNBT nbt);
	
	public void readFromNbt(CompoundNBT nbt);
	
	public double getRotation(Direction dir);
	public double getDeltaRotation(Direction dir);
	
	public void setRotation(double rotation, Direction dir);
	
	public double getMaxRotationalAcceleration();

	public void setDeltaRotation(double rotation, Direction dir);
=======
	void writeToNbt(NBTTagCompound nbt);
	
	void readFromNbt(NBTTagCompound nbt);
	
	double getRotation(EnumFacing dir);
	double getDeltaRotation(EnumFacing dir);
	
	void setRotation(double rotation, EnumFacing dir);
	
	double getMaxRotationalAcceleration();
	
	void setDeltaRotation(double rotation, EnumFacing dir);
>>>>>>> origin/feature/nuclearthermalrockets

	double getInsolationMultiplier();

	/**
	 * @return true if there is an empty pad to land on
	 */
	boolean hasFreeLandingPad();
	
	/**
	 * @return next viable place to land
	 */
	HashedBlockPosition getNextLandingPad(boolean commit);
	
	/**
	 * Adds a landing pad to the station
	 * @param x
	 * @param z
	 * @param name the name of the landing pad
	 */
	void addLandingPad(int x, int z, String name);
	
	/**
	 * Removes an existing landing pad from the station
	 * @param x
	 * @param z
	 */
	void removeLandingPad(int x, int z);

	/**
	 * @param posX
	 * @param posZ
	 * @param full true if the pad is avalible to use
	 */
	void setPadStatus(int posX, int posZ, boolean full);
	
	/**
	 * Called when a time is given between dim transitions (warpships mostly)
	 * @param time time in ticks
	 */
	void beginTransition(long time);
	
	/**
	 * Returns total world time for when the transition is due to complete
	 * @return
	 */
	long getTransitionTime();

	/**
	 * Set the destination dim id if a jump were to be made
	 * @param id
	 */
	void setDestOrbitingBody(ResourceLocation id);

	/**
	 * Get the destination dimid of this object
	 * @return
	 */
	ResourceLocation getDestOrbitingBody();

	/**
	 * Set the properties of the dimension
	 * @param properties
	 */
	void setProperties(IDimensionProperties properties);
	
	/**
	 * Called when a check for a cleanup is performed on objects registered as temporary
	 * @return worldtime expiration is to occur
	 */
	long getExpireTime();

	/**
	 * @return
	 */
<<<<<<< HEAD
	public Direction getForwardDirection();
=======
	EnumFacing getForwardDirection();
>>>>>>> origin/feature/nuclearthermalrockets
}
