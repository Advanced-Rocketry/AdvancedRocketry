package zmaster587.advancedRocketry.api.stations;

import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.advancedRocketry.world.decoration.MapGenSpaceStation;
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
		properties = (DimensionProperties) zmaster587.advancedRocketry.world.DimensionManager.defaultSpaceDimensionProperties.clone();
	}

	public int getId() {
		return properties.getId();
	}
	
	public DimensionProperties getProperties() {
		return properties;
	}

	public int getOrbitingPlanetId() {
		return properties.getParentPlanet();
	}

	public int getAltitude() {
		return altitude;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public Vector3F<Integer> getSpawnLocation() {
		return spawnLocation;
	}

	public void setId(int id) {
		properties.setId(id);
	}

	public void setPos(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void setSpawnLocation(int x, int y, int z) {
		spawnLocation = new Vector3F<Integer>(x,y,z);
	}

	public void setOrbitingBody(int id) {
		properties.setParentPlanet(id, false);
	}

	public void onFirstCreated(StorageChunk chunk) {
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

	public boolean hasCustomSpawnLocation() {
		return false;
	}
}
