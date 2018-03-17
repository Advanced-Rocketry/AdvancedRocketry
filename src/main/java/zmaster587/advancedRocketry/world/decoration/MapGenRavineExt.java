package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExt extends MapGenRavine {

	Block fillerBlock;
	Block oceanBlock;
	
	
	public void setFillerBlock(Block state)
	{
		fillerBlock = state;
	}
	
	public void setOceanBlock(Block state)
	{
		oceanBlock = state;
	}
	
    //Exception biomes to make sure we generate like vanilla
    private boolean isExceptionBiome(BiomeGenBase biome)
    {
    	if (biome == BiomeGenBase.mushroomIslandShore) return true;
        if (biome == BiomeGenBase.mushroomIsland) return true;
        if (biome == BiomeGenBase.beach) return true;
        if (biome == BiomeGenBase.desert) return true;
        return false;
    }
    
    protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
    {
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        Block top    = (isExceptionBiome(biome) ? Blocks.grass : biome.topBlock);
        Block filler = (isExceptionBiome(biome) ? Blocks.dirt  : biome.fillerBlock);
        Block block  = data[index];

        if (block == fillerBlock || block == filler || block == top)
        {
            if (y < 10)
            {
                data[index] = Blocks.flowing_lava;
            }
            else
            {
                data[index] = null;

                if (foundTop && data[index - 1] == filler)
                {
                    data[index - 1] = top;
                }
            }
        }
    }
	
}
