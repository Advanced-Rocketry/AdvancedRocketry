package zmaster587.advancedRocketry.api.stations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class SpaceObjectManager {
	private final int stationSize = 512;
	private int nextId = 0;

	//station ids to object
	HashMap<Integer,SpaceObject> stationLocations;
	//Map of planet IDs to station Ids
	HashMap<Integer, List<SpaceObject>> spaceStationOrbitMap;
	HashMap<String, Class> nameToClass;
	HashMap<Class, String> classToString;

	public SpaceObjectManager() {
		stationLocations = new HashMap<Integer,SpaceObject>();
		spaceStationOrbitMap = new HashMap<Integer, List<SpaceObject>>();
		nameToClass = new HashMap<String, Class>();
		classToString = new HashMap<Class, String>();
		
		registerSpaceObjectType("genericObject", SpaceObject.class);
	}

	public SpaceObject getSpaceStation(int id) {
		return stationLocations.get(id);
	}

	public int getNextStationId() {
		return nextId++;
	}

	public void registerSpaceObjectType(String str, Class<? extends SpaceObject> clazz) {
		nameToClass.put(str, clazz);
		classToString.put(clazz, str);
	}

	private void registerSpaceObject(SpaceObject object, int dimId, int stationId) {
		object.setId(stationId);
		stationLocations.put(stationId, object);
		object.setPos(2*stationSize*object.getId(), 0);
		if(!object.hasCustomSpawnLocation())
			object.setSpawnLocation(2*stationSize*object.getId() + stationSize/2, 128, stationSize/2);

		object.setOrbitingBody(dimId);
		moveStationToBody(object, dimId);
	}

	public void registerSpaceObject(SpaceObject object, int dimId) {
		registerSpaceObject(object, dimId, getNextStationId());
	}

	public List<SpaceObject> getSpaceStationsOrbitingPlanet(int planetId) {
		return spaceStationOrbitMap.get(planetId);
	}

	public void moveStationToBody(SpaceObject station, int dimId) {
		//Remove station from the planet it's in orbit around before moving it!
		if(station.getOrbitingPlanetId() != -1) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		if(!spaceStationOrbitMap.get(dimId).contains(station))
			spaceStationOrbitMap.get(dimId).add(station);
		station.setOrbitingBody(dimId);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		Iterator<SpaceObject> iterator = stationLocations.values().iterator();
		NBTTagList nbtList = new NBTTagList();

		while(iterator.hasNext()) {
			SpaceObject object = iterator.next();
			NBTTagCompound nbtTag = new NBTTagCompound();
			object.writeToNbt(nbtTag);
			nbtTag.setString("type", classToString.get(object));
			nbtList.appendTag(nbtTag);
		}
		nbt.setTag("spaceContents", nbtList);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("spaceContents", NBT.TAG_COMPOUND);

		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			try {
				SpaceObject object = (SpaceObject)nameToClass.get(tag.getString("type")).newInstance();
				object.readFromNbt(tag);
				registerSpaceObject(object, object.getOrbitingPlanetId(), object.getId());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
