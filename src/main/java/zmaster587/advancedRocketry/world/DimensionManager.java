package zmaster587.advancedRocketry.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.solar.StellarBody;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;


public class DimensionManager {

	//TODO: fix satellites not unloading on disconnect
	private Random random;
	private static DimensionManager instance = new DimensionManager();
	public static final String workingPath = "advRocketry";
	public static final String filePath = workingPath + "/temp.dat";
	private HashMap<Integer,DimensionProperties> dimensionList;
	private HashMap<Integer, StellarBody> starList;

	private static long nextSatelliteId;
	private static StellarBody sol;
	private static SpaceObjectManager spaceObjectManager;

	public static DimensionProperties overworldProperties;
	public static DimensionProperties defaultSpaceDimensionProperties;

	public static StellarBody getSol() {
		return sol;
	}

	public static DimensionManager getInstance() {
		return instance;
	};

	public void syncToPlayer(EntityPlayer entity) {
		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {
			PacketHandler.sendToPlayer(new PacketDimInfo(dimSet.getKey(), dimSet.getValue()), entity);
		}
	}

	public DimensionManager() {
		spaceObjectManager = new SpaceObjectManager();
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
		overworldProperties.name = "Earth";
		
		defaultSpaceDimensionProperties = new DimensionProperties(-1, false);
		defaultSpaceDimensionProperties.atmosphereDensity = 0;
		defaultSpaceDimensionProperties.averageTemperature = 0;
		defaultSpaceDimensionProperties.gravitationalMultiplier = 0.1f;
		defaultSpaceDimensionProperties.orbitalDist = 100;
		defaultSpaceDimensionProperties.skyColor = new float[] {0f,0f,0f};
		defaultSpaceDimensionProperties.setStar(sol);
		defaultSpaceDimensionProperties.name = "Space";
		defaultSpaceDimensionProperties.fogColor = new float[] {0f,0f,0f};
		defaultSpaceDimensionProperties.setParentPlanet(0,false);
		defaultSpaceDimensionProperties.orbitalDist = 1;

		random = new Random(System.currentTimeMillis());
	}
	
	public static SpaceObjectManager getSpaceManager() {
		return spaceObjectManager;
	}

	public Integer[] getregisteredDimensions() {
		Integer ret[] = new Integer[dimensionList.size()];
		return dimensionList.keySet().toArray(ret);
	}

	public Integer[] getLoadedDimensions() {
		return getregisteredDimensions();
	}

	public long getNextSatelliteId() {
		return nextSatelliteId++;
	}

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
	public String getNextName(int dimId) {
		return "Sol-" + dimId;
	}

	public void tickDimensions() {
		//Tick satellites
		overworldProperties.tick();
		for(int i : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionManager.getInstance().getDimensionProperties(i).tick();
		}
	}

	public void setDimProperties( int dimId, DimensionProperties properties) {
		dimensionList.put(new Integer(dimId),properties);
	}

