package zmaster587.advancedRocketry.dimension;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.IGalaxy;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.backwardCompat.VersionCompat;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.advancedRocketry.util.SpawnListEntryNBT;
import zmaster587.advancedRocketry.util.XMLPlanetLoader;
import zmaster587.advancedRocketry.util.XMLPlanetLoader.DimensionPropertyCoupling;
import zmaster587.advancedRocketry.world.provider.WorldProviderAsteroid;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;


public class DimensionManager implements IGalaxy {
	public static Logger logger = AdvancedRocketry.logger;
	
	//TODO: fix satellites not unloading on disconnect
	private Random random;
	private static DimensionManager instance = (DimensionManager) (AdvancedRocketryAPI.dimensionManager = new DimensionManager());
	public static final String workingPath = "advRocketry";
	public static final String tempFile = "/temp.dat";
	public static final String worldXML = "/planetDefs.xml";
	public static int dimOffset = 0;
	public static final DimensionType PlanetDimensionType = DimensionType.register("planet", "planet", 2, WorldProviderPlanet.class, false);
	public static final DimensionType spaceDimensionType = DimensionType.register("space", "space", 3, WorldProviderSpace.class, false);
	public static final DimensionType AsteroidDimensionType = DimensionType.register("asteroid", "asteroid", 4, WorldProviderAsteroid.class, false);
	private boolean hasBeenInitiallized = false;
	public static String prevBuild;

	//Stat tracking
	public static boolean hasReachedMoon;
	public static boolean hasReachedWarp;

	
	//Reference to the worldProvider for any dimension created through this system, normally WorldProviderPlanet, set in AdvancedRocketry.java in preinit
	public static Class<? extends DimensionType> planetWorldProvider;
	//private HashMap<Integer,DimensionProperties> dimensionList;
	private HashMap<ResourceLocation, DimensionProperties> dimensionListResource;
	private HashMap<ResourceLocation, StellarBody> starList;

	public static final int GASGIANT_DIMID_OFFSET = 0x100; //Offset by 256
	private static long nextSatelliteId;
	private static StellarBody sol;
	public Set<ResourceLocation> knownPlanets;

	//The default properties belonging to the overworld
	public static DimensionProperties overworldProperties;
	//the default property for any dimension created in space, normally, space over earth
	public static DimensionProperties defaultSpaceDimensionProperties;

	@Deprecated
	public static StellarBody getSol() {
		return getInstance().getStar(0);
	}

	public static DimensionManager getInstance() {
		return AdvancedRocketry.proxy.getDimensionManager(); //instance;
	};

	public DimensionManager() {
		dimensionListResource = new HashMap<ResourceLocation, DimensionProperties>();
		starList = new HashMap<ResourceLocation, StellarBody>();
		sol = new StellarBody();
		sol.setTemperature(100);
		sol.setId(new ResourceLocation(Constants.STAR_NAMESPACE, "Sol"));
		sol.setName("Sol");

		overworldProperties = new DimensionProperties(new ResourceLocation(Constants.PLANET_NAMESPACE, DimensionType.field_235999_c_.getRegistryName().getPath()));
		overworldProperties.setAtmosphereDensityDirect(100);
		//Temperature in Kelvin, 286 is 13 Degrees C
		overworldProperties.averageTemperature = 286;
		overworldProperties.gravitationalMultiplier = 1f;
		overworldProperties.orbitalDist = 100;
		overworldProperties.skyColor = new float[] {1f, 1f, 1f};
		overworldProperties.setName("Earth");
		overworldProperties.isNativeDimension = false;
		overworldProperties.setStar(sol);
		
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
		knownPlanets = new HashSet<ResourceLocation>();
	}

	/**
	 * @return an Integer array of dimensions registered with this DimensionManager
	 */
	public ResourceLocation[] getRegisteredDimensions() {
		ResourceLocation ret[] = new ResourceLocation[dimensionListResource.size()];
		return dimensionListResource.keySet().toArray(ret);
	}

	/**
	 * @return List of dimensions registered with this manager that are currently loaded on the server/integrated server
	 */
	public ResourceLocation[] getLoadedDimensions() {
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
		if(!hasBeenInitiallized && FMLEnvironment.dist == Dist.DEDICATED_SERVER ) {
			DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.dimension.DimensionManager.workingPath);
		}

		SatelliteBase satellite = overworldProperties.getSatellite(satId);

		if(satellite != null)
			return satellite;

