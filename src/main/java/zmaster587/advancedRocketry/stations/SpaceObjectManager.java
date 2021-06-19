package zmaster587.advancedRocketry.stations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.ISpaceObjectManager;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import javax.annotation.Nonnull;
import java.util.*;

public class SpaceObjectManager implements ISpaceObjectManager {
	private int nextId = 1;
	public static final int WARPDIMID = Integer.MIN_VALUE;
	private long nextStationTransitionTick = -1;
	//station ids to object
	private HashMap<Integer,ISpaceObject> stationLocations;
	//Map of planet IDs to station Ids
	private HashMap<Integer, List<ISpaceObject>> spaceStationOrbitMap;
	private HashMap<Integer, Long> temporaryDimensions;				//Stores a list of temporary dimensions to time they vanish
	private HashMap<Integer, Integer> temporaryDimensionPlayerNumber;
	private HashMap<String, Class> nameToClass;
	private HashMap<Class, String> classToString;

	private final static SpaceObjectManager spaceObjectManager = new SpaceObjectManager();

	private SpaceObjectManager() {
		stationLocations = new HashMap<>();
		spaceStationOrbitMap = new HashMap<>();
		nameToClass = new HashMap<>();
		classToString = new HashMap<>();
		temporaryDimensions = new HashMap<>();
		AdvancedRocketryAPI.spaceObjectManager = this;
	}

	/**
	 * The {@link SpaceObjectManager} is used for tasks such as managing space stations and orbiting worlds 
	 * @return the {@link SpaceObjectManager} registered with the DimensionManager
	 */
	public static SpaceObjectManager getSpaceManager() {
		return spaceObjectManager;
	}

	/**
	 * @param id
	 * @return {@link SpaceStationObject} object registered to this spaceObject id, or null if doesn't exist
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
		for(int i = 1; i < Integer.MAX_VALUE; i++)
			if(!stationLocations.containsKey(i))
				return i;
		return Integer.MAX_VALUE;
	}

	/**
	 * Registers the spaceobject class with this manager, this must be done or the object cannot be saved!
	 * @param str key with which to register the spaceObject type
	 * @param clazz class of space object to register
	 */
	public void registerSpaceObjectType(String str, Class<?> clazz) {
		nameToClass.put(str, clazz);
		classToString.put(clazz, str);
	}

