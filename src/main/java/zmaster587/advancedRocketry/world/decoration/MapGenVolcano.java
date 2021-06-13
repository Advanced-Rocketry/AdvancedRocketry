package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class MapGenVolcano extends MapGenBase {

	int chancePerChunk;
	private static final Block blockEnrichedLava = AdvancedRocketryBlocks.blockEnrichedLavaFluid;
	private static final Block blockCasing = AdvancedRocketryBlocks.blockBasalt;

	public MapGenVolcano(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;
	}


	@Override
	protected void recursiveGenerate(World world, int chunkX,
			int chunkZ, int p_180701_4_, int p_180701_5_,
			ChunkPrimer chunkPrimerIn) {

		chancePerChunk = 15;

		if(rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk) {

			int size = 64;
			int sizeDeviation = rand.nextInt(8); // 0 -> 8
			int baseHeight = 40;
			int lavaNodeHeight = 25;

			int xCoord = -chunkX + p_180701_4_;
			int zCoord =  -chunkZ + p_180701_5_;
			int crackle;

			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {

					//Do some roughness
					crackle = rand.nextInt(2);

					int xCoordNew = (xCoord*16)+x;
					int zCoordNew = (zCoord*16)+z;

					int x2 = xCoordNew*xCoordNew;
					int z2 = zCoordNew*zCoordNew;
					double radius = Math.sqrt(x2 + z2);
					double func = 1/(Math.pow(1.028, radius-(size-sizeDeviation)*3)) + 
							+ baseHeight
							- 8/(Math.pow(1.09, radius-((size-sizeDeviation)/2.6)))
							-Math.pow(1.7, radius - size*.9);
					
					for(int y = 254; y >= 1; y--) {
						boolean underSurface = func >= y+crackle;
						boolean innerLayer = func >= y+crackle && radius < 5 && y > lavaNodeHeight;

						if(innerLayer)
							chunkPrimerIn.setBlockState(x, y, z, blockEnrichedLava.getDefaultState());
						else if(underSurface)
							chunkPrimerIn.setBlockState(x, y, z, blockCasing.getDefaultState());
						
						
						double sphereradius = x2+z2+(y - lavaNodeHeight)*(y - lavaNodeHeight);
						if(sphereradius < 23*23)
							chunkPrimerIn.setBlockState(x, y, z, blockEnrichedLava.getDefaultState());
						else if(sphereradius < 25*25)
							chunkPrimerIn.setBlockState(x, y, z, blockCasing.getDefaultState());
						
						//For the top of the bulb, im lazy
						if(innerLayer)
							chunkPrimerIn.setBlockState(x, y, z, blockEnrichedLava.getDefaultState());
					}
				}
			}
		}
	}
}