package zmaster587.advancedRocketry.api;

import java.util.ArrayList;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * Stores information relating to the biomes and biome registry of AdvancedRocketry
 */
public class AdvancedRocketryBiomes {
	
	public static final AdvancedRocketryBiomes instance = new AdvancedRocketryBiomes();
	private ArrayList<BiomeGenBase> registeredBiomes;
	
	public static BiomeGenBase moonBiome;
	public static BiomeGenBase hotDryBiome;
	public static BiomeGenBase alienForest;
	public static BiomeGenBase spaceBiome;
	
	private AdvancedRocketryBiomes() {
		registeredBiomes = new ArrayList<BiomeGenBase>();
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
