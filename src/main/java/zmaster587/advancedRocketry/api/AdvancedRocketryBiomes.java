package zmaster587.advancedRocketry.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * Stores information relating to the biomes and biome registry of AdvancedRocketry
 */
public class AdvancedRocketryBiomes {
	
	public static final AdvancedRocketryBiomes instance = new AdvancedRocketryBiomes();
	private List<BiomeGenBase> registeredBiomes;
	private List<BiomeGenBase> registeredHighPressureBiomes;
	private List<BiomeGenBase> registeredSingleBiome;
	private static List<Integer> blackListedBiomeIds;
	
	public static BiomeGenBase moonBiome;
	public static BiomeGenBase hotDryBiome;
	public static BiomeGenBase alienForest;
	public static BiomeGenBase spaceBiome;
	public static BiomeGenBase stormLandsBiome;
	public static BiomeGenBase crystalChasms;
	public static BiomeGenBase swampDeepBiome;
	public static BiomeGenBase marsh;
	public static BiomeGenBase oceanSpires;
	
	private AdvancedRocketryBiomes() {
		registeredBiomes = new ArrayList<BiomeGenBase>();
		registeredHighPressureBiomes = new LinkedList<BiomeGenBase>();
		blackListedBiomeIds = new ArrayList<Integer>();
		registeredSingleBiome = new ArrayList<BiomeGenBase>();
		
		registerBlackListBiome(BiomeGenBase.sky);
		registerBlackListBiome(BiomeGenBase.hell);
		registerBlackListBiome(BiomeGenBase.river);
	}
	
	/**
	 * TODO: support id's higher than 255.  
	 * Any biome registered through vanilla forge does not need to be registered here
	 * @param biome BiomeGenBase to register with AdvancedRocketry's Biome registry
	 */
	public void registerBiome(BiomeGenBase biome) {
		registeredBiomes.add(biome);
	}
	
	
	/**
	 * Registers biomes you don't want to spawn on any planet unless registered with highpressure or similar feature
	 */
	public void registerBlackListBiome(BiomeGenBase biome) {
		blackListedBiomeIds.add(biome.biomeID);
	}
	
	/**
	 * Gets a list of the blacklisted Biome Ids
	 */
	public List<Integer> getBlackListedBiomes() {
		return blackListedBiomeIds;
	}
	
	/**
	 * Registers a biome as high pressure for use with the planet generators (It will only spawn on planets with high pressure)
	 * @param biome
	 */
	public void registerHighPressureBiome(BiomeGenBase biome) {
		registeredHighPressureBiomes.add(biome);
		registerBlackListBiome(biome);
	}
	
	public List<BiomeGenBase> getHighPressureBiomes() {
		return registeredHighPressureBiomes;	
	}
	
	/**
	 * Registers a biome to have a chance to spawn as the only biome on a planet
	 * @param biome
	 */
	public void registerSingleBiome(BiomeGenBase biome) {
		registeredSingleBiome.add(biome);
	}
	
	public List<BiomeGenBase> getSingleBiome() {
		return registeredSingleBiome;	
	}
	
	/**
	 * Gets Biomes from Advanced Rocketry's biomes registry.  If it does not exist attepts to retrieve from vanilla forge
	 * @param id biome id
	 * @return BiomeGenBase retrieved from the biome ID
	 */
	public BiomeGenBase getBiomeById(int id) {
		
		for(BiomeGenBase biome : registeredBiomes) {
			if( biome.biomeID == id)
				return biome;
		}
		
		return BiomeGenBase.getBiome(id);
	}
	
}
