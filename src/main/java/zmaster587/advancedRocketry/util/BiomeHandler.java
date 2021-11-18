package zmaster587.advancedRocketry.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BiomeHandler {
	
	
	public static void changeBiome(World world, int biomeId, BlockPos pos) {
		/*Chunk chunk = world.getChunkFromBlockCoords(pos);

		Biome biome = world.getBiome(pos);
		Biome biomeTo = Biome.getBiome(biomeId);
		
		if(biome == biomeTo)
			return;
		
		int y = 60;
		if(biome.topBlock != biomeTo.topBlock) {
			BlockPos yy = world.getHeight(pos);
			
			while(!world.getBlockState(yy.down()).isOpaqueCube() && yy.getY() > 0)
				yy = yy.down();
			
			if(world.getBlockState(yy.down()) == biome.topBlock)
				world.setBlockState(yy.down(), biomeTo.topBlock);
		}

		byte[] biomeArr = chunk.getBiomeArray();
		try {
			biomeArr[(pos.getX() & 15) + (pos.getZ() & 15)*16] = (byte)biomeId;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, new HashedBlockPosition(pos)), world.provider.getDimension(), pos, 256);*/
	}
	
	public static void changeBiome(World world, int biomeId, int x, int z) {
		changeBiome(world, biomeId, new BlockPos(x, 0, z));
	}
	
	public static void changeBiome(World world, int biomeId, Chunk chunk, int x, int z) {
		
	}
	
	public static void changeBiome(World world, int biomeId, Chunk chunk, BlockPos pos) {

		/*Biome biome = world.getBiome(pos);
		Biome biomeTo = Biome.getBiome(biomeId);
		
		int x = pos.getX();
		int z = pos.getZ();
		if(biome == biomeTo)
			return;
		
		int y = 60;
		if(biome.topBlock != biomeTo.topBlock) {
			int yy = chunk.getHeightValue(x & 15, z & 15);
			
			while(!world.getBlockState(new BlockPos(x, yy - 1, z)).isOpaqueCube() && yy > 0)
				yy--;
			
			if(yy == 0)
				return;
			
			
			
			if(chunk.getBlockState(x & 15, yy - 1, z & 15) == biome.topBlock)
				chunk.setBlockState(new BlockPos(x & 15, yy - 1, z & 15), biomeTo.topBlock);

		}

		byte[] biomeArr = chunk.getBiomeArray();
		biomeArr[(x & 15) + (z & 15)*16] = (byte)biomeId;

		//PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, new BlockPosition(x, y, z)), world.provider.dimensionId, x, y, z, 256);*/
	}
}
