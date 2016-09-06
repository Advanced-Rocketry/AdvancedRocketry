package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.network.PacketBiomeIDChange;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class BiomeHandler {
	public static void changeBiome(World world, int biomeId, int x, int z) {
		Chunk chunk = world.getChunkFromBlockCoords(x, z);

		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		BiomeGenBase biomeTo = BiomeGenBase.getBiome(biomeId);
		
		if(biome == biomeTo)
			return;
		
		int y = 60;
		if(biome.topBlock != biomeTo.topBlock) {
			int yy = world.getHeightValue(x, z);
			
			while(!world.getBlock(x, yy - 1, z).isOpaqueCube() && yy > 0)
				yy--;
			
			if(world.getBlock(x, yy - 1, z) == biome.topBlock)
				world.setBlock(x, yy - 1, z, biomeTo.topBlock);

			y = (short)yy;
		}

		byte[] biomeArr = chunk.getBiomeArray();
		try {
			biomeArr[(x & 15) + (z & 15)*16] = (byte)biomeId;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, new BlockPosition(x, y, z)), world.provider.dimensionId, x, y, z, 256);
	}
	
	public static void changeBiome(World world, int biomeId, Chunk chunk, int x, int z) {

		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		BiomeGenBase biomeTo = BiomeGenBase.getBiome(biomeId);
		int y = 60;
		if(biome.topBlock != biomeTo.topBlock) {
			int yy = world.getHeightValue(x, z);
			if(world.getBlock(x, yy - 1, z) == biome.topBlock)
				world.setBlock(x, yy - 1, z, biomeTo.topBlock);

			y = (short)yy;
		}

		byte[] biomeArr = chunk.getBiomeArray();
		biomeArr[(x % 16)+ (z % 16)*16] = (byte)biomeId;

		PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, new BlockPosition(x, y, z)), world.provider.dimensionId, x, y, z, 256);
	}
}