		for(ResourceLocation i : DimensionManager.getInstance().getLoadedDimensions()) {
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
	private String getNextName(ResourceLocation dimId) {
		return "Sol-" + dimId;
	}

	/**
	 * Called every tick to tick satellites
	 */
	public void tickDimensions() {
		//Tick satellites
		for(ResourceLocation i : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionManager.getInstance().getDimensionProperties(i).tick();
		}
	}

	public void tickDimensionsClient() {
		//Tick satellites
		for(ResourceLocation i : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionManager.getInstance().getDimensionProperties(i).updateOrbit();
		}
	}

	/**
	 * Sets the properies supplied for the supplied dimensionID, if the dimension does not exist, it is added to the list but not registered with minecraft
	 * @param dimId id to set the properties of
	 * @param properties to set for that dimension
	 */
	public void setDimProperties( ResourceLocation dimId, DimensionProperties properties) {
		dimensionListResource.put(dimId,properties);
	}

	/**
	 * Iterates though the list of existing dimIds, and returns the closest free id greater than two
	 * @return next free id
	 */
	public ResourceLocation getNextFreeDim() {
		for(int i = 0; i < 10000; i++) {
			ResourceLocation planetID = new ResourceLocation( Constants.PLANET_NAMESPACE, "Planet-" + String.valueOf(i) );
			if(!ZUtils.isWorldRegistered(planetID) && !dimensionListResource.containsKey(planetID))
				return planetID;
		}
		return Constants.INVALID_PLANET;
	}

	public ResourceLocation getNextFreeStarId() {
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			ResourceLocation starId = new ResourceLocation(Constants.STAR_NAMESPACE, "Star" + String.valueOf(i));
			if(!starList.containsKey(starId))
				return starId;
		}
		return Constants.INVALID_PLANET;
	}

