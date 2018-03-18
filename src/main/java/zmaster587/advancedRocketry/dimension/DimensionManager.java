package zmaster587.advancedRocketry.dimension;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.IGalaxy;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.XMLPlanetLoader;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.network.PacketHandler;


public class DimensionManager implements IGalaxy {

	//TODO: fix satellites not unloading on disconnect
	private Random random;
	private static DimensionManager instance = (DimensionManager) (AdvancedRocketryAPI.dimensionManager = new DimensionManager());
	public static final String workingPath = "advRocketry";
	public static final String tempFile = "/temp.dat";
	public static final String worldXML = "/planetDefs.xml";
	public static int dimOffset = 0;
	public static final DimensionType PlanetDimensionType = DimensionType.register("planet", "planet", 2, WorldProviderPlanet.class, false);
	public static final DimensionType spaceDimensionType = DimensionType.register("space", "space", 3, WorldProviderSpace.class, false);
	private boolean hasBeenInitiallized = false;
	public static String prevBuild;

	//Stat tracking
	public static boolean hasReachedMoon;
	public static boolean hasReachedWarp;

	//Reference to the worldProvider for any dimension created through this system, normally WorldProviderPlanet, set in AdvancedRocketry.java in preinit
	public static Class<? extends WorldProvider> planetWorldProvider;
	private HashMap<Integer,DimensionProperties> dimensionList;
	private HashMap<Integer, StellarBody> starList;

	public static final int GASGIANT_DIMID_OFFSET = 0x100; //Offset by 256
	private static long nextSatelliteId;
	private static StellarBody sol;
	public Set<Integer> knownPlanets;

	//The default properties belonging to the overworld
	public static DimensionProperties overworldProperties;
	//the default property for any dimension created in space, normally, space over earth
	public static DimensionProperties defaultSpaceDimensionProperties;

	@Deprecated
	public static StellarBody getSol() {
		return instance.getStar(0);
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
		sol.setName("Sol");

		overworldProperties = new DimensionProperties(0);
		overworldProperties.setAtmosphereDensityDirect(100);
		overworldProperties.averageTemperature = 100;
		overworldProperties.gravitationalMultiplier = 1f;
		overworldProperties.orbitalDist = 100;
		overworldProperties.skyColor = new float[] {1f, 1f, 1f};
		overworldProperties.setName("Earth");
		overworldProperties.isNativeDimension = false;

		defaultSpaceDimensionProperties = new DimensionProperties(SpaceObjectManager.WARPDIMID, false);
		defaultSpaceDimensionProperties.setAtmosphereDensityDirect(0);
		defaultSpaceDimensionProperties.averageTemperature = 0;
		defaultSpaceDimensionProperties.gravitationalMultiplier = 0.1f;
		defaultSpaceDimensionProperties.orbitalDist = 100;
		defaultSpaceDimensionProperties.skyColor = new float[] {0f,0f,0f};
		defaultSpaceDimensionProperties.setName("Space");
		defaultSpaceDimensionProperties.fogColor = new float[] {0f,0f,0f};
		//defaultSpaceDimensionProperties.setParentPlanet(overworldProperties,false);
		defaultSpaceDimensionProperties.orbitalDist = 1;

		random = new Random(System.currentTimeMillis());
		knownPlanets = new HashSet<Integer>();
	}

	/**
	 * @return an Integer array of dimensions registered with this DimensionManager
	 */
	public Integer[] getRegisteredDimensions() {
		Integer ret[] = new Integer[dimensionList.size()];
		return dimensionList.keySet().toArray(ret);
	}

	/**
	 * @return List of dimensions registered with this manager that are currently loaded on the server/integrated server
	 */
	public Integer[] getLoadedDimensions() {
		return getRegisteredDimensions();
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

		//Hack to allow monitoring stations to properly reload after a server restart
		//Because there should never be a tile in the world where no planets have been generated load file first
		//Worst thing that can happen is there is no file and it gets genned later and the monitor does not reconnect
		if(!hasBeenInitiallized && FMLCommonHandler.instance().getSide().isServer()) {
			zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.dimension.DimensionManager.workingPath);
		}

