package zmaster587.advancedRocketry.api.stations;

import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.nbt.NBTTagCompound;

public class SpaceObject {
	private int orbitingPlanetId;
	private int posX, posY;
	private int altitude;
	private int id;
	private Vector3F<Integer> spawnLocation;
	String name;
	
	public SpaceObject() {
		id = -1;
		orbitingPlanetId = -1;
	}
	
	public int getId() {
		return id;
	}
	
	public int getOrbitingPlanetId() {
		return orbitingPlanetId;
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPos(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public void setSpawnLocation(int x, int y, int z) {
		spawnLocation = new Vector3F<Integer>(x,y,z);
	}
	
	public void setOrbitingBody(int id) {
		orbitingPlanetId = id;
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setInteger("id", id);
		nbt.setInteger("orbitingPlanetId", orbitingPlanetId);
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setInteger("alitude", altitude);
		nbt.setInteger("spawnX", spawnLocation.x);
		nbt.setInteger("spawnY", spawnLocation.y);
		nbt.setInteger("spawnZ", spawnLocation.z);
		nbt.setString("name", name);
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		id = nbt.getInteger("id");
		orbitingPlanetId = nbt.getInteger("orbitingPlanetId");
		posX = nbt.getInteger("posX");
		posY = nbt.getInteger("posY");
		altitude = nbt.getInteger("altitude");
		spawnLocation = new Vector3F<Integer>(nbt.getInteger("spawnX"), nbt.getInteger("spawnY"), nbt.getInteger("spawnZ"));
		name = nbt.getString("name");
	}

	public boolean hasCustomSpawnLocation() {
		return false;
	}
}
