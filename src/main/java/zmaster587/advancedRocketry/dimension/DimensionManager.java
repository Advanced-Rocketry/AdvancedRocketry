package zmaster587.advancedRocketry.dimension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketHandler;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldProvider;


public class DimensionManager {

	//TODO: fix satellites not unloading on disconnect
	private Random random;
	private static DimensionManager instance = new DimensionManager();
	public static final String workingPath = "advRocketry";
	public static final String filePath = workingPath + "/temp.dat";
	public static int dimOffset = 0;

	//Reference to the worldProvider for any dimension created through this system, normally WorldProviderPlanet, set in AdvancedRocketry.java in preinit
	public static Class<? extends WorldProvider> planetWorldProvider;
	private HashMap<Integer,DimensionProperties> dimensionList;
	private HashMap<Integer, StellarBody> starList;

	private static long nextSatelliteId;
	private static StellarBody sol;

	//The default properties belonging to the overworld
	public static DimensionProperties overworldProperties;
	//the default property for any dimension created in space, normally, space over earth
	public static DimensionProperties defaultSpaceDimensionProperties;

	public static StellarBody getSol() {
		return sol;
	}

	public static DimensionManager getInstance() {
		return instance;
	};

	public DimensionManager() {
		dimensionList = new HashMap<Integer,DimensionProperties>();
		starList = new HashMap<Integer, StellarBody>();
		sol = new StellarBody();
		sol.setTemperature(100);
		sol.setId(0);
		addStar(sol);

		overworldProperties = new DimensionProperties(0);
		overworldProperties.atmosphereDensity = 100;
		overworldProperties.averageTemperature = 100;
		overworldProperties.gravitationalMultiplier = 1f;
		overworldProperties.orbitalDist = 100;
		overworldProperties.skyColor = new float[] {1f, 1f, 1f};
		overworldProperties.setStar(sol);
		overworldProperties.setName("Earth");

		defaultSpaceDimensionProperties = new DimensionProperties(-1, false);
		defaultSpaceDimensionProperties.atmosphereDensity = 0;
		defaultSpaceDimensionProperties.averageTemperature = 0;
		defaultSpaceDimensionProperties.gravitationalMultiplier = 0.1f;
		defaultSpaceDimensionProperties.orbitalDist = 100;
		defaultSpaceDimensionProperties.skyColor = new float[] {0f,0f,0f};
		defaultSpaceDimensionProperties.setStar(sol);
		defaultSpaceDimensionProperties.setName("Space");
		defaultSpaceDimensionProperties.fogColor = new float[] {0f,0f,0f};
		defaultSpaceDimensionProperties.setParentPlanet(overworldProperties,false);
		defaultSpaceDimensionProperties.orbitalDist = 1;

		random = new Random(System.currentTimeMillis());
	}

	/**
	 * @return an Integer array of dimensions registered with this DimensionManager
	 */
	public Integer[] getregisteredDimensions() {
		Integer ret[] = new Integer[dimensionList.size()];
		return dimensionList.keySet().toArray(ret);
	}

	/**
	 * @return List of dimensions registered with this manager that are currently loaded on the server/integrated server
	 */
	public Integer[] getLoadedDimensions() {
		return getregisteredDimensions();
	}

	/**
	 * Increments the nextAvalible satellite ID and returns one
	 * @return next avalible id for satellites
	 */
	public long getNextSatelliteId() {
		return nextSatelliteId++;
	}

	/**
	 * @param satId long id of the satellite
	 * @return a reference to the satellite object with the supplied ID
	 */
	public SatelliteBase getSatellite(long satId) {
		SatelliteBase satellite = overworldProperties.getSatallite(satId);

		if(satellite != null)
			return satellite;

		for(int i : DimensionManager.getInstance().getLoadedDimensions()) {


			if( (satellite = DimensionManager.getInstance().getDimensionProperties(i).getSatallite(satId)) != null )
				return satellite;
		}
		return null;
	}

