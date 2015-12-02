package zmaster587.advancedRocketry.api.stations;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class SpaceObject {
	private int posX, posY;
	private int altitude;
	private Vector3F<Integer> spawnLocation;
	DimensionProperties properties;

	public SpaceObject() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.api.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
	}

	/**
	 * @return id of the space object (NOT the DIMID)
	 */
	public int getId() {
		return properties.getId();
	}

	/**
	 * @return dimension properties of the object
	 */
	public DimensionProperties getProperties() {
		return properties;
	}

	/**
	 * @return the DIMID of the planet the object is currently orbiting, -1 if none
	 */
	public int getOrbitingPlanetId() {
		return properties.getParentPlanet();
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
	public Vector3F<Integer> getSpawnLocation() {
		return spawnLocation;
	}
	/**
	 * @param id the space object id of this object (NOT DIMID)
	 */
	public void setId(int id) {
		properties.setId(id);
	}

	/**
	 * Sets the coords of the space object on the graph
	 * @param posX
	 * @param posY
	 */
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
	public void setSpawnLocation(int x, int y, int z) {
		spawnLocation = new Vector3F<Integer>(x,y,z);
	}

	/**
	 * Sets the orbiting planet for the space object but does NOT register it with the planet
	 * @param id
	 */
	public void setOrbitingBody(int id) {
		properties.setParentPlanet(id, false);
	}

	/**
	 * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
	 * @param chunk
	 */
	public void onFirstCreated(IStorageChunk chunk) {
		World worldObj = DimensionManager.getWorld(Configuration.spaceDimId);
		chunk.pasteInWorld(worldObj, spawnLocation.x - chunk.getSizeX()/2, spawnLocation.y - chunk.getSizeY()/2, spawnLocation.z - chunk.getSizeZ()/2);

	}

	public void writeToNbt(NBTTagCompound nbt) {
		properties.writeToNBT(nbt);
		nbt.setInteger("id", getId());
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setInteger("alitude", altitude);
		nbt.setInteger("spawnX", spawnLocation.x);
		nbt.setInteger("spawnY", spawnLocation.y);
		nbt.setInteger("spawnZ", spawnLocation.z);
	}

	public void readFromNbt(NBTTagCompound nbt) {
		properties.readFromNBT(nbt);
		posX = nbt.getInteger("posX");
		posY = nbt.getInteger("posY");
		altitude = nbt.getInteger("altitude");
		spawnLocation = new Vector3F<Integer>(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		properties.setId(nbt.getInteger("id"));
	}

	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	public boolean hasCustomSpawnLocation() {
		return false;
	}
}
