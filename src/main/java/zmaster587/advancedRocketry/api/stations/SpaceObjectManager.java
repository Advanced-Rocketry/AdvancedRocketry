package zmaster587.advancedRocketry.api.stations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.libVulpes.util.BlockPosition;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;

public class SpaceObjectManager {
	private int nextId = 1;
	public static final int WARPDIMID = -1;
	private long nextStationTransitionTick = -1;
	//station ids to object
	HashMap<Integer,ISpaceObject> stationLocations;
	//Map of planet IDs to station Ids
	HashMap<Integer, List<ISpaceObject>> spaceStationOrbitMap;
	HashMap<String, Class> nameToClass;
	HashMap<Class, String> classToString;
	
	private final static SpaceObjectManager spaceObjectManager = new SpaceObjectManager();

	private SpaceObjectManager() {
		stationLocations = new HashMap<Integer,ISpaceObject>();
		spaceStationOrbitMap = new HashMap<Integer, List<ISpaceObject>>();
		nameToClass = new HashMap<String, Class>();
		classToString = new HashMap<Class, String>();
	}

	/**
	 * The {@link SpaceObjectManager} is used for tasks such as managing space stations and orbiting worlds 
	 * @return the {@link SpaceObjectManager} registered with the DimensionManager
	 */
	public final static SpaceObjectManager getSpaceManager() {
		return spaceObjectManager;
	}
	
	/**
	 * @param id
	 * @return {@link SpaceObject} object registered to this spaceObject id, or null if doesn't exist
	 */
	public ISpaceObject getSpaceStation(int id) {
		return stationLocations.get(id);
	}

	public Collection<ISpaceObject> getSpaceObjects() {
		return stationLocations.values();
	}

	/**
	 * @return the next valid space object id and increments the value for the next one
	 */
	public int getNextStationId() {
		return nextId++;
	}

	/**
	 * Registers the spaceobject class with this manager, this must be done or the object cannot be saved!
	 * @param str key with which to register the spaceObject type
	 * @param clazz class of space object to register
	 */
	public void registerSpaceObjectType(String str, Class<? extends Object> clazz) {
		nameToClass.put(str, clazz);
		classToString.put(clazz, str);
	}