	//TODO: fix naming system
	/**
	 * @param dimId id to register the planet with
	 * @return the name for the next planet
	 */
	private String getNextName(int dimId) {
		return "Sol-" + dimId;
	}

	/**
	 * Called every tick to tick satellites
	 */
	public void tickDimensions() {
		//Tick satellites
		overworldProperties.tick();
		for(int i : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionManager.getInstance().getDimensionProperties(i).tick();
		}
	}

	/**
	 * Sets the properies supplied for the supplied dimensionID, if the dimension does not exist, it is added to the list but not registered with minecraft
	 * @param dimId id to set the properties of
	 * @param properties to set for that dimension
	 */
	public void setDimProperties( int dimId, DimensionProperties properties) {
		dimensionList.put(new Integer(dimId),properties);
	}

	/**
	 * Iterates though the list of existing dimIds, and returns the closest free id greater than two
	 * @return next free id
	 */
	public int getNextFreeDim() {
		for(int i = dimOffset; i < 1024; i++) {
			if(!net.minecraftforge.common.DimensionManager.isDimensionRegistered(i))
				return i;
		}
		return 0;
	}

	public DimensionProperties generateRandom(int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(100, 100, 100, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public DimensionProperties generateRandom(String name, int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(name, 100, 100, 100, atmosphereFactor, distanceFactor, gravityFactor);
	}

	/**
	 * Creates and registers a planet with the given properties, Xfactor is the amount of variance from the supplied base property; ie: base - (factor/2) <= generated property value <= base - (factor/2)
	 * @param name name of the planet
	 * @param baseAtmosphere 
	 * @param baseDistance
	 * @param baseGravity
	 * @param atmosphereFactor
	 * @param distanceFactor
	 * @param gravityFactor
	 * @return the new dimension properties created for this planet
	 */
	public DimensionProperties generateRandom(String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties(getNextFreeDim());

		if(name == "")
			properties.setName(getNextName(properties.getId()));
		else {
			properties.setName(name);
		}
		properties.atmosphereDensity = MathHelper.clamp_int(baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2, 0, 200); 
		properties.orbitalDist = MathHelper.clamp_int(baseDistance + random.nextInt(distanceFactor) - distanceFactor/2,0,200);
		properties.gravitationalMultiplier = Math.min(Math.max(0.05f,(baseGravity + random.nextInt(gravityFactor) - gravityFactor/2)/100f), 1.3f);

		double minDistance;

		do {
			minDistance = Double.MAX_VALUE;

			properties.orbitTheta  = random.nextInt(360)*(2f*Math.PI)/360f;

			for(IDimensionProperties properties2 : sol.getPlanets()) {
				double dist = Math.abs(((DimensionProperties)properties2).orbitTheta - properties.orbitTheta);
				if(dist < minDistance)
					minDistance = dist;
			}

		} while(minDistance < (Math.PI/20f));

		//Get Star Color
		properties.setStar(sol);

		//Linear is easier. Earth is nominal!
		properties.averageTemperature = (sol.getTemperature() + (200 - properties.orbitalDist)*10 + properties.atmosphereDensity*18)/20;

		properties.addBiomes(properties.getViableBiomes());

		registerDim(properties, true);
		return properties;
	}


	public DimensionProperties generateRandom(int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom("", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);
	}

	/**
	 * Attempts to register a dimension with {@link DimensionProperties}, if the dimension has not yet been registered, sends a packet containing the dimension information to all connected clients
	 * @param properties {@link DimensionProperties} to register
	 * @return false if the dimension has not been registered, true if it is being newly registered
	 */
	public boolean registerDim(DimensionProperties properties, boolean registerWithForge) {
		boolean bool = registerDimNoUpdate(properties, registerWithForge);

		if(bool)
			PacketHandler.sendToAll(new PacketDimInfo(properties.getId(), properties));
		return bool;
	}

	/**
	 * Attempts to register a dimension without sending an update to the client
	 * @param properties {@link DimensionProperties} to register
	 * @param registerWithForge if true also registers the dimension with forge
	 * @return true if the dimension has NOT been registered before, false if the dimension IS registered exist already
	 */
	public boolean registerDimNoUpdate(DimensionProperties properties, boolean registerWithForge) {
		int dimId = properties.getId();
		Integer dim = new Integer(dimId);

		if(dimensionList.containsKey(dim))
			return false;

		if(registerWithForge && !net.minecraftforge.common.DimensionManager.isDimensionRegistered(dim)) {
			net.minecraftforge.common.DimensionManager.registerProviderType(properties.getId(), DimensionManager.planetWorldProvider, false);
			net.minecraftforge.common.DimensionManager.registerDimension(dimId, dimId);
		}
		dimensionList.put(dimId, properties);

		return true;
	}

	/**
	 * Unregisters all dimensions associated with this DimensionManager from both Minecraft and this DimnensionManager
	 */
	public void unregisterAllDimensions() {
		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {
			if(dimSet.getValue().isNativeDimension) {
				net.minecraftforge.common.DimensionManager.unregisterProviderType(dimSet.getKey());
				net.minecraftforge.common.DimensionManager.unregisterDimension(dimSet.getKey());
			}
		}
		dimensionList.clear();
	}

	/**
	 * Deletes and unregisters the dimensions, as well as all child dimensions, from the game
	 * @param dimId the dimensionId to delete
	 */
	public void deleteDimension(int dimId) {

		DimensionProperties properties = dimensionList.get(dimId);
		properties.getStar().removePlanet(properties);
		if(properties.isMoon()) {
			properties.getParentProperties().removeChild(properties.getId());
		}

		if(properties.hasChildren()) {

			Iterator<Integer> iterator = properties.getChildPlanets().iterator();
			while (iterator.hasNext()){
				Integer child = iterator.next();
				iterator.remove(); //Avoid CME
				deleteDimension(child);

				PacketHandler.sendToAll(new PacketDimInfo(child, null));
			}
		}

		//TODO: check for world loaded
		// If not native to AR let the mod it's registered to handle it
		if(!properties.isNativeDimension) {
			net.minecraftforge.common.DimensionManager.unloadWorld(dimId);
			net.minecraftforge.common.DimensionManager.unregisterProviderType(dimId);
			net.minecraftforge.common.DimensionManager.unregisterDimension(dimId);
			dimensionList.remove(new Integer(dimId));
		}

		//Delete World Folder
		File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), workingPath + "/DIM" + dimId );

		try {
			FileUtils.deleteDirectory(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param dimId id of the dimention of which to get the properties
	 * @return DimensionProperties representing the dimId given
	 */
	public DimensionProperties getDimensionProperties(int dimId) {
		DimensionProperties properties = dimensionList.get(new Integer(dimId));
		if(dimId == Configuration.spaceDimId) {
			return defaultSpaceDimensionProperties;
		}
		return properties == null ? overworldProperties : properties;
	}

	/**
	 * @param id star id for which to get the object
	 * @return the {@link StellarBody} object
	 */
	public StellarBody getStar(int id) {
		return starList.get(new Integer(id));
	}

	/**
	 * @return a list of star ids
	 */
	public Set<Integer> getStars() {
		return starList.keySet();
	}

	/**
	 * Adds a star to the handler
	 * @param star star to add
	 */
	public void addStar(StellarBody star) {
		starList.put(star.getId(), star);
	}

	/**
	 * Removes the star from the handler
	 * @param id id of the star to remove
	 */
	public void removeStar(int id) {
		//TODO: actually remove subPlanets et
		starList.remove(id);
	}

	/**
	 * Saves all dimension data, satellites, and space stations to disk, SHOULD NOT BE CALLED OUTSIDE OF WORLDSAVEEVENT
	 * @param filePath file path to which to save the data
	 */
	public void saveDimensions(String filePath) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound dimListnbt = new NBTTagCompound();


		//Save SolarSystems first
		NBTTagCompound solarSystem = new NBTTagCompound();
		for(Entry<Integer, StellarBody> stars: starList.entrySet()) {
			NBTTagCompound solarNBT = new NBTTagCompound();
			stars.getValue().writeToNBT(solarNBT);
			solarSystem.setTag(stars.getKey().toString(), solarNBT);
		}

		nbt.setTag("starSystems", solarSystem);

		//Save satelliteId
		nbt.setLong("nextSatelliteId", nextSatelliteId);

		//Save Overworld
		NBTTagCompound dimNbt = new NBTTagCompound();
		overworldProperties.writeToNBT(dimNbt);
		dimListnbt.setTag("0", dimNbt);

		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {

			dimNbt = new NBTTagCompound();
			dimSet.getValue().writeToNBT(dimNbt);

			dimListnbt.setTag(dimSet.getKey().toString(), dimNbt);
		}

		nbt.setTag("dimList", dimListnbt);

		NBTTagCompound nbtTag = new NBTTagCompound();
		SpaceObjectManager.getSpaceManager().writeToNBT(nbtTag);
		nbt.setTag("spaceObjects", nbtTag);

		FileOutputStream outStream;
		try {


			File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath);

			if(!file.exists())
				file.createNewFile();

			outStream = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(nbt, outStream);
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dimId integer id of the dimension
	 * @return true if the dimension exists and is registered
	 */
	public boolean isDimensionCreated( int dimId) {
		return dimensionList.containsKey(new Integer(dimId));
	}

	/**
	 * Loads all information to rebuild the galaxy and solar systems from disk into the current instance of DimensionManager
	 * @param filePath file path from which to load the information
	 */
	public boolean loadDimensions(String filePath) {

		FileInputStream inStream;
		NBTTagCompound nbt;
		try {
			File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath);

			if(!file.exists()) {
				new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length())).mkdirs();


				file.createNewFile();
				return false;
			}

			inStream = new FileInputStream(file);
			nbt = CompressedStreamTools.readCompressed(inStream);
			inStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			//TODO: try not to obliterate planets in the future
			e.printStackTrace();
			return false;
		}

		//Load SolarSystems first
		NBTTagCompound solarSystem = nbt.getCompoundTag("starSystems");

		for(Object key : solarSystem.func_150296_c()) {

			NBTTagCompound solarNBT = solarSystem.getCompoundTag((String)key);
			StellarBody star = new StellarBody();
			star.readFromNBT(solarNBT);
			starList.put(star.getId(), star);
		}

		nbt.setTag("starSystems", solarSystem);

		nextSatelliteId = nbt.getLong("nextSatelliteId");

		NBTTagCompound dimListNbt = nbt.getCompoundTag("dimList");


		for(Object key : dimListNbt.func_150296_c()) {
			String keyString = (String)key;
			//Special Handling for overworld
			if(keyString.equals("0")) {
				overworldProperties.readFromNBT(dimListNbt.getCompoundTag(keyString));
			} 
			else {

				DimensionProperties propeties = DimensionProperties.createFromNBT(Integer.parseInt(keyString) ,dimListNbt.getCompoundTag(keyString));

				if(propeties != null) {
					int keyInt = Integer.parseInt(keyString);
					if(!net.minecraftforge.common.DimensionManager.isDimensionRegistered(keyInt) && propeties.isNativeDimension) {
						net.minecraftforge.common.DimensionManager.registerProviderType(keyInt, DimensionManager.planetWorldProvider, false);
						net.minecraftforge.common.DimensionManager.registerDimension(keyInt, keyInt);
						//propeties.isNativeDimension = true;
					}

					dimensionList.put(new Integer(keyInt), propeties);
				}
				else{
					Logger.getLogger("advancedRocketry").warning("Null Dimension Properties Recieved");
				}
				//TODO: print unable to register world
			}
		}

		//Check for tag in case old version of Adv rocketry is in use
		if(nbt.hasKey("spaceObjects")) {
			NBTTagCompound nbtTag = nbt.getCompoundTag("spaceObjects");
			SpaceObjectManager.getSpaceManager().readFromNBT(nbtTag);
		}

		return true;
	}
}
