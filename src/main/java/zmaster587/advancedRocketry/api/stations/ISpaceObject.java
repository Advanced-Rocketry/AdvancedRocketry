package zmaster587.advancedRocketry.api.stations;

import net.minecraft.nbt.NBTTagCompound;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.libVulpes.util.BlockPosition;

public interface ISpaceObject {
	
	/**
	 * @return id of the space object (NOT the DIMID)
	 */
	public int getId();
	
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
	public BlockPosition getSpawnLocation();
	
	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	public boolean hasCustomSpawnLocation();
	
	/**
	 * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
	 * @param chunk
	 */
	public void onFirstCreated(IStorageChunk chunk);
	
	public void writeToNbt(NBTTagCompound nbt);
	
	public void readFromNbt(NBTTagCompound nbt);
}
