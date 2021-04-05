package zmaster587.advancedRocketry.stations;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
import zmaster587.libVulpes.util.ZUtils;

import java.util.*;

public class SpaceObjectManager implements ISpaceObjectManager {
	public static final ResourceLocation WARPDIMID = new ResourceLocation("warp" , "warp");
	private long nextStationTransitionTick = -1;
	//station ids to object
	HashMap<ResourceLocation,ISpaceObject> stationLocations;
	//Map of planet IDs to station Ids
	HashMap<ResourceLocation, List<ISpaceObject>> spaceStationOrbitMap;
	HashMap<ResourceLocation, Long> temporaryDimensions;				//Stores a list of temporary dimensions to time they vanish
	HashMap<ResourceLocation, Integer> temporaryDimensionPlayerNumber;
	HashMap<String, Class> nameToClass;
	HashMap<Class, String> classToString;
	
	public final static String STATION_NAMESPACE = "station";

	private final static SpaceObjectManager spaceObjectManager = new SpaceObjectManager();

	private SpaceObjectManager() {
		stationLocations = new HashMap<ResourceLocation,ISpaceObject>();
		spaceStationOrbitMap = new HashMap<ResourceLocation, List<ISpaceObject>>();
		nameToClass = new HashMap<String, Class>();
		classToString = new HashMap<Class, String>();
		temporaryDimensions = new HashMap<ResourceLocation, Long>();
		AdvancedRocketryAPI.spaceObjectManager = this;
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
	 * @return {@link SpaceStationObject} object registered to this spaceObject id, or null if doesn't exist
	 */
	public ISpaceObject getSpaceStation(ResourceLocation id) {
		return stationLocations.get(id);
	}

	public Collection<ISpaceObject> getSpaceObjects() {
		return stationLocations.values();
	}

	/**
	 * @return the next valid space object id and increments the value for the next one
	 */
	public ResourceLocation getNextStationId() {
		for(int i = 1; i < Integer.MAX_VALUE; i++)
		{
			ResourceLocation newID = new ResourceLocation(STATION_NAMESPACE, String.valueOf(i));
			if(!stationLocations.containsKey(newID))
				return newID;
		}
		return null;
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
	 * Attempts to get a registered SpaceObject
	 * @param id string identifier of the spaceobject
	 * @return a new instance of the spaceobject or null if not registered
	 */
	public ISpaceObject getNewSpaceObjectFromIdentifier(String id) {
		Class clazz = nameToClass.get(id);

		try {
			return (ISpaceObject)clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getItentifierFromClass(Class<? extends ISpaceObject> clazz) {
		return classToString.get(clazz);
	}

	/**
	 * Gets the object at the location of passed Block x and z
	 * @return Space object occupying the block coords of null if none
	 */
	public ISpaceObject getSpaceStationFromBlockCoords(BlockPos pos) {

		int x = pos.getX(); int z = pos.getZ();
		x = (int) Math.round((x)/(2f*ARConfiguration.getCurrentConfig().stationSize.get()));
		z = (int) Math.round((z)/(2f*ARConfiguration.getCurrentConfig().stationSize.get()));
		int radius = Math.max(Math.abs(x), Math.abs(z));

		int index = (int) Math.pow((2*radius-1),2) + x + radius;
		
		if(Math.abs(z) != radius) {
			index = (int) Math.pow((2*radius-1),2) + z + radius + (4*radius + 2) - 1;
			
			if(x > 0)
				index += 2*radius-1;
		}
		else if(z > 0)
			index += 2*radius+1;
		
		return getSpaceStation(new ResourceLocation(STATION_NAMESPACE, String.valueOf(index)));
	}

	/**
	 * Registers a space object with this manager, the class must have been registered prior to this with registerSpaceObjectType!
	 * @param object
	 * @param dimId
	 * @param stationId
	 */
	public void registerSpaceObject(ISpaceObject object, ResourceLocation dimId, ResourceLocation stationId) {
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
		
		int stationIDAsInt = Integer.parseInt( stationId.getPath());

		int radius = (int) Math.floor(Math.ceil(Math.sqrt(stationIDAsInt+1))/2);
		int ringIndex = (int) (stationIDAsInt-Math.pow((radius*2) - 1,2));
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
		
		if(!object.hasCustomSpawnLocation())
			object.setSpawnLocation(2*ARConfiguration.getCurrentConfig().stationSize.get()*x + ARConfiguration.getCurrentConfig().stationSize.get()/2, 128, 2*ARConfiguration.getCurrentConfig().stationSize.get()*z + ARConfiguration.getCurrentConfig().stationSize.get()/2);

		object.setOrbitingBody(dimId);
		moveStationToBody(object, dimId, false);
	}

	/**
	 * Registers a dimension that is set to expire at a given an expiration time
	 * @param object object to register
	 * @param dimId dimid to orbit around
	 * @param expireTime time at which to expire the dimension
	 */
	public void registerTemporarySpaceObject(ISpaceObject object, ResourceLocation dimId, long expireTime) {
		ResourceLocation nextDimId = getNextStationId();
		temporaryDimensions.put(nextDimId, expireTime);
		temporaryDimensionPlayerNumber.put(dimId, 0);
		registerSpaceObject(object, nextDimId);
	}

	/**
	 * Registers a space station and updates clients
	 * @param object
	 * @param nextDimId dimension to place it in orbit around, Constants.INVALID_PLANET for undefined
	 */
	public void registerSpaceObject(ISpaceObject object, ResourceLocation nextDimId) {
		registerSpaceObject(object, nextDimId, getNextStationId());
		PacketHandler.sendToAll(new PacketSpaceStationInfo(object.getId(), object));
	}
	
	public void unregisterSpaceObject(ResourceLocation id) {
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
	 * @param object
	 * @param dimId dimension to place it in orbit around, Constants.INVALID_PLANET for undefined
	 */
	@OnlyIn(value=Dist.CLIENT)
	public void registerSpaceObjectClient(ISpaceObject object, ResourceLocation dimId, ResourceLocation stationId) {
		registerSpaceObject(object, dimId, stationId);
	}

	/**
	 * 
	 * @param planetId id of the planet to get stations around
	 * @return list of spaceObjects around the planet
	 */
	public List<ISpaceObject> getSpaceStationsOrbitingPlanet(ResourceLocation planetId) {
		return spaceStationOrbitMap.get(planetId);
	}

	/**
	 * Event designed to teleport a player to the spawn point for the station if he'she falls out of the world in space
	 * TODO: prevent inf loop if nowhere to fall!
	 */
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(ARConfiguration.GetSpaceDimId().equals(ZUtils.getDimensionIdentifier(event.player.world))) {

			if(event.player.getPosY() < 0 && !event.player.world.isRemote) {
				ISpaceObject object = getSpaceStationFromBlockCoords(new BlockPos(event.player.getPositionVec()));
				if(object != null) {

					HashedBlockPosition loc = object.getSpawnLocation();

					event.player.fallDistance=0;
					event.player.setMotion(event.player.getMotion().mul(1, 0, 1));
					event.player.setPositionAndUpdate(loc.x, loc.y + 2, loc.z);
					event.player.sendMessage(new StringTextComponent("You wake up finding yourself back on the station"), Util.DUMMY_UUID);
				}
			}

			int result = Math.abs(2*(((int)event.player.getPosZ() + ARConfiguration.getCurrentConfig().stationSize.get()/2) % (2*ARConfiguration.getCurrentConfig().stationSize.get()) )/ARConfiguration.getCurrentConfig().stationSize.get());
			if(result == 0 || result == 3) {
				event.player.setMotion(event.player.getMotion().mul(1, 1, -1));
				if(result == 0) {
					event.player.setPosition(event.player.getPosX(), event.player.getPosY(), event.player.getPosZ() + (event.player.getPosZ() < 0 ? Math.abs(event.player.getPosZ() % 16) : (16 - event.player.getPosZ() % 16)));
				}
				else
					event.player.setPosition(event.player.getPosX(), event.player.getPosY(), event.player.getPosZ() - (event.player.getPosZ() < 0 ? 16 - Math.abs(event.player.getPosZ() % 16) : (event.player.getPosZ() % 16)));

			}

			//double getPosX() = event.player.getPosX() < 0 ? -event.player.getPosX() - Configuration.stationSize : event.player.getPosX();

			result = Math.abs(2*(((int)event.player.getPosX() + ARConfiguration.getCurrentConfig().stationSize.get()/2) % (2*ARConfiguration.getCurrentConfig().stationSize.get()) )/ARConfiguration.getCurrentConfig().stationSize.get());

			if(event.player.getPosX() < -ARConfiguration.getCurrentConfig().stationSize.get()/2)
				if(result == 3)
					result = 0;
				else if(result == 0)
					result = 3;

			if(result == 0 || result == 3) {
				event.player.setMotion(event.player.getMotion().mul(-1, 1, 1));
				if(result == 0) {
					event.player.setPosition(event.player.getPosX() + (event.player.getPosX() < 0 ? Math.abs(event.player.getPosX() % 16) : (16 - event.player.getPosX() % 16)), event.player.getPosY(), event.player.getPosZ());
				}
				else
					event.player.setPosition(event.player.getPosX() - (event.player.getPosX() < 0 ? 16 - Math.abs(event.player.getPosX() % 16) : (event.player.getPosX() % 16)), event.player.getPosY(), event.player.getPosZ());

			}
		}
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(ZUtils.getWorld(ARConfiguration.GetSpaceDimId()) == null)
			return;
		
		long worldTime = ZUtils.getWorld(ARConfiguration.GetSpaceDimId()).getGameTime();
		//Assuming server
		//If no dim undergoing transition then nextTransitionTick = -1
		if((nextStationTransitionTick != -1 && worldTime >= nextStationTransitionTick && spaceStationOrbitMap.get(WARPDIMID) != null) || (nextStationTransitionTick == -1 && spaceStationOrbitMap.get(WARPDIMID) != null && !spaceStationOrbitMap.get(WARPDIMID).isEmpty())) {
			long newNextTransitionTick = -1;
			for(ISpaceObject obj : spaceStationOrbitMap.get(WARPDIMID)) {
				if(obj.getTransitionTime() <= worldTime) {
					moveStationToBody(obj, obj.getDestOrbitingBody());
					spaceStationOrbitMap.get(WARPDIMID).remove(obj);
				}
				else if(newNextTransitionTick == -1 || obj.getTransitionTime() < newNextTransitionTick)
					newNextTransitionTick = obj.getTransitionTime();
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
		
		if(event.toDim == Configuration.spaceDimId && getSpaceStationFromBlockCoords((int)event.player.getPosX(), (int)event.player.getPosZ()) != null &&
				temporaryDimensions.containsKey(getSpaceStationFromBlockCoords((int)event.player.getPosX(), (int)event.player.getPosZ()))) {
			int stationId = getSpaceStationFromBlockCoords((int)event.player.getPosX(), (int)event.player.getPosZ()).getId();
			
			temporaryDimensionPlayerNumber.put(stationId, temporaryDimensionPlayerNumber.get(stationId)+1);
		}
		if(event.fromDim != Configuration.spaceDimId) 
			return;
		
		ISpaceObject spaceObj = getSpaceStationFromBlockCoords((int)event.player.getPosX(), (int)event.player.getPosZ());
		Long expireTime = spaceObj.getExpireTime();
		int numplayers;
		temporaryDimensionPlayerNumber.put(spaceObj.getId(), (numplayers = temporaryDimensionPlayerNumber.get(spaceObj.getId())-1));
		
		if(expireTime == null)
			return;
		
		long worldTime = DimensionManager.getWorld(event.toDim).getGameTime();

		if(expireTime >= worldTime && numplayers == 0) {
			//expired and delete
			unregisterSpaceObject(spaceObj.getId());
		}

	}*/

	public void moveStationToBody(ISpaceObject station, ResourceLocation dimId) {
		moveStationToBody(station, dimId, true);
	}

	/**
	 * Changes the orbiting body of the space object
	 * @param station
	 * @param dimId
	 */
	public void moveStationToBody(ISpaceObject station, ResourceLocation dimId, boolean update) {
		//Remove station from the planet it's in orbit around before moving it!
		if(spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		if(spaceStationOrbitMap.get(dimId) == null)
			spaceStationOrbitMap.put(dimId, new LinkedList<ISpaceObject>());

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
	public void moveStationToBody(ISpaceObject station, ResourceLocation dimId, int timeDelta) {
		//Remove station from the planet it's in orbit around before moving it!
		if(!WARPDIMID.equals(station.getOrbitingPlanetId()) && spaceStationOrbitMap.get(station.getOrbitingPlanetId()) != null) {
			spaceStationOrbitMap.get(station.getOrbitingPlanetId()).remove(station);
		}

		if(spaceStationOrbitMap.get(WARPDIMID) == null)
			spaceStationOrbitMap.put(WARPDIMID,new LinkedList<ISpaceObject>());

		if(!spaceStationOrbitMap.get(WARPDIMID).contains(station))
			spaceStationOrbitMap.get(WARPDIMID).add(station);
		station.setOrbitingBody(WARPDIMID);

		//if(FMLCommonHandler.instance().getSide().isServer()) {
			PacketHandler.sendToAll(new PacketStationUpdate(station, PacketStationUpdate.Type.ORBIT_UPDATE));
		//}
		AdvancedRocketry.proxy.fireFogBurst(station);


		((DimensionProperties)station.getProperties()).setAtmosphereDensityDirect(0);
		nextStationTransitionTick = (int)(ARConfiguration.getCurrentConfig().travelTimeMultiplier.get()*timeDelta) + ZUtils.getWorld(ARConfiguration.GetSpaceDimId()).getGameTime();
		station.beginTransition(nextStationTransitionTick);
		
	}

	public void writeToNBT(CompoundNBT nbt) {
		Iterator<ISpaceObject> iterator = stationLocations.values().iterator();
		ListNBT nbtList = new ListNBT();

		while(iterator.hasNext()) {
			ISpaceObject object = iterator.next();
			CompoundNBT nbtTag = new CompoundNBT();
			object.writeToNbt(nbtTag);
			
			nbtTag.putString("type", classToString.get(object.getClass()));
			if(temporaryDimensions.containsKey(object.getId())) {
				nbtTag.putLong("expireTime", temporaryDimensions.get(object.getId()));
				nbtTag.putInt("numPlayers", temporaryDimensionPlayerNumber.get(object.getId()));
			}
			
			nbtList.add(nbtTag);
		}
		
		
		nbt.put("spaceContents", nbtList);
		nbt.putLong("nextStationTransitionTick", nextStationTransitionTick);
	}

	public void readFromNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("spaceContents", NBT.TAG_COMPOUND);
		nextStationTransitionTick = nbt.getLong("nextStationTransitionTick");

		for(int i = 0; i < list.size(); i++) {
			CompoundNBT tag = list.getCompound(i);
			try {
				ISpaceObject object = (ISpaceObject)nameToClass.get(tag.getString("type")).newInstance();
				object.readFromNbt(tag);
				
				
				if(tag.contains("expireTime")) {
					long expireTime = tag.getLong("expireTime");
					int numPlayers = tag.getInt("numPlayers");
					if (ZUtils.getWorld(ARConfiguration.GetSpaceDimId()).getGameTime() >= expireTime && numPlayers == 0)
						continue;
					temporaryDimensions.put(object.getId(), expireTime);
					temporaryDimensionPlayerNumber.put(object.getId(), numPlayers);
				}
				
				registerSpaceObject(object, object.getOrbitingPlanetId(), object.getId() );

			} catch (Exception e) {
				System.out.println(tag.getString("type"));
				e.printStackTrace();
			}
		}
	}
}
