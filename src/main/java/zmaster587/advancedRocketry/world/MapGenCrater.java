package zmaster587.advancedRocketry.world;

import zmaster587.advancedRocketry.AdvancedRocketry;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;

public class MapGenCrater extends MapGenBase {

    
	@Override
	protected void func_151538_a(World world, int rangeX,
			int rangeZ, int chunkX, int chunkZ,
			Block[] chunkArray) {
		
		if(rand.nextInt(30) == Math.abs(rangeX) % 30 && rand.nextInt(30) == Math.abs(rangeZ) % 30) {

			int radius = rand.nextInt(56) + 8; //64; 8 -> 64
			
			//TODO: make hemisphere from surface and line the side with ore of some kind

			int depth = radius*radius;
			
			int xCoord = -rangeX + chunkX;
			int zCoord =  -rangeZ + chunkZ;
			
			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {
					for(int y = 255; y >= 0; y--) {
						int index = (x * 16 + z) * 256 + y;
						
						if(chunkArray[index] != null) {
							int count = ( depth - ( ((xCoord*16)+x)*((xCoord*16)+x) + ((zCoord*16)+z)*((zCoord*16)+z) ) )/(radius*2);
							
							for(int dist = 0; dist < count; dist++) {
								index = (x * 16 + z) * 256 + y-dist;
								chunkArray[index] = null;
							}
							
							int ridgeSize = 12;
							
							if(count <= 0 && count > -2*ridgeSize) {

								
								for(int dist = 0; dist < ((ridgeSize*ridgeSize) - (count+ridgeSize)*(count+ridgeSize))/(ridgeSize*2); dist++) {
									index = (x * 16 + z) * 256 + y + dist;
									chunkArray[index] = world.getBiomeGenForCoords(rangeX, rangeZ).topBlock;
								}
								
								
							}
							
							if(count > 1)
								chunkArray[(x * 16 + z) * 256 + y-count] = AdvancedRocketry.blockMoonTurf;
							break;
						}
					}
				}
			}
		}
	}
}