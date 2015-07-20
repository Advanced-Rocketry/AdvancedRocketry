package zmaster587.advancedRocketry.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.world.solar.StellarBody;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.ChunkDataEvent;


public class DimensionManager {
	
	private Random random;
	private static DimensionManager instance = new DimensionManager();
	public static final String workingPath = "advRocketry";
	public static final String filePath = workingPath + "/temp.dat";
	
	private static StellarBody sol = new StellarBody(0);
	
	public static DimensionManager getInstance() {
		return instance;
	};

	public void syncToPlayer(EntityPlayer entity) {
		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {
			PacketHandler.sendToPlayer(new PacketDimInfo(dimSet.getKey(), dimSet.getValue()), entity);
		}
	}
	
	private HashMap<Integer,DimensionProperties> dimensionList;

	public DimensionManager() {
		dimensionList = new HashMap<Integer,DimensionProperties>();
		random = new Random(System.currentTimeMillis());
	}
	
	public Integer[] getregisteredDimensions() {
		Integer ret[] = new Integer[dimensionList.size()];
		return dimensionList.keySet().toArray(ret);
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
	
	public void generateRandom(int atmosphereFactor, int distanceFactor, int gravityFactor) {
		generateRandom(100, 100, 1, atmosphereFactor, distanceFactor, gravityFactor);
	}
	
	public void generateRandom(String name ,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		generateRandom(name, 100, 100, 1, atmosphereFactor, distanceFactor, gravityFactor);
	}
	
	public void generateRandom(String name, int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		DimensionProperties properties = new DimensionProperties();
		
		properties.name = name;
		properties.atmosphereDensity = baseAtmosphere + random.nextInt(atmosphereFactor) - atmosphereFactor/2; 
		properties.orbitalDist = baseDistance + random.nextInt(distanceFactor) - distanceFactor/2;
		properties.gravitationalMultiplier = (baseGravity + random.nextInt(gravityFactor) - gravityFactor/2)/100f;
		
		//Get Star Color
		properties.sunColor = sol.getColor();
		
		//Linear is easier. Earth is nominal!
		properties.averageTemperature = (sol.getTemperature() + (200 - properties.orbitalDist)*4 + properties.atmosphereDensity)/6;
	
		properties.addBiomes(properties.getViableBiomes());
		
		registerDim(getNextFreeDim(), properties);
	}
		
	
	public void generateRandom(int baseAtmosphere, int baseDistance, int baseGravity,int atmosphereFactor, int distanceFactor, int gravityFactor) {
		generateRandom("temp", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);
	}

	public boolean registerDim(int dimId, DimensionProperties properties) {
		Integer dim = new Integer(dimId);

		if(dimensionList.containsKey(dim))
			return false;

		net.minecraftforge.common.DimensionManager.registerProviderType(dimId, ProviderPlanet.class, false);
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

	public void unregisterDimension(int dimId) {
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
		return properties == null ? DimensionProperties.overworldProperties : properties;
	}

	public void saveDimensions(String filePath) {
		NBTTagCompound nbt = new NBTTagCompound();

		for(Entry<Integer, DimensionProperties> dimSet : dimensionList.entrySet()) {
			
			NBTTagCompound dimNbt = new NBTTagCompound();
			dimSet.getValue().writeToNBT(dimNbt);
			
			nbt.setTag(dimSet.getKey().toString(), dimNbt);
		}
		
		
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
		

		for(Object key : nbt.func_150296_c()) {
			String keyString = (String)key;
			
			DimensionProperties propeties = DimensionProperties.createFromNBT(nbt.getCompoundTag(keyString));
			
			if(propeties != null) {
				int keyInt = Integer.parseInt(keyString);
				net.minecraftforge.common.DimensionManager.registerProviderType(keyInt, ProviderPlanet.class, false);
				net.minecraftforge.common.DimensionManager.registerDimension(keyInt, keyInt);
				dimensionList.put(new Integer(keyInt), propeties);
			}
			else{}
				//TODO: print unable to register world
		}
	}
}