		SatelliteBase satellite = overworldProperties.getSatellite(satId);

		if(satellite != null)
			return satellite;

		for(int i : DimensionManager.getInstance().getLoadedDimensions()) {
			if( (satellite = DimensionManager.getInstance().getDimensionProperties(i).getSatellite(satId)) != null )
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

	public void tickDimensionsClient() {
		//Tick satellites
		overworldProperties.updateOrbit();
		for(int i : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionManager.getInstance().getDimensionProperties(i).updateOrbit();
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
	public int getNextFreeDim(int startingValue) {
		for(int i = startingValue; i < 10000; i++) {
			if(!net.minecraftforge.common.DimensionManager.isDimensionRegistered(i) && !dimensionList.containsKey(i))
				return i;
		}
		return -1;
	}

	public int getNextFreeStarId() {
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			if(!starList.containsKey(i))
				return i;
		}
		return -1;
	}
	
	public int getTemperature(StellarBody star, int orbitalDistance, int atmPressure)
	{
		return (star.getTemperature() + (100 - orbitalDistance)*15 + atmPressure*18)/20;
	}
	
	public DimensionProperties generateRandom(int starId, int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(starId, 100, 100, 100, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public DimensionProperties generateRandom(int starId, String name, int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(starId, name, 100, 100, 100, atmosphereFactor, distanceFactor, gravityFactor);
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
	public DimensionProperties generateRandom(int starId, String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties(getNextFreeDim(dimOffset));

		if(properties.getId() == -1)
			return null;

		if(name == "")
			properties.setName(getNextName(properties.getId()));
		else {
			properties.setName(name);
		}
		properties.setAtmosphereDensityDirect(MathHelper.clamp_int(baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2, 0, 200)); 
		int newDist = properties.orbitalDist = MathHelper.clamp_int(baseDistance + random.nextInt(distanceFactor),0,200);

		properties.gravitationalMultiplier = Math.min(Math.max(0.05f,(baseGravity + random.nextInt(gravityFactor) - gravityFactor/2)/100f), 1.3f);

		double minDistance;
		int walkDist = 0;

		do {
			minDistance = Double.MAX_VALUE;

			for(IDimensionProperties properties2 : getStar(starId).getPlanets()) {
				int dist = Math.abs(((DimensionProperties)properties2).orbitalDist - newDist);
				if(minDistance > dist)
					minDistance = dist;
			}

			newDist = properties.orbitalDist + walkDist;
			if(walkDist > -1)
				walkDist = -walkDist - 1;
			else
				walkDist = -walkDist;

		} while(minDistance < 4);

		properties.orbitalDist = newDist;

		properties.orbitalPhi = (random.nextGaussian() -0.5d)*180;
		properties.rotationalPhi = (random.nextGaussian() -0.5d)*180;

		//Get Star Color
		properties.setStar(getStar(starId));

		//Linear is easier. Earth is nominal!
		properties.averageTemperature = getTemperature(properties.getStar(), properties.orbitalDist, properties.getAtmosphereDensity());
		
		if(properties.averageTemperature > 100)
			properties.setOceanBlock(Blocks.LAVA.getDefaultState());
		
		
		if( AtmosphereTypes.getAtmosphereTypeFromValue(properties.getAtmosphereDensity()) == AtmosphereTypes.NONE && random.nextInt() % 5 == 0)
		{
			properties.setOceanBlock(AdvancedRocketryBlocks.blockOxygenFluid.getDefaultState());
			properties.setSeaLevel(random.nextInt(6) + 72);
		}
		
		if(random.nextInt() % 10 == 0)
		{
			properties.setSeaLevel(random.nextInt(40) + 43);
		}

		properties.skyColor[0] *= 1 - MathHelper.clamp_float(random.nextFloat()*0.1f + (70 - properties.averageTemperature)/100f,0.2f,1);
		properties.skyColor[1] *= 1 - (random.nextFloat()*.5f);
		properties.skyColor[2] *= 1 - MathHelper.clamp_float(random.nextFloat()*0.1f + (properties.averageTemperature - 70)/100f,0,1);

		if(random.nextInt() % 50 == 0)
		{
			properties.setHasRings(true);
			properties.ringColor[0] = properties.skyColor[0];
			properties.ringColor[1] = properties.skyColor[1];
			properties.ringColor[2] = properties.skyColor[2];
		}
		
		properties.rotationalPeriod = (int) (Math.pow((1/properties.gravitationalMultiplier),3) * 24000);

		properties.addBiomes(properties.getViableBiomes());

		registerDim(properties, true);
		return properties;
	}


	public DimensionProperties generateRandom(int starId, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(starId, "", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public DimensionProperties generateRandomGasGiant(int starId, String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties(getNextFreeDim(dimOffset));

		if(name == "")
			properties.setName(getNextName(properties.getId()));
		else {
			properties.setName(name);
		}
		properties.setAtmosphereDensityDirect(MathHelper.clamp_int(baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2, 0, 200)); 
		properties.orbitalDist = MathHelper.clamp_int(baseDistance + random.nextInt(distanceFactor),0,200);
		//System.out.println(properties.orbitalDist);
		properties.gravitationalMultiplier = Math.min(Math.max(0.05f,(baseGravity + random.nextInt(gravityFactor) - gravityFactor/2)/100f), 1.3f);

		double minDistance;

		do {
			minDistance = Double.MAX_VALUE;

			properties.orbitTheta  = random.nextInt(360)*(2f*Math.PI)/360f;

			for(IDimensionProperties properties2 : getStar(starId).getPlanets()) {
				double dist = Math.abs(((DimensionProperties)properties2).orbitTheta - properties.orbitTheta);
				if(dist < minDistance)
					minDistance = dist;
			}

		} while(minDistance < (Math.PI/40f));

		//Get Star Color
		properties.setStar(getStar(starId));

		//Linear is easier. Earth is nominal!
		properties.averageTemperature = getTemperature(properties.getStar(), properties.orbitalDist, properties.getAtmosphereDensity());
		properties.setGasGiant();
		//TODO: add gasses
		registerDim(properties, true);
		return properties;
	}

	/**
	 * 
	 * @param dimId dimension id to check
	 * @return true if it can be traveled to, in general if it has a surface
	 */
	public boolean canTravelTo(int dimId){
		return net.minecraftforge.common.DimensionManager.isDimensionRegistered(dimId) && dimId != -1 && !getDimensionProperties(dimId).isGasGiant();
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

		//Avoid registering gas giants as dimensions
		if(registerWithForge && !properties.isGasGiant() && !net.minecraftforge.common.DimensionManager.isDimensionRegistered(dim)) {

			net.minecraftforge.common.DimensionManager.registerDimension(dimId, PlanetDimensionType);
		}
		dimensionList.put(dimId, properties);

		return true;
	}

	/**
	 * Unregisters all dimensions associated with this DimensionManager from both Minecraft and this DimnensionManager
	 */
	public void unregisterAllDimensions() {
		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {
			if(dimSet.getValue().isNativeDimension && !dimSet.getValue().isGasGiant()) {
				net.minecraftforge.common.DimensionManager.unregisterDimension(dimSet.getKey());
			}
		}
		dimensionList.clear();
		starList.clear();
	}

	/**
	 * Deletes and unregisters the dimensions, as well as all child dimensions, from the game
	 * @param dimId the dimensionId to delete
	 */
	public void deleteDimension(int dimId) {

		if(net.minecraftforge.common.DimensionManager.getWorld(dimId) != null) {
			AdvancedRocketry.logger.warn("Cannot delete dimension " + dimId + " it is still loaded");
			return;
		}
		
		DimensionProperties properties = dimensionList.get(dimId);
		
		//Can happen in some rare cases
		if(properties == null)
			return;
		
		if(properties.getStar() != null)
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
		if(properties.isNativeDimension ) {
			if(net.minecraftforge.common.DimensionManager.isDimensionRegistered(dimId)) {
				if(net.minecraftforge.common.DimensionManager.getWorld(dimId) != null)
					net.minecraftforge.common.DimensionManager.unloadWorld(dimId);
				
				net.minecraftforge.common.DimensionManager.unregisterDimension(dimId);
			}
		}
		dimensionList.remove(new Integer(dimId));

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
		if(dimId == Configuration.spaceDimId || dimId == Integer.MIN_VALUE) {
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
	public Set<Integer> getStarIds() {
		return starList.keySet();
	}

	public Collection<StellarBody> getStars() {

		return starList.values();
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
	public void saveDimensions(String filePath) throws Exception {
		
		if(starList.isEmpty() || dimensionList.isEmpty()) {
			throw new Exception("Missing Stars");
		}
		
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

		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {

			dimNbt = new NBTTagCompound();
			dimSet.getValue().writeToNBT(dimNbt);

			dimListnbt.setTag(dimSet.getKey().toString(), dimNbt);
		}

		nbt.setTag("dimList", dimListnbt);

		//Stats
		NBTTagCompound stats = new NBTTagCompound();
		stats.setBoolean("hasReachedMoon", hasReachedMoon);
		stats.setBoolean("hasReachedWarp", hasReachedWarp);
		nbt.setTag("stat", stats);

		NBTTagCompound nbtTag = new NBTTagCompound();
		SpaceObjectManager.getSpaceManager().writeToNBT(nbtTag);
		nbt.setTag("spaceObjects", nbtTag);

		FileOutputStream outStream;
		String xmlOutput = XMLPlanetLoader.writeXML(this);
		
		try {

			File planetXMLOutput = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath + worldXML);
			if(!planetXMLOutput.exists())
				planetXMLOutput.createNewFile();
			PrintWriter bufoutStream = new PrintWriter(planetXMLOutput);
			bufoutStream.write(xmlOutput);
			bufoutStream.close();
			
			File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath + tempFile);

			if(!file.exists())
				file.createNewFile();

			//Getting real sick of my planet file getting toasted during debug...
			File tmpFile = File.createTempFile("dimprops", null);
			outStream = new FileOutputStream(tmpFile);
			try {
				CompressedStreamTools.writeCompressed(nbt, outStream);
				
				//copy the correctly written output
				FileOutputStream properOstream = new FileOutputStream(file);
				FileInputStream inStream = new FileInputStream(tmpFile);
				byte buffer[] = new byte[1024];
				int numRead = 0;
				while((numRead = inStream.read(buffer)) > 0) {
					properOstream.write(buffer, 0, numRead);
				}
				inStream.close();
				properOstream.close();
				tmpFile.delete();
				
			} catch(Exception e) {
				AdvancedRocketry.logger.error("Cannot save advanced rocketry planet file");
				e.printStackTrace();
			}
			
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
		return dimensionList.containsKey(new Integer(dimId)) || dimId == Configuration.spaceDimId;
	}

	/**
	 * Loads all information to rebuild the galaxy and solar systems from disk into the current instance of DimensionManager
	 * @param filePath file path from which to load the information
	 */
	public boolean loadDimensions(String filePath) {
		hasBeenInitiallized = true;

		FileInputStream inStream;
		NBTTagCompound nbt;
		try {
			File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath + tempFile);

			if(!file.exists()) {
				new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length())).mkdirs();


				file.createNewFile();
				return false;
			}

			inStream = new FileInputStream(file);
			nbt = CompressedStreamTools.readCompressed(inStream);
			inStream.close();
		} catch(EOFException e) {
			//Silence you fool!
			//Patch to fix JEI printing when trying to load planets too early
			return false;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			//TODO: try not to obliterate planets in the future
			e.printStackTrace();
			return false;
		}

		//Load SolarSystems first
		NBTTagCompound solarSystem = nbt.getCompoundTag("starSystems");

		if(solarSystem.hasNoTags())
			return false;

		NBTTagCompound stats = nbt.getCompoundTag("stat");
		hasReachedMoon = stats.getBoolean("hasReachedMoon");
		hasReachedWarp = stats.getBoolean("hasReachedWarp");

		for(Object key : solarSystem.getKeySet()) {

			NBTTagCompound solarNBT = solarSystem.getCompoundTag((String)key);
			StellarBody star = new StellarBody();
			star.readFromNBT(solarNBT);
			starList.put(star.getId(), star);
		}

		nbt.setTag("starSystems", solarSystem);

		nextSatelliteId = nbt.getLong("nextSatelliteId");

		NBTTagCompound dimListNbt = nbt.getCompoundTag("dimList");


		for(Object key : dimListNbt.getKeySet()) {
			String keyString = (String)key;


			DimensionProperties propeties = DimensionProperties.createFromNBT(Integer.parseInt(keyString) ,dimListNbt.getCompoundTag(keyString));

			if(propeties != null) {
				int keyInt = Integer.parseInt(keyString);
				if(!net.minecraftforge.common.DimensionManager.isDimensionRegistered(keyInt) && propeties.isNativeDimension && !propeties.isGasGiant()) {
					net.minecraftforge.common.DimensionManager.registerDimension(keyInt, PlanetDimensionType);
					//propeties.isNativeDimension = true;
				}

				dimensionList.put(new Integer(keyInt), propeties);
			}
			else{
				Logger.getLogger("advancedRocketry").warning("Null Dimension Properties Recieved");
			}
			//TODO: print unable to register world
		}


		//Check for tag in case old version of Adv rocketry is in use
		if(nbt.hasKey("spaceObjects")) {
			NBTTagCompound nbtTag = nbt.getCompoundTag("spaceObjects");
			SpaceObjectManager.getSpaceManager().readFromNBT(nbtTag);
		}
		
		//Try to fix invalid objects
		for(ISpaceObject i : SpaceObjectManager.getSpaceManager().getSpaceObjects())
		{
			if(!isDimensionCreated(i.getOrbitingPlanetId()) && i.getOrbitingPlanetId() != 0 && i.getOrbitingPlanetId() != SpaceObjectManager.WARPDIMID)
			{
				AdvancedRocketry.logger.warn("Dimension ID " + i.getOrbitingPlanetId() + " is not registered and a space station is orbiting it, moving to dimid 0");
				i.setOrbitingBody(0);
			}
		}

		prevBuild = nbt.getString("prevVersion");
		nbt.setString("prevVersion", AdvancedRocketry.version);

		return true;
	}

	/**
	 * 
	 * @param destinationDimId
	 * @param dimension
	 * @return true if the two dimensions are in the same planet/moon system
	 */
	public boolean areDimensionsInSamePlanetMoonSystem(int destinationDimId,
			int dimension) {
		//This is a mess, clean up later
		if(dimension == SpaceObjectManager.WARPDIMID || destinationDimId == SpaceObjectManager.WARPDIMID)
			return false;

		DimensionProperties properties = getDimensionProperties(dimension);
		DimensionProperties properties2 = getDimensionProperties(destinationDimId);
		
		while(properties.getParentProperties() != null) properties = properties.getParentProperties();
		while(properties2.getParentProperties() != null) properties2 = properties2.getParentProperties();
		
		return areDimensionsInSamePlanetMoonSystem(properties, destinationDimId) || areDimensionsInSamePlanetMoonSystem(properties2, dimension);
	}

	private boolean areDimensionsInSamePlanetMoonSystem(DimensionProperties properties, int id) {
		if(properties.getId() == id)
			return true;

		for(int child : properties.getChildPlanets()) {
			if(areDimensionsInSamePlanetMoonSystem(getDimensionProperties(child), id)) return true;
		}
		return false;
	}
	
	public static DimensionProperties getEffectiveDimId(int dimId, BlockPos pos) {

		if(dimId == Configuration.spaceDimId) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(obj != null)
				return (DimensionProperties) obj.getProperties().getParentProperties();
			else 
				return defaultSpaceDimensionProperties;
		}
		else return getInstance().getDimensionProperties(dimId);
	}

	public static DimensionProperties getEffectiveDimId(World world, BlockPos pos) {
		int dimId = world.provider.getDimension();

		if(dimId == Configuration.spaceDimId) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(obj != null)
				return (DimensionProperties) obj.getProperties().getParentProperties();
			else 
				return defaultSpaceDimensionProperties;
		}
		else return getInstance().getDimensionProperties(dimId);
	}
}
