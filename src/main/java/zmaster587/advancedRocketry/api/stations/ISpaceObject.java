package zmaster587.advancedRocketry.api.stations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.libVulpes.util.HashedBlockPosition;

public interface ISpaceObject {

    /**
     * @return id of the space object (NOT the DIMID)
     */
    int getId();

    /**
     * @param id the space object id of this object (NOT DIMID)
     */
    void setId(int id);

    float getOrbitalDistance();

    void setOrbitalDistance(float finalVel);

    /**
     * @return dimension properties of the object
     */
    IDimensionProperties getProperties();

    /**
     * Set the properties of the dimension
     *
     * @param properties
     */
    void setProperties(IDimensionProperties properties);

    /**
     * @return the DIMID of the planet the object is currently orbiting, Constants.INVALID_PLANET if none
     */
    int getOrbitingPlanetId();

    /**
     * @return if the object is anchored in place by anything
     */
    boolean isAnchored();

    /**
     * Sets if the object is anchored or not
     */
    void setIsAnchored(boolean anchored);

    /**
     * Sets the coords of the space object on the graph
     *
     * @param posX
     * @param posY
     */
    void setPos(int posX, int posY);

    /**
     * Sets the spawn location for the space object
     *
     * @param x
     * @param y
     * @param z
     */
    void setSpawnLocation(int x, int y, int z);

    /**
     * Sets the orbiting planet for the space object but does NOT register it with the planet
     *
     * @param id
     */
    void setOrbitingBody(int id);

    /**
     * @return the spawn location of the object
     */
    HashedBlockPosition getSpawnLocation();

    /**
     * True if the spawn location for this space object is not the default one assigned to it
     *
     * @return
     */
    boolean hasCustomSpawnLocation();

    /**
     * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
     *
     * @param chunk
     */
    void onModuleUnpack(IStorageChunk chunk);

    void writeToNbt(NBTTagCompound nbt);

    void readFromNbt(NBTTagCompound nbt);

    double getRotation(EnumFacing dir);

    double getDeltaRotation(EnumFacing dir);

    void setRotation(double rotation, EnumFacing dir);

    double getMaxRotationalAcceleration();

    void setDeltaRotation(double rotation, EnumFacing dir);

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
     *
     * @param x
     * @param z
     * @param name the name of the landing pad
     */
    void addLandingPad(int x, int z, String name);

    /**
     * Removes an existing landing pad from the station
     *
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
     *
     * @param time time in ticks
     */
    void beginTransition(long time);

    /**
     * Returns total world time for when the transition is due to complete
     *
     * @return
     */
    long getTransitionTime();

    /**
     * Get the destination dimid of this object
     *
     * @return
     */
    int getDestOrbitingBody();

    /**
     * Set the destination dim id if a jump were to be made
     *
     * @param id
     */
    void setDestOrbitingBody(int id);

    /**
     * Called when a check for a cleanup is performed on objects registered as temporary
     *
     * @return worldtime expiration is to occur
     */
    long getExpireTime();

    /**
     * @return
     */
    EnumFacing getForwardDirection();
}
