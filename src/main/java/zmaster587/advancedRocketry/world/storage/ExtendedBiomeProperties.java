package zmaster587.advancedRocketry.world.storage;

import java.util.Arrays;
import java.util.HashMap;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class ExtendedBiomeProperties {

	World world;
	
	HashMap<ChunkCoordIntPair, ChunkProperties> chunkPropertyMap;
	
	public ExtendedBiomeProperties(World world) {
		this.world = world;
		chunkPropertyMap = new HashMap<ChunkCoordIntPair, ExtendedBiomeProperties.ChunkProperties>();
	}
	
	public ChunkProperties getChunkPropertiesFromChunkCoords(int x, int z) {
		ChunkCoordIntPair coordPair = new ChunkCoordIntPair(x, z);
		ChunkProperties properties = chunkPropertyMap.get(coordPair);
		
		if(properties == null) {
			properties = new ChunkProperties(world, x, z);
		}
		
		return properties;
	}
	
	public ChunkProperties getChunkPropertiesFromBlockCoords(int x, int z) {
		
		x = x >> 4;
		z = z >> 4;
		
		ChunkCoordIntPair coordPair = new ChunkCoordIntPair(x, z);
		ChunkProperties properties = chunkPropertyMap.get(coordPair);
		
		if(properties == null) {
			properties = new ChunkProperties(world, x, z);
		}
		
		return properties;
	}
	
	public class ChunkProperties {

		private int[] blockBiomeArray;
		private int positionX, positionZ;

		public ChunkProperties(World world, int x, int z) {
			blockBiomeArray = new int[256];
			Arrays.fill(blockBiomeArray, -1);
			positionX = x;
			positionZ = z;

		}

		public int[] getBlockBiomeArray() {
			return blockBiomeArray;
		}
		

		public void setBlockBiomeArray(int[] ints) {
			blockBiomeArray = ints;
		}
		
		public BiomeGenBase getBiomeGenForWorldCoords(int x, int z, WorldChunkManager worldChunkManager) {

			int id = blockBiomeArray[z << 4 | x];
			
	        if (id == -1)
	        {
	            BiomeGenBase biomegenbase = worldChunkManager.getBiomeGenAt((this.positionX << 4) + x, (this.positionZ << 4) + z);
	            id = biomegenbase.biomeID;
	            this.blockBiomeArray[z << 4 | x] = id;
	        }
			
			BiomeGenBase biome = AdvancedRocketryBiomes.instance.getBiomeById(id);

			if(biome == null)
				return BiomeGenBase.getBiome(id) == null ? BiomeGenBase.plains : BiomeGenBase.getBiome(id);


			
			return biome;
		}
	}
}
