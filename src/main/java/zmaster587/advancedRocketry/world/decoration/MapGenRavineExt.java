package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExt extends MapGenRavine {

	IBlockState fillerBlock;
	IBlockState oceanBlock;
	
	
	public void setFillerBlock(IBlockState state)
	{
		fillerBlock = state;
	}
	
	public void setOceanBlock(IBlockState state)
	{
		oceanBlock = state;
	}
	
    private boolean isExceptionBiome(net.minecraft.world.biome.Biome biome)
    {
        if (biome == net.minecraft.init.Biomes.BEACH) return true;
        if (biome == net.minecraft.init.Biomes.DESERT) return true;
        if (biome == net.minecraft.init.Biomes.MUSHROOM_ISLAND) return true;
        if (biome == net.minecraft.init.Biomes.MUSHROOM_ISLAND_SHORE) return true;
        return false;
    }
    
    protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
    {
        net.minecraft.world.biome.Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState state = data.getBlockState(x, y, z);
        IBlockState top = isExceptionBiome(biome) ? Blocks.GRASS.getDefaultState() : biome.topBlock;
        IBlockState filler = isExceptionBiome(biome) ? Blocks.DIRT.getDefaultState() : biome.fillerBlock;

        if (state.getBlock() == Blocks.STONE || state.getBlock() == top.getBlock() || state.getBlock() == filler.getBlock() || (fillerBlock != null && state.getBlock() == fillerBlock.getBlock()))
        {
            if (y - 1 < 10)
            {
                data.setBlockState(x, y, z, FLOWING_LAVA);
            }
            else
            {
                data.setBlockState(x, y, z, AIR);

                if (foundTop && data.getBlockState(x, y - 1, z).getBlock() == filler.getBlock())
                {
                    data.setBlockState(x, y - 1, z, top.getBlock().getDefaultState());
                }
            }
        }
    }
	
}