	/**
	 * Gets the object at the location of passed Block x and z
	 * @return Space object occupying the block coords of null if none
	 */
	public ISpaceObject getSpaceStationFromBlockCoords(int x, int z) {

		int radius = Math.max((int)Math.ceil(Math.abs((x/2)/(float)Configuration.stationSize)), (int)Math.ceil(Math.abs((z/2)/(float)Configuration.stationSize)));

		int index;

		if(Math.abs(x/Configuration.stationSize) <= Math.abs(z/Configuration.stationSize)) {
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

	/**
	 * Registers a space object with this manager, the class must have been registered prior to this with registerSpaceObjectType!
	 * @param object
	 * @param dimId
	 * @param stationId
	 */
	public void registerSpaceObject(ISpaceObject object, int dimId, int stationId) {
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
	 * Registers a space station and updates clients
	 * @param object
	 * @param dimId dimension to place it in orbit around, -1 for undefined
	 */
	public void registerSpaceObject(ISpaceObject object, int dimId) {
		registerSpaceObject(object, dimId, getNextStationId());
		PacketHandler.sendToAll(new PacketSpaceStationInfo(object.getId(), (DimensionProperties)object.getProperties()));
	}

	/**
	 * registers a dimension with the given station ID
	 * Used on client to create stations on packet recieve from server
	 * FOR INTERNAL USE ONLY
	 * @param object
	 * @param dimId dimension to place it in orbit around, -1 for undefined
	 */
	@SideOnly(Side.CLIENT)
	public void registerSpaceObjectClient(ISpaceObject object, int dimId, int stationId) {
		registerSpaceObject(object, dimId, stationId);
	}
	
	/**
	 * 
	 * @param planetId id of the planet to get stations around
	 * @return list of spaceObjects around the planet
	 */
	public List<ISpaceObject> getSpaceStationsOrbitingPlanet(int planetId) {
		return spaceStationOrbitMap.get(planetId);
	}

	/*
	 * Event designed to teleport a player to the spawn point for the station if he'she falls out of the world in space
	 * TODO: prevent inf loop if nowhere to fall!
	 */
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(event.player.worldObj.provider.dimensionId == Configuration.spaceDimId) {

			if(event.player.posY < 0 && !event.player.worldObj.isRemote) {
				ISpaceObject object = getSpaceStationFromBlockCoords((int)event.player.posX, (int)event.player.posZ);
				if(object != null) {
	
					BlockPosition loc = object.getSpawnLocation();
	
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
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		long worldTime;
		//Assuming server
		//If no dim undergoing transition then nextTransitionTick = -1
		if(nextStationTransitionTick != -1 && (worldTime = DimensionManager.getWorld(Configuration.spaceDimId).getTotalWorldTime()) >= nextStationTransitionTick && spaceStationOrbitMap.get(WARPDIMID) != null) {
			long newNextTransitionTick = -1;
			for(ISpaceObject obj : spaceStationOrbitMap.get(WARPDIMID)) {
				if(obj.getTransitionTime() <= worldTime)
					moveStationToBody(obj, obj.getDestOrbitingBody());
				else if(newNextTransitionTick == -1 || obj.getTransitionTime() < newNextTransitionTick)
					newNextTransitionTick = obj.getTransitionTime();
			}
			
			nextStationTransitionTick = newNextTransitionTick;
		}
		
	}

	/**
	 * Changes the orbiting body of the space object
	 * @param station
	 * @param dimId
	 */
	public void moveStationToBody(ISpaceObject station, int dimId) {
		//Remove station from the planet it's in orbit around before moving it!
		if(station.getOrbitingPlanetId() != -1 && spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		if(spaceStationOrbitMap.get(dimId) == null)
			spaceStationOrbitMap.put(dimId,new LinkedList<ISpaceObject>());

		if(!spaceStationOrbitMap.get(dimId).contains(station))
			spaceStationOrbitMap.get(dimId).add(station);
		station.setOrbitingBody(dimId);
		
		if(FMLCommonHandler.instance().getSide().isServer()) {
			PacketHandler.sendToAll(new PacketStationUpdate(station, PacketStationUpdate.Type.ORBIT_UPDATE));
		}
		AdvancedRocketry.proxy.fireFogBurst(station);
	}
	
	/**
	 * Changes the orbiting body of the space object
	 * @param station
	 * @param dimId
	 * @param timeDelta time in ticks to fully make the jump
	 */
	public void moveStationToBody(ISpaceObject station, int dimId, int timeDelta) {
		//Remove station from the planet it's in orbit around before moving it!
		if(station.getOrbitingPlanetId() != -1 && spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		if(spaceStationOrbitMap.get(WARPDIMID) == null)
			spaceStationOrbitMap.put(WARPDIMID,new LinkedList<ISpaceObject>());

		if(!spaceStationOrbitMap.get(WARPDIMID).contains(station))
			spaceStationOrbitMap.get(WARPDIMID).add(station);
		station.setOrbitingBody(WARPDIMID);
		
		if(FMLCommonHandler.instance().getSide().isServer()) {
			PacketHandler.sendToAll(new PacketStationUpdate(station, PacketStationUpdate.Type.ORBIT_UPDATE));
		}
		AdvancedRocketry.proxy.fireFogBurst(station);
		
		
		((DimensionProperties)station.getProperties()).atmosphereDensity = 0;
		station.beginTransition(timeDelta + DimensionManager.getWorld(Configuration.spaceDimId).getTotalWorldTime());
		nextStationTransitionTick = timeDelta + DimensionManager.getWorld(Configuration.spaceDimId).getTotalWorldTime();
	}

	public void writeToNBT(NBTTagCompound nbt) {
		Iterator<ISpaceObject> iterator = stationLocations.values().iterator();
		NBTTagList nbtList = new NBTTagList();

		while(iterator.hasNext()) {
			ISpaceObject object = iterator.next();
			NBTTagCompound nbtTag = new NBTTagCompound();
			object.writeToNbt(nbtTag);
			nbtTag.setString("type", classToString.get(object.getClass()));
			nbtList.appendTag(nbtTag);
		}
		nbt.setTag("spaceContents", nbtList);
		nbt.setInteger("nextInt", nextId);
		nbt.setLong("nextStationTransitionTick", nextStationTransitionTick);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("spaceContents", NBT.TAG_COMPOUND);
		nextId = nbt.getInteger("nextInt");
		nextStationTransitionTick = nbt.getLong("nextStationTransitionTick");
		
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			try {
				ISpaceObject object = (ISpaceObject)nameToClass.get(tag.getString("type")).newInstance();
				object.readFromNbt(tag);
				registerSpaceObject(object, object.getOrbitingPlanetId(), object.getId() );

			} catch (Exception e) {
				System.out.println(tag.getString("type"));
				e.printStackTrace();
			}
		}
	}
}
