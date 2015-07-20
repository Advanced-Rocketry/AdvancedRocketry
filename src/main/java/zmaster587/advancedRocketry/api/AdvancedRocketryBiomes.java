package zmaster587.advancedRocketry.api;

import java.util.ArrayList;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;

public class AdvancedRocketryBiomes {
	
	public static final AdvancedRocketryBiomes instance = new AdvancedRocketryBiomes();
	private ArrayList<BiomeGenBase> registeredBiomes;
	
	public static BiomeGenBase moonBiome;
	public static BiomeGenBase hotDryBiome;
	public static BiomeGenBase alienForest;
	
	private AdvancedRocketryBiomes() {
		registeredBiomes = new ArrayList<BiomeGenBase>();
		
		registeredBiomes.add(moonBiome = new BiomeGenMoon(90, false));
		registeredBiomes.add(alienForest = new BiomeGenAlienForest(91, false));
		registeredBiomes.add(hotDryBiome = new BiomeGenHotDryRock(256, false));
		
		
	}
	
	public static void init() {
		
	}
	
	public void registerBiome(BiomeGenBase biome) {
		registeredBiomes.add(biome);
	}
	
	public BiomeGenBase getBiomeById(int id) {
		
		
		
		for(BiomeGenBase biome : registeredBiomes) {
			if( biome.biomeID == id)
				return biome;
		}
		
		return BiomeGenBase.getBiome(id);
	}
	
}