	public DimensionProperties generateRandom(ResourceLocation starId, int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(starId, 100, 100, 100, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public DimensionProperties generateRandom(ResourceLocation starId, String name, int atmosphereFactor, int distanceFactor, int gravityFactor) {
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
	public DimensionProperties generateRandom(ResourceLocation starId, String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties(getNextFreeDim());

		if(properties.getId() == Constants.INVALID_PLANET)
			return null;

		if(name == "")
			properties.setName(getNextName(properties.getId()));
		else {
			properties.setName(name);
		}
		properties.setAtmosphereDensityDirect(MathHelper.clamp(baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2, DimensionProperties.MIN_ATM_PRESSURE, DimensionProperties.MAX_ATM_PRESSURE)); 
		int newDist = properties.orbitalDist = MathHelper.clamp(baseDistance + random.nextInt(distanceFactor), DimensionProperties.MIN_DISTANCE, DimensionProperties.MAX_DISTANCE);

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
		properties.baseOrbitTheta = random.nextInt(360) * Math.PI/180d;

		properties.orbitalPhi = (random.nextGaussian() -0.5d)*180;
		properties.rotationalPhi = (random.nextGaussian() -0.5d)*180;

		//Get Star Color
		properties.setStar(getStar(starId));

		//Linear is easier. Earth is nominal!
		properties.averageTemperature = AstronomicalBodyHelper.getAverageTemperature(properties.getStar(), properties.getSolarOrbitalDistance(), properties.getAtmosphereDensity());


		if( AtmosphereTypes.getAtmosphereTypeFromValue(properties.getAtmosphereDensity()) == AtmosphereTypes.NONE && random.nextInt() % 5 == 0)
		{
			properties.setOceanBlock(AdvancedRocketryBlocks.blockOxygenFluid.getDefaultState());
			properties.setSeaLevel(random.nextInt(6) + 72);
		}

		if(random.nextInt() % 10 == 0)
		{
			properties.setSeaLevel(random.nextInt(40) + 43);
		}

		properties.skyColor[0] *= 1 - MathHelper.clamp(random.nextFloat()*0.1f + (70 - (properties.averageTemperature/3))/100f,0.2f,1);
		properties.skyColor[1] *= 1 - (random.nextFloat()*.5f);
		properties.skyColor[2] *= 1 - MathHelper.clamp(random.nextFloat()*0.1f + ((properties.averageTemperature/3) - 70)/100f,0,1);

		if(random.nextInt() % 50 == 0)
		{
			properties.setHasRings(true);
			properties.ringColor[0] = properties.skyColor[0];
			properties.ringColor[1] = properties.skyColor[1];
			properties.ringColor[2] = properties.skyColor[2];
		}

		properties.rotationalPeriod = (int) (Math.pow((1/properties.gravitationalMultiplier),3) * 24000);

		properties.addBiomes(properties.getViableBiomes());
		properties.initDefaultAttributes();

		registerDim(properties, true);
		return properties;
	}


	public DimensionProperties generateRandom(ResourceLocation starId, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom(starId, "", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public DimensionProperties generateRandomGasGiant(ResourceLocation starId, String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties(getNextFreeDim());

		if(name == "")
			properties.setName(getNextName(properties.getId()));
		else {
			properties.setName(name);
		}
		properties.setAtmosphereDensityDirect(MathHelper.clamp(baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2, DimensionProperties.MIN_ATM_PRESSURE, DimensionProperties.MAX_ATM_PRESSURE));
		properties.orbitalDist = MathHelper.clamp(baseDistance + random.nextInt(distanceFactor), DimensionProperties.MIN_DISTANCE, 800);
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
		properties.averageTemperature = AstronomicalBodyHelper.getAverageTemperature(properties.getStar(), properties.getSolarOrbitalDistance(), properties.getAtmosphereDensity());
		properties.setGasGiant(true);

		// Add all gasses for the default world
		// TODO: add variation
		for( Fluid gas : AdvancedRocketryFluids.getGasGiantGasses() )
			properties.getHarvestableGasses().add(gas);
		registerDim(properties, true);
		return properties;
	}

	/**
	 * 
	 * @param dimId dimension id to check
	 * @return true if it can be traveled to, in general if it has a surface
	 */
	public boolean canTravelTo(ResourceLocation dimId){
		
		return ZUtils.isWorldRegistered(dimId) && getDimensionProperties(dimId).hasSurface();
	}
	
	/**
	 * 
	 * @param dimId dimension id to check
	 * @return true if it can be traveled to, in general if it has a surface
	 */
	public boolean canTravelTo(ServerWorld dimId){
		
		return getDimensionProperties(ZUtils.getDimensionIdentifier(dimId)).hasSurface();
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
		ResourceLocation dimId = properties.getId();

		if(dimensionListResource.containsKey(dimId))
			return false;

		//Avoid registering gas giants as dimensions
		if(registerWithForge && properties.hasSurface() && !ZUtils.isWorldRegistered(dimId)) {

			if(properties.isAsteroid())
				ZUtils.RegisterWorld(dimId, AsteroidDimensionType);
			else
				ZUtils.RegisterWorld(dimId, PlanetDimensionType);
		}
		dimensionListResource.put(dimId, properties);

		return true;
	}

	/**
	 * Unregisters all dimensions associated with this DimensionManager from both Minecraft and this DimnensionManager
	 */
	public void unregisterAllDimensions() {
		for(Entry<ResourceLocation, DimensionProperties> dimSet : dimensionListResource.entrySet()) {
			if(dimSet.getValue().isNativeDimension && dimSet.getValue().hasSurface() && ZUtils.isWorldRegistered(dimSet.getKey())) {
				ZUtils.unegisterWorld(dimSet.getKey());
			}
		}
		dimensionListResource.clear();
		starList.clear();
	}

	/**
	 * Deletes and unregisters the dimensions, as well as all child dimensions, from the game
	 * @param dimId the dimensionId to delete
	 */
	public void deleteDimension(ResourceLocation dimId) {

		if(ZUtils.isWorldLoaded(dimId)) {
			AdvancedRocketry.logger.warn("Cannot delete dimension " + dimId + " it is still loaded");
			return;
		}

		DimensionProperties properties = dimensionListResource.get(dimId);

		//Can happen in some rare cases
		if(properties == null)
			return;

		if(properties.getStar() != null)
			properties.getStar().removePlanet(properties);
		if(properties.isMoon()) {
			properties.getParentProperties().removeChild(properties.getId());
		}

		if(properties.hasChildren()) {

			Iterator<ResourceLocation> iterator = properties.getChildPlanets().iterator();
			while (iterator.hasNext()){
				ResourceLocation child = iterator.next();
				iterator.remove(); //Avoid CME
				deleteDimension(child);

				PacketHandler.sendToAll(new PacketDimInfo(child, null));
			}
		}

		//TODO: check for world loaded
		// If not native to AR let the mod it's registered to handle it
		if(properties.isNativeDimension ) {
			if(ZUtils.isWorldRegistered(dimId)) {
				if(!ZUtils.isWorldLoaded(dimId))
					net.minecraftforge.common.DimensionManager.unloadWorld(dimId);

				ZUtils.unegisterWorld(dimId);
			}
		}
		dimensionListResource.remove(dimId);

		//Delete World Folder
		File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), workingPath + "/DIM" + dimId );

		try {
			FileUtils.deleteDirectory(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isInitialized()
	{

		return hasBeenInitiallized;
	}

	public void onServerStopped()
	{
		unregisterAllDimensions();
		knownPlanets.clear();
		overworldProperties.resetProperties();
		hasBeenInitiallized = false;
	}

	/**
	 * 
	 * @param dimId id of the dimention of which to get the properties
	 * @return DimensionProperties representing the dimId given
	 */
	public DimensionProperties getDimensionProperties(ResourceLocation resourceLocation)
	{
		DimensionProperties properties = dimensionListResource.get(resourceLocation);
		if(resourceLocation == ARConfiguration.getCurrentConfig().spaceDimId || resourceLocation == null) {
			return defaultSpaceDimensionProperties;
		}
		return properties == null ? overworldProperties : properties;
	}
	
	public DimensionProperties getDimensionProperties(ResourceLocation resourceLocation, BlockPos pos)
	{
		
		if(resourceLocation == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(obj == null)
				return defaultSpaceDimensionProperties;
			
			return (DimensionProperties) obj.getProperties();
		}
		DimensionProperties properties = dimensionListResource.get(resourceLocation);
		return properties == null ? overworldProperties : properties;
		
	}
	
	public DimensionProperties getDimensionProperties(World world)
	{
		return getDimensionProperties(ZUtils.getDimensionIdentifier(world));
	}
	
	public DimensionProperties getDimensionProperties(World world, BlockPos pos)
	{
		return getDimensionProperties(ZUtils.getDimensionIdentifier(world), pos);
	}

	/**
	 * @param id star id for which to get the object
	 * @return the {@link StellarBody} object
	 */
	public StellarBody getStar(ResourceLocation id) {
		return starList.get(id);
	}

	/**
	 * @return a list of star ids
	 */
	public Set<ResourceLocation> getStarIds() {
		return starList.keySet();
	}
	
	public boolean isStar(ResourceLocation id) {
		return starList.keySet().contains(id);
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
	 * @param starId id of the star to remove
	 */
	public void removeStar(ResourceLocation starId) {
		//TODO: actually remove subPlanets et
		starList.remove(starId);
	}

	/**
	 * Saves all dimension data, satellites, and space stations to disk, SHOULD NOT BE CALLED OUTSIDE OF WORLDSAVEEVENT
	 * @param filePath file path to which to save the data
	 */
	public void saveDimensions(String filePath) throws Exception {

		final File saveDir = net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory();
		if(starList.isEmpty() || dimensionListResource.isEmpty()) {
			throw new Exception("Missing Stars");
		}

		CompoundNBT nbt = new CompoundNBT();
		CompoundNBT dimListnbt = new CompoundNBT();


		//Save SolarSystems first
		CompoundNBT solarSystem = new CompoundNBT();
		for(Entry<ResourceLocation, StellarBody> stars: starList.entrySet()) {
			CompoundNBT solarNBT = new CompoundNBT();
			stars.getValue().writeToNBT(solarNBT);
			solarSystem.put(stars.getKey().toString(), solarNBT);
		}

		nbt.put("starSystems", solarSystem);

		//Save satelliteId
		nbt.putLong("nextSatelliteId", nextSatelliteId);

		//Save Overworld
		CompoundNBT dimNbt = new CompoundNBT();

		for(Entry<ResourceLocation, DimensionProperties> dimSet : dimensionListResource.entrySet()) {

			dimNbt = new CompoundNBT();
			dimSet.getValue().writeToNBT(dimNbt);

			dimListnbt.put(dimSet.getKey().toString(), dimNbt);
		}

		nbt.put("dimList", dimListnbt);

		//Stats
		CompoundNBT stats = new CompoundNBT();
		stats.putBoolean("hasReachedMoon", hasReachedMoon);
		stats.putBoolean("hasReachedWarp", hasReachedWarp);
		nbt.put("stat", stats);

		CompoundNBT nbtTag = new CompoundNBT();
		SpaceObjectManager.getSpaceManager().writeToNBT(nbtTag);
		nbt.put("spaceObjects", nbtTag);

		String xmlOutput = XMLPlanetLoader.writeXML(this);

		try {
			File planetXMLOutput = new File(saveDir, filePath + worldXML);
			if(!planetXMLOutput.exists())
				planetXMLOutput.createNewFile();

			File tmpFileXml = File.createTempFile("ARXMLdata_", ".DAT", saveDir);
			FileOutputStream bufoutStream = new FileOutputStream(tmpFileXml);
			bufoutStream.write(xmlOutput.getBytes());

			//Commit to OS, tell OS to commit to disk, release and close stream
			bufoutStream.flush();
			bufoutStream.getFD().sync();
			bufoutStream.close();

			//Temp file was written OK, commit
			Files.copy(tmpFileXml, planetXMLOutput);
			tmpFileXml.delete();

			File file = new File(saveDir, filePath + tempFile);

			if(!file.exists())
				file.createNewFile();

			//Getting real sick of my planet file getting toasted during debug...
			File tmpFile = File.createTempFile("dimprops", ".DAT", saveDir);
			FileOutputStream tmpFileOut = new FileOutputStream(tmpFile);
			DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(tmpFileOut)));
			try {
				//Closes output stream internally without flush... why tho...
				CompressedStreamTools.write(nbt, outStream);

				//Open in append mode to make sure the file syncs, hacky AF
				outStream.flush();
				tmpFileOut.getFD().sync();
				outStream.close();

				Files.copy(tmpFile, file);
				tmpFile.delete();

			} catch(Exception e) {
				AdvancedRocketry.logger.error("Cannot save advanced rocketry planet file, you may be able to find backups in " + saveDir);
				e.printStackTrace();
			}



		} catch (IOException e) {
			AdvancedRocketry.logger.error("Cannot save advanced rocketry planet files, you may be able to find backups in " + saveDir);
			e.printStackTrace();
		}
	}

	/**
	 * @param dimId integer id of the dimension
	 * @return true if the dimension exists and is registered
	 */
	public boolean isDimensionCreated( ResourceLocation dimId) {
		return dimensionListResource.containsKey(dimId) || dimId == ARConfiguration.getCurrentConfig().spaceDimId;
	}

	public boolean isDimensionCreated( World dimId) {
		return isDimensionCreated(ZUtils.getDimensionIdentifier(dimId));
	}
	
	private List<DimensionProperties> generateRandomPlanets(StellarBody star, int numRandomGeneratedPlanets, int numRandomGeneratedGasGiants) {
		List<DimensionProperties> dimPropList = new LinkedList<DimensionProperties>();

		Random random = new Random(System.currentTimeMillis());


		for(int i = 0; i < numRandomGeneratedGasGiants; i++) {
			int baseAtm = 180;
			int baseDistance = 100;

			DimensionProperties	properties = DimensionManager.getInstance().generateRandomGasGiant(star.getId(), "",baseDistance + 50,baseAtm,125,100,100,75);

			dimPropList.add(properties);
			if(properties.gravitationalMultiplier >= 1f) {
				int numMoons = random.nextInt(8);

				for(int ii = 0; ii < numMoons; ii++) {
					DimensionProperties moonProperties = DimensionManager.getInstance().generateRandom(star.getId(), properties.getName() + ": " + ii, 25,100, (int)(properties.gravitationalMultiplier/.02f), 25, 100, 50);
					if(moonProperties == null)
						continue;

					dimPropList.add(moonProperties);

					moonProperties.setParentPlanet(properties);
					star.removePlanet(moonProperties);
				}
			}
		}

		for(int i = 0; i < numRandomGeneratedPlanets; i++) {
			int baseAtm = 75;
			int baseDistance = 100;

			if(i % 4 == 0) {
				baseAtm = 0;
			}
			else if(i != 6 && (i+2) % 4 == 0)
				baseAtm = 120;

			if(i % 3 == 0) {
				baseDistance = 170;
			}
			else if((i + 1) % 3 == 0) {
				baseDistance = 30;
			}

			DimensionProperties properties = DimensionManager.getInstance().generateRandom(star.getId(), baseDistance,baseAtm,125,100,100,75);

			if(properties == null)
				continue;

			dimPropList.add(properties);

			if(properties.gravitationalMultiplier >= 1f) {
				int numMoons = random.nextInt(4);

				for(int ii = 0; ii < numMoons; ii++) {
					DimensionProperties moonProperties = DimensionManager.getInstance().generateRandom(star.getId(), properties.getName() + ": " + ii, 25,100, (int)(properties.gravitationalMultiplier/.02f), 25, 100, 50);

					if(moonProperties == null)
						continue;

					dimPropList.add(moonProperties);
					moonProperties.setParentPlanet(properties);
					star.removePlanet(moonProperties);
				}
			}
		}

		return dimPropList;
	}
	
	public void createAndLoadDimensions(boolean resetFromXml)
	{
		//Load planet files
		//Note: loading this modifies dimOffset
		int dimOffset = DimensionManager.dimOffset;
		DimensionPropertyCoupling dimCouplingList = null;
		XMLPlanetLoader loader = null;
		boolean loadedFromXML = false;
		File file;

		//Check advRocketry folder first
		File localFile;
		localFile = file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory() + "/" + DimensionManager.workingPath + "/planetDefs.xml");
		logger.info("Checking for config at " + file.getAbsolutePath());

		if(!file.exists() || resetFromXml) { //Hi, I'm if check #42, I am true if the config is not in the world/advRocketry folder
			String newFilePath = "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/planetDefs.xml";
			if(!file.exists())
				logger.info("File not found.  Now checking for config at " + newFilePath);

			file = new File(newFilePath);

			//Copy file to local dir
			if(file.exists()) {
				logger.info("Advanced Planet Config file Found!  Copying to world specific directory");
				try {
					File dir = new File(localFile.getAbsolutePath().substring(0, localFile.getAbsolutePath().length() - localFile.getName().length()));

					//File cannot exist due to if check #42
					if((dir.exists() || dir.mkdir()) && localFile.createNewFile()) {
						char buffer[] = new char[1024];

						FileReader reader = new FileReader(file);
						FileWriter writer = new FileWriter(localFile);
						int numChars = 0;
						while((numChars = reader.read(buffer)) > 0) {
							writer.write(buffer, 0, numChars);
						}

						reader.close();
						writer.close();
						logger.info("Copy success!");
					}
					else
						logger.warn("Unable to create file " + localFile.getAbsolutePath());
				} catch(IOException e) {
					logger.warn("Unable to write file " + localFile.getAbsolutePath());
				}
			}
		}

		if(file.exists()) {
			logger.info("Advanced Planet Config file Found!  Loading from file.");
			loader = new XMLPlanetLoader();
			try {
				loader.loadFile(file);
				if(!loader.isValid())
					throw new Exception("Cannot read XML");
				dimCouplingList = loader.readAllPlanets();
				DimensionManager.dimOffset += dimCouplingList.dims.size();
			} catch(Exception e) {
				e.printStackTrace();
				logger.fatal("A serious error has occured while loading the planetDefs XML");
				FMLCommonHandler.instance().exitJava(-1, false);
			}
		}
		//End load planet files

		//Register hard coded dimensions
		Map<Integer,IDimensionProperties> loadedPlanets = loadDimensions(zmaster587.advancedRocketry.dimension.DimensionManager.workingPath);
		if(loadedPlanets.isEmpty()) {
			int numRandomGeneratedPlanets = 9;
			int numRandomGeneratedGasGiants = 1;

			if(dimCouplingList != null) {
				logger.info("Loading initial planet config!");

				for(StellarBody star : dimCouplingList.stars) {
					DimensionManager.getInstance().addStar(star);
				}

				for(DimensionProperties properties : dimCouplingList.dims) {
					DimensionManager.getInstance().registerDimNoUpdate(properties, properties.isNativeDimension);
					properties.setStar(properties.getStarId());
				}

				for(StellarBody star : dimCouplingList.stars) {
					numRandomGeneratedPlanets = loader.getMaxNumPlanets(star);
					numRandomGeneratedGasGiants = loader.getMaxNumGasGiants(star);
					dimCouplingList.dims.addAll(generateRandomPlanets(star, numRandomGeneratedPlanets, numRandomGeneratedGasGiants));
				}

				loadedFromXML = true;
			}

			if(!loadedFromXML) {
				//Make Sol				
				StellarBody sol = new StellarBody();
				sol.setTemperature(100);
				sol.setId(new Re);
				sol.setName("Sol");

				DimensionManager.getInstance().addStar(sol);

				//Add the overworld
				DimensionManager.getInstance().registerDimNoUpdate(DimensionManager.overworldProperties, false);
				sol.addPlanet(DimensionManager.overworldProperties);

				if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId == Constants.INVALID_PLANET)
					zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId = DimensionManager.getInstance().getNextFreeDim();



				//Register the moon
				if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId != Constants.INVALID_PLANET) {
					DimensionProperties dimensionProperties = new DimensionProperties(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId);
					dimensionProperties.setAtmosphereDensityDirect(0);
					dimensionProperties.averageTemperature = 20;
					dimensionProperties.rotationalPeriod = 128000;
					dimensionProperties.gravitationalMultiplier = .166f; //Actual moon value
					dimensionProperties.setName("Luna");
					dimensionProperties.orbitalDist = 150;
					dimensionProperties.addBiome(AdvancedRocketryBiomes.moonBiome);
					dimensionProperties.addBiome(AdvancedRocketryBiomes.moonBiomeDark);

					dimensionProperties.setParentPlanet(DimensionManager.overworldProperties);
					dimensionProperties.setStar(DimensionManager.getSol());
					dimensionProperties.isNativeDimension = !ModList.get().isLoaded("GalacticraftCore");
					dimensionProperties.initDefaultAttributes();

					DimensionManager.getInstance().registerDimNoUpdate(dimensionProperties, !ModList.get().isLoaded("GalacticraftCore"));
				}

				generateRandomPlanets(DimensionManager.getSol(), numRandomGeneratedPlanets, numRandomGeneratedGasGiants);

				StellarBody star = new StellarBody();
				star.setTemperature(10);
				star.setPosX(300);
				star.setPosZ(-200);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Wolf 12");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 5, 0);

				star = new StellarBody();
				star.setTemperature(170);
				star.setPosX(-200);
				star.setPosZ(80);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Epsilon ire");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 7, 0);

				star = new StellarBody();
				star.setTemperature(200);
				star.setPosX(-150);
				star.setPosZ(250);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Proxima Centaurs");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 3, 0);

				star = new StellarBody();
				star.setTemperature(70);
				star.setPosX(-150);
				star.setPosZ(-250);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Magnis Vulpes");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 2, 0);


				star = new StellarBody();
				star.setTemperature(200);
				star.setPosX(50);
				star.setPosZ(-250);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Ma-Roo");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 6, 0);

				star = new StellarBody();
				star.setTemperature(120);
				star.setPosX(75);
				star.setPosZ(200);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Alykitt");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 3, 1);

			}
		}
		else {
			VersionCompat.upgradeDimensionManagerPostLoad(DimensionManager.prevBuild);
		}

		//Attempt to load ore config from adv planet XML
		if(dimCouplingList != null) {
			//Register new stars
			for(StellarBody star : dimCouplingList.stars) {
				if(DimensionManager.getInstance().getStar(star.getId()) == null)
					DimensionManager.getInstance().addStar(star);

				DimensionManager.getInstance().getStar(star.getId()).setName(star.getName());
				DimensionManager.getInstance().getStar(star.getId()).setPosX(star.getPosX());
				DimensionManager.getInstance().getStar(star.getId()).setPosZ(star.getPosZ());
				DimensionManager.getInstance().getStar(star.getId()).setSize(star.getSize());
				DimensionManager.getInstance().getStar(star.getId()).setTemperature(star.getTemperature());
				DimensionManager.getInstance().getStar(star.getId()).subStars = star.subStars;
				DimensionManager.getInstance().getStar(star.getId()).setBlackHole(star.isBlackHole());
			}

			for(DimensionProperties properties : dimCouplingList.dims) {

				//Register dimensions loaded by other mods if not already loaded
				if(!properties.isNativeDimension && properties.getStar() != null && !DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
					for(StellarBody star : dimCouplingList.stars) {
						for(StellarBody loadedStar : DimensionManager.getInstance().getStars()) {
							if(star.getId() == properties.getStarId() && star.getName().equals(loadedStar.getName())) {
								DimensionManager.getInstance().registerDimNoUpdate(properties, false);
								properties.setStar(loadedStar);
							}
						}
					}
				}

				
				if(loadedPlanets.containsKey(properties.getId()))
				{
					DimensionProperties loadedDim = (DimensionProperties)loadedPlanets.get(properties.getId());
					if(loadedDim != null)
					{
						properties.copySatellites(loadedDim);
						properties.copyTerraformedBiomes(loadedDim);
					}
				}
				if(properties.isNativeDimension)
					DimensionManager.getInstance().registerDim(properties, properties.isNativeDimension);

				if(!properties.customIcon.isEmpty()) {
					DimensionProperties loadedProps;
					if(DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
						loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());
						loadedProps.customIcon = properties.customIcon;
					}
				}
				//TODO: add properties fromXML

				//Add artifacts if needed
				if(DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
					DimensionProperties loadedProps;
					loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());
					List<ItemStack> list = new LinkedList<ItemStack>(properties.getRequiredArtifacts());
					loadedProps.getRequiredArtifacts().clear();
					loadedProps.getRequiredArtifacts().addAll(list);

					List<SpawnListEntryNBT> list2 = new LinkedList<SpawnListEntryNBT>(properties.getSpawnListEntries());
					loadedProps.getSpawnListEntries().clear();
					loadedProps.getSpawnListEntries().addAll(list2);

				}


				if(properties.oreProperties != null) {
					DimensionProperties loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());

					if(loadedProps != null)
						loadedProps.oreProperties = properties.oreProperties;
				}
			}

			//Don't load random planets twice on initial load
			//TODO: rework the logic, low priority because low time cost and one time run per world
			if(!loadedFromXML)
			{
				//Add planets
				for(StellarBody star : dimCouplingList.stars) {
					int numRandomGeneratedPlanets = loader.getMaxNumPlanets(star);
					int numRandomGeneratedGasGiants = loader.getMaxNumGasGiants(star);
					generateRandomPlanets(star, numRandomGeneratedPlanets, numRandomGeneratedGasGiants);
				}
			}
		}