	public int getNextFreeDim() {
		for(int i = 2; i < 1024; i++) {
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

	public DimensionProperties generateRandom(String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties(getNextFreeDim());

		if(name == "")
			properties.name = getNextName(properties.getId());
		else {
			properties.name = name;
		}
		properties.atmosphereDensity = MathHelper.clamp_int(baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2, 0, 200); 
		properties.orbitalDist = MathHelper.clamp_int(baseDistance + random.nextInt(distanceFactor) - distanceFactor/2,0,200);
		properties.gravitationalMultiplier = Math.min(Math.max(0.05f,(baseGravity + random.nextInt(gravityFactor) - gravityFactor/2)/100f), 1.3f);
		properties.skyColor = new float []{.5f, .5f, .8f};

		double minDistance;

		do {
			minDistance = Double.MAX_VALUE;

			properties.orbitTheta  = random.nextInt(360)*(2f*Math.PI)/360f;

			for(DimensionProperties properties2 : sol.getPlanets()) {
				double dist = Math.abs(properties2.orbitTheta - properties.orbitTheta);
				if(dist < minDistance)
					minDistance = dist;
			}

		} while(minDistance < (Math.PI/20f));

		//Get Star Color
		properties.setStar(sol);

		//Linear is easier. Earth is nominal!
		properties.averageTemperature = (sol.getTemperature() + (200 - properties.orbitalDist)*10 + properties.atmosphereDensity*18)/20;

		properties.addBiomes(properties.getViableBiomes());

		registerDim(properties);
		return properties;
	}


	public DimensionProperties generateRandom(int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		return generateRandom("", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public boolean registerDim(DimensionProperties properties) {
		boolean bool = registerDimNoUpdate(properties);

		if(bool)
			PacketHandler.sendToAll(new PacketDimInfo(properties.getId(), properties));
		return bool;
	}

	public boolean registerDimNoUpdate(DimensionProperties properties) {
		int dimId = properties.getId();
		Integer dim = new Integer(dimId);

		if(dimensionList.containsKey(dim))
			return false;

		net.minecraftforge.common.DimensionManager.registerProviderType(properties.getId(), WorldProviderPlanet.class, false);
		net.minecraftforge.common.DimensionManager.registerDimension(dimId, dimId);
		dimensionList.put(dimId, properties);

		return true;
	}

	public void unregisterAllDimensions() {
		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {
			net.minecraftforge.common.DimensionManager.unregisterProviderType(dimSet.getKey());
			net.minecraftforge.common.DimensionManager.unregisterDimension(dimSet.getKey());
		}
		dimensionList.clear();
	}

	public void deleteDimension(int dimId) {

		DimensionProperties properties = dimensionList.get(dimId);
		properties.getStar().removePlanet(properties);
		if(properties.isMoon()) {
			properties.getParentProperties().removeChild(properties.getId());
		}

		if(properties.hasChildren()) {
			for(Integer child : properties.getChildPlanets()) {
				deleteDimension(child);
				PacketHandler.sendToAll(new PacketDimInfo(child, null));
			}
		}

		//TODO: check for world loaded
		net.minecraftforge.common.DimensionManager.unloadWorld(dimId);

		//Delete World Folder
		File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), workingPath + "/DIM" + dimId );

		try {
			FileUtils.deleteDirectory(file);

		} catch(IOException e) {
			e.printStackTrace();
		}

		net.minecraftforge.common.DimensionManager.unregisterProviderType(dimId);
		net.minecraftforge.common.DimensionManager.unregisterDimension(dimId);
		dimensionList.remove(new Integer(dimId));
	}

	public DimensionProperties getDimensionProperties(int dimId) {
		DimensionProperties properties = dimensionList.get(new Integer(dimId));
		return properties == null ? overworldProperties : properties;
	}

	public StellarBody getStar(int id) {
		return starList.get(new Integer(id));
	}

	public Set<Integer> getStars() {
		return starList.keySet();
	}

	public void addStar(StellarBody star) {
		starList.put(star.getId(), star);
	}

	public void removeStar(int id) {
		//TODO: actually remove subPlanets et
		starList.remove(id);
	}

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
		spaceObjectManager.writeToNBT(nbtTag);
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

	public void loadDimensions(String filePath) {

		FileInputStream inStream;
		NBTTagCompound nbt;
		try {
			File file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory(), filePath);

			if(!file.exists()) {
				new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length())).mkdirs();


				file.createNewFile();
				return;
			}

			inStream = new FileInputStream(file);
			nbt = CompressedStreamTools.readCompressed(inStream);
			inStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;

		} catch (IOException e) {
			e.printStackTrace();
			return;
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
					net.minecraftforge.common.DimensionManager.registerProviderType(keyInt, WorldProviderPlanet.class, false);
					net.minecraftforge.common.DimensionManager.registerDimension(keyInt, keyInt);
					dimensionList.put(new Integer(keyInt), propeties);
				}
				else{
					AdvancedRocketry.logger.warning("Null Dimension Properties Recieved");
				}
				//TODO: print unable to register world
			}
		}
		
		//Check for tag in case old version of Adv rocketry is in use
		if(nbt.hasKey("spaceObjects")) {
			NBTTagCompound nbtTag = nbt.getCompoundTag("spaceObjects");
			spaceObjectManager.readFromNBT(nbtTag);
		}
	}
}
