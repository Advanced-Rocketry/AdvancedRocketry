package zmaster587.advancedRocketry.api.stations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;

public class SpaceObject {
	private int posX, posY;
	private int altitude;
	private BlockPosition spawnLocation;
	private List<BlockPosition> spawnLocations;
	private HashMap<BlockPosition,Boolean> occupiedLandingPads;
	DimensionProperties properties;

	public SpaceObject() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.api.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
		spawnLocations = new ArrayList<BlockPosition>();
		occupiedLandingPads = new HashMap<BlockPosition,Boolean>();
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
	public BlockPosition getSpawnLocation() {
		return spawnLocation;
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
	 * @param empty true if the pad is avalible to use
	 */
	public void setPadStatus(int x, int z, boolean full) {
		BlockPosition pos = new BlockPosition(x, 0, z);
		if(occupiedLandingPads.containsKey(pos))
			occupiedLandingPads.put(pos, full);
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
		spawnLocation = new BlockPosition(x,y,z);
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
		
		NBTTagList list = new NBTTagList();
		for(BlockPosition pos : this.spawnLocations) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("occupied", occupiedLandingPads.get(pos));
			tag.setIntArray("pos", new int[] {pos.x, pos.z});
			list.appendTag(tag);
		}
		
		nbt.setTag("spawnPositions", list);
	}

	public void readFromNbt(NBTTagCompound nbt) {
		properties.readFromNBT(nbt);
		posX = nbt.getInteger("posX");
		posY = nbt.getInteger("posY");
		altitude = nbt.getInteger("altitude");
		spawnLocation = new BlockPosition(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		properties.setId(nbt.getInteger("id"));
		
		
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
	}

	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	public boolean hasCustomSpawnLocation() {
		return false;
	}
}