	/**
	 * Attempts to get a registered SpaceObject
	 * @param id string identifier of the spaceobject
	 * @return a new instance of the spaceobject or null if not registered
	 */
	public ISpaceObject getNewSpaceObjectFromIdentifier(String id) {
		Class clazz = nameToClass.get(id);

		try {
			return (ISpaceObject)clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getIdentifierFromClass(Class<? extends ISpaceObject> clazz) {
		return classToString.get(clazz);
	}

	/**
	 * Gets the object at the location of passed Block x and z
	 * @return Space object occupying the block coords of null if none
	 */
	public ISpaceObject getSpaceStationFromBlockCoords(@Nonnull BlockPos pos) {

		int x = pos.getX(); int z = pos.getZ();
		x = Math.round((x)/(2f*ARConfiguration.getCurrentConfig().stationSize));
		z = Math.round((z)/(2f*ARConfiguration.getCurrentConfig().stationSize));
		int radius = Math.max(Math.abs(x), Math.abs(z));

		int index = (int) Math.pow((2*radius-1),2) + x + radius;
		
		if(Math.abs(z) != radius) {
			index = (int) Math.pow((2*radius-1),2) + z + radius + (4*radius + 2) - 1;
			
			if(x > 0)
				index += 2*radius-1;
		}
		else if(z > 0)
			index += 2*radius+1;
		
		return getSpaceStation(index);
	}

	/**
	 * Registers a space object with this manager, the class must have been registered prior to this with registerSpaceObjectType!
	 * @param spaceObject
	 * @param dimId
	 * @param stationId
	 */
	public void registerSpaceObject(@Nonnull ISpaceObject spaceObject, int dimId, int stationId) {
		spaceObject.setId(stationId);
		stationLocations.put(stationId, spaceObject);


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
		
		if(!spaceObject.hasCustomSpawnLocation())
			spaceObject.setSpawnLocation(2*ARConfiguration.getCurrentConfig().stationSize*x + ARConfiguration.getCurrentConfig().stationSize/2, 128, 2*ARConfiguration.getCurrentConfig().stationSize*z + ARConfiguration.getCurrentConfig().stationSize/2);

		spaceObject.setOrbitingBody(dimId);
		moveStationToBody(spaceObject, dimId, false);
	}

	/**
	 * Registers a dimension that is set to expire at a given an expiration time
	 * @param spaceObject spaceObject to register
	 * @param dimId dimid to orbit around
	 * @param expireTime time at which to expire the dimension
	 */
	public void registerTemporarySpaceObject(@Nonnull ISpaceObject spaceObject, int dimId, long expireTime) {
		int nextDimId = getNextStationId();
		temporaryDimensions.put(nextDimId, expireTime);
		temporaryDimensionPlayerNumber.put(dimId, 0);
		registerSpaceObject(spaceObject, nextDimId);
	}

	/**
	 * Registers a space station and updates clients
	 * @param spaceObject
	 * @param dimId dimension to place it in orbit around, Constants.INVALID_PLANET for undefined
	 */
	public void registerSpaceObject(@Nonnull ISpaceObject spaceObject, int dimId) {
		registerSpaceObject(spaceObject, dimId, getNextStationId());
		PacketHandler.sendToAll(new PacketSpaceStationInfo(spaceObject.getId(), spaceObject));
	}
	
	public void unregisterSpaceObject(int id) {
		temporaryDimensions.remove(id);
		temporaryDimensionPlayerNumber.remove(id);
		spaceStationOrbitMap.remove(id);
		stationLocations.remove(id);
		PacketHandler.sendToAll(new PacketSpaceStationInfo(id, null));
	}

	/**
	 * registers a dimension with the given station ID
	 * Used on client to create stations on packet recieve from server
	 * FOR INTERNAL USE ONLY
	 * @param spaceObject
	 * @param dimId dimension to place it in orbit around, Constants.INVALID_PLANET for undefined
	 */
	@SideOnly(Side.CLIENT)
	public void registerSpaceObjectClient(@Nonnull ISpaceObject spaceObject, int dimId, int stationId) {
		registerSpaceObject(spaceObject, dimId, stationId);
	}

	/**
	 * 
	 * @param planetId id of the planet to get stations around
	 * @return list of spaceObjects around the planet
	 */
	public List<ISpaceObject> getSpaceStationsOrbitingPlanet(int planetId) {
		return spaceStationOrbitMap.get(planetId);
	}

	/**
	 * Event designed to teleport a player to the spawn point for the station if he'she falls out of the world in space
	 * TODO: prevent inf loop if nowhere to fall!
	 */
	@SubscribeEvent
	public void onPlayerTick(@Nonnull PlayerTickEvent event) {
		if(event.player.world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {

			if(event.player.posY < 0 && !event.player.world.isRemote) {
				ISpaceObject spaceObject = getSpaceStationFromBlockCoords(event.player.getPosition());
				if(spaceObject != null) {

					HashedBlockPosition loc = spaceObject.getSpawnLocation();

					event.player.fallDistance=0;
					event.player.motionY = 0;
					event.player.setPositionAndUpdate(loc.x, loc.y + 2, loc.z);
					event.player.sendMessage(new TextComponentString("You wake up finding yourself back on the station"));
				}
			}

			int result = Math.abs(2*(((int)event.player.posZ + ARConfiguration.getCurrentConfig().stationSize/2) % (2*ARConfiguration.getCurrentConfig().stationSize) )/ARConfiguration.getCurrentConfig().stationSize);
			if(result == 0 || result == 3) {
				event.player.motionZ = -event.player.motionZ;
				if(result == 0) {
					event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ + (event.player.posZ < 0 ? Math.abs(event.player.posZ % 16) : (16 - event.player.posZ % 16)));
				}
				else
					event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ - (event.player.posZ < 0 ? 16 - Math.abs(event.player.posZ % 16) : (event.player.posZ % 16)));

			}

			//double posX = event.player.posX < 0 ? -event.player.posX - Configuration.stationSize : event.player.posX;

			result = Math.abs(2*(((int)event.player.posX + ARConfiguration.getCurrentConfig().stationSize/2) % (2*ARConfiguration.getCurrentConfig().stationSize) )/ARConfiguration.getCurrentConfig().stationSize);

			if(event.player.posX < -ARConfiguration.getCurrentConfig().stationSize/2.)
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
		if(DimensionManager.getWorld(ARConfiguration.getCurrentConfig().spaceDimId) == null)
			return;
		
		long worldTime = DimensionManager.getWorld(ARConfiguration.getCurrentConfig().spaceDimId).getTotalWorldTime();
		//Assuming server
		//If no dim undergoing transition then nextTransitionTick = -1
		if((nextStationTransitionTick != -1 && worldTime >= nextStationTransitionTick && spaceStationOrbitMap.get(WARPDIMID) != null) || (nextStationTransitionTick == -1 && spaceStationOrbitMap.get(WARPDIMID) != null && !spaceStationOrbitMap.get(WARPDIMID).isEmpty())) {
			long newNextTransitionTick = -1;
			for(ISpaceObject spaceObject : spaceStationOrbitMap.get(WARPDIMID)) {
				if(spaceObject.getTransitionTime() <= worldTime) {
					moveStationToBody(spaceObject, spaceObject.getDestOrbitingBody());
					spaceStationOrbitMap.get(WARPDIMID).remove(spaceObject);
				}
				else if(newNextTransitionTick == -1 || spaceObject.getTransitionTime() < newNextTransitionTick)
					newNextTransitionTick = spaceObject.getTransitionTime();
			}

			nextStationTransitionTick = newNextTransitionTick;
		}

	}
	
	public void onServerStopped() {
		stationLocations.clear();
		spaceStationOrbitMap.clear();
		temporaryDimensions.clear();
		nextStationTransitionTick = -1;
	}
	
	/*@SubscribeEvent
	public void onPlayerTransition(PlayerEvent.PlayerChangedDimensionEvent event) {
		
		if(event.toDim == Configuration.spaceDimId && getSpaceStationFromBlockCoords((int)event.player.posX, (int)event.player.posZ) != null &&
				temporaryDimensions.containsKey(getSpaceStationFromBlockCoords((int)event.player.posX, (int)event.player.posZ))) {
			int stationId = getSpaceStationFromBlockCoords((int)event.player.posX, (int)event.player.posZ).getId();
			
			temporaryDimensionPlayerNumber.put(stationId, temporaryDimensionPlayerNumber.get(stationId)+1);
		}
		if(event.fromDim != Configuration.spaceDimId) 
			return;
		
		ISpaceObject spaceObj = getSpaceStationFromBlockCoords((int)event.player.posX, (int)event.player.posZ);
		Long expireTime = spaceObj.getExpireTime();
		int numplayers;
		temporaryDimensionPlayerNumber.put(spaceObj.getId(), (numplayers = temporaryDimensionPlayerNumber.get(spaceObj.getId())-1));
		
		if(expireTime == null)
			return;
		
		long worldTime = DimensionManager.getWorld(event.toDim).getTotalWorldTime();

		if(expireTime >= worldTime && numplayers == 0) {
			//expired and delete
			unregisterSpaceObject(spaceObj.getId());
		}

	}*/

	public void moveStationToBody(@Nonnull ISpaceObject station, int dimId) {
		moveStationToBody(station, dimId, true);
	}

	/**
	 * Changes the orbiting body of the space object
	 * @param station
	 * @param dimId
	 */
	public void moveStationToBody(@Nonnull ISpaceObject station, int dimId, boolean update) {
		//Remove station from the planet it's in orbit around before moving it!
		if(spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		spaceStationOrbitMap.computeIfAbsent(dimId, k -> new LinkedList<>());

		if(!spaceStationOrbitMap.get(dimId).contains(station))
			spaceStationOrbitMap.get(dimId).add(station);
		station.setOrbitingBody(dimId);

		if(update) {
			//if(FMLCommonHandler.instance().getSide().isServer()) {
				PacketHandler.sendToAll(new PacketStationUpdate(station, PacketStationUpdate.Type.ORBIT_UPDATE));
			//}
			AdvancedRocketry.proxy.fireFogBurst(station);
		}
	}

	/**
	 * Changes the orbiting body of the space object
	 * @param station
	 * @param dimId
	 * @param timeDelta time in ticks to fully make the jump
	 */
	public void moveStationToBody(@Nonnull ISpaceObject station, int dimId, int timeDelta) {
		//Remove station from the planet it's in orbit around before moving it!
		if(station.getOrbitingPlanetId() != WARPDIMID && spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		spaceStationOrbitMap.computeIfAbsent(WARPDIMID, k -> new LinkedList<>());

		if(!spaceStationOrbitMap.get(WARPDIMID).contains(station))
			spaceStationOrbitMap.get(WARPDIMID).add(station);
		station.setOrbitingBody(WARPDIMID);

		//if(FMLCommonHandler.instance().getSide().isServer()) {
			PacketHandler.sendToAll(new PacketStationUpdate(station, PacketStationUpdate.Type.ORBIT_UPDATE));
		//}
		AdvancedRocketry.proxy.fireFogBurst(station);


		((DimensionProperties)station.getProperties()).setAtmosphereDensityDirect(0);
		nextStationTransitionTick = (int)(ARConfiguration.getCurrentConfig().travelTimeMultiplier*timeDelta) + DimensionManager.getWorld(ARConfiguration.getCurrentConfig().spaceDimId).getTotalWorldTime();
		station.beginTransition(nextStationTransitionTick);
		
	}

	public void writeToNBT(NBTTagCompound nbt) {
		Iterator<ISpaceObject> iterator = stationLocations.values().iterator();
		NBTTagList nbtList = new NBTTagList();

		while(iterator.hasNext()) {
			ISpaceObject spaceObject = iterator.next();
			NBTTagCompound nbtTag = new NBTTagCompound();
			spaceObject.writeToNbt(nbtTag);
			
			nbtTag.setString("type", classToString.get(spaceObject.getClass()));
			if(temporaryDimensions.containsKey(spaceObject.getId())) {
				nbtTag.setLong("expireTime", temporaryDimensions.get(spaceObject.getId()));
				nbtTag.setInteger("numPlayers", temporaryDimensionPlayerNumber.get(spaceObject.getId()));
			}
			
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
				ISpaceObject spaceObject = (ISpaceObject)nameToClass.get(tag.getString("type")).newInstance();
				spaceObject.readFromNbt(tag);
				
				
				if(tag.hasKey("expireTime")) {
					long expireTime = tag.getLong("expireTime");
					int numPlayers = tag.getInteger("numPlayers");
					if (DimensionManager.getWorld(ARConfiguration.getCurrentConfig().spaceDimId).getTotalWorldTime() >= expireTime && numPlayers == 0)
						continue;
					temporaryDimensions.put(spaceObject.getId(), expireTime);
					temporaryDimensionPlayerNumber.put(spaceObject.getId(), numPlayers);
				}
				
				registerSpaceObject(spaceObject, spaceObject.getOrbitingPlanetId(), spaceObject.getId() );

			} catch (Exception e) {
				System.out.println(tag.getString("type"));
				e.printStackTrace();
			}
		}
	}
}
