package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenCaveExt extends MapGenCaves {

	IBlockState fillerBlock;
	IBlockState oceanBlock;
	
	public MapGenCaveExt()
	{
		super();
		fillerBlock = null;
		oceanBlock = null;
	}
	
	public void setFillerBlock(IBlockState state)
	{
		fillerBlock = state;
	}
	
	public void setOceanBlock(IBlockState state)
	{
		oceanBlock = state;
	}
	
    protected boolean canReplaceBlock(IBlockState p_175793_1_, IBlockState p_175793_2_)
    {
    	return super.canReplaceBlock(p_175793_1_, p_175793_2_) || ( fillerBlock != null && p_175793_1_.getBlock() == fillerBlock.getBlock());
    }
    
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ)
    {
        net.minecraft.block.Block block = data.getBlockState(x, y, z).getBlock();
        return super.isOceanBlock(data, x, y, z, chunkX, chunkZ) || (oceanBlock != null && oceanBlock.getBlock() == block);
    }
}
