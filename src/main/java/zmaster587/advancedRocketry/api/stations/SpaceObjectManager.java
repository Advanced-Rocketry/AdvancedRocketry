package zmaster587.advancedRocketry.api.stations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.libVulpes.util.Vector3F;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.Constants.NBT;

public class SpaceObjectManager {
	private int nextId = 1;

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

	/**
	 * Gets the object at the location of passed Block x and z
	 * @return Space object occupying the block coords of null if none
	 */
	public SpaceObject getSpaceStationFromBlockCoords(int x, int z) {

		int radius = Math.max((int)Math.ceil(Math.abs((x/2)/(float)Configuration.stationSize)), (int)Math.ceil(Math.abs((z/2)/(float)Configuration.stationSize)));

		int index;

		if(Math.abs(x) <= Math.abs(z)) {
			if(z < 0)
				index = (int)Math.pow(2*radius-1,2) + radius +(x/Configuration.stationSize);
			else
				index = (int)Math.pow(2*radius-1,2) + radius + (x/Configuration.stationSize) + (radius*2 + 1);
		}
		else {
			if(x < 0)
				index = (int)Math.pow(2*radius-1,2) + radius - 1 + (radius*2 + 1)*2 + (z/Configuration.stationSize);
			else
				index = (int)Math.pow(2*radius-1,2) + (3*radius) - 2 + (radius*2 + 1)*2 + (z/Configuration.stationSize);
		}

		return getSpaceStation(index);
	}

	public void registerSpaceObject(SpaceObject object, int dimId, int stationId) {
		object.setId(stationId);
		stationLocations.put(stationId, object);


		/*Calculate the location of a space station along a square spiral
		 * here the top and bottom(including the corner locations) are filled first then the left and right last
		 * 
		 * Example shown below:
		 *9 A B C D
		 *  1 2 3
		 *  7 0 8
		 *  4 5 6
		 *E F.....
		 */

		int radius = (int) Math.floor(Math.ceil(Math.sqrt(stationId+1))/2);
		int ringIndex = (int) (stationId-Math.pow((radius*2) - 1,2));
		int x,z;

		if(ringIndex < (radius*2 + 1)*2) {
			x = ringIndex % (radius*2 + 1) - radius;
			if(ringIndex < (radius*2 + 1))
				z = -radius;
			else
				z = radius;
		}
		else {
			int newIndex = ringIndex - (radius*2 + 1)*2;
			z = newIndex % ((radius-1)*2 + 1) - (radius - 1);
			if(newIndex < ((radius-1)*2 + 1))
				x = -radius;
			else
				x = radius;
		}

		object.setPos(2*x, 2*z);
		if(!object.hasCustomSpawnLocation())
			object.setSpawnLocation(2*Configuration.stationSize*x + Configuration.stationSize/2, 128, 2*Configuration.stationSize*z + Configuration.stationSize/2);

		object.setOrbitingBody(dimId);
		moveStationToBody(object, dimId);
	}

	/**
	 * 
	 * @param object
	 * @param dimId dimension to place it in orbit around, -1 for undefined
	 */
	public void registerSpaceObject(SpaceObject object, int dimId) {
		registerSpaceObject(object, dimId, getNextStationId());
	}

	/**
	 * 
	 * @param planetId id of the planet to get stations around
	 * @return list of spaceObjects around the planet
	 */
	public List<SpaceObject> getSpaceStationsOrbitingPlanet(int planetId) {
		return spaceStationOrbitMap.get(planetId);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(event.player.worldObj.provider.dimensionId == Configuration.spaceDimId) {

			if(event.player.posY < 0 && !event.player.worldObj.isRemote) {
				SpaceObject object = getSpaceStationFromBlockCoords((int)event.player.posX, (int)event.player.posZ);
				if(object != null) {
	
					Vector3F<Integer> loc = object.getSpawnLocation();
	
					event.player.fallDistance=0;
					event.player.motionY = 0;
					event.player.setPositionAndUpdate(loc.x, loc.y, loc.z);
					event.player.addChatComponentMessage(new ChatComponentText("You wake up finding yourself back on the station"));
				}
			}
	
			int result = Math.abs(2*(((int)event.player.posZ + Configuration.stationSize/2) % (2*Configuration.stationSize) )/Configuration.stationSize);
			if(result == 0 || result == 3) {
				event.player.motionZ = -event.player.motionZ;
				if(result == 0) {
					event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ + (event.player.posZ < 0 ? Math.abs(event.player.posZ % 16) : (16 - event.player.posZ % 16)));
				}
				else
					event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ - (event.player.posZ < 0 ? 16 - Math.abs(event.player.posZ % 16) : (event.player.posZ % 16)));
	
			}
	
			//double posX = event.player.posX < 0 ? -event.player.posX - Configuration.stationSize : event.player.posX;
	
			result = Math.abs(2*(((int)event.player.posX + Configuration.stationSize/2) % (2*Configuration.stationSize) )/Configuration.stationSize);
	
			if(event.player.posX < -Configuration.stationSize/2)
				if(result == 3)
					result = 0;
				else if(result == 0)
					result = 3;
	
			if(result == 0 || result == 3) {
				event.player.motionX = -event.player.motionX;
				if(result == 0) {
					event.player.setPosition(event.player.posX + (event.player.posX < 0 ? Math.abs(event.player.posX % 16) : (16 - event.player.posX % 16)), event.player.posY, event.player.posZ);
				}
				else
					event.player.setPosition(event.player.posX - (event.player.posX < 0 ? 16 - Math.abs(event.player.posX % 16) : (event.player.posX % 16)), event.player.posY, event.player.posZ);
	
			}
		}
	}

	/**
	 * 
	 * @param station
	 * @param dimId
	 */
	public void moveStationToBody(SpaceObject station, int dimId) {
		//Remove station from the planet it's in orbit around before moving it!
		if(station.getOrbitingPlanetId() != -1 && spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		if(spaceStationOrbitMap.get(dimId) == null)
			spaceStationOrbitMap.put(dimId,new LinkedList<SpaceObject>());

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
			nbtTag.setString("type", classToString.get(object.getClass()));
			nbtList.appendTag(nbtTag);
		}
		nbt.setTag("spaceContents", nbtList);
		nbt.setInteger("nextInt", nextId);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("spaceContents", NBT.TAG_COMPOUND);
		nextId = nbt.getInteger("nextInt");

		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			try {
				SpaceObject object = (SpaceObject)nameToClass.get(tag.getString("type")).newInstance();
				object.readFromNbt(tag);
				registerSpaceObject(object, object.getOrbitingPlanetId(), object.getId() );

			} catch (Exception e) {
				System.out.println(tag.getString("type"));
				e.printStackTrace();
			}
		}
	}
}
