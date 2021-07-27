package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExt extends MapGenRavine {

	BlockState fillerBlock;
	BlockState oceanBlock;
	

	public void setFillerBlock(BlockState state)
	{
		fillerBlock = state;
	}
	
	public void setOceanBlock(BlockState state)
	{
		oceanBlock = state;
	}
	
    private boolean isExceptionBiome(net.minecraft.world.biome.Biome biome)
    {
        return biome == Biomes.BEACH ||
        biome == Biomes.DESERT ||
        biome == Biomes.MUSHROOM_FIELDS ||
        biome == Biomes.MUSHROOM_FIELD_SHORE;
    }
    
    protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
    {
        BlockPos pos = new BlockPos(x, y, z);
        net.minecraft.world.biome.Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        BlockState state = data.getBlockState(pos);
        BlockState top = isExceptionBiome(biome) ? Blocks.GRASS.getDefaultState() : biome.topBlock;
        BlockState filler = isExceptionBiome(biome) ? Blocks.DIRT.getDefaultState() : biome.fillerBlock;

        if (state.getBlock() == Blocks.STONE || state.getBlock() == top.getBlock() || state.getBlock() == filler.getBlock() || (fillerBlock != null && state.getBlock() == fillerBlock.getBlock()))
        {
            if (y - 1 < 10)
            {
                data.setBlockState(pos, FLOWING_LAVA);
            }
            else
            {
                data.setBlockState(pos, Blocks.AIR.getDefaultState(), false);

                BlockPos posBelow = new BlockPos(x, y - 1, z);
                if (foundTop && data.getBlockState(posBelow).getBlock() == filler.getBlock())
                {
                    data.setBlockState(posBelow, top.getBlock().getDefaultState(), false);
                }
            }
        }
    }
	
}
