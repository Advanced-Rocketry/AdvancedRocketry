package zmaster587.advancedRocketry.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;


/**
 * Stores information relating to the biomes and biome registry of AdvancedRocketry
 */
public class AdvancedRocketryBiomes {
	
	public static final AdvancedRocketryBiomes instance = new AdvancedRocketryBiomes();
	private List<Biome> registeredBiomes;
	private List<Biome> registeredHighPressureBiomes;
	private List<Biome> registeredSingleBiome;
	private static List<Integer> blackListedBiomeIds;
	
	public static Biome moonBiome;
	public static Biome hotDryBiome;
	public static Biome alienForest;
	public static Biome spaceBiome;
	public static Biome stormLandsBiome;
	public static Biome crystalChasms;
	public static Biome swampDeepBiome;
	public static Biome marsh;
	public static Biome oceanSpires;
	
	private AdvancedRocketryBiomes() {
		registeredBiomes = new ArrayList<Biome>();
		registeredHighPressureBiomes = new LinkedList<Biome>();
		blackListedBiomeIds = new ArrayList<Integer>();
		registeredSingleBiome = new ArrayList<Biome>();
		
		registerBlackListBiome(Biomes.SKY); //Sky
		registerBlackListBiome(Biomes.HELL); //Hell
		registerBlackListBiome(Biomes.RIVER); //River
	}
	
	/**
	 * TODO: support id's higher than 255.  
	 * Any biome registered through vanilla forge does not need to be registered here
	 * @param biome Biome to register with AdvancedRocketry's Biome registry
	 */
	public void registerBiome(Biome biome) {
		registeredBiomes.add(biome);
	}
	
	
	/**
	 * Registers biomes you don't want to spawn on any planet unless registered with highpressure or similar feature
	 */
	public void registerBlackListBiome(Biome biome) {
		blackListedBiomeIds.add(Biome.getIdForBiome(biome));
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
	public void registerHighPressureBiome(Biome biome) {
		registeredHighPressureBiomes.add(biome);
		registerBlackListBiome(biome);
	}
	
	public List<Biome> getHighPressureBiomes() {
		return registeredHighPressureBiomes;	
	}
	
	/**
	 * Registers a biome to have a chance to spawn as the only biome on a planet
	 * @param biome
	 */
	public void registerSingleBiome(Biome biome) {
		registeredSingleBiome.add(biome);
	}
	
	public List<Biome> getSingleBiome() {
		return registeredSingleBiome;	
	}
	
	/**
	 * Gets Biomes from Advanced Rocketry's biomes registry.  If it does not exist attepts to retrieve from vanilla forge
	 * @param id biome id
	 * @return Biome retrieved from the biome ID
	 */
	public Biome getBiomeById(int id) {
		
		for(Biome biome : registeredBiomes) {
			if( Biome.getIdForBiome(biome) == id)
				return biome;
		}
		
		return Biome.getBiome(id);
	}
	
}