		// make sure to set dim offset back to original to make things consistant
		DimensionManager.dimOffset = dimOffset;

		DimensionManager.getInstance().knownPlanets.addAll(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().initiallyKnownPlanets);
		
		
		// Run all sanity checks now
		//Try to fix invalid objects
		for(ISpaceObject i : SpaceObjectManager.getSpaceManager().getSpaceObjects())
		{
			ResourceLocation orbitingId = i.getOrbitingPlanetId(); 
			if(!isDimensionCreated(orbitingId) && orbitingId != SpaceObjectManager.WARPDIMID && orbitingId.getNamespace().equals(Constants.PLANET_NAMESPACE))
			{
				AdvancedRocketry.logger.warn("Dimension ID " + i.getOrbitingPlanetId() + " is not registered and a space station is orbiting it, moving to dimid 0");
				i.setOrbitingBody(0);
			}
		}
	}

	/**
	 * Loads all information to rebuild the galaxy and solar systems from disk into the current instance of DimensionManager
	 * @param filePath file path from which to load the information
	 */
	public Map<ResourceLocation,IDimensionProperties> loadDimensions(String filePath) {
		hasBeenInitiallized = true;
		Map<ResourceLocation,IDimensionProperties> loadedDimProps = new HashMap<ResourceLocation,IDimensionProperties>();

		FileInputStream inStream;
		CompoundNBT nbt;
		try {
			File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath + tempFile);

			if(!file.exists()) {
				new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length())).mkdirs();


				file.createNewFile();
				return loadedDimProps;
			}

			inStream = new FileInputStream(file);
			nbt = CompressedStreamTools.readCompressed(inStream);
			inStream.close();
		} catch(EOFException e) {
			//Silence you fool!
			//Patch to fix JEI printing when trying to load planets too early
			return loadedDimProps;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return loadedDimProps;

		} catch (IOException e) {
			//TODO: try not to obliterate planets in the future
			e.printStackTrace();
			return loadedDimProps;
		}

		//Load SolarSystems first
		CompoundNBT solarSystem = nbt.getCompound("starSystems");

		if(solarSystem.isEmpty())
			return loadedDimProps;

		CompoundNBT stats = nbt.getCompound("stat");
		hasReachedMoon = stats.getBoolean("hasReachedMoon");
		hasReachedWarp = stats.getBoolean("hasReachedWarp");

		for(Object key : solarSystem.keySet()) {

			CompoundNBT solarNBT = solarSystem.getCompound((String)key);
			StellarBody star = new StellarBody();
			star.readFromNBT(solarNBT);
			starList.put(star.getId(), star);
		}

		nbt.put("starSystems", solarSystem);

		nextSatelliteId = nbt.getLong("nextSatelliteId");

		CompoundNBT dimListNbt = nbt.getCompound("dimList");


		for(String key : dimListNbt.keySet()) {
			ResourceLocation keyString = new ResourceLocation(Constants.PLANET_NAMESPACE,key);
			DimensionProperties propeties = DimensionProperties.createFromNBT(keyString ,dimListNbt.getCompound(key));

			if(propeties != null) {
				loadedDimProps.put(keyString, propeties);
			}
			else{
				logger.warn("Null Dimension Properties Recieved");
			}
			//TODO: print unable to register world
		}


		//Check for tag in case old version of Adv rocketry is in use
		if(nbt.contains("spaceObjects")) {
			CompoundNBT nbtTag = nbt.getCompound("spaceObjects");
			SpaceObjectManager.getSpaceManager().readFromNBT(nbtTag);
		}

		prevBuild = nbt.getString("prevVersion");
		nbt.putString("prevVersion", AdvancedRocketry.version);

		return loadedDimProps;
	}

	/**
	 * 
	 * @param destinationDimId
	 * @param dimension
	 * @return true if the two dimensions are in the same planet/moon system
	 */
	public boolean areDimensionsInSamePlanetMoonSystem(ResourceLocation destinationDimId,
			ResourceLocation dimension) {
		//This is a mess, clean up later
		if(dimension == SpaceObjectManager.WARPDIMID || destinationDimId == SpaceObjectManager.WARPDIMID)
			return false;

		DimensionProperties properties = getDimensionProperties(dimension);
		DimensionProperties properties2 = getDimensionProperties(destinationDimId);

		while(properties.getParentProperties() != null) properties = properties.getParentProperties();
		while(properties2.getParentProperties() != null) properties2 = properties2.getParentProperties();

		return areDimensionsInSamePlanetMoonSystem(properties, destinationDimId) || areDimensionsInSamePlanetMoonSystem(properties2, dimension);
	}

	private boolean areDimensionsInSamePlanetMoonSystem(DimensionProperties properties, ResourceLocation id) {
		if(properties.getId() == id)
			return true;

		for(ResourceLocation child : properties.getChildPlanets()) {
			if(areDimensionsInSamePlanetMoonSystem(getDimensionProperties(child), id)) return true;
		}
		return false;
	}

	public static DimensionProperties getEffectiveDimId(ResourceLocation dimId, BlockPos pos) {

		if(dimId == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(obj != null)
				return (DimensionProperties) obj.getProperties().getParentProperties();
			else 
				return defaultSpaceDimensionProperties;
		}
		else return getInstance().getDimensionProperties(dimId);
	}

	public static DimensionProperties getEffectiveDimId(World world, BlockPos pos) {
		ResourceLocation dimId = ZUtils.getDimensionIdentifier(world);

		if(dimId == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(obj != null)
				return (DimensionProperties) obj.getProperties().getParentProperties();
			else 
				return defaultSpaceDimensionProperties;
		}
		else return getInstance().getDimensionProperties(dimId);
	}

	public static DimensionProperties getEffectiveDimId(World world, Vector3d positionVec) {
		return getEffectiveDimId(ZUtils.getDimensionIdentifier(world), positionVec);
	}
}
