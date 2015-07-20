package zmaster587.advancedRocketry.world.storage;

import zmaster587.advancedRocketry.world.ProviderPlanet;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkExtendedBiome extends Chunk {

	
	public ChunkExtendedBiome(World p_i45447_1_, Block[] p_i45447_2_,
			byte[] p_i45447_3_, int p_i45447_4_, int p_i45447_5_) {
		super(p_i45447_1_, p_i45447_2_, p_i45447_3_, p_i45447_4_, p_i45447_5_);
	}
	

	public int[] getBiomeIntArray() {
		return ((ProviderPlanet)worldObj.provider).getDimensionProperties().biomeProperties.getChunkPropertiesFromChunkCoords(this.xPosition, this.zPosition).getBlockBiomeArray();
	}
	
	
}
