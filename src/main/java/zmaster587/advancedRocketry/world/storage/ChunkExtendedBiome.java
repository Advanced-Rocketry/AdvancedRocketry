package zmaster587.advancedRocketry.world.storage;

import java.util.Arrays;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.world.ProviderPlanet;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;

public class ChunkExtendedBiome extends Chunk {

	private int[] blockBiomeArray;
	
	public ChunkExtendedBiome(World world, int x, int z) {
		super(world, x, z);
		
		this.blockBiomeArray = new int[Configuration.maxBiomes];
		
		Arrays.fill(blockBiomeArray, Integer.MAX_VALUE);
	}
	
	public ChunkExtendedBiome(World p_i45447_1_, Block[] p_i45447_2_,
			byte[] p_i45447_3_, int p_i45447_4_, int p_i45447_5_) {
		super(p_i45447_1_, p_i45447_2_, p_i45447_3_, p_i45447_4_, p_i45447_5_);
	}
	

	/*public int[] getBiomeIntArray() {
		return ((ProviderPlanet)worldObj.provider).getDimensionProperties().biomeProperties.getChunkPropertiesFromChunkCoords(this.xPosition, this.zPosition).getBlockBiomeArray();
	}*/
	

	public void fillExtraBiomes(int[] biomes) {
		blockBiomeArray = biomes;
	}
	
	/*public int[] getBiomeIntArray() {
		return blockBiomeArray;
	}*/
	
	public void setBiomeArray(int[] biomes) {
		blockBiomeArray = biomes;
	}
	
	/*@Override
    public BiomeGenBase getBiomeGenForWorldCoords(int p_76591_1_, int p_76591_2_, WorldChunkManager chunkmanager)
    {
		ExtendedBiomeProperties properties = ((ProviderPlanet)worldObj.provider).getDimensionProperties().biomeProperties;
    	int[] blockBiomeArray = properties.getChunkPropertiesFromChunkCoords(this.xPosition, this.zPosition).getBlockBiomeArray();
        int k = blockBiomeArray[p_76591_2_ << 4 | p_76591_1_];

        if (k == Integer.MAX_VALUE)
        {
            BiomeGenBase biomegenbase = chunkmanager.getBiomeGenAt((this.xPosition << 4) + p_76591_1_, (this.zPosition << 4) + p_76591_2_);
            k = biomegenbase.biomeID;
            blockBiomeArray[p_76591_2_ << 4 | p_76591_1_] = k;
            
            properties.getChunkPropertiesFromChunkCoords(this.xPosition, this.zPosition).setBlockBiomeArray(blockBiomeArray);
        }

        return AdvancedRocketryBiomes.instance.getBiomeById(k);
    }*/
}
