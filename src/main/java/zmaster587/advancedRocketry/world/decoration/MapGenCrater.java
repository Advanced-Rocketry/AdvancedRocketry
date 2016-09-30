package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public class MapGenCrater extends MapGenBase {
    
	int chancePerChunk;
	
	public MapGenCrater(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;
	}
	
	
	@Override
	protected void recursiveGenerate(World world, int chunkX,
			int chunkZ, int p_180701_4_, int p_180701_5_,
			ChunkPrimer chunkPrimerIn) {
		
		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk) {

			int radius = rand.nextInt(56) + 8; //64; 8 -> 64
			
			//TODO: make hemisphere from surface and line the side with ore of some kind

			int depth = radius*radius;
			
			int xCoord = -chunkX + p_180701_4_;
			int zCoord =  -chunkZ + p_180701_5_;
			
			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {
					for(int y = 254; y >= 0; y--) {
						if(chunkPrimerIn.getBlockState(x, y, z) != Blocks.AIR.getDefaultState()) { //&& chunkPrimerIn.getBlockState(x, y, z).isOpaqueCube()) {
							int count = ( depth - ( ((xCoord*16)+x)*((xCoord*16)+x) + ((zCoord*16)+z)*((zCoord*16)+z) ) )/(radius*2);
							
							for(int dist = 0; dist < count; dist++) {
								chunkPrimerIn.setBlockState(x, y-dist, z, Blocks.AIR.getDefaultState());
							}
							
							int ridgeSize = 12;
							
							if(count <= 0 && count > -2*ridgeSize) {

								
								for(int dist = 0; dist < ((ridgeSize*ridgeSize) - (count+ridgeSize)*(count+ridgeSize))/(ridgeSize*2); dist++) {
									chunkPrimerIn.setBlockState(x, y + dist, z, world.getBiomeGenForCoords(new BlockPos(chunkX*16,0, chunkZ*16)).topBlock);
								}
							}
							
							if(count > 1)
								chunkPrimerIn.setBlockState(x, y - count, z, world.getBiomeGenForCoords(new BlockPos(chunkX*16,0, chunkZ*16)).topBlock);
							break;
						}
					}
				}
			}
		}
	}
}